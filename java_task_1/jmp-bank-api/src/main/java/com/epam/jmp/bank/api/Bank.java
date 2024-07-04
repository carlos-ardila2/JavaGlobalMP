package com.epam.jmp.bank.api;

import com.epam.jmp.dto.BankCard;
import com.epam.jmp.dto.BankCardType;
import com.epam.jmp.dto.User;

public interface Bank {
    BankCard createBankCard(User user, BankCardType type);
}
