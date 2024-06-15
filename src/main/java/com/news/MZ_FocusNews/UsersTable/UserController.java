package com.news.MZ_FocusNews.UsersTable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    public User getUser(@PathVariable String userId) {
        return userService.getUserByUserId(userId);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}/keywords")
    public ResponseEntity<List<String>> getUserKeywords(@PathVariable String userId) {
        List<String> keywords = userService.getUserKeywords(userId);
        return ResponseEntity.ok(keywords);
    }

    @PostMapping("/saveKeyword")
    public void saveKeyword(@RequestParam String userId, @RequestParam String keyword, @RequestParam int keywordPosition) {
        userService.saveUserKeyword(userId, keyword, keywordPosition);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @PutMapping("/{userId}")
    public User updateUser(@PathVariable String userId, @RequestBody User userDetails) {
        return userService.updateUser(userId, userDetails);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
    }
}
