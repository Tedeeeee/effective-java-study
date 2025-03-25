package javaOOPStudy.car;

import java.util.Scanner;

public class InputScanner implements Console {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public String readLine() {
        return scanner.nextLine();
    }
}
