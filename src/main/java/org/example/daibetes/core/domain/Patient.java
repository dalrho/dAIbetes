package org.example.daibetes.core.domain;

public class Patient extends User{
    private int pId;
    private int age;
    public Patient(String firstname, String lastname, String email, String password, String contactNumber, String gender, String birthdate, int age) {
        super(firstname, lastname, email, password, contactNumber, gender, birthdate);
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public void setPId(int pId) {
    }

    public int getPId() {
        return pId;
    }
}
