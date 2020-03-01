package com.matiboux.griffith.contactmanager;

import java.security.InvalidParameterException;

public class ContactInfo {

    public final int id;
    public final String lastname;
    public final String firstname;
    public final String phone;
    public final String email;

    public ContactInfo(int id, String lastname, String firstname, String phone, String email) {
        if (id < 0) throw new InvalidParameterException();
        this.id = id;
        this.lastname = lastname;
        this.firstname = firstname;
        this.phone = phone;
        this.email = email;

    }

    public String getFullName() {
        return firstname + " " + lastname;
    }
}
