package javaOOPStudy.car;

public class Car {
    private boolean engine;
    private Gear gear;
    private int speed;
    private int mileage;
    private int position;

    public Car() {
        this.engine = false;
        this.gear = Gear.P;
        this.speed = 0;
        this.mileage = 0;
        this.position = 0;
    }

    public void changeEngineStatus(String status) {
        if ("off".equals(status)) {
            resetSpeed();
            this.engine = false;
        } else if ("on".equals(status)) {
            this.engine = true;
        }
    }

    public void changeGear(String newGear) {
        resetSpeed();
        this.gear = Gear.valueOf(newGear);
    }

    public void changeSpeed(String newSpeed) {
        isMovable();
        if ("speedDown".equals(newSpeed)) {
            validationSpeedDown();
            this.speed--;
        }

        if ("speedUp".equals(newSpeed)) {
            this.speed++;
        }
    }

    private void validationSpeedDown() {
        if (this.speed <= 0) {
            throw new IllegalArgumentException("속도가 0입니다");
        }
    }

    public void movePosition() {
        zeroPositionCheck();
        plusTotalPosition();
        if (gear.equals(Gear.R)) {
            this.speed = -(this.speed);
        }
        this.position += speed;
    }

    public boolean isEngine() {
        return engine;
    }

    public Gear nowGear() {
        return this.gear;
    }

    public int nowSpeed() {
        return this.speed;
    }

    private void zeroPositionCheck() {
        if (this.position < 0) {
            throw new IllegalArgumentException("더 이상 후진 할 수 없습니다");
        }
    }

    private void plusTotalPosition() {
        this.mileage += nowSpeed();
    }

    public int nowPosition() {
        return this.position;
    }

    public int nowMileage() {
        return this.mileage;
    }

    public void respondToInput(String nextAction) {
        if ("speedUp".equals(nextAction) || "speedDown".equals(nextAction)) {
            changeSpeed(nextAction);
        }

        if ("P".equals(nextAction) || "D".equals(nextAction) || "R".equals(nextAction)) {
            changeGear(nextAction);
        }

        if ("off".equals(nextAction)) {
            changeEngineStatus(nextAction);
        }

        if ("move".equals(nextAction)) {
            movePosition();
        }
    }

    private void isMovable() {
        if (!engine) {
            throw new IllegalArgumentException("시동을 걸어주세요!");
        }

        if (gear == Gear.P) {
            throw new IllegalArgumentException(String.format("기어를 변경해주세요! 현재 기어 상태 : %s", gear));
        }
    }

    private void resetSpeed() {
        this.speed = 0;
    }
}
