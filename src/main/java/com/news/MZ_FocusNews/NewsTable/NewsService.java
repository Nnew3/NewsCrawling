package com.news.MZ_FocusNews.NewsTable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NewsService {

    @Autowired
    private NewsRepository newsRepository;

    // 모든 뉴스 가져오기
    public List<News> getAllNews() {
        return newsRepository.findBySummaryIsNotNull();
    }

    // 키워드로 뉴스 검색
    public List<News> searchNewsByKeywords(String keyword1, String keyword2, String keyword3) {
        if (keyword1.isEmpty() && keyword2.isEmpty() && keyword3.isEmpty()) {
            return List.of(); // 모든 키워드가 비어있을 경우 빈 리스트 반환
        } else if (keyword2.isEmpty() && keyword3.isEmpty()) {
            return newsRepository.findByTitleContaining(keyword1);
        } else if (keyword3.isEmpty()) {
            return newsRepository.findByTitleContainingOrTitleContaining(keyword1, keyword2);
        } else {
            return newsRepository.findByTitleContainingOrTitleContainingOrTitleContaining(keyword1, keyword2, keyword3);
        }
    }

    // 카테고리 뉴스 정렬 (최신순, 조회수순, 기본순)
    public List<News> getNewsByCategory(String category, String sort) {
        List<News> newsList = newsRepository.findByCategory(category);

        switch (sort) {
            case "latest":
                newsList.sort(Comparator.comparing(News::getDate).reversed());
                break;
            case "views":
                newsList.sort(Comparator.comparing(News::getView).reversed());
                break;
        }

        return newsList;
    }

    // 특정 키워드가 포함된 최신 뉴스 가져오기
    public List<News> getBreakingNews(int limit, String keyword) {
        List<News> newsList = newsRepository.findByTitleContaining(keyword);
        newsList.sort(Comparator.comparing(News::getDate).reversed());
        return newsList.stream().limit(limit).collect(Collectors.toList());
    }

    // 키워드로 뉴스 검색 (중복된 메서드)
    public List<News> searchNewsByKeyword(String keyword) {
        return newsRepository.findByTitleContaining(keyword);
    }

    // 새로운 뉴스 생성
    public News createNews(News news) {
        return newsRepository.save(news);
    }

    // ID로 뉴스 가져오기
    public News getNewsById(Integer id) {
        return newsRepository.findById(id).orElseThrow(() -> new RuntimeException("News not found with id " + id));
    }

    // 뉴스 업데이트
    public News updateNews(Integer id, News newsDetails) {
        News news = newsRepository.findById(id).orElseThrow(() -> new RuntimeException("News not found with id " + id));

        news.setTitle(newsDetails.getTitle());
        news.setLink(newsDetails.getLink());
        news.setDate(newsDetails.getDate());
        news.setCategory(newsDetails.getCategory());
        news.setSummary(newsDetails.getSummary());
        news.setPublish(newsDetails.getPublish());
        news.setImgUrl(newsDetails.getImgUrl());

        return newsRepository.save(news);
    }

    // 뉴스 삭제
    public void deleteNews(Integer id) {
        News news = newsRepository.findById(id).orElseThrow(() -> new RuntimeException("News not found with id " + id));
        newsRepository.delete(news);
    }
}
