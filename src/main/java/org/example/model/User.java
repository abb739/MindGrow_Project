package org.example.model;

public class User {
    private int id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String cin;
    private Role role;
    private String passwordHash;

    public User() {
    }

    public User(int id, String fullName, String email, String phoneNumber, String cin, Role role, String passwordHash) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.cin = cin;
        this.role = role;
        this.passwordHash = passwordHash;
    }

    public User(String fullName, String email, String phoneNumber, String cin, Role role, String passwordHash) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.cin = cin;
        this.role = role;
        this.passwordHash = passwordHash;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
