package javaOOPStudy.car;

public class Controller {
    private final View view;
    private final Console console;

    public Controller(View view) {
        this.view = view;
        this.console = new InputScanner();
    }

    public void drive() {
        Car car = new Car();
        startDriver(car);
        drivingCar(car);
        endDriver(car);
    }

    private void startDriver(Car car) {
        view.greetDriver();
        String engineStatus = console.readLine();
        car.changeEngineStatus(engineStatus);

        if (car.isEngine()) {
            view.startDriver();
        }
    }

    private void drivingCar(Car car) {
        while (car.isEngine()) {
            try {
                view.nextAction();
                String nextAction = console.readLine();
                car.respondToInput(nextAction);
                viewResult(car);
            } catch (IllegalArgumentException e) {
                view.errorView(e.getMessage());
            }
        }
    }

    private void endDriver(Car car) {
        view.totalMileage(car);
        view.endDrive();
    }

    private void viewResult(Car car) {
        if (car.isEngine()) {
            view.nowGearStatus(car);
            view.nowSpeedStatus(car);
            view.nowPositionStatus(car);
            view.enter();
        }
    }
}
