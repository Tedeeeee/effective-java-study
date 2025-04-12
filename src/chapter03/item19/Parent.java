package chapter03.item19;

public class Parent {
    /**
     * 비즈니스 로직을 처리합니다.
     *
     * @implSpec
     * 이 메서드는 내부적으로 {@link #step()}을 호출합니다.
     * step()은 하위 클래스에서 재정의될 수 있으며,
     * 이 메서드의 전체 실행 흐름에 영향을 줄 수 있습니다.
     */
    public void process() {
        System.out.println("process 시작");
        step();
        System.out.println("process 종료");
    }

    protected void step() {
        System.out.println("Parent의 step 실행");
    }
}
