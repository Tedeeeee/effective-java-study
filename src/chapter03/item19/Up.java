package chapter03.item19;

public class Up {
    public Up() {
        System.out.println("Up 생성자 시작");
        overrideMe();
        System.out.println("Up 생성 작업 완료");
    }

    protected void overrideMe() {
        System.out.println("override me");
    }
}
