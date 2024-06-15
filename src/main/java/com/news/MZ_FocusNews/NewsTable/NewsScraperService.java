package com.news.MZ_FocusNews.NewsTable;

import com.news.MZ_FocusNews.ResourceNotFoundException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

// 뉴스 크롤링 후 MySql 저장
@Service
public class NewsScraperService {

    private static final Logger logger = Logger.getLogger(NewsScraperService.class.getName());

    @Autowired
    private NewsRepository newsRepository;

    // 키워드로 뉴스 검색 -> [속보]와 사용자가 입력한 키워드
    public List<News> searchNewsByKeyword(String keyword) {
        return newsRepository.findByTitleContaining(keyword);
    }

    // ID로 뉴스 검색
    public News getNewsById(Integer id) {
        return newsRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("News not found with id " + id));
    }

    // 모든 뉴스 가져오기
    public List<News> getAllNews() {
        return newsRepository.findAll();
    }

    // 각 카테고리별로 크롤링할 RSS URL 목록
    private static final Map<String, String> RSS_URLS;
    static {
        Map<String, String> urls = new HashMap<>();
        urls.put("politics", "https://news.google.com/rss/topics/CAAqIQgKIhtDQkFTRGdvSUwyMHZNRFZ4ZERBU0FtdHZLQUFQAQ?hl=ko&gl=KR&ceid=KR%3Ako");
        urls.put("economy", "https://news.google.com/rss/topics/CAAqIggKIhxDQkFTRHdvSkwyMHZNR2RtY0hNekVnSnJieWdBUAE?hl=ko&gl=KR&ceid=KR:ko");
        urls.put("society", "https://news.google.com/rss/topics/CAAqIQgKIhtDQkFTRGdvSUwyMHZNRFp4WkRNU0FtdHZLQUFQAQ?hl=ko&gl=KR&ceid=KR%3Ako");
        urls.put("recruitment", "https://news.google.com/rss/topics/CAAqJAgKIh5DQkFTRUFvS0wyMHZNRFF4TVRWME1oSUNhMjhvQUFQAQ?hl=ko&gl=KR&ceid=KR%3Ako");
        urls.put("science", "https://news.google.com/rss/topics/CAAqKAgKIiJDQkFTRXdvSkwyMHZNR1ptZHpWbUVnSnJieG9DUzFJb0FBUAE?hl=ko&gl=KR&ceid=KR:ko");
        urls.put("entertainment", "https://news.google.com/rss/topics/CAAqIQgKIhtDQkFTRGdvSUwyMHZNREZ5Wm5vU0FtdHZLQUFQAQ?hl=ko&gl=KR&ceid=KR:ko");
        urls.put("sports", "https://news.google.com/rss/topics/CAAqJggKIiBDQkFTRWdvSUwyMHZNRFp1ZEdvU0FtdHZHZ0pMVWlnQVAB?hl=ko&gl=KR&ceid=KR%3Ako");
        RSS_URLS = Collections.unmodifiableMap(urls);
    }

    // 카테고리별 뉴스 크롤링 및 저장
    @Async
    public CompletableFuture<Void> fetchAndSaveNews(String category) {
        try {
            String rssUrl = RSS_URLS.get(category);
            if (rssUrl == null) {
                logger.log(Level.WARNING, "Invalid category: " + category);
                return CompletableFuture.completedFuture(null);
            }

            Document doc = Jsoup.connect(rssUrl).get();
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            // RSS에서 제목, 발행날짜, 원본 링크, 언론사 크롤링
            doc.select("item").forEach(item -> {
                String title = item.select("title").text();
                String pubDate = item.select("pubDate").text();
                String link = item.select("link").text().trim();
                String publish = item.select("source").text();

                if (newsRepository.existsByLink(link)) {
                    return; // 이미 존재하는 링크면 저장하지 않음
                }
                if (link != null && link.length() > 600) {
                    logger.log(Level.WARNING, "link 값이 너무 길어서 저장하지 않습니다: " + link);
                    return;
                }
                if (publish != null && publish.length() > 20) {
                    logger.log(Level.WARNING, "Publish 값이 너무 길어서 저장하지 않습니다: " + publish);
                    return;
                }
                if (title != null && title.length() > 100) {
                    logger.log(Level.WARNING, "title 값이 너무 길어서 저장하지 않습니다: " + title);
                    return;
                }

                LocalDateTime date = DateTimeUtils.parseDate(pubDate);

                News news = new News();
                news.setTitle(title);
                news.setLink(link);
                news.setDate(date);
                news.setCategory(category);
                news.setView(0);
                news.setPublish(publish);

                CompletableFuture<Void> future = saveNewsAsync(news);
                futures.add(future);
            });

            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching news for category: " + category, e);
            return CompletableFuture.failedFuture(e);
        }
    }

    // 뉴스 비동기 저장
    @Async
    @Transactional
    public CompletableFuture<Void> saveNewsAsync(News news) {
        newsRepository.save(news);
        return CompletableFuture.completedFuture(null);
    }
}
