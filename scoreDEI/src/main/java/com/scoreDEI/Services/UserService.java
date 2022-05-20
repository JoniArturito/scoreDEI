package com.scoreDEI.Services;

import com.scoreDEI.Entities.User;
import com.scoreDEI.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUser() {
        List<User> allUsers = new ArrayList<>();
        userRepository.findAll().forEach(allUsers::add);
        return allUsers;
    }

    public void addUser(User user){
        System.out.println(user);
        userRepository.save(user);
    }

    public Optional<User> getUser(int id){
        return userRepository.findById(id);
    }
}