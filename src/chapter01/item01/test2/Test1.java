package chapter01.item01.test2;

public class Test1 {
    public static void main(String[] args) {
        Color2 color1 = Color2.of("red");
        Color2 color2 = Color2.of("red");

        System.out.println(color1 == color2);
    }
}
