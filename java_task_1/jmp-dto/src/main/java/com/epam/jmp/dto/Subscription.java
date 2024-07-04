package com.epam.jmp.dto;

import java.time.LocalDate;
import java.util.Objects;

public final class Subscription implements Comparable{
    private final String bankCard;
    private final LocalDate startDate;

    public Subscription(String bankcard, LocalDate startDate) {
        this.bankCard = bankcard;
        this.startDate = startDate;
    }

    public String getBankCard() {
        return bankCard;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Subscription) obj;
        return Objects.equals(this.bankCard, that.bankCard) &&
                Objects.equals(this.startDate, that.startDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bankCard, startDate);
    }

    @Override
    public String toString() {
        return "Subscription[" +
                "bankcard=" + bankCard + ", " +
                "startDate=" + startDate + ']';
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Subscription) {
            if (this.getStartDate() != null) {
                return this.getStartDate().compareTo(((Subscription) o).getStartDate());
            }
        }
        throw new IllegalArgumentException("Object to compare must be of type com.epam.jmp.dto.Subscription");
    }
}
