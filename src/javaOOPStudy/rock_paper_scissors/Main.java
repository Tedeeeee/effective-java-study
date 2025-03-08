package javaOOPStudy.rock_paper_scissors;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try ( Scanner console = new Scanner(System.in) ) {
            View view = new View();
            Controller controller = new Controller(console, view);

            controller.play();
        }
    }
}
