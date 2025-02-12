package chapter01.item03;

import java.io.Serializable;

public class SingletonBillPugh implements Serializable {
    private SingletonBillPugh() {}

    private static class Holder {
        private static final SingletonBillPugh INSTANCE = new SingletonBillPugh();
    }

    public static SingletonBillPugh getInstance() {
        return Holder.INSTANCE;
    }

    protected Object readResolve() {
        return getInstance();
    }
}
