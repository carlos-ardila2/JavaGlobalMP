module jmp.app {
    requires jmp.bank.api;
    requires jmp.service.api;
    requires jmp.dto;
    uses com.epam.jmp.bank.api.Bank;
    uses com.epam.jmp.service.api.Service;
}