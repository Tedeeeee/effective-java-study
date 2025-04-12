package chapter03.item19;

public class NewParent {
    /**
     * 전체 로직 흐름을 고정한 템플릿 메서드
     *
     * @implSpec
     * 이 메서드는 preStep(), step(), postStep()을 이 순서대로 호출합니다.
     * 하위 클래스는 각 메서드를 재정의하여 중간 단계에 개입할 수 있습니다.
     */
    public final void process() {
        System.out.println("process 시작");
        preStep();
        step();
        postStep();
        System.out.println("process 종료");
    }

    protected void preStep() {
        // 기본 구현
        System.out.println("Parent의 preStep");
    }

    protected void step() {
        // 기본 구현
        System.out.println("Parent의 step");
    }

    protected void postStep() {
        // 기본 구현
        System.out.println("Parent의 postStep");
    }
}
