import com.epam.jmp.bank.api.Bank;
import com.epam.jmp.bank.impl.cloud.CloudBank;

module jmp.cloud.bank.impl {
    requires transitive jmp.bank.api;
    requires jmp.dto;
    exports com.epam.jmp.bank.impl.cloud;
    provides Bank with CloudBank;
}