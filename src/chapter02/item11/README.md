# Item 11. equals를 재정의 하려거든 hashCode도 함께 정의해라

우리가 `eqauls()`를 정의할때 IDE의 도움을 받는 경우가 있다. 그때 `hashCode()`도 같이 재정의 되는 것을 볼 수 있다<br/>
그걸 보면 `equals()`를 재정의 한다는 것은 `hashCode()`도 같이 정의해야 한다는 뜻이다

# Object의 hashCode 명세 규약
## 1️⃣ `hashCode()`는 같은 객체에서 여러 번 호출해도 같은 값이어야 한다.
> `x.hashCode()`는 프로그램이 실행되는 동안 같은 값을 유지해야 한다.
```java
class BadMutableHashCode {
    private String name;

    public BadMutableHashCode(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BadMutableHashCode)) return false;
        BadMutableHashCode other = (BadMutableHashCode) obj;
        return this.name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}

public class Main {
    public static void main(String[] args) {
        Map<BadMutableHashCode, String> map = new HashMap<>();
        BadMutableHashCode key = new BadMutableHashCode("Alice");

        map.put(key, "Developer");

        key.setName("Bob");

        System.out.println(map.get(key)); // null
    }
}
```
값이 변경되면서 원하는 동작이 변경되었다. 때문에 key로써 사용되는 값은 변경이 되지 않도록 해야하기 때문에
<code>private final String name;</code>이렇게 변경하면 된다.

## 2️⃣ `eqauls()`가 같다면 `hashCode()`도 같아야 한다
> `x.eqauls(y) == true`이면 x와 y는 같은 hashCode를 가지고 있다
```java

import java.util.Objects;

public class PhoneNumber {
    private int first;
    private int second;
    private int third;

    public PhoneNumber(int first, int second, int third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PhoneNumber that = (PhoneNumber) o;
        return first == that.first && second == that.second && third == that.third;
    }
}

class Main {
    public static void main(String[] args) {
        Map<PhoneNumber, String> map = new HashMap<>();
        map.put((new PhoneNumber(707, 867, 5309)), "제니");

        System.out.println(map.get((new PhoneNumber(707, 867, 5309)))); // null
    }
}
```
위와 같은 경우 같은 인스턴스로써 제작을 했지만 `get()`의 반환값이 null 인것을 알 수 있다.<br/>
왜냐하면 해당 객체의 해시 함수 반환값이 정의되어 있지 않기 때문에 문제가 발생하는 것이다.

이는 해시 함수를 정의하면 쉽게 해결 할 수 있다.

## 3️⃣ `equals()`가 다르더라도 `hashCode()`는 같을 수도 있다.
> `x.equals(y) == false`라고 하더라도 두 개의 해쉬값이 다를 필요는 없다.<br/>
> 하지만 가급적이면 다른 해시코드를 가지고 있는것이 좋다.

```java
public class PhoneNumber {
    private int first;
    private int second;
    private int third;

    public PhoneNumber(int first, int second, int third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PhoneNumber that = (PhoneNumber) o;
        return first == that.first && second == that.second && third == that.third;
    }

    @Override
    public int hashCode() {
        return 1;
    }
    @Override
    public int hashCode() {
        return Objects.hash(first, second, third);
    }
}

class Main {
    public static void main(String[] args) {
        Map<PhoneNumber, String> map = new HashMap<>();
        map.put((new PhoneNumber(707, 867, 5309)), "제니");

        System.out.println(map.get((new PhoneNumber(707, 867, 5309)))); // 제니
    }
}
```
우선 해시 함수를 구현하는 것으로 해결은 했지만 해당 해시 함수는 문제가 있다<br/>
저렇게 함수를 정의하면 값이 달라도 모든 인스턴스는 같은 해시 값을 가지게 되고 이는 큰 문제가 된다

해시 함수가 부딫히면 같은 버킷에 계속해서 객체가 저장되고 해당 버킷의 값들이 `Linked List`로 저장되기 때문에 검색 속도가 O(1)에서 O(n)로 낮아진다
그래서 `hashCode()`를 잘 작성해야한다.

## ✅ 좋은 hashCode()를 작성하는 요령
### ✔ 해시 값 직접 설정하기
1. 합리적인 초기 값을 선택
   - 일반적인 초기 값을 셋팅한다 
   - <code>int result = 1;</code>
2. 객체의 주요 필드(값)를 hashCode()에 반영한다
   - 각 필드를 적절한 해싱 방식으로 결합해서 hashCode 생성
   - 각 필드의 값을 합칠때 곱셈을 활용한다
   - <code>result = 31 * result + (필드명)</code>
   - `31`은 소수의 값을 활용해서 해시 충돌을 최대한 줄인다.
```java
@Override
public int hashCode() {
    int result = 1;
    result = 31 * result + first;
    result = 31 * result + second;
    result = 31 * result + third;
    return result;
}

// Object의 hash 메소드를 활용해서 develop
@Override
public int hashCode() {
    int result = Integer.hashCode(first);
    result = 31 * result + Integer.hashCode(second);
    result = 31 * result + Integer.hashCode(third);
    return result;
}
```

### ✔ Objects 클래스를 사용한 해시 값 설정
Objects 클래스는 임의의 갯수만큼 객체를 받아 해시코드를 계산하는 메서드가 있다
아주 간단하게 작성할 수 있다.
```java
public class PhoneNumber {
    // ...
    
    @Override
    public int hashCode() {
        return Objects.hash(first, second, third);
    }
}

// hash 메서드 -> 배열로 만들어진다.
public static int hash(Object... values) {
    return Arrays.hashCode(values);
}

// hashCode 메소드 -> 입력값 (박싱 OR 언박싱) 작업
public static int hashCode(Object a[]) {
    if (a == null)
        return 0;

    int result = 1;

    for (Object element : a)
        result = 31 * result + (element == null ? 0 : element.hashCode());

    return result;
}
```
위의 코드를 보면 다양한 작업이 진행되는 것을 볼 수 있다. 때문에 직접 작성한 값보다 조금 느릴 순 있다.<br/>

그렇다면 어떻게 개선할 수 있을까?

#### ✅ 1️⃣ `hashCode` 캐싱을 적용한 코드
```java
public final class PhoneNumber {
    // ...
    private final int hashCode; 

    public PhoneNumber(int first, int second, int third) {
        // ...
        this.hashCode = Objects.hash(first, second, third);
    }

    // ...

    @Override
    public int hashCode() {
        return hashCode;
    }
}
```
생성 당시에 hashCode를 초기화하고 사용하면 해시 기반 컬렉션에서 여러번 사용될 때 성능 최적화된다.

#### ✅ 2️⃣ hashCode()를 지연(lazy) 초기화하여 캐싱하는 방법
만약 해시 기반 컬렉션에서 자주 사용하지 않는 경우에는 hashCode가 불필요하기 때문에 실제로 해시 함수를 사용하게 된다면 해시 값을 설정하도록 진행
```java
public final class PhoneNumber {
    private volatile int hashCode; // 🚀 초기값 0, 필요할 때만 계산

    // ...

    @Override
    public int hashCode() {
        if (hashCode == 0) { // 🚀 처음 호출될 때만 계산
            synchronized (this) { // 멀티스레드 환경에서도 안전
                if (hashCode == 0) {
                    hashCode = Objects.hash(first, second, third);
                }
            }
        }
        return hashCode;
    }
}
```
`volatile`과 `synchronized`을 활용해서 만들어서 멀티 스레드 환경에서도 안전하게 진행할 수 있다

## 🚀 `hashCode()`의 API 명세서 작성
> `hashCode()`는 문서화 하지 않는것이 좋다.
> 
> - hashCode()값의 생성 방식에 의존하지 않고 더 나은 해시 함수가 있다면 반영할 수 있어야 한다<br/>
> - hashCode()가 언제든 변경될 수 있기 때문에 사용자는 값을 사용하거나 저장해서 외부에서 사용하면 안된다.







