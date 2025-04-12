package chapter03.item19;

public class Down extends Up{
    private final String message;

    public Down() {
        this.message = "초기화 완료!!";
        System.out.println("down 생성 작업 완료");
    }

    @Override
    protected void overrideMe() {
        System.out.println("override me 호출 완료");
        System.out.println("message = " + message);
    }
}
