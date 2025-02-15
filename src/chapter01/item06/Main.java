package chapter01.item06;

class Person {
    private final String name;

    public Person(String name) {
        this.name = name;
    }

    public Person changeName(String name) {
        return new Person(name);
    }
}

public class Main {
    public static void main(String[] args) {
//        Boolean b1 = new Boolean(true);
//        Boolean b2 = Boolean.FALSE;
//        Boolean b3 = Boolean.FALSE;
//
//        System.out.println(b2);
//        System.out.println(b3);
//
//        b2 = true;
//
//        System.out.println(b2);
//        System.out.println(b3);

        // 2번째 예시
        Person John = new Person("John");
        Person victor = John.changeName("Victor");

        System.out.println(victor == John);
    }
}
