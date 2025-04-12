package chapter03.item19;

public class NewChild extends NewParent{
    @Override
    protected void preStep() {
        System.out.println("Child의 preStep 실행");
    }

    @Override
    protected void step() {
        System.out.println("Child의 step 실행");

        throw new RuntimeException("Child 문제 발생");
    }

    @Override
    protected void postStep() {
        System.out.println("Child의 postStep 실행");
    }
}
