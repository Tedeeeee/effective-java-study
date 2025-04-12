package chapter03.item19;

public class Child extends Parent {
    @Override
    protected void step() {
        System.out.println("Child의 step실행");
        throw new RuntimeException("문제 발생");
    }
}
