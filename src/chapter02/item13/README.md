# Item 13. clone 재정의는 주의해서 진행하라

## 1. Cloneable 인터페이스는 복제 기능이 없다

```java
public interface Cloneable { }
```
원래 Cloneable 인터페이스는 아무런 메소드가 없다.<br/>
하지만 실제 객체에서 Cloneable을 구현하지 않고 `clone()`을 사용하면 `CloneNotSupportedException`이 발생하는 것을 볼 수 있다

그 이유는 내부적으로 해당 객체가 Cloneable을 구현했는지 확인한다는 이야기인데 내부 동작에서 힌트를 얻을 수 있다<br/>
물론 `Object.clone()`의 코드 구현을 볼 수 없지만 공식 문서에서는 이런식으로 표현되어 있다

📌 clone()의 Javadoc 설명 (위 코드에서 발췌)

> If the class of this object does not implement the interface Cloneable, then a CloneNotSupportedException is thrown.


결국 이런식으로 코드가 작성되어 있을 수 있다는 생각을 해볼 수 있다
```java
if (!(this instanceof Cloneable)) {
    throw new CloneNotSupportedException();
}
```

### 1. 정리 
> 즉, Cloneable은 해당 객체가 `clone()`이 가능하다라는 신호로 사용되는 것이다 

## 2. Object의 `clone()`은 얕은 복사를 수행
### 부제 : 올바른 복제 기능 만들기
기본적으로 복사를 하면 해당 객체의 상세 복사를 생각할 수 있지만 실제로는 그렇지 않다. 그이유는 허술한 일반 규약에 의해 결정되었다

> 규약
> 
> "Otherwise, this method creates a new instance of the class and initializes all its fields with exactly the contents of the corresponding fields of this object, as if by assignment."
> 
> 필드의 내용을 그대로 복사한다 -> 얕은 복사 <br/>
> 깊은 복사에 대한 명확한 규칙이 없기 때문에 `clone()`이 깊은 복사를 보장해야 한다는 규약이 없다

그래서 만약 필드가 가변 객체를 참조하고 있다면 원본과 복제본이 같은 객체를 바라보게 되고 이는 예상치 못한 버그를 발생 시킬 수 있다

##### 올바른 복제 기능 (feat. 깊은 복사)
그래서 우리가 생각하는 값은 똑같지만 서로 전혀 다른 객체로써 `clone`이 되려면 clone을 `재정의`해야 한다<br/>
기존의 clone은 오버라이딩을 하되 super.clone()를 통해 얕은 복사를 먼저 진행하고 필드를 새롭게 셋팅해주는 로직을 추가하면 된다
```java
class DeepCopyExam implements Cloneable {
    int[] data = {1, 2, 3};

    @Override
    protected Object clone() throws CloneNotSupportedException {
        DeepCopyExam clone = (DeepCopyExam) super.clone();
        clone.data = this.data.clone();
        return clone;
    }
}

public class Main {
    public static void main(String[] args) throws CloneNotSupportedException {
        DeepCopyExam origin = new DeepCopyExam();
        DeepCopyExam clone = (DeepCopyExam) origin.clone();

        clone.data[0] = 2;

        System.out.println(origin.data[0]); // 1
        System.out.println(clone.data[0]); // 2
    }
}
```
이렇게 가벼운 객체 복사를 진행하고 가변 객체와 같이 공유할 수도 있는 객체를 복사하여 복사된 객체의 필드 값을 정해주면 된다.

### `super.clone()`은 try-catch로 감싸주어야 한다
```java
class DeepCopyExam implements Cloneable {
    int[] data = {1, 2, 3};

    @Override
    protected Object clone() throws CloneNotSupportedException {
        try {
            DeepCopyExam clone = (DeepCopyExam) super.clone();
            clone.data = this.data.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new CloneNotSupportedException(e.getMessage());
        }
    }
}
```
이전에서 Object에서 clone에 문제가 생기면 `CloneNotSupportedException`이 발생하는 것을 알 수 있다 물론 JVM 내부에서 동작하는 네이티브 코드를 호출하면서 예외를 잡긴하지만 그 이유가 정확하지 않을 수 있다<br/>
그래서 이렇게 정확한 오류를 잡아주는 것이 좋다

> 배열의 `clone()`
> 
> 배열의 `clone()`은 런타임 타입과 컴파일타임 타입 모두가 원본 배열과 똑같은 배열을 반환한다<br/>
> 그래서 배열의 복사는 `clone()`을 사용해도 좋다. `clone()`기능을 제대로 사용하는 유일한 예이다
 
### 재귀적 `clone()`이면 될까?
```java
public class HashTableExam implements Cloneable {
    private Entry[] buckets = ...;

    private static class Entry {
        final Object key;
        Object value;
        Entry next;

        public Entry(Object key, Object value, Entry next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    @Override
    public HashTableExam clone() throws CloneNotSupportedException {
        try {
            HashTableExam result = (HashTableExam) super.clone();
            result.buckets = buckets.clone();
            return result;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
```
HashTable의 예시로 만든 결과이다. 위의 buckets를 복사하면 배열이 복사되지만 직접 만든 배열이기때문에 얕은 복사의 문제점이 같이 발생한다

```java
public class HashTableExam implements Cloneable {
    private Entry[] buckets = ...;

    private static class Entry {
        final Object key;
        Object value;
        Entry next;

        public Entry(Object key, Object value, Entry next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
        
        Entry deepCopy() {
            return new Entry(key, value, 
                    next == null ? null : next.deepCopy());
        }
    }

    @Override
    public HashTableExam clone() throws CloneNotSupportedException {
        try {
            HashTableExam result = (HashTableExam) super.clone();
            result.buckets = new Entry[buckets.length];
            for (int i = 0; i < buckets.length; i++) {
                if (buckets[i] != null) {
                    result.buckets[i] = buckets[i].deepCopy();
                }
            }
            return result;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
```
그래서 Entry 배열 안에 있는 값도 모두 새롭게 복사하는 것을 알 수 있다. 이렇게 하면 우리가 위에서 배열을 복사했던것 처럼 `모두 서로 다른 객체로 깊은 복사`로 진행되는 것을 알 수 있다<br/>

하지만 이렇게 끝을 알 수 없는 연결리스트 형태를 재귀적으로 불러내는 것은 스택 오버플로우를 발생 시킬 수 있다. 그래서 반복문을 사용해서 해결하는 방식도 있다
```java
Entry deepCopy() {
    Entry result = new Entry(key, value, next);
    for (Entry p = result; p.next != null ; p = p.next) {
        p.next = new Entry(p.next.key, p.next.value, p.next.next);
    }
    return result;
}
```
이런시그올 진행하면 된다

### 필드가 final이라면?
<strong>Cloneable 아키텍처는 `가변 객체를 참조하는 필드는 final로 선언하라`라는 것과 충돌된다</strong><br/>
그래서 복제를 할 수 있는 클래스를 만들어야 할 경우 final 한정자를 제거해야할 수 있다<br/>



## 3. `clone()`은 오롯이 복제라기 보다는 생성자의 역할을 한다고 볼 수 있다
### 부제 : 정적 팩토리 메서드를 고려하라



## ?. `clone()`과 final 키워드의 관계
