package com.news.MZ_FocusNews.NewsTable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    private static final Logger logger = Logger.getLogger(SchedulerConfig.class.getName());

    @Autowired
    private NewsScraperService newsScraperService;

    @Scheduled(fixedRate = 1800000) // 30분마다 실행
    public void scheduleNewsFetching() {
        logger.log(Level.INFO, "뉴스 스케줄러 실행중");

        String[] categories = {"politics", "economy", "society", "recruitment", "science", "entertainment", "sports"};
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (String category : categories) {
            CompletableFuture<Void> future = newsScraperService.fetchAndSaveNews(category);
            future.exceptionally(e -> {
                logger.log(Level.SEVERE, "Error fetching news for category: " + category, e);
                return null;
            });
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        logger.log(Level.INFO, "Completed scheduled news fetching.");
    }
}