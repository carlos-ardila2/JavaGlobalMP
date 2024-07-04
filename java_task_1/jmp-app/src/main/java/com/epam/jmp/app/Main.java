package com.epam.jmp.app;

import com.epam.jmp.bank.api.Bank;
import com.epam.jmp.dto.BankCardType;
import com.epam.jmp.service.api.Service;
import com.epam.jmp.service.api.exception.SubscriptionNotFoundException;

import java.util.NoSuchElementException;
import java.util.ServiceLoader;

public class Main {
    public static void main(String[] args) {

        Iterable<Bank> bankImplementations = ServiceLoader.load(Bank.class);
        var bankIterator = bankImplementations.iterator();
        var bank = bankIterator.hasNext() ? bankIterator.next() : null;

        Iterable<Service> serviceImplementations = ServiceLoader.load(Service.class);
        var servicesIterator = serviceImplementations.iterator();
        var service = servicesIterator.hasNext() ? servicesIterator.next() : null;

        if (bank == null) {
            throw new IllegalStateException("Could not load any implementation of com.epam.jmp.bank.api.Bank");
        }

        if (service == null) {
            throw new IllegalStateException("Could not load any implementation of com.epam.jmp.service.api.Service");
        }

         /* Dummy testing of the banking api. */

         try {
             var user1 = service.getAllUsers().stream().findAny().orElseThrow();
             var card1 = bank.createBankCard(user1, BankCardType.CREDIT);

             System.out.printf("\nUser %s %s, aged %d has created a Bank Card with number %s\n",
                     card1.getUser().getName(), card1.getUser().getSurname(), Service.getUserAge(card1.getUser()),
                     card1.getNumber());

             System.out.printf("BTW, Bank user's average age is %.1f\n\n", service.getAverageUsersAge());

             var user2 = service.getAllUsers().stream().findAny().orElseThrow();
             var card2 = bank.createBankCard(user2, BankCardType.DEBIT);

             if (Service.isPayableUser(user1)) { // They all are
                 service.subscribe(card1);
             }
             if (Service.isPayableUser(user2)) {
                 service.subscribe(card2);
             }

             var subscription1 = service.getSubscriptionByBankCardNumber(card1.getNumber())
                     .orElseThrow(SubscriptionNotFoundException::new);

             System.out.printf("Subscription of card %s started on %s\n", subscription1.getBankCard(),
                     subscription1.getStartDate());

             var subs = service.getAllSubscriptionsByCondition(subscription ->
                     card2.getNumber().equals(subscription.getBankCard()));

             if (!subs.isEmpty()) {
                 subs.forEach(subscription ->
                    System.out.printf("Subscription of card %s started on %s\n\n", subscription.getBankCard(),
                         subscription.getStartDate())
                 );
             }

             // Should throw
             service.getSubscriptionByBankCardNumber("999999").orElseThrow(SubscriptionNotFoundException::new);

         } catch (NoSuchElementException noUser) {
             System.out.println("ERROR: No users found in Bank");
         } catch (SubscriptionNotFoundException noSubscription) {
             System.out.println("ERROR: No subscription found by the Service");
         }
    }
}