package chapter01.item07;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;

class Cache {
    private static final Map<String, byte[]> cache = new HashMap<>();

    public static void put(String key, byte[] value) {
        cache.put(key, value);
    }

    public static byte[] getCache(String key) {
        return cache.get(key);
    }
}

class SafeCache {
    private static final Map<String, byte[]> cache = new WeakHashMap<>();

    public static void put(String key, byte[] value) {
        cache.put(key, value);
    }

    public static byte[] getCache(String key) {
        return cache.get(key);
    }
}

class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;

    public LRUCache(int capacity) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
}

public class CacheMemory {

}
