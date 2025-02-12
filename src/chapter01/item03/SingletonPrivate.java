package chapter01.item03;

public class SingletonPrivate {
    private static final SingletonPrivate INSTANCE = new SingletonPrivate();

    private SingletonPrivate() {}

    public static SingletonPrivate getInstance() {
        return INSTANCE;
    }
}
