package chapter02.item10;

import java.util.Objects;

class Employee {
    private final String name;
    private final int id;

    public Employee(String name, int id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return id == employee.id && Objects.equals(name, employee.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id);
    }
}

class Manager extends Employee {
    private final String department;

    public Manager(String name, int id, String department) {
        super(name, id);
        this.department = department;
    }
}

public class EqualsExam {
    public static void main(String[] args) {
        Employee e1 = new Employee("Alice", 1001);
        Employee e2 = new Employee("Alice", 1001);
        Manager m1 = new Manager("Alice", 1001, "Sales");
        Manager m2 = new Manager("Alice", 1001, "Marketing");

        System.out.println(e1.equals(e2));
        System.out.println(m1.equals(m2));
    }
}
