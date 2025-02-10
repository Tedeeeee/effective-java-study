package chapter01.item01.test2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Color2 {
    private static final Map<String, Color2> CACHE = new ConcurrentHashMap<>();
    private final String name;
    
    public Color2(String name) {
        this.name = name;
    }

    public static Color2 of(String name) {
        return CACHE.computeIfAbsent(name, Color2::new);
    }
}
