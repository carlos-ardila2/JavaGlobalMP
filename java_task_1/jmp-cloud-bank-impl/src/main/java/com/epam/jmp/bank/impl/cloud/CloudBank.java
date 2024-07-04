package com.epam.jmp.bank.impl.cloud;

import com.epam.jmp.bank.api.Bank;
import com.epam.jmp.dto.*;

import java.util.function.BiFunction;

import static com.epam.jmp.dto.BankCardType.CREDIT;
import static com.epam.jmp.dto.BankCardType.DEBIT;

public class CloudBank implements Bank {
    @Override
    public BankCard createBankCard(User user, BankCardType type) {

        BiFunction<String, User, BankCard> bankCardCreator;

        if (CREDIT.equals(type)) {
            bankCardCreator = CreditBankCard::new;
        } else if(DEBIT.equals(type)) {
            bankCardCreator = DeditBankCard::new;
        } else {
            throw new UnsupportedOperationException(String.format("%s %s", "Can't create card of type", type));
        }

        var number = String.valueOf(Math.round(Math.random() * 10_000_000));

        return bankCardCreator.apply(number, user);
    }
}
