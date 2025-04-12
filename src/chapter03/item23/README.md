# Item 23. 태그달린 클래스보다는 클래스 계층 구조를 활용하라

## ✅ 태그가 달린 클래스
- 하나의 클래스에 여러 타입을 표현하기 위해 태그 필드를 사용하는 방식
```java
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
```
이런 태그가 달린 클래스는 클래스 계층구조를 어설프게 흉내낸 아류일 뿐이다.

### ❌ 태그가 있는 클래스의 단점
1. 가독성이 안좋다
   - 쓸데없는 코드가 많다 그리고 인해 가독성도 떨어진다
2. 유지보수가 어렵다
   - 새로운 타입이 추가되면 switch문을 직접 수정하고 이는 클래스 전반적인 내용이 수정될 수 있다
3. 타입 안정성 부족
   - 특정 필드는 특정 타입에만 유효하고 컴파일러는 이를 보장 해줄 수 없다
4. 객체가 일관되지 않는다
   - 특정 필드를 final로 선언하려면 타입과 맞지 않는 필드도 정의해주어야 한다

## ✅ 태그 클래스에서 클래스 계층 구조로 전환
1. 계층 구조의 `루트가 될 추상 클래스를 정의` 하는 것이다.
```java
abstract class Delivery{}
```
2. 태그 값에 따라 동작이 달라지는 메서드들을 루트 클래스의 추상 메서드로 선언
```java
abstract class Delivery {
   abstract void handleDelivery();
}
```
3. 태그 값에 상관없이 동작이 일정한 메서드들을 루트 클래스에 일반 메서드로 추가한다
```java
abstract class Delivery {
    abstract void handleDelivery();

    void logStart() {
        System.out.println("배송 처리 시작");
    }
}
```
4. 모든 하위 클래스에서 공통으로 사용하는 데이터 필드들도 전부 루트 클래스로 올린다
```java
abstract class Delivery {
    final String recipientName;

    Delivery(String recipientName) {
        this.recipientName = recipientName;
    }

    abstract void handleDelivery();

    void logStart() {
        System.out.println("배송 처리 시작 - 수령인: " + recipientName);
    }
}
```
5. 루트 클래스를 확장한 구체 클래스를 의미별로 하나씩 정의 및 추상 메서드를 각자의 의미에 맞게 구현
```java
class CourierDelivery extends Delivery {
    final String trackingNumber;
    final String courierCompany;

    CourierDelivery(String recipientName, String trackingNumber, String courierCompany) {
        super(recipientName);
        this.trackingNumber = trackingNumber;
        this.courierCompany = courierCompany;
    }

    @Override
    void handleDelivery() {
        System.out.println("택배 배송 - 택배사: " + courierCompany + ", 송장번호: " + trackingNumber);
    }
}

class DirectDelivery extends Delivery {
    final String driverName;
    final String vehicleNumber;

    DirectDelivery(String recipientName, String driverName, String vehicleNumber) {
        super(recipientName);
        this.driverName = driverName;
        this.vehicleNumber = vehicleNumber;
    }

    @Override
    void handleDelivery() {
        System.out.println("직접 배송 - 기사: " + driverName + ", 차량번호: " + vehicleNumber);
    }
}

class PickupDelivery extends Delivery {
    final String pickupLocation;

    PickupDelivery(String recipientName, String pickupLocation) {
        super(recipientName);
        this.pickupLocation = pickupLocation;
    }

    @Override
    void handleDelivery() {
        System.out.println("직접 수령 - 장소: " + pickupLocation);
    }
}
```

#### 🎯 요약
- 루트 추상 클래스 : Delivery
- 공통 메서드 : logStart()
- 공통 필드 : recipientName
- 구체 클래스 : CourierDelivery, DirectDelivery, PickupDelivery
- 추상 메서드 구현 : 배송 수단마다 handleDelivery() overriding

### ✅ 장점
- 간결하고 명확하다
- 서로에게 관련이 없던 데이터 필드를 모두 제거할 수 있다
- 살아남은 필드는 final이다
- 컴파일러가 타입의 안정성을 체크해줄 수 있다
- 루트 클래스를 건드리지 않고 타입을 추가 할 수 있다

## 📌 결과
이렇게 태그를 사용한 클래스보다 계층적으로 만든다면 유지보수하기가 좋고, 타입 안전한 계층 구조를 만들 수 있다
