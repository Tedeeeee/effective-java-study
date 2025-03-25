package javaOOPStudy.car;

public class View {
    public void greetDriver() {
        System.out.println("안녕하세요. 운전을 시작할 준비가 되셨나요? 시동을 걸까요? (on / off)");
    }

    public void startDriver() {
        System.out.println("운행을 시작합니다. 안전 운전 하세요!");
    }

    public void nextAction() {
        System.out.println("다음은 어떤 걸 할까요?");
        System.out.println("기어 변경 명령어 => (P / D / R)");
        System.out.println("가속 명령어 => (speedUp / speedDown)");
        System.out.println("이동 명령어 => (move)");
        System.out.println("엔진 종료 명령어 => (off)");
    }

    public void nowGearStatus(Car car) {
        System.out.printf("현재 기어 상태는 %s로 %s할 수 있는 상태 입니다%n", car.nowGear().getSymbol(), car.nowGear().getKrSymbol());
    }

    public void nowSpeedStatus(Car car) {
        System.out.printf("현재 속도 %dkm/h 입니다%n", car.nowSpeed());
    }

    public void nowPositionStatus(Car car) {
        int nowPosition = car.nowPosition();
        StringBuilder sb = new StringBuilder();
        sb.append("🚗".repeat(nowPosition));

        System.out.printf("위치 : %s", sb.toString());
    }

    public void totalMileage(Car car) {
        System.out.printf("총 주행거리는 %dkm 입니다%n", car.nowMileage());
    }

    public void endDrive() {
        System.out.println("운행을 종료합니다. 좋은 하루 되세요 😁");
    }

    public void wrongInput() {
        System.out.println("제가 알 수 없는 명령입니다 😂");
    }

    public void enter() {
        System.out.println();
    }

    public void errorView(String message) {
        System.out.printf("[ERROR] %s", message);
    }
}
