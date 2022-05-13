package com.scoreDEI.Database;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@JsonIgnoreProperties({"admin_users"})
@XmlRootElement
public class AdminUser extends User{
    public AdminUser() {
    }

    public AdminUser(String name, String email, Long phone, String password) {
        super(name, email, phone, password);
    }
}
