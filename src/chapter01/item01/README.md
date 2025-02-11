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

> 정적 팩토리 메소드는 언제 어떤 인스턴스를 살아 있게 할지 철저히 통제 할 수 있다 <br/>
> 이런 클래스를 인스턴스 통제 클래스라고 한다

대표적으로 인스턴스가 통제되는것이 바로 <u><strong>싱글톤</strong></u>이다 <br />
만약 우리가 같은 클래스를 반복해서 부른다면 무슨 문제가 발생할까?

1. 불필요한 객체 생성에 따른 메모리 낭비
    - 객체를 통제하지 않고 요청에 따라 새로운 객체를 계속해서 생성한다면 메모리를 불필요하게 과하게 차지하고 이는 성능 저하로 이어진다
2. 우리가 어떤 객체의 상태에 따른 값을 공유하고 싶어도 하지 못한다
    - 객체를 계속해서 새롭게 만든다면 객체는 여러개이기 때문에 일관성있는 데이터를 보장하지 못한다

이 문제를 정적 팩토리 메소드를 통해 객체의 생성을 통제하고 불변 값 클래스에서 동치인 인스턴스가 단 하나뿐임을 보장할 수 있다 <br/>
불변 값 클래스에서 동치인 인스턴스가 단 하나뿐임을 보장한다는 것은 equals를 통해 상세한 구현없이 == 만으로도 비교가 가능하다는 것이다 <br/>
이를 코드를 통해 더욱 상세하게 확인해본다면

```java
public final class Color {
    private static final Map<String, Color> CACHE = new ConcurrentHashMap<>();
    private final String name;

    private Color(String name) {
        this.name = name;
    }

    public static Color of(String name) {
        return CACHE.computeIfAbsent(name, Color::new);
    }

    public String getName() {
        return name;
    }
}
```
이렇게 final을 통해 불변 클래스를 만들고 해당 값을 new ConcurrentHashMap()으로 값을 캐싱해놓는다. 그리고 우리가 Integer를 valueOf로 호출을 해서 객체를 만들었던 것 처럼 객체를 만드는 것이다

그러면 굳이 new 연산자를 통한 인스턴스 생성없이 객체를 생성할 수 있고 이렇게 생성된 객체는 .equals()가 아닌 == 으로 비교가 가능한 것이다

```java
public class Main {
    public static void main(String[] args) {
        Color red1 = Color.of("red");
        Color red2 = Color.of("red");
        Color blue = Color.of("blue");

        System.out.println(red1 == red2);  // true (같은 객체)
        System.out.println(red1 == blue);  // false (다른 객체)
    }
}
```
이렇게 객체를 미리 Map에 저장하고 해당 불변 객체의 값인 name이 key로써 존재하지 않으면 새롭게 만들고 있다면 기존의 것을 주면서 동일성을 유지할 수 있다
</details>


### 3. 반환 타입의 하위 타입 객체를 반환할 수 있는 능력이 있다

<details>
  <summary>상위 객체를 반환하려 하지만 하위 타입이 반환</summary>

반환할 객체의 클래스를 자유롭게 선택할 수 있는 것은 엄청난 유연성을 보여 줄 수 있다. <br/>
이 유연성을 API를 만들때 구현 클래스를 공개하지 않고 원하는 객체를 반환하면서 그 효력이 톡톡히 발휘된다. <br/>

예를 들어서 Collections를 들어보자
```java
public class Main {
   public static List<Integer> createArrayList() {
      return new ArrayList<>();
   }

   public static void main(String[] args) {
      List<Integer> list = createArrayList();
      list.add(10);
      list.add(20);
   }
}
```
우리가 흔하게 사용하는 상위 객체를 반환하는 것을 기대하지만 실제로는 하위 객체를 반환하는 예시 중 <br/>
<code>List<Integer> arr = new ArrayList<>();</code>와 같은것을 정적 메서드를 사용해서 표현해보려고 했다

위와 같이 List의 반환을 기대하지만 결국 ArrayList인 것을 알 수 있듯이 하위 객체를 이런식으로 반환할 수 있다
</details>

### 4. 입력 매개변수에 따라 매번 다른 클래스의 객체를 반환할 수 있다

<details>
  <summary>매개변수의 값에 따른 다른 클래스 변환 예시</summary>

매개변수가 어떻게 들어오느냐에 따라 제작할 수 있는 클래스가 다를 수 있고 그것을 정적 팩토리 메서드가 진행할 수 있다
```java
interface Product {
    void use();
}

class ConcreteProductA implements Product {
    @Override
    public void use() {
        System.out.println("Using Product A");
    }
}

class ConcreteProductB implements Product {
    @Override
    public void use() {
        System.out.println("Using Product B");
    }
}

class ProductFactory {
    public static Product createProduct(String type) {
        if (type.equalsIgnoreCase("A")) {
            return new ConcreteProductA();
        } else if (type.equalsIgnoreCase("B")) {
            return new ConcreteProductB();
        }
        throw new IllegalArgumentException("Unknown product type");
    }
}

public class Main2 {
    public static void main(String[] args) {
        Product productA = ProductFactory.createProduct("A");
        productA.use();
    }
}
```
굳이 코드를 해석하자면 다 같은 Product생성 메소드이지만 값에 따라 각각 다른 클래스로 변환되어 나올 수 있다는 것이다
</details>

### 5. 정적 팩터리 메서드를 작성하는 시점에는 반환할 객체의 클래스는 존재하지 않아도 된다

<details>
   <summary>구현체를 반환하지 않는 코드 예시</summary>

반환하는 시점이 중요하다 구현체를 반환하지 않고 인터페이스의 역할을 하는 것으로 반환을 하여 진행을 하는 것이다
```java
interface Animal {
    void speak();
}

class Dog implements Animal {
    @Override
    public void speak() {
        System.out.println("Woof!");
    }
}

class Cat implements Animal {
    @Override
    public void speak() {
        System.out.println("Meow!");
    }
}

class AnimalFactory {
    public static Animal createAnimal(String type) {
        switch (type.toLowerCase()) {
            case "dog":
                return new Dog();
            case "cat":
                return new Cat();
            default:
                throw new IllegalArgumentException("Unknown animal type: " + type);
        }
    }
}

public class Main3 {
    public static void main(String[] args) {
        Animal dog = AnimalFactory.createAnimal("dog");
        dog.speak();

        Animal cat = AnimalFactory.createAnimal("cat");
        cat.speak();
    }
}
```
이렇게 보면 createAnimal이라는 정적 메소드는 Dog나 Cat이 아닌 Animal자체를 반환하는 것을 볼 수 있다 <br/>
그리고 각각의 구현체는 Animal의 speak()를 구현하여 사용하는 것을 볼 수 있다

이렇게 상세한 구현체를 바로 반환하지 않고 그저 인터페이스인 Animal만을 반환하지만 Animal에 존재하는 메소드는 speak()는 사용할 수 있다

또한 자세히보면 이는 네번째의 성질이였던 <u><strong>매개변수에 따라 반환되는 객체의 변환</strong></u>과도 연관이 있는 코드인것을 볼 수있는데 이렇게 다양하게 사용할 수 있는것이다

이와 굉장히 비슷한 것으로 JDBC가 존재한다 JDBC에서는 각 DB를 연결하는 getConnection에서 해당 내용이 잘 드러난다 <br/>
<code>Connection conn = DriverManager.getConnection("연결하고자 하는 DB 내용")</code><br/>
이렇게 JDBC 또한 코드를 작성하는 당시에는 정확히 어떤 DB로 연결될지는 모르지만 코드를 이어 작성하는 것에는 전혀 문제가 없는 것을 확인할 수 있다
코드도 보면 new 연산자가 아닌것으로 알 수 있듯이 정적 팩토리로 그 메소드를 진행하는 것으로 알 수 있다
</details>

## 단점

### 1. 상속을 위해 public이나 protected 생성자가 필수로 필요하다

우리가 정적 팩토리 메소드를 사용해서 값을 만들때 싱글턴을 사용하는 경우 해당 클래스의 생성자를 종종 private하게 만들어 외부에서 생성 자체를 막는 경우가 많다는 것을 알 수 있다 <br/>
그리고 생성 자체를 정적 팩토리 메소드에게 맡겨 컴파일 타임에서 객체 생성을 진행할 수 있는데 이렇게 되면 해당 클래스는 다른 클래스가 해당 클래스를 상속할 수 없다
```java
public class Parent {
    private Parent() {}

    public static Parent create() {
        return new Parent();
    }
}

class Child extends Parent {
    public Child() {
        super(); // 문제 발생
    }
}
```
super()를 통해 생성자로 만들어주어 구성을 해줘야 하지만 그렇게 하지 못하게 되는 것이다<br/>
해결 방법으로는 해당 생성자를 protect 접근 제어자를 사용하는 방법도 있겠지만 어쨌든 상속을 해주고자 하는 클래스가 존재한다면 private한 생성자는 만들기 어렵다
물론 상속을 한다는 행위와 싱글톤을 유지하려는 행위는 상충한다 ( 상속은 부모의 객체를 만드려고 하고 싱글톤은 해당 부모의 객체를 하나만 만들어서 사용 ) <br/>

싱글톤을 유지하면서 상속을 진행하려면 자식이 부모를 super하여 만드는 것이 아닌 부모가 자식 클래스를 만드는 행위를 해야한다
```java
class Parent {
    public static class SinletonHolder {
        private static final Parent INSTANCE = new Parent();
    }

    protected Parent() {}

    public static Parent getInstance() {
        return Parent.SinletonHolder.INSTANCE;
    }

    public static Parent createChildren() {
        return new Child();
    }
}

class Child extends Parent {
    public Child() {
        super();
    }
}
```
이렇게 생성자가 뭔가 역전 된듯한 모습을 볼 수 있다. <br/>
물론 사용하는것에 문제는 없다. 이또한 정적 팩토리 메소드의 장점 중 하나인 <u><strong>반환 타입의 하위 타입 객체를 반환하는 능력</strong></u>덕분이지만 코드가 좋은 코드라고 하기엔 무리가 있다

그래서 결국 우리는 의존성 주입을 활용해서 만드는것이 좋은 해결 방안이라는 것을 알 수 있다

### 2. 찾기 어렵다

정적 팩토리 메소드는 개발자가 직접 만드는 것이기 때문에 다른 개발자는 역시 정적 팩토리 메소드가 어떻게 작성되었는지 알아야 한다 아니면 만들때 설명서에 아주 자세하게 잘 작성해놓는 방법도 있다

하지만 일일히 작성하기엔 시간이 없기때문에 정적 팩토리의 첫번째 장점인 이름을 정하는 것에서 규칙을 만들어 두기로 한다

- from : 매개변수를 `한 개` 받아 해당 타입의 인스턴스를 반환하는 형변환 메서드 <br/>
```java
public static User from(UserRequest request) {
  return new User(request.getName(), request.getEmail());
}
```
- of : `여러개의 매개변수`를 받아 적합한 타입의 인스턴스 반환 <br/>
```java
public static User of(String name, String email, int age) {
    return new User(name, email, age);
}
```
- valueOf : from과 of보다 더 자세한 버전 ( 추가 검증과 같은 좀 더 까다롭게 변환을 한다 )<br/>
```java
public static User valueOf(UserRequest request) {
     if (request.getEmail() == null || !request.getEmail().contains("@")) {
         throw new IllegalArgumentException("Invalid email format");
     }
     return new User(request.getName(), request.getEmail());
}
```
- getInstance : 매개변수로 명시한 인스턴스 반환 but 동일한 인스턴스를 보장하지 않는다<br/>
```java
public class UserFactory {
    // 반환 객체가 클래스와 다르다
    public static User getInstance(UserRequest request) {
        return new User(request.getName(), request.getEmail());
    }
}
```
- newInstance : 매번 새로운 인스턴스를 생성하고 반환한다 (코드는 from과 비슷하지만 정확한 의미 전달 목적)<br/>
```java
public static User newInstance(UserRequest request) {
  return new User(request.getName(), request.getEmail());
}
```
