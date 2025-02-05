# Item 01 <br /> 생성자 대신 정적 팩토리 메서드를 고려해라

```java
class Item {
    private int id;
    private String name;

    public Item(int id, String name) {
        this.id = id;
        this.name = name;
    }
}

public class Main {
    public static void main(String[] args) {
        Item testItem = new Item(1, "테스트");
    }
}
```

## 정적 팩토리 메소드
> 위와 같은 생성자를 통해 값을 만드는 것은 다양한 문제가 발생할 수 있다 <br />
> 이와 같은 문제를 장점을 통해 알아보고 정적 팩토리 메소드를 통해 개성해보고자 한다
> 
> 우선 정적 팩토리 메소드를 짧게 설명하자면 해당 클래스의 인스턴스를 팩토리 패턴처럼 생성하기 위해 static으로 선언하는 메소드를 뜻한다
 

## 장점
### 1. 이름을 가질 수 있다
<details>
  <summary>내용</summary>

기본 생성자를 통해 인스턴스를 만드는 것은 다른 개발자와 개발을 하면서 발생할 수 있는 문제점을 알아야 한다. <br />
우선 만약 우리가 위에서 만든 객체에서 id는 내부에서 조작하고 오롯이 name만을 받아서 객체를 생성하고 싶다면 어떻게 해야할까
```java
class Item {
    private int id;
    private String name;

    public Item(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Item(String name) {
        this.name = name;
    }
}

public class Main {
    public static void main(String[] args) {
        Item testItem2 = new Item("테스트");
    }
}
```
> <strong>위와 같이 부생성자를 통해 만들수 있지만 이것은 나만 알고 있는 방법일 뿐 다른 개발자가 사용하려 한다면 결국 내부 로직을 파헤쳐야한다 </strong><br />

이것을 해결할 수 있다
```java
class Item {
    private int id;
    private String name;

    public Item(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Item createItemWithoutId(String name) {
        return new Item(0, name);
    }
}

public class Main {
    public static void main(String[] args) {
        Item testItem = Item.createItemWithoutId("테스트");
    }
}
```
포인트는 바로 Item 클래스에 있는 static 메소드이다.
이름을 저렇게 따로 작성을 해서 사용할 수 있기 때문에 다른 개발자가 사용하기 좀 더 수월해지고 해당 클래스가 무엇을 위한 메소드인지 명확하게 알기 쉽다
</details>

### 2. 호출 시 인스턴스를 새롭게 생성하지 않아도 된다
<details>
  <summary>불필요한 객체 생성 방지</summary>
<strong>불필요한 객체 생성은 피하는 것이 좋다. </strong><br />

대표적인 예로 자주 사용하는 객체인 Boolean과 Integer와 같은 객체의 경우 값을 변경할때 valueOf를 사용하지만 이를 위해 일일히 객체를 생성하지 않는다

```java
class Test {
public static void main(String[] args) {
    Integer i1 = Integer.valueOf(100);
    Integer i2 = Integer.valueOf(100);

    Boolean b1 = Boolean.valueOf(true);
    Boolean b2 = Boolean.valueOf(true);

    System.out.println(i1 == i2); // true
    System.out.println(b1 == b2); // true
}
}
```
이렇게 각각의 객체는 굳이 인스턴스를 진행하지 않고 바로 static 메소드를 통해 값을 셋팅할 수 있도록 한다 <br />
여기서 Integer는 심지어 값을 캐싱하여 사용하기 때문에 값을 true로 반환하기도 한다
> <strong>약간의 팁</strong> <br/> Integer는 값을 -128 ~ 127 까지 캐싱하고 그 이외는 객체의 값을 새로 만든다

```java
class Test {
    public static void main(String[] args) {
        Integer i1 = Integer.valueOf(100);
        Integer i2 = Integer.valueOf(100);

        Integer i3 = Integer.valueOf(200);
        Integer i4 = Integer.valueOf(200);

        System.out.println(i1 == i2); // true
        System.out.println(i3 == i4); // false
    }
}
```
</details>

<details>
  <summary>인스턴스 통제 클래스</summary>
</details>

> 정적 팩토리 메소드는 언제 어떤 인스턴스를 살아 있게 할지 철저히 통제 할 수 있다 <br/>
> 이런 클래스를 인스턴스 통제 클래스라고 한다

대표적으로 인스턴스가 통제되는것이 바로 <u><strong>싱글톤</strong></u>이다
