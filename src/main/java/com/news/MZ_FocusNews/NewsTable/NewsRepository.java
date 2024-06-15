package com.news.MZ_FocusNews.NewsTable;

import com.news.MZ_FocusNews.NewsTable.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Integer> {
    boolean existsByLink(String link);

    List<News> findByCategory(String category); // 카테고리 별

    List<News> findByTitleContainingAndNewsIdGreaterThan(String keyword, int newsId); // 알림설정을 한 user에게만 [속보] 알림 보내기

    List<News> findByTitleContaining(String keyword);

    List<News> findByTitleContainingOrTitleContaining(String keyword1, String keyword2);

    List<News> findByTitleContainingOrTitleContainingOrTitleContaining(String keyword1, String keyword2, String keyword3);

    List<News> findBySummaryIsNotNull();

}