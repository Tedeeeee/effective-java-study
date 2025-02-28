package chapter02.item10;

import java.util.Objects;
import java.util.UUID;

class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return age == person.age && Objects.equals(name, person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }
}

class privateClass {
    private String name;
    private int age;

    private privateClass(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        throw new AssertionError();
    }
}

public class Test {
    public static void main(String[] args) {
        Person person1 = new Person("John", 20);
        Person person2 = new Person("John", 20);


        System.out.println(person1.equals(person2));

        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        System.out.println(uuid1.equals(uuid2));

        Thread thread1 = new Thread(() -> System.out.println("Hello1"));
        Thread thread2 = new Thread(() -> System.out.println("Hello2"));

        System.out.println(thread1.equals(thread2));
    }
}
