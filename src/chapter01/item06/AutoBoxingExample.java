package chapter01.item06;

public class AutoBoxingExample {
    public static void main(String[] args) {
//        Integer i1 = 100;
//        Integer i2 = 100;
//
//        System.out.println(i1 == i2); // true
//
//        Integer i3 = 200;
//        Integer i4 = 200;
//
//        System.out.println(i3 == i4); // false

        long startTime1 = System.nanoTime();
        long sum1 = 0;
        for (int i = 0; i < 1_000_000; i++) {
            sum1 += i; // ✅ 기본 타입 사용 (빠름)
        }
        long endTime1 = System.nanoTime();
        System.out.println("기본 타입 수행 시간: " + (endTime1 - startTime1)); // 1.8ms

        long startTime2 = System.nanoTime();
        Long sum2 = 0L;
        for (int i = 0; i < 1_000_000; i++) {
            sum2 += i; // ✅ 기본 타입 사용 (빠름)
        }
        long endTime2 = System.nanoTime();
        System.out.println("기본 타입 수행 시간: " + (endTime2 - startTime2)); // 14ms
    }
}
