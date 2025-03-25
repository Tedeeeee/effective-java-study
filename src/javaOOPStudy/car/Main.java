package javaOOPStudy.car;

public class Main {
    public static void main(String[] args) {
        View view = new View();
        Controller carController = new Controller(view);

        carController.drive();
    }
}
