package com.news.MZ_FocusNews.UsersTable;

import jakarta.persistence.*;

import java.util.List;
import java.util.Map;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "user_pw", nullable = false)
    private String userPw;

    @Column(name = "alarm_permission", nullable = false)
    private boolean alarmPermission;

    @Column(name = "quiz_score")
    private int quizScore;

    @Column(name = "interest_category", columnDefinition = "json")
    @Convert(converter = StringListConverter.class)
    private Map<String, Integer> interestCategory;


    @Column(name = "keyword1", length = 10)
    private  String keyword1;
    @Column(name = "keyword2", length = 10)
    private String keyword2;
    @Column(name = "keyword3", length = 10)
    private String keyword3;


    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPw() {
        return userPw;
    }

    public void setUserPw(String userPw) {
        this.userPw = userPw;
    }

    public boolean isAlarmPermission() {
        return alarmPermission;
    }

    public void setAlarmPermission(boolean alarmPermission) {
        this.alarmPermission = alarmPermission;
    }

    public int getQuizScore() {
        return quizScore;
    }

    public void setQuizScore(int quizScore) {
        this.quizScore = quizScore;
    }

    public Map<String, Integer> getInterestCategory() {
        return interestCategory;
    }

    public void setInterestCategory(Map<String, Integer> interestCategory) {
        this.interestCategory = interestCategory;
    }
    public String getKeyword1() {
        return keyword1;
    }

    public void setKeyword1(String keyword1) {
        this.keyword1 = keyword1;
    }

    public String getKeyword2() {
        return keyword2;
    }

    public void setKeyword2(String keyword2) {
        this.keyword2 = keyword2;
    }

    public String getKeyword3() {
        return keyword3;
    }

    public void setKeyword3(String keyword3) {
        this.keyword3 = keyword3;
    }
}
