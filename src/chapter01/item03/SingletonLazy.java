package chapter01.item03;

public class SingletonLazy {
    private static SingletonLazy instance;

    private SingletonLazy() {}

    public static synchronized SingletonLazy getInstance() {
        if (instance == null) {
            instance = new SingletonLazy();
        }
        return instance;
    }
}
