package chapter01.item01;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static List<Integer> createArrayList() {
        return new ArrayList<>();
    }

    public static void main(String[] args) {
        List<Integer> list = createArrayList();
        list.add(10);
        list.add(20);
    }
}
