package chapter01.item04;

abstract class Animal {
    abstract void makeSound();

    void sleep() {
        System.out.println("잠자는 중....");
    }
}

class Dog extends Animal {

    @Override
    void makeSound() {
        System.out.println("멍멍!");
    }
}

public class Main {
    public static void main(String[] args) {
        Animal dog = new Dog();
        dog.sleep();
        dog.makeSound();
    }
}
