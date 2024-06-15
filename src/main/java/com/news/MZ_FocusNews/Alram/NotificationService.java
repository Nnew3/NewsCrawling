package com.news.MZ_FocusNews.Alram;

import com.news.MZ_FocusNews.NewsTable.News;
import com.news.MZ_FocusNews.NewsTable.NewsRepository;
import com.news.MZ_FocusNews.UsersTable.User;
import com.news.MZ_FocusNews.UsersTable.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NewsRepository newsRepository;

    private Integer lastCheckedNewsId = 0;

    // 사용자 알림 정보 가져오기
    public List<NotificationDto> getNotifications() {
        List<User> usersWithAlarmPermission = userRepository.findByAlarmPermission(true); // 알림 권한이 있는 사용자 목록 가져오기
        List<News> breakingNews = newsRepository.findByTitleContaining("[속보]"); // "[속보]"가 포함된 뉴스 목록 가져오기

        return usersWithAlarmPermission.stream()
                .flatMap(user -> breakingNews.stream()
                        .map(news -> new NotificationDto(user.getUserId(), news.getTitle()))) // 사용자와 뉴스를 매핑하여 NotificationDto 생성
                .collect(Collectors.toList());
    }

    // 새로운 속보 뉴스 가져오기
    public List<News> getNewBreakingNews() {
        List<News> newBreakingNews = newsRepository.findByTitleContainingAndNewsIdGreaterThan("[속보]", lastCheckedNewsId); // 새로운 속보 뉴스 가져오기
        if (!newBreakingNews.isEmpty()) {
            lastCheckedNewsId = newBreakingNews.get(newBreakingNews.size() - 1).getNewsId(); // 마지막 확인된 뉴스 ID 업데이트
        }
        return newBreakingNews;
    }

    // 사용자 알림 권한 업데이트
    public void updateAlarmPermission(String userId, boolean alarmPermission) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        user.setAlarmPermission(alarmPermission); // 사용자 알림 권한 설정
        userRepository.save(user); // 사용자 정보 저장
    }
}
