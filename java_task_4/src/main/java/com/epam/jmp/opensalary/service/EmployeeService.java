package com.epam.jmp.opensalary.service;

import com.epam.jmp.opensalary.model.Employee;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static java.time.format.DateTimeFormatter.BASIC_ISO_DATE;
import static java.time.LocalDateTime.now;

public class EmployeeService {

    private List<Employee> defaultEmployees;
    private boolean useRemoteService = true;
    private static final ObjectMapper mapper = new ObjectMapper();

    private final String remoteServiceUrl = "https://66a8eeeae40d3aa6ff59fff9.mockapi.io/api/v1/employees";

    public CompletionStage<List<Employee>> hiredEmployeesAsync() {
        return CompletableFuture.supplyAsync(this::defaultEmployees);
    }

    public List<Employee> hiredEmployees() {
        List<Employee> list;

        try {
            URL url = URI.create(remoteServiceUrl).toURL();
            list = mapper.readerForListOf(Employee.class).readValue(url);
        } catch (IOException e) {
            System.out.println("Error reading employees from remote service (" + e.getMessage() + "), using local service.");
            list = defaultEmployees();
        }

        return list;
    }

    public CompletionStage<Double> getSalaryAsync(String employeeId) {
        return CompletableFuture.supplyAsync(() -> getSalary(employeeId));
    }

    public double getSalary(String employeeId) {
        double salary;
        try {
            if (!useRemoteService) {
                throw new IOException("Remote service down");
            }
            URL url = URI.create(remoteServiceUrl + "/" + employeeId).toURL();
            Employee employee = new ObjectMapper().readerFor(Employee.class).readValue(url);
            salary = employee.getSalary();
        } catch (IOException e) {
            useRemoteService = false;
            salary = defaultEmployees().stream()
                    .filter(employee -> employee.getId().equals(employeeId))
                    .findFirst()
                    .map(Employee::getSalary).orElse(0.0);
        }

        return salary;
    }

    private List<Employee> defaultEmployees() {

        if (defaultEmployees == null) {
            URL url = getClass().getClassLoader().getResource("json/sampleEmployees.json");
            try {
                defaultEmployees = mapper.readerForListOf(Employee.class).readValue(url);
            } catch (IOException | IllegalArgumentException ex) {
                System.out.println("Error reading employees from local service (" + ex.getMessage() + "), using hardcoded list");

                defaultEmployees = List.of(new Employee("1", "Joe", now().format(BASIC_ISO_DATE), 0),
                        new Employee("2", "Jane", now().format(BASIC_ISO_DATE), 0));
            }
        }

        return defaultEmployees;
    }

}
