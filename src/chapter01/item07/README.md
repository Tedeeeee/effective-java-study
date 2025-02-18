# Item 07. 다쓴 객체 참조는 해제하라

자바는 네이티브언어인 C 혹은 C++ 처럼 메모리 관리를 직접하지 않고 JVM에게 전담시킨다<br/>
이로 인해 개발자들은 정말 편하게 개발을 할 수 있게 되었다 하지만 이로써 Java개발자는 더 이상 메모리 관리에서 완전히 자유로워졌을까?

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

## 모든 곳에 NULL 처리?

메모리 누수를 방지하기 위해 





