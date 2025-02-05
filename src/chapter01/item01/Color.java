package chapter01.item01;

import java.util.HashMap;
import java.util.Map;

public final class Color {
    private final String name;

    private static final Map<String, Color> CACHE = new HashMap<>();

    private Color(String name) {
        this.name = name;
    }

    public static Color valueOf(String name) {
        return CACHE.computeIfAbsent(name, Color::new);
    }
}

class Test {
    public static void main(String[] args) {
        Integer i1 = Integer.valueOf(100);
        Integer i2 = Integer.valueOf(100);

        Integer i3 = Integer.valueOf(200);
        Integer i4 = Integer.valueOf(200);

        System.out.println(i1 == i2);
        System.out.println(i3 == i4);
    }
}
