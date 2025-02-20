package chapter01.item06;

import java.util.*;

public class MapExam {
    public static void main(String[] args) {
        Map<String, Integer> map = new HashMap<>();
        map.put("A", 1);
        map.put("B", 2);

        Set<String> keys1 = map.keySet();
        Set<String> keys2 = map.keySet();

        System.out.println(keys1 == keys2);
        System.out.println(keys2);

        keys1.remove("A");
        keys1.add("A");


        System.out.println(keys1);
        System.out.println(keys2);
        System.out.println(map);
    }
}
