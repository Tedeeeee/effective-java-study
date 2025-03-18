package chapter02.item14;

import java.util.*;

class Person implements Comparable<Person> {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " " + age;
    }

    @Override
    public int compareTo(Person other) {
        if (this.age == other.age) {
            return this.name.compareTo(other.name); // 이름 기준 추가 비교 (일관성 깨짐)
        }
        return Integer.compare(this.age, other.age);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return age == person.age;
    }

    @Override
    public int hashCode() {
        return Objects.hash(age);
    }
}

public class Main {
    public static void main(String[] args) {
        Set<Person> treeSet = new TreeSet<>();
        Person p1 = new Person("Alice", 30);
        Person p2 = new Person("Bob", 30);

        treeSet.add(p1);
        treeSet.add(p2);

        System.out.println(treeSet.size()); // 2
        System.out.println(treeSet); // [Alice 30, Bob 30]
//        List<Person> people = Arrays.asList(
//                new Person("Alice", 30),
//                new Person("Bob", 25),
//                new Person("Charlie", 30),
//                new Person("David", 25)
//        );
//
//        Collections.sort(people);
//        System.out.println("정렬 이후 : " + people);
    }
}
