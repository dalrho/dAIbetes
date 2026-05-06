package org.example.daibetes.core.domain;

import java.io.File;

public class Doctor extends User {

    private int dId;
    private String licenseNumber;
    private File doctorId;
    private String hospital;

    public Doctor(String firstname, String lastname, String email, String password,
                  String contactNumber, String gender, String birthdate,
                  String licenseNumber, String hospital, File doctorId) {

        super(firstname, lastname, email, password, contactNumber, gender, birthdate);
        this.licenseNumber = licenseNumber;
        this.hospital = hospital;
        this.doctorId = doctorId;
    }

    public int getDId() {
        return dId;
    }

    public void setDId(int dId) {
        this.dId = dId;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public File getDoctorId() {
        return doctorId;
    }

    public String getHospital() {
        return hospital;
    }
}