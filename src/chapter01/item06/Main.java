package chapter01.item06;

public class Main {
    public static void main(String[] args) {
        Boolean b1 = new Boolean(true);
        Boolean b2 = Boolean.FALSE;
        Boolean b3 = Boolean.FALSE;

        System.out.println(b2);
        System.out.println(b3);

        b2 = true;

        System.out.println(b2);
        System.out.println(b3);
    }
}
