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

# Equals 를 재정의하는 기준
Equals를 재정의 하는 기준은 결국 위의 반대 상황인 `논리적 동치성이 필요할때`라는 것이다<br/>
두개의 객체가 같은 것 뿐만이 아닌 같은 값까지 확인하기 위해 만들어질때 아주 유용하게 사용할 수 있다는 것이다<br/>
또한 이는 나중에 Map과 Set 객체에서도 아주아주 유용하게 사용된다.

>주의!
> 
> 값이 같더라도 해당 인스턴스가 둘 이상 만들어질 수 없는 상황(ex. 싱글톤)일때와 Enum일때는 애초에 2개 이상의 같은 클래스가 만들어지지 않기 때문에 굳이 만들필요 없다

## Equals의 규약
### 반사성 
> x.equals(x)같이 자기 자신과 비교하는 것은 항상 true여야 한다
  - <code>if(this == obj) return true</code> 이런 코드를 자주 볼 수 있을것이다.
  - 어긴다면 해당 클래스가 컬렉션에 들어갔을때 contains()로 비교할 수 없다
### 대칭성 
> x와 y의 참조값이 만들어지고 x.equals(y)가 true 라면 그 반대도 항상 true여야 한다
```java
public final class CaseInsensitiveString {
    private final String s;

    public CaseInsensitiveString(String s) {
        this.s = Objects.requireNonNull(s);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CaseInsensitiveString) {
            return s.equalsIgnoreCase(((CaseInsensitiveString) o).s);
        }
        
        if (o instanceof String) {
            return s.equalsIgnoreCase((String) o);
        }
        
        return false;
    }
    
    // 그외 코드
}

class CaseInsensitiveStringTest {
    public static void main(String[] args) {
        CaseInsensitiveString cis = new CaseInsensitiveString("abc");
        String s = "abc";

        System.out.println(cis.equals(s)); // true
        System.out.println(s.equals(cis)); // false
    }
}
```
두개의 상황이 대칭성이 무너진 상황이다. cis.equals(s) 가 true가 나오면 s도 true로 나와야 하지만 String은 cis의 존재를 모르기에 false로 보일 수 밖에 없다
또한 이는 컬렉션의 `contains()`에서도 문제가 발생한다. 

```java
class CaseInsensitiveStringTest {
  public static void main(String[] args) {
    List<CaseInsensitiveString> list = new ArrayList<>();
    list.add(cis);

    System.out.println(list.contains(s));
  }
}
```
이렇게 해도 s는 cis와 비교가 안되기 때문에 false가 되는 것이다.<br/>
그래서 String과 연관시키려는 것은 포기하고 cis의 equals()가 변경되어야 한다

```java
public final class CaseInsensitiveString {
    private final String s;

    public CaseInsensitiveString(String s) {
        this.s = Objects.requireNonNull(s);
    }

    @Override
    public boolean equals(Object o) {
      return o instanceof CaseInsensitiveString &&
              ((CaseInsensitiveString) o).s.equalsIgnoreCase(s);
    }
    
    // 그외 코드
}

class CaseInsensitiveStringTest {
  public static void main(String[] args) {
    CaseInsensitiveString cis = new CaseInsensitiveString("abc");
    String s = "abc";

    System.out.println(cis.equals(s)); // false
    System.out.println(s.equals(cis)); // false

    List<CaseInsensitiveString> list = new ArrayList<>();
    list.add(cis);

    System.out.println(list.contains(s)); // false
  }
}
```
이렇게 변경되면 String과의 비교는 아예 배제하고 같은 객체끼리만 비교할 수 있게 되는것이다.<br/>
모두 true로 바꾸고자 하는 것이 아닌 `x와 y의 기준이 변경되어도 같은 결과가 발생하는 대칭성`이 만족하게 되었다

### 추이성 
> 참조값 x, y, z중 `x.equals(y) => true`이고 `y.equals(z) => true` 라면 `z.equals(x) => true`여야 한다
### 일관성 
> x와 y의 참조값이 만들어지고 x.equals(y)의 결과값은 항상 같아야 한다.
### null 아님
> equals(null)은 항상 false이다
  - <code>if(obj == null) false</code>여야 한다
