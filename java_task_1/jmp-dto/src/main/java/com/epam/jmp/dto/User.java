package com.epam.jmp.dto;

import java.time.LocalDate;
import java.util.Objects;

public final class User {
    private final String name;
    private final String surname;
    private final LocalDate birthday;

    public User(String name, String surname, LocalDate birthday) {
        this.name = name;
        this.surname = surname;
        this.birthday = birthday;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (User) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.surname, that.surname) &&
                Objects.equals(this.birthday, that.birthday);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, birthday);
    }

    @Override
    public String toString() {
        return "User[" +
                "name=" + name + ", " +
                "surname=" + surname + ", " +
                "birthday=" + birthday + ']';
    }

}
