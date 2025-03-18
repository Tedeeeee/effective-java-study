# Item 14. comparable을 구현할지 고려해라

Comparable 인터페이스에 존재하는 단 한개의 메소드 `compareTo()`를 알아보자

우선 `compareTo()`는 Object의 `equals()`와 굉장히 비슷한 성격을 가지고 있다 하지만 두가지 정도의 차이점이 있다

## 1. `compareTo()`는 단순 동치성 비교 뿐만이 아닌 순서까지 비교할 수있다
특정 클래스에 Comparable을 구현했다는 것은 해당 클래스의 인스턴스들은 `순서`가 있다는 뜻이다<br/>
만약 Comparable을 구현했다면 순서를 정리하는 코드는 아주 간단하다<br/>
<code>Arrays.sort(a)</code><br/>

컬렉션의 순서 관리 또한 손쉽게 관리되는 것을 알 수 있다
```java
class Person implements Comparable<Person> {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(Person o) {
        return Integer.compare(this.age, o.age);
    }

    @Override
    public String toString() {
        return name + " " + age;
    }
}

public class Main {
    public static void main(String[] args) {
        List<Person> people = Arrays.asList(
                new Person("Alice", 30),
                new Person("Bob", 25),
                new Person("Charlie", 30),
                new Person("David", 25)
        );

        Collections.sort(people);
        System.out.println("정렬 이후 : " + people);
    }
}
```
이렇게 자바 플랫폼 라이브러리의 모든 값 클래스와 열거 타입은 대부분 Comparable을 구현했다. <br/>
이렇게 순서가 명확한 값 클래스를 작성한다면 Comparable 인터페이스를 구현하면 유용하게 사용할 수 있다

### 📌 기본 타입과 객체 참조 필드의 값비교
해당 객체안의 모든 필드를 Comparable이 구현하지 않았거나 표준이 아닌 순서로 비교를 해야한다면 Comparator를 사용하면 된다.<br/>

기본 타입(int, double)은 `Integer.compare(x, y)`, `Double.compare(a, b)`처럼 사용하면 된다
객체 참조 필드(String, LocalDate)는 해당 클래스가 Comparable을 구현하고 있기에 `compareTo()`를 그대로 사용하면 된다

### 📌 여러 필드를 통해 순서 정하기 feat. 람다
가장 핵심 필드가 한개라면 다행이지만 여러개라면 어느것을 먼저 비교하느냐가 굉장히 중요해진다. 하지만 너무 많아지면 코드가 불편해진다

```java
public int compareTo(PhoneNumber pn) {
    int result = Short.compare(areaCode, pn.areaCode);
    if (result == 0) {
        result = Short.compare(prefix, pn.prefix);
        if (result == 0) {
            result = Short.compare(lineNum, pn.lineNum);
        }
    }
    return result;
}
```
이렇게 더러워지는 것을 대비하기 위해 `비교자 생성 메서드`와 함께 메서드 연쇄 방식으로 비교자를 생성할 수 있다
```java
private static final Comparator<PhoneNumber> COMPARATOR =
        Comparator.comparingInt((PhoneNumber pn) -> pn.areaCode)
                .thenComparingInt(pn -> pn.prefix)
                .thenComparingInt(pn -> pn.lineNumber);

@Override
public int compareTo(PhoneNumber o) {
    return COMPARATOR.compare(this, o);
}
```
코드가 굉장히 깔끔해진 것을 알 수 있다

1. 최초에 비교자(Comparator)를 생성한다
2. comparingInt()라는 `정적 펙토리 메서드`를 통해 가장 먼저 분류하고
   - 이때 `PhoneNumber pn`이라고 적어놓은 것은 타입 추론 능력을 항상을 돕기위해 작성
3. 그 이후부터 `then`이라고 시작하는 `인스턴스 메서드`를 활용해서 반환한다. 

<details>
    <summary>객체 참조용 비교자 생성 메서드도 준비</summary>

#### 🎯 정적 메소드
```java
// Comparator 인터페이스 내부 정적 메소드 (1)
public static <T, U> Comparator<T> comparing(
        Function<? super T, ? extends U> keyExtractor,
        Comparator<? super U> keyComparator)
{
    Objects.requireNonNull(keyExtractor);
    Objects.requireNonNull(keyComparator);
    return (Comparator<T> & Serializable)
        (c1, c2) -> keyComparator.compare(keyExtractor.apply(c1),
                                          keyExtractor.apply(c2));
}

// Comparator 인터페이스 내부 정적 메소드 (2)
default <U extends Comparable<? super U>> Comparator<T> thenComparing(
        Function<? super T, ? extends U> keyExtractor)
{
    return thenComparing(comparing(keyExtractor));
}
```
1. 키 추출자를 받아 해당 키의 자연적 순서를 사용
2. 키 추출자 하나와 추출된 키를 비교할 비교자

#### 🎯 인스턴스 메소드
```java
// Comparator 인터페이스 내부 메소드 thenComparing (1)
default Comparator<T> thenComparing(Comparator<? super T> other) {
    Objects.requireNonNull(other);
    return (Comparator<T> & Serializable) (c1, c2) -> {
        int res = compare(c1, c2);
        return (res != 0) ? res : other.compare(c1, c2);
    };
}

// Comparator 인터페이스 내부 메소드 thenComparing (2)
default <U> Comparator<T> thenComparing(
        Function<? super T, ? extends U> keyExtractor,
        Comparator<? super U> keyComparator)
{
    return thenComparing(comparing(keyExtractor, keyComparator));
}

// Comparator 인터페이스 내부 메소드 thenComparing (3)
default <U extends Comparable<? super U>> Comparator<T> thenComparing(
        Function<? super T, ? extends U> keyExtractor)
{
    return thenComparing(comparing(keyExtractor));
}
```
1. 비교자 하나만 인수로 받아 해당 비교자로 순서 저장
2. 키 추출자를 인수로 받아 그 키의 자연적 순서로 보조 순서를 정한다
3. 키 추출자 하나와 추출된 키를 비교할 비교자까지 총 2개의 인수를 받는다

</details>

## 2. `compareTo()`는 `equals()`와 일관되게 동작하지만 항상 그런것은 아니다
`compareTo()`의 규약을 보면 `equals()`의 규약과 굉장히 비슷하다

> `compareTo()`메서드는 객체의 순서를 비교한다.
> 
> 이때 해당 객체가 비교하려고 하는 객체보다 작으면 음의 정수(-1), 같은면 0, 크면 양의 정수(1)을 반환한다<br/>
> 비교할 수 없는 객체가 주어진다면 ClassCastException이 발생한다
 
- ✅ 첫번째 규약. 두 객체 참조의 순서를 바꿔도 결과는 같다 - 대칭성
  -  x.compareTo(y) > 0이면 y.compareTo(x) < 0이 되어야 한다.<br/>
- ✅ 두번째 규약. 첫번째가 두번째보다 크고 두번째가 세번째보다 크면, 첫번째는 세번째보다 커야한다 - 추이성
  - x.compareTo(y) > 0이고 y.compareTo(z) > 0이면 x.compareTo(z) > 0이 되어야 한다.
- ✅ 세번째 규약. 크기가 같은 객체들끼리는 어떤 객체와 비교해도 항상 같다 - 일관성
  - compareTo의 결과는 객체가 변경되지 않는 한 항상 동일해야 한다.

위에 세개의 값은 기본적인 `compareTo()`의 규약이지만 `equals()`의 규약과 굉장히 비슷하다는 것을 알 수있다<br/>
그렇기에 주의사항마저도 굉장히 흡사하다.
> 주의 사항
> 
> 기존 클래스를 확장한 구체 클래스에서 새로운 값 컴포넌트를 추가하면 compareTo 규약이 무너진다

역시나 이를 간파하는 방법으로 컴포지션을 활용하는 것으로 해결할 수 있다

### 🔍 마지막 규약은 권장된다
책에서 필수는 아니지만 꼭 지켜줬으면 하는 규약중 `compare()의 동치성 테스트의 결과가 equals()와 같아야 한다.`가 있다<br/>
이것이 잘 지켜진다면 정렬된 객체의 순서의 결과가`equals()`의 결과와 일관되게 되기 때문이다 물론 일관되지 않아도 동작에는 문제가 없다<br/>
하지만 나중에 해당 객체를 정렬하기 위해 컬렉션을 사용하면 그때 문제가 발생할 수 있다.

```java
// 객체
@Override
public int compareTo(Person other) {
    if (this.age == other.age) {
        return this.name.compareTo(other.name); // 이름 기준 추가 비교 (일관성 깨짐)
    }
    return Integer.compare(this.age, other.age);
}

@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Person)) return false;
    Person person = (Person) o;
    return age == person.age;
}

// 실제로 실행
public static void main(String[] args) {
    Set<Person> treeSet = new TreeSet<>();
    Person p1 = new Person("Alice", 30);
    Person p2 = new Person("Bob", 30);
    
    treeSet.add(p1);
    treeSet.add(p2);
    
    System.out.println(treeSet.size()); // 2
    System.out.println(treeSet); // [Alice 30, Bob 30]
}
```
객체를 사용하는 것에는 문제가 없지만 `equals()`에서는 나이만 비교하고 실제 `compareTo()`에서는 나이, 이름 모두 비교하는 것을 알 수 있다<br/>
이건 컬렉션이 정렬을 진행할 때 동치성을 비교하면서 사용되는 것이 `compareTo()`이기 때문이다.

### 🚀 compareTo() 작성 요령
작성 요령 역시 `equals()`와 굉장히 비슷하지만 몇가지 차이점이 있다
1. 타입 확인

Comparable의 인수를 제네릭으로 받으면서 컴파일 타임에서 값이 정해진다. 즉, `equals()`처럼 타입 확인을 하지 않아도 된다. 무조건 타입을 맞춰야하고 타입이 맞지 않다면 그저 `ClassCastException`을 발생시킨다

2. Null 체크

null을 비교하는 것 역시 `NullPointerException`을 발생하는 것으로 바로 return 할 수 있다.


# 해시 코드를 비교해서 순서를 정하는 방법
우리는 흔히 사용하는 방식으로 직접 비교하는 것을 만들수 있다

```java
static Comparator<Object> hashCodeOrder = new Comparator<>() {
    public int compare(Object o1, Object o2) {
        return o1.hashCode() - o2.hashCode();
    }
};
```
하지만 이는 연산 과정에서 `정수 오버플로우`나 `부동 소수점 계산 방식`의 문제를 발생시킬수 있다
그래서 해결하는 방법에 위에서 배운 두가지를 사용할 수 있다

1. 정적 compare 메서드를 활용한 비교자

```java
static Comparator<Object> hashCodeOrder = new Comparator<>() {
    public int compare(Object o1, Object o2) {
        return Integer.compare(o1.hashCode(), o2.hashCode());
    }    
};
```

2. 비교자 생성 메서드를 활용한 비교자

```java
static Comparator<Object> hashCodeOrder =
        Comparator.comparingInt(o -> o.hashCode());
```
