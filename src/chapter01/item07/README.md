# Item 07. 다쓴 객체 참조는 해제하라

자바는 네이티브언어인 C 혹은 C++ 처럼 메모리 관리를 직접하지 않고 JVM에게 전담시킨다<br/>
이로 인해 개발자들은 정말 편하게 개발을 할 수 있게 되었다 하지만 이로써 Java개발자는 더 이상 메모리 관리에서 완전히 자유로워졌을까?

## 메모리 누수의 주범 1. 객체 내부에서 메모리 관리
```java
public class Stack {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 10;
    
    public Stack() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }
    
    public void push(Object element) {
        ensureCapacity();
        elements[size++] = element;
    }
    
    public Object pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        return elements[--size];
    }
    
    private void ensureCapacity() {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, size * 2);
        }
    }
}
```
아무런 문제가 없는 Stack클래스이다. 각 메소드는 가장 흔하게 사용되는 push와 pop의 메소드를 가지고 있는 것을 알 수있다<br/>
완전히 문제가 없진 않고 이번 주제인 `메모리`를 이야기 하기 위해 가져왔다

> #### pop()
> 만약 pop() 메소드를 사용해서 값을 뺀다고는 하지만 그렇게 보일 뿐 실제로 빠지진 않는다. 이로써 더이상 사용되지 않을 공간이 낭비되는 `메모리 누수`가 발생할 수 있다

GC는 객체 참조를 단 하나라도 살려둔다면 해당 객체 뿐만이 아닌 그 객체가 참조하는 모든 객체를 회수하지 못한다
```java
class Department {
    String name;
    List<Employee> employees = new ArrayList<>();

    public Department(String name) {
        this.name = name;
    }

    public void addEmployee(Employee e) {
        employees.add(e);
    }
}

class Employee {
    String name;

    public Employee(String name) {
        this.name = name;
    }
}

public class GCDemo {
    public static void main(String[] args) {
        Department department = new Department("Engineering");
        department.addEmployee(new Employee("Bob"));
        department.addEmployee(new Employee("Alice"));

        List<Employee> employees = department.employees;
        department = null;
    }
}
```
이렇게 만약 외부에서 객체 안의 값을 직접 불러내 사용한다면 원본 객체인 Department는 회수되었지만 내부의 값을 살아있는 의도치 않은 메모리 누수가 발생하는 것이다

물론 의도했다면 상관없지만 말이다 어쨌든 다시 넘어와서 그렇다면 메모리 누수를 방지하기 위해서는 어떻게 하면 좋을까

### 참조가 유지 될 수 있는 곳에 가서 NULL 처리
```java
// 1번 Stack 예시
public Object pop() {
  if (size == 0) {
      throw new EmptyStackException();
  }
  Object element = elements[--size];
  elements[size] = null;
  return element;
}
```
```java
// 2번, Department 내부 employees
public class GCDemo {
   public static void main(String[] args) {
      Department department = new Department("Engineering");
      department.addEmployee(new Employee("Bob"));
      department.addEmployee(new Employee("Alice"));

      List<Employee> employees = department.employees;
      department.employees = null;
      department = null;
   }
}
```

<details>
   <summary>Getter의 지양하는 이유 중 하나가 될 수 있을까?</summary>

우리가 객체 지향에 대해서 공부를 하다보면 Setter는 물론이고 Getter마저도 지양하자고 주장하는 경우를 많이 볼 수 있다<br/>

### 그이유가 뭘까?
우선 가볍게 생각하면 객체 지향의 특징 중 하나인 캡슐화를 지키기 위해서이다. 캡슐화는 `데이터를 숨기고 행동을 특정하여 접근하도록 하게 한다` 이다. <br/>
- 객체 지향보다는 절차 지향에 가까워질수 있다.
- 외부에서 값을 마냥 바꾼다면 해당 객체의 데이터는 보호 될 수 없다

그래서 우리는 Getter를 최대한 지양하고자 하는데 이번 Item에서 문득 든 생각이 이는 메모리의 누수 또한 방지할 수 있겠다라는 생각이였다<br/>
앞서 해당 객체에서 값을 꺼내가서 외부에서 받아갈때 메모리 누수가 될 이유 중 하나가 될 수 있다고 했는데 getter가 딱 자신의 값을 내주는 용도라는 생각이 들었기 때문이다.<br/>

그래서 나는 애초에 getter를 없애고 스스로 일을 하게 한다면 외부에서는 해당 객체의 값만 null 처리해도 메모리 누수의 걱정이 반으로 줄어들 것이라는 생각이 들었다<br/>
그럼 메모리 누수는 해당 객체 내부에서만 신경을 쓰면 되고 이는 사용된 모든 코드를 확인하는 것보다 훨씬 더 간단하다고 생각한다
</details>

### 모든 곳에 NULL 처리?

메모리 누수의 방지를 위해 객체가 사용된 모든 곳에 NULL 처리를 하면 오히려 코드가 지저분해진다 <br/> 그래서 객체를 직접 NULL 처리하는 방식은 예외적인 일이여야 한다 <br/>
Stack과 같은 경우 pop()이후 더이상 배열에서 사용되지 않을 객체가 GC에 의해 정리되지 않는 이유는 정리하고 싶어도 해당 값을 진짜로 앞으로 사용 여부를 판단하기는 쉽지 않기 때문이다

즉, 자신이 직접 메모리를 관리하는 클래스는 개발자가 신경써서 NULL을 이용해 참조를 끊어 메모리 누수를 방지할 수 있다

## 메모리 누수의 주범 2. 캐시(Cache)

필요 없는 데이터가 캐시에 계속 남게되는 경우에도 메모리 누수는 발생할 수 있다 

```java
class Cache {
    private static final Map<String, byte[]> cache = new HashMap<>();
    
    public static void put(String key, byte[] value) {
        cache.put(key, value);
    }

    public static byte[] getCache(String key) {
        return cache.get(key);
    }
}
```
HashMap을 사용한 캐싱은 명시적으로 제거하지 않으면 캐시에 오래 남고, GC가 오래된 데이터를 수거하지 않는다

### 해결 방법

- WeakHashMap을 사용
```java
class SafeCache {
    private static final Map<String, byte[]> cache = new WeakHashMap<>();
    
    public static void put(String key, byte[] value) {
        cache.put(key, value);
    }

    public static byte[] getCache(String key) {
        return cache.get(key);
    }
}
```
WeakHashMap은 키가 더이상 참조되지 않는다면 자동으로 제거된다.<br/>
기준은 WeakHashMap에 남겨진 데이터 중 참조가 끊긴 값이 있다면 그 값을 스스로 지우는 것이다<br/>
이는 개발자가 직접 Map의 값에 null을 하지 않아도 객체 참조를 끊어주면 스스로 지울 수 있다

- LRU 캐시 적용
```java
class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;
    
    public LRUCache(int capacity) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
}
```
> LRU (Least Recently Used)
> 
> 가장 오래된 데이터 찾기 

데이터 중 가장 오래된 데이터를 찾아 데이터를 청소하는 것이다. 이때 값을 보면 LinkedHashMap을 사용하는것을 볼 수 있다<br/>
`removeEldestEntry()` 메소드는 새로운 Entry를 넣고 기존 Entry 중 가장 오래된 것을 제거할지 여부를 결정한다 
```java
public class Main {
    public static void main(String[] args) {
        LRUCache<Integer, String> cache = new LRUCache<>(3);

        cache.put(1, "A");
        cache.put(2, "B");
        cache.put(3, "C");

        System.out.println(cache); // {1=A, 2=B, 3=C}

        cache.put(4, "D"); // 용량 초과 -> 가장 오래된 1=A 제거

        System.out.println(cache); // {2=B, 3=C, 4=D}

        cache.get(2);  // 2를 다시 사용하면 LRU에서 최신으로 업데이트
        cache.put(5, "E"); // 가장 적게 사용된 3=C 제거

        System.out.println(cache); // {2=B, 4=D, 5=E}
    }
}
```
이렇게 시간에 따라 데이터를 정리하게 되는데 그 결정은 생성자에서 진행했던 `super(capacity, 0.75f, true);`가 결정하는 것이다<br/>
- 초기 용량: 5 (처음에는 5개의 버킷 크기로 시작)
- 부하율: 0.75 (버킷이 75% 차면 자동 확장)
- 접근 순서 유지: true (LRU 정책 적용)
<details>
    <summary>각 매개변수 상세 설명</summary>

- 첫번째 매개 변수 : initialCapacity
    - 초기 용량
- 두번째 매개 변수 : loadFactor
    - 부하율 : 해시 테이블의 버킷이 얼마나 차면 크기를 늘릴지 결정 ( % 단위 )
    - 크기를 늘릴땐 2배씩 증가시킨다
- 세번째 매개 변수 : accessOrder
    - 접근 순서 유지 여부
    - false : 삽입된 순서대로 값을 유지한다
    - true : LRU를 적용하여 순서를 유지한다
        - get() 또는 put()을 호출할 때 가장 최근에 사용된 항목이 마지막으로 이동한다.
</details>


## 메모리 누수의 주범 3. 리스너(listener), 콜백(callback)
Java에서 왜 이벤트 리스너와 콜백 얘기를 하게 되는 걸까?

- 이벤트 리스너 : UI 이벤트 처리
- 콜백 함수 : 비동기 프로그래밍, 네트워크 요청, 쓰레드 처리

둘다 마찬가지이다 만약 외부에서 객체가 만들어지고 그 객체를 받는 쪽에서 주입받아 사용된다면 콜백함수로써 잠깐 사용되고자 하는 메소드가 특정 객체에 필드로써 유지될 수 있다는 이야기이다. 
