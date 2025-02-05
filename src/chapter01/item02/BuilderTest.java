package chapter01.item02;

class Person {
    private String name;
    private int age;
    private String gender;
    private String address;
    private String phoneNumber;
    private boolean alive;

    public Person(String name, int age, String gender, String address, String phoneNumber, boolean alive) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.alive = alive;
    }

    public Person(String name, int age) {
        this(name, age, "unknown", "unknown", "unknown", true);
    }

    public Person(String name, int age, String gender) {
        this(name, age, gender, "unknown", "unknown", true);
    }

    public Person(String name, int age, String gender, String address) {
        this(name, age, gender, address, "unknown", true);
    }

    public Person(String name, int age, String gender, String address, String phoneNumber) {
        this(name, age, gender, address, phoneNumber, true);
    }
}

public class BuilderTest {
    public static void main(String[] args) {
        Person person = new Person("테스트", 12, "남", "주소");
    }
}
