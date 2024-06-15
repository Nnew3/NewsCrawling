package com.news.MZ_FocusNews.NewsTable;

import com.news.MZ_FocusNews.UsersTable.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Autowired
    private NewsService newsService;
    @Autowired
    private UserService userService;

    @GetMapping
    public List<News> getAllNews() {
        return newsService.getAllNews();
    }
    @GetMapping("/non-null-summary")
    public List<News> getNonNullSummaryNews() {
        return newsService.getNonNullSummaryNews();
    }

    @GetMapping("/category/{category}")
    public List<News> getNewsByCategory(@PathVariable String category, @RequestParam String sort) {
        return newsService.getNewsByCategory(category, sort);
    }

    @GetMapping("/breaking")
    public List<News> getBreakingNews(@RequestParam(defaultValue = "1") int limit, @RequestParam(defaultValue = "[속보]") String keyword) {
        return newsService.getBreakingNews(limit, keyword);
    }

    @GetMapping("/search")
    public ResponseEntity<List<News>> searchNewsByKeyword(@RequestParam String keyword) {
        List<News> newsList = newsService.searchNewsByKeyword(keyword);
        return ResponseEntity.ok(newsList);
    }

    @GetMapping("/searchByKeywords")
    public List<News> getNewsByKeywords(@RequestParam("userId") String userId) {
        List<String> keywords = userService.getUserKeywords(userId);
        System.out.println("Received keywords: " + keywords);
        if (keywords.isEmpty()) {
            return List.of(); // 키워드가 없을 경우 빈 리스트 반환
        }
        String keyword1 = keywords.size() > 0 ? keywords.get(0) : "";
        String keyword2 = keywords.size() > 1 ? keywords.get(1) : "";
        String keyword3 = keywords.size() > 2 ? keywords.get(2) : "";

        if (keyword1.isEmpty() && keyword2.isEmpty() && keyword3.isEmpty()) {
            return List.of(); // 모든 키워드가 비어있을 경우 빈 리스트 반환
        }

        return newsService.searchNewsByKeywords(keyword1, keyword2, keyword3);
    }

    @PostMapping
    public News createNews(@RequestBody News news) {
        return newsService.createNews(news);
    }

    @GetMapping("/{id}")
    public News getNewsById(@PathVariable Integer id) {
        return newsService.getNewsById(id);
    }

    @PutMapping("/{id}")
    public News updateNews(@PathVariable Integer id, @RequestBody News newsDetails) {
        return newsService.updateNews(id, newsDetails);
    }

    @DeleteMapping("/{id}")
    public void deleteNews(@PathVariable Integer id) {
        newsService.deleteNews(id);
    }
}