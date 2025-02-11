package chapter01.item01;

class Parent {
    public static class SinletonHolder {
        private static final Parent INSTANCE = new Parent();
    }

    protected Parent() {}

    public static Parent getInstance() {
        return Parent.SinletonHolder.INSTANCE;
    }

    public static Parent createChildren() {
        return new Child();
    }
}

class Child extends Parent {
    public Child() {
        super();
    }
}

public class Main4 {
    public static void main(String[] args) {
        Parent singleton = Parent.getInstance();
        Parent children = Parent.createChildren();
    }
}
