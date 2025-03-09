package chapter02.item12;

class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public static Person fromString(String string) {
        String[] split = string.split(", ");
        return new Person(split[0], Integer.parseInt(split[1]));
    }

    @Override
    public String toString() {
        return name + ", " + age;
    }
}

public class Main {
    public static void main(String[] args) {
        Person john = new Person("John", 23);

        String str = john.toString();
        Person secondJohn = Person.fromString(str);

        System.out.println(secondJohn);
    }
}
