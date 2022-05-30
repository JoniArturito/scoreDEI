/**
 * It's a regular user
 */
package com.scoreDEI.Entities;

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

    /**
     * This function returns the type of the object.
     *
     * @return The type of the object.
     */
    @Override
    public int getType() {
        return 2;
    }
}
