package org.example.daibetes.core.domain;

public class User {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String contactNumber;
    private String gender;
    private String birthdate;
    private int age;

    public User(String firstname, String lastname, String email, String password, String contactNumber, String gender, String birthdate) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.contactNumber = contactNumber;
        this.gender = gender;
        this.birthdate = birthdate;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthdate() {
        return birthdate;
    }
}
