package com.scoreDEI.Services;

import com.scoreDEI.Entities.AdminUser;
import com.scoreDEI.Entities.RegularUser;
import com.scoreDEI.Entities.User;
import com.scoreDEI.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.annotations.Test;

import javax.transaction.Transactional;
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

    public Optional<User> getUser(String email, String password) {
        return userRepository.findUserByEmailAndPassword(email, password);
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

    @Transactional
    public boolean deleteUser(int id) {
        Optional<User> opUser = userRepository.findById(id);
        if (opUser.isPresent()) {
            User user = opUser.get();
            userRepository.delete(user);
            return true;
        }
        return false;
    }

    @Transactional
    public void changeUser(int id, User user) {
        Optional<User> u = this.getUser(id);

        if(u.isPresent()) {
            u.get().setName(user.getName());
            u.get().setEmail(user.getEmail());
            u.get().setPassword(user.getPassword());
            u.get().setPhone(user.getPhone());
        }
    }

    @Transactional
    public boolean updateUsername(int id, String username) {
        Optional<User> u = this.getUser(id);

        if(u.isPresent()) {
            u.get().setName(username);
            return true;
        }

        return false;
    }

    @Transactional
    public boolean updateEmail(int id, String email) {
        Optional<User> u = this.getUser(id);

        if(u.isPresent()) {
            u.get().setEmail(email);
            return true;
        }

        return false;
    }

    @Transactional
    public boolean updatePhone(int id, long phone) {
        Optional<User> u = this.getUser(id);

        if(u.isPresent()) {
            u.get().setPhone(phone);
            return true;
        }

        return false;
    }

    @Transactional
    public boolean updatePassword(int id, String password) {
        Optional<User> u = this.getUser(id);

        if(u.isPresent()) {
            u.get().setPassword(password);
            return true;
        }

        return false;
    }
}
