package com.scoreDEI.Services;

import com.scoreDEI.Entities.AdminUser;
import com.scoreDEI.Entities.RegularUser;
import com.scoreDEI.Entities.User;
import com.scoreDEI.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public UserService() {};

    public List<User> getAllUsers() {
        List<User> allUsers = new ArrayList<>();
        userRepository.findAll().forEach(allUsers::add);
        return allUsers;
    }

    public List<AdminUser> getAllAdmins() {
        List<User> allUsers = new ArrayList<>();
        userRepository.findAll().forEach(allUsers::add);
        List<AdminUser> allAdmins = new ArrayList<>();
        for (User user: allUsers)
        {
            if (user.getType() == 1)
            {
                allAdmins.add((AdminUser) user);
            }
        }
        return allAdmins;
    }

    public List<RegularUser> getAllRegUsers() {
        List<User> allUsers = new ArrayList<>();
        userRepository.findAll().forEach(allUsers::add);
        List<RegularUser> allReg = new ArrayList<>();
        for (User user: allUsers)
        {
            if (user.getType() == 2)
            {
                allReg.add((RegularUser) user);
            }
        }
        return allReg;
    }

    public void addUser(User user){
        userRepository.save(user);
    }

    public Optional<User> getUser(int id){
        return userRepository.findById(id);
    }

    public void addAdmin(AdminUser user)
    {
        userRepository.save(user);
    }

    public void addRegUser(RegularUser user)
    {
        userRepository.save(user);
    }

    public void clearAllUsers(){
        userRepository.deleteAll();
    }
}
