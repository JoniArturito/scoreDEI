package com.scoreDEI.Database;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@JsonIgnoreProperties({"regular_users"})
@XmlRootElement
public class RegularUser extends User{
    public RegularUser() {
    }

    public RegularUser(String name, String email, Long phone, String password) {
        super(name, email, phone, password);
    }

}
