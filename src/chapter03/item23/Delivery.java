package chapter03.item23;

public class Delivery {
    enum DeliveryType {COURIER, DIRECT, PICKUP}

    final DeliveryType type;

    // COURIER일때 사용
    String trackingNumber;
    String courierCompany;

    // DIRECT일때 사용
    String driverName;
    String vehicleNumber;

    // PICKUP일때만 사용
    String pickupLocation;

    Delivery(DeliveryType type) {
        this.type = type;
    }

    void handleDelivery() {
        switch (type) {
            case COURIER:
                System.out.println("택배사: " + courierCompany + ", 송장번호: " + trackingNumber);
                break;
            case DIRECT:
                System.out.println("기사: " + driverName + ", 차량번호: " + vehicleNumber);
                break;
            case PICKUP:
                System.out.println("수령 장소: " + pickupLocation);
                break;
        }
    }
}
