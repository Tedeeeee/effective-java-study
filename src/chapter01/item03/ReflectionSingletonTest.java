package chapter01.item03;

import java.lang.reflect.Constructor;

public class ReflectionSingletonTest {
    public static void main(String[] args) throws Exception {
        SingletonBillPugh instance = SingletonBillPugh.getInstance();

        Constructor<SingletonBillPugh> constructor = SingletonBillPugh.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        SingletonBillPugh reflectionInstance = constructor.newInstance();

        // 결과 : false == 싱글톤 깨짐
        System.out.println(instance == reflectionInstance);
    }
}
