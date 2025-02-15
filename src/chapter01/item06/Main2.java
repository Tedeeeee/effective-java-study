package chapter01.item06;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class Main2 {
    private static final Pattern PATTERN1 = Pattern.compile("정규화1");
    private static final Pattern PATTERN2 = Pattern.compile("정규화2");

    private static final Map<String, Pattern> PATTERNS = new ConcurrentHashMap<>();

    private static Pattern getCompile(String str) {
        return PATTERNS.computeIfAbsent(str, Pattern::compile);
    }

    public void cachedStringMatched() {
        String str = "Hi~";

        boolean matches1 = PATTERN1.matcher(str).matches();
        boolean matches2 = PATTERN2.matcher(str).matches();
    }

    public static void main(String[] args) {
        String compileStr1 = "정규화1";
        String compileStr2 = "정규화2";

        Pattern compile1 = getCompile(compileStr1);
        Pattern compile2 = getCompile(compileStr2);

        System.out.println(compile1 == getCompile(compileStr1)); // true (캐싱됨)
        System.out.println(compile2 == getCompile(compileStr2)); // true (캐싱됨)
        System.out.println(compile1 == compile2); // false (다른 정규식)
    }
}
