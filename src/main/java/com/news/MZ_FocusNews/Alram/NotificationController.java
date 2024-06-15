package com.news.MZ_FocusNews.Alram;

import com.news.MZ_FocusNews.NewsTable.News;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/send-notifications")
    public List<NotificationDto> sendNotifications() {
        return notificationService.getNotifications();
    }

    @GetMapping("/new-breaking-news")
    public List<News> getNewBreakingNews() {
        return notificationService.getNewBreakingNews();
    }

    @PostMapping("/update-alarm-permission")
    public void updateAlarmPermission(@RequestParam String userId, @RequestParam boolean alarmPermission) {
        notificationService.updateAlarmPermission(userId, alarmPermission);
    }
}
