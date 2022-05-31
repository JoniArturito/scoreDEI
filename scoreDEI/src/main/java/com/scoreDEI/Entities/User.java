/**
 * It's a class that represents a user in the database
 */
package com.scoreDEI.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users")
@JsonIgnoreProperties({"users"})
@XmlRootElement
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "user_gen")
    @Column(name = "userId", nullable = false)
    protected int userId;
    @Column(name = "name", nullable = false)
    protected String name;
    @Column(name = "email", nullable = false, unique = true)
    protected String email;
    @Column(name = "phone", nullable = false, unique = true)
    protected Long phone;
    @Column(name = "password", nullable = false)
    protected String password;

    public User() {
    }

    public User(String name, String email, Long phone, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getPhone() {
        return phone;
    }

    public void setPhone(Long phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * This function returns 0.
     *
     * @return The type of the object.
     */
    public int getType()
    {
        return 0;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone=" + phone +
                '}';
    }
}
