package org.example.daibetes.core.domain;

public class PatientFactory implements UserFactory{
        private String firstname, lastname, email, password;
        private String contactNumber, gender, birthdate;
        private int age;

        public PatientFactory(String firstname, String lastname, String email,
                              String password, String contactNumber,
                              String gender, String birthdate, int age) {

            this.firstname = firstname;
            this.lastname = lastname;
            this.email = email;
            this.password = password;
            this.contactNumber = contactNumber;
            this.gender = gender;
            this.birthdate = birthdate;
            this.age = age;
        }

        // Creates Patient object
        @Override
        public User createUser() {
            return new Patient(
                    firstname, lastname, email,
                    password, contactNumber,
                    gender, birthdate, age
            );
        }
    }

