package org.example.daibetes.core.domain;

import java.io.File;

public class  DoctorFactory implements UserFactory{

    private String firstname, lastname, email, password;
    private String contactNumber, gender, birthdate;
    private String licenseNumber, hospital;
    private File doctorId;

    public DoctorFactory(String firstname, String lastname, String email,
                         String password, String contactNumber,
                         String gender, String birthdate,
                         String licenseNumber, String hospital,
                         File doctorId){

        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.contactNumber = contactNumber;
        this.gender = gender;
        this.birthdate = birthdate;
        this.licenseNumber = licenseNumber;
        this.hospital = hospital;
        this.doctorId = doctorId;
    }

    // Creates Doctor object
    @Override
    public User createUser() {
        return new Doctor(
                firstname, lastname, email,
                password, contactNumber,
                gender, birthdate,
                licenseNumber, hospital, doctorId
        );
    }
}
