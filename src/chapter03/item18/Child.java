package chapter03.item18;

public class Child extends Parents {
    public void greet() {
        System.out.println("Child's greet");
    }
}
//public class Child extends Bridge {
//    // 자동적으로 메서드가 딸려오지 않는다
//    public Child(Parents parents) {
//        super(parents);
//    }
//
//    public void greetCustom() {
//        System.out.println("Child greet custom");
//    }
//}
