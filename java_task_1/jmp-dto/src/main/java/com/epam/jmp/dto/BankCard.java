package com.epam.jmp.dto;

import java.util.Objects;

public class BankCard {
    private final String number;
    private final User user;

    public BankCard(String number, User user) {
        this.number = number;
        this.user = user;
    }

    public String getNumber() {
        return number;
    }

    public User getUser() {
        return user;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (BankCard) obj;
        return Objects.equals(this.number, that.number) &&
                Objects.equals(this.user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, user);
    }

    @Override
    public String toString() {
        return "BankCard[" +
                "number=" + number + ", " +
                "user=" + user + ']';
    }

}
