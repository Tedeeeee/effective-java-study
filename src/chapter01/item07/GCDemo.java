package chapter01.item07;

import java.util.ArrayList;
import java.util.List;

class Department {
    String name;
    List<Employee> employees = new ArrayList<>();

    public Department(String name) {
        this.name = name;
    }

    public void addEmployee(Employee e) {
        employees.add(e);
    }
}

class Employee {
    String name;

    public Employee(String name) {
        this.name = name;
    }
}

public class GCDemo {
    public static void main(String[] args) {
        Department department = new Department("Engineering");
        department.addEmployee(new Employee("Bob"));
        department.addEmployee(new Employee("Alice"));

        List<Employee> employees = department.employees;
        department = null;

        System.out.println(department.name);
    }
}
