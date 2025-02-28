# Item 10. equals는 일반 규약을 지켜 재정의해라

주로 객체를 비교하면서 같은 값을 가지고 있을때 같은 것이다 라고 만들고 싶다<br/>
그때 우리는 eqauls 메소드를 정의하여 사용한다 <br/>
정의하는 것도 사실 매우 간단하다. 비교하려는 객체가 해당 객체와 같은 객체인지 마지막으로 같은 값을 가지고 있는지도 확인만 하면 된다
```java
class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return age == person.age && Objects.equals(name, person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }
}

public class Test {
    public static void main(String[] args) {
        Person person1 = new Person("John", 20);
        Person person2 = new Person("John", 20);


        System.out.println(person1.equals(person2));
    }
}
```
하지만 이는 위험한 함정이 도사리고 있다 때문에 특정 상황이 생긴다면 eqauls를 재정의하는 것은 추천하지 않는다

# Equals 구현을 하지 않아도 되는 상황
## 1. 인스턴스가 본질적으로 고유
이는 만약 같은 값을 가지고 있더라도 해당 객체 자체가 고유해야 한다는 것이다

- UUID는 본질적으로 각 인스턴스가 유일해야하는 대표적인 예이다
```java
public class Test {
    public static void main(String[] args) {
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        
        System.out.println(uuid1.equals(uuid2));
    }
}
```
- Thread 또한 시스템 자원과 연결되어 사용되며 같은 설정이라고 별개의 실행이되어야 한다
```java
public class Test {
    public static void main(String[] args) {
        Thread thread1 = new Thread(() -> System.out.println("Hello1"));
        Thread thread2 = new Thread(() -> System.out.println("Hello2"));

        System.out.println(thread1.equals(thread2));
    }
}
```

## 2. 인스턴스의 `논리적 동치성`을 검사할 일이 없다
다시 읽어보면 없는 상황을 얘기하는 것이다. 해당 인스턴의 값을 굳이 비교할 필요가 있을까? 그럴때도 구현을 할 필요없다

## 3. 상위 클래스에서 재정의한 equals가 하위 클래스에도 딱 들어맞는다.
하위 클래스에서 사용할 eqauls가 상위 클래스에서 정의한 것으로도 충분하다면 굳이 할 필요가 없다
```java
class Employee {
    private final String name;
    private final int id;

    public Employee(String name, int id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return id == employee.id && Objects.equals(name, employee.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id);
    }
}

class Manager extends Employee {
    private final String department;

    public Manager(String name, int id, String department) {
        super(name, id);
        this.department = department;
    }
}

public class EqualsExam {
    public static void main(String[] args) {
        Employee e1 = new Employee("Alice", 1001);
        Employee e2 = new Employee("Alice", 1001);
        Manager m1 = new Manager("Alice", 1001, "Sales");
        Manager m2 = new Manager("Alice", 1001, "Marketing");

        System.out.println(e1.equals(e2));
        System.out.println(m1.equals(m2));
    }
}
```
위의 예시는 Employee의 동등성은 중요시되지만 department는 동등성과 관계가 없다면 이렇게 작성할 수 있다<br/>
위의 주제처럼 하위 클래스의 값으로 동등성을 맞추지 않아도 된다면 상위 클래스의 equals로써 가만히 두면 된다

## 4. 클래스가 private거나 package-private이고 equals 메서드를 호출할 일이 없다
클래스가 private라는건 외부에서 사용을 원치 않는다는 것이다. 그렇다면 equals 역시 사용할 일이 없겠지만 아주아주 꼼꼼한 개발자라서 만약을 방지한다면<br/>
equals 메소드 자체를 막을 수 있다
```java
class privateClass {
    private String name;
    private int age;

    private privateClass(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        throw new AssertionError();
    }
}
```
이런식으로 아예 equals를 아예 막아버리는 방법도 있다

> 위의 모든 상황은 `Object의 equals()`만으로도 충분히 해결된다는 것을 알 수 있다.

그렇다면 Equals를 최대한 사용하지 말라는 뜻일까?

# Equals 를 재정의하는 기준
