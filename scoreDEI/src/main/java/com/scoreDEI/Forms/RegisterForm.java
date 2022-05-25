package com.scoreDEI.Forms;

public class RegisterForm {
    private String username;
    private String password;
    private String email;
    private long phone;
    private boolean admin_role;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public long getPhone() {
        return phone;
    }

    public boolean isAdmin_role() {
        return admin_role;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public void setAdmin_role(boolean admin_role) {
        this.admin_role = admin_role;
    }

    @Override
    public String toString() {
        return "RegisterForm{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber=" + phone +
                ", isAdmin=" + admin_role +
                '}';
    }
}
