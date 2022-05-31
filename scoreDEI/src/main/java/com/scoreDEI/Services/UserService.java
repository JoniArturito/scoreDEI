/**
 * This class is responsible for all the operations that can be done on the User entity
 */
package com.scoreDEI.Services;

import com.scoreDEI.Entities.AdminUser;
import com.scoreDEI.Entities.RegularUser;
import com.scoreDEI.Entities.User;
import com.scoreDEI.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public UserService() {};

    /**
     * Get all users from the database and return them as a list.
     *
     * @return A list of all users
     */
    public List<User> getAllUsers() {
        List<User> allUsers = new ArrayList<>();
        userRepository.findAll().forEach(allUsers::add);
        return allUsers;
    }

    /**
     * "Get all users from the database, then filter out all the users that are not admins."
     * @return A list of all the admins in the database.
     */
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

    /**
     * "Get all users from the database, then filter out the ones that aren't regular users."
     * @return A list of all regular users.
     */
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

    /**
     * This function adds a user to the database.
     *
     * @param user The user object that we want to save.
     */
    public void addUser(User user){
        userRepository.save(user);
    }

    /**
     * If the user exists, return the user, otherwise return null.
     *
     * @param id The id of the user you want to get.
     * @return Optional<User>
     */
    public Optional<User> getUser(int id){
        return userRepository.findById(id);
    }

    /**
     * Find a user by email and password, and return it as an Optional.
     *
     * @param email The email of the user
     * @param password The password of the user.
     * @return Optional<User>
     */
    public Optional<User> getUser(String email, String password) {
        return userRepository.findUserByEmailAndPassword(email, password);
    }

    /**
     * This function adds an admin user to the database.
     *
     * @param user The user object that is to be added to the database.
     */
    public void addAdmin(AdminUser user)
    {
        userRepository.save(user);
    }

    /**
     * This function takes a RegularUser object as a parameter and saves it to the database
     *
     * @param user The user object that is to be added to the database.
     */
    public void addRegUser(RegularUser user)
    {
        userRepository.save(user);
    }

    /**
     * Delete all users from the database.
     */
    public void clearAllUsers(){
        userRepository.deleteAll();
    }

    /**
     * > If the user exists, delete it
     *
     * @param id The id of the user to be deleted.
     * @return A boolean value.
     */
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

    /**
     * > This function changes the user's information in the database
     *
     * @param id the id of the user to be changed
     * @param user The user object that contains the new values for the user.
     */
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

    /**
     * If the user exists, update the username and return true, otherwise return false.
     *
     * @param id The id of the user you want to update
     * @param username The username of the user to update
     * @return A boolean value.
     */
    @Transactional
    public boolean updateUsername(int id, String username) {
        Optional<User> u = this.getUser(id);

        if(u.isPresent()) {
            u.get().setName(username);
            return true;
        }

        return false;
    }

    /**
     * If the user exists, update the email and return true, otherwise return false.
     *
     * @param id The id of the user you want to update
     * @param email The email to update the user with.
     * @return A boolean value.
     */
    @Transactional
    public boolean updateEmail(int id, String email) {
        Optional<User> u = this.getUser(id);

        if(u.isPresent()) {
            User user = u.get();
            //user.setEmail(email);
            //return true;
            return userRepository.updateEmail(email, user.getUserId()) > 0;
        }

        return false;
    }

    /**
     * If the user exists, update the phone number and return true, otherwise return false.
     *
     * @param id The id of the user you want to update
     * @param phone The new phone number
     * @return A boolean value.
     */
    @Transactional
    public boolean updatePhone(int id, long phone) {
        Optional<User> u = this.getUser(id);

        if(u.isPresent()) {
            u.get().setPhone(phone);
            return true;
        }

        return false;
    }

    /**
     * If the user exists, update the password and return true, otherwise return false.
     *
     * @param id The id of the user to update
     * @param password The new password to be set.
     * @return A boolean value.
     */
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
