package chapter03.item18;

public class Bridge {
    // Parents에 어떤 메서드가 추가로 생겨도 Bridge에서 위임만 하면 문제가 없다
    protected final Parents parents;

    public Bridge(Parents parents) {
        this.parents = parents;
    }

    public void greet() {
        parents.greet();
    }

    public void plus() {
        parents.plus();
    }
}
