package com.epam.jmp.service.api;

import com.epam.jmp.dto.BankCard;
import com.epam.jmp.dto.Subscription;
import com.epam.jmp.dto.User;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface Service {
    void subscribe(BankCard bankCard);
    Optional<Subscription> getSubscriptionByBankCardNumber(String number);
    List<Subscription> getAllSubscriptionsByCondition(Predicate<Subscription> condition);

    List<User> getAllUsers();

    long ADULT_AGE = 18L;

    default double getAverageUsersAge() {
        return getAllUsers().stream().mapToLong(Service::getUserAge).average().orElse(0.0);
    }

    static long getUserAge(User user) {
        return ChronoUnit.YEARS.between(user.getBirthday(), LocalDate.now());
    }

    static boolean isPayableUser(User user) {
        return getUserAge(user) >= ADULT_AGE;
    }
}
