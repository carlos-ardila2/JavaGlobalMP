package com.epam.jmp.service.cloud.impl;

import com.epam.jmp.dto.BankCard;
import com.epam.jmp.dto.Subscription;
import com.epam.jmp.dto.User;
import com.epam.jmp.service.api.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;

public class CloudService implements Service {

    /* ------------------------------------------------------------- */
    /* Simple in-memory data structures to hold the crated instances */
    /* In a real world scenario we should compose this class with a  */
    /* proper persistence storage engine.                            */
    /* ------------------------------------------------------------- */
    private final SortedSet<Subscription> SUBSCRIPTIONS = new TreeSet<>();
    private final List<User> USERS = new ArrayList<>();

    public CloudService() {
        /* Requirements don't include User creation functionality. Although trivial, it won't be implemented.
         * Dummy data will be used instead. */

        USERS.add(new User("Sirius", "Black", LocalDate.ofYearDay(1959, 306)));
        USERS.add(new User("James", "Potter", LocalDate.ofYearDay(1960, 58)));
        USERS.add(new User("Remus", "Lupin", LocalDate.ofYearDay(1960, 69)));
        USERS.add(new User("Severus", "Snape", LocalDate.ofYearDay(1960, 6)));
    }

    /* ------------------------------------------------------------- */

    @Override
    public void subscribe(BankCard bankCard) {
        /* It should be LocalDate.now(), but we'll spice things up for testing purposes. */
        var days = Math.round(Math.random() * 10);
        SUBSCRIPTIONS.add(new Subscription(bankCard.getNumber(), LocalDate.now().plusDays(days)));
    }

    @Override
    public Optional<Subscription> getSubscriptionByBankCardNumber(String bankCardNumber) {
        return SUBSCRIPTIONS.stream().filter(subscription ->
                subscription.getBankCard().equals(bankCardNumber)).findFirst();
    }

    @Override
    public List<Subscription> getAllSubscriptionsByCondition(Predicate<Subscription> condition) {
        return SUBSCRIPTIONS.stream().filter(condition).toList();
    }

    @Override
    public List<User> getAllUsers() {
        // No need to use Collectors.toUnmodifiableList(), toList() already returns an Unmodifiable list
        return USERS.stream().toList();
    }
}
