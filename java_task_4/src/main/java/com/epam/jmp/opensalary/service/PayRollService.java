package com.epam.jmp.opensalary.service;

import com.epam.jmp.opensalary.model.Employee;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class PayRollService {

    private final EmployeeService employeeService;

    public PayRollService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public CompletionStage<List<Employee>> fetchHiredEmployeesWithSalariesAsync() {
        return employeeService.hiredEmployeesAsync().thenCompose(employees -> {
            List<CompletionStage<Employee>> employeeFutures = employees.stream()
                    .map(employee -> employeeService.getSalaryAsync(employee.getId())
                            .thenApply(salary -> {
                                employee.setSalary(salary);
                                return employee;
                            }))
                    .toList();

            return CompletableFuture.allOf(employeeFutures.toArray(new CompletableFuture[0]))
                    .thenApply(_void -> employeeFutures.stream()
                            .map(CompletionStage::toCompletableFuture)
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList()));
        });
    }

    public List<Employee> fetchHiredEmployeesWithSalaries() {
        List<Employee> employees = employeeService.hiredEmployees();
        employees.forEach(employee -> employee.setSalary(employeeService.getSalary(employee.getId())));
        return employees;
    }
}
