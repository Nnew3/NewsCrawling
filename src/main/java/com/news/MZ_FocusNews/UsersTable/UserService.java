package com.news.MZ_FocusNews.UsersTable;

import com.news.MZ_FocusNews.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // ID로 사용자 가져오기
    public User getUserByUserId(String userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
    }

    // 모든 사용자 가져오기
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 사용자 저장
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // 사용자의 키워드 가져오기
    public List<String> getUserKeywords(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        List<String> keywords = new ArrayList<>();
        if (user.getKeyword1() != null && !user.getKeyword1().isEmpty()) {
            keywords.add(user.getKeyword1());
        }
        if (user.getKeyword2() != null && !user.getKeyword2().isEmpty()) {
            keywords.add(user.getKeyword2());
        }
        if (user.getKeyword3() != null && !user.getKeyword3().isEmpty()) {
            keywords.add(user.getKeyword3());
        }
        return keywords;
    }

    // 사용자 정보 업데이트
    public User updateUser(String userId, User userDetails) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        user.setUserName(userDetails.getUserName());
        user.setUserPw(userDetails.getUserPw());
        user.setAlarmPermission(userDetails.isAlarmPermission());
        user.setQuizScore(userDetails.getQuizScore());
        user.setInterestCategory(userDetails.getInterestCategory());
        user.setKeyword1(userDetails.getKeyword1());
        user.setKeyword2(userDetails.getKeyword2());
        user.setKeyword3(userDetails.getKeyword3());
        return userRepository.save(user);
    }

    // 사용자 삭제
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        userRepository.delete(user);
    }

    // 사용자 키워드 저장
    public void saveUserKeyword(String userId, String keyword, int keywordPosition) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        switch (keywordPosition) {
            case 1:
                user.setKeyword1(keyword);
                break;
            case 2:
                user.setKeyword2(keyword);
                break;
            case 3:
                user.setKeyword3(keyword);
                break;
            default:
                throw new IllegalArgumentException("Invalid keyword position: " + keywordPosition);
        }
        userRepository.save(user);
    }
}
