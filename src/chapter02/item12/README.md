# Item 12. toString을 항상 재정의하라

> toString의 일반 규약
> 
> 간결하면서 사람이 읽기 쉬운 형태의 유익한 정보

자체적으로 제작한 클래스의 `toString()`은 Object의 toString메소드를 사용한다
```java
class MyObejct {
    String name;

    public MyObejct(String name) {
        this.name = name;
    }
}

public class Main {
    public static void main(String[] args) {
        MyObejct mine = new MyObejct("내거");

        System.out.println(mine.toString());
        // MyObejct@119d7047
    }
}
```
하지만 이때 우리는 그 결과가 name이 나올것이라고 생각하지만 막상 보면 `클래스이름@16진수로 표현한 해시코드`로 나온다<br/>
그래서 우리는 원하는 동작을 할 수 있게끔 `재정의`하려고 한다

### 🚀 toString을 재정의 할 때 고려해야할 사항
#### ✅ 사람이 읽기 쉬운 정보를 제공
- `toString`은 객체의 핵심 정보를 포함해야 한다
- Person 클래스를 예로 든다면, `"Person[name=Alice, age=30]"`과 같은 형식으로 나와야 한다

#### ✅ `toString()`의 포맷을 문서화할지 고민해라
- 만약 `toString()`의 반환 형식이 API 사용자가 직접 파싱해서 활용해야 하는 경우, 포맷을 문서화해야 한다.
- 포맷을 명시하면 평생 `toString()`은 해당 포맷으로 얽매인다.
- 일반적으로 `toString()`은 `디버깅용`으로 사용되기에 포맷을 문서화하지 않는 것이 좋다
- 해야한다면 `toJson()`, `toCsv()`와 같이 명시한 메소드를 추가해주는 것도 좋다

✔ 포맷의 명시화
```java

/**
 * 해당 번호는 전화번호처럼 보여야 한다
 * xxx-yyy-zzzz 의 형식으로 진행된다
 * 
 * ....추가 조건
 */
@Override
public String toString() {
    return String.format("%03d-%03d-%04d", firstCode, secondCode, thirdCode);
}

/**
 * 다음은 이 설명의 일반적인 형태지만 상세 형식은 정해지지 않았다
 * 
 * ex ) [약물 #9: 유형=사람, 냄새=테레빈유, 겉모습=먹물]
 */
@Override
public String toString() {
    //....
}
```
1번은 포맷을 명시한것이고 2번은 명시하지 않은 것이다.

#### ✅ 포맷을 문서화해야한다면, 객체로 다시 변환하는 API도 함께 제공하라
- `toString()`의 포맷을 공개 API로 제공한다면, 해당 포맷을 다시 객체로 변환하는 생성자 or 정적 팩터리 메소드를 함께 제공해야한다
```java
class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public static Person fromString(String string) {
        String[] split = string.split(", ");
        return new Person(split[0], Integer.parseInt(split[1]));
    }

    @Override
    public String toString() {
        return name + ", " + age;
    }
}

public class Main {
    public static void main(String[] args) {
        Person john = new Person("John", 23);

        String str = john.toString();
        Person secondJohn = Person.fromString(str);

        System.out.println(secondJohn);
    }
}
```

<details>
    <summary>굳이?</summary>

`toString()`으로 본 결과를 통해 굳이 다시 객체를 만들어야 하는 경우가 무슨 경우일까

### ✅ 예제 1: CSV 또는 로그 데이터를 활용하는 경우
- `toString()`을 이용해서 CSV 형식의 문자열을 로그 파일에 저장했다
- 나중에 해당 데이터를 다시 객체로 복원하기 위해 `정적 팩토리 메소드 or 생성자`와 같은 API가 필요하다

### ✅ 예제 2: 네트워크 통신 또는 파일 저장
- 객체를 네트워크로 전송하거나 파일로 저장할때 간단한 문자열 형태로 변환한다
- 해당 값을 받는 곳에서 다시 객체화 할 필요가 있을 수 있다

> 하지만 역시 꼭 해야하는 것이 아니다. <br/>
> 대부분의 경우 `toString()`은 디버깅 용도로 많이 사용하기 때문에 필요없는 경우가 다반사이다.

</details>

#### ✅ `toString()`에 포함된 정보를 얻을 수 있는 API도 함께 제공
- 혹여 `toString()` 의 정보를 외부에서 복잡하게 사용하는 것을 방지하여 값을 가져오는 메서드도 따로 제공하는 것이 좋다

# 🚀 AutoValue란?
- Java에서 `eqauls()`, `hashCode()`, `toString()`을 직접 구현하는 것은 매우 귀찮다
- 이를 Google의 `AutoValue` 라이브러리가 해결한다 ( 외부 라이브러리 )
```java
@AutoValue
class Person { 
    String name();
    int age();

    public static Person create(String name, int age) {
        return new AutoValue_Person(name, age);
    }
}
```
- 사용처 <br/>
✅ 불변(immutable) 클래스를 만들 때<br/>
✅ toString(), equals(), hashCode()를 자동으로 생성하고 싶을 때<br/>
✅ 객체의 핵심 데이터를 깔끔하게 표현하고 싶을 때<br/>
때문에 Object의 `toString()`을 그냥 사용하는 것보다는 훨씬 편리하고 좋다

- 하지만 클래스의 `의미`까지 파악하기 쉽지 않기때문에 여건에 따라 제작하는 것이 좋다
