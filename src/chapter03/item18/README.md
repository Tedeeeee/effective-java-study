# Item 18. 상속보다는 컴포지션을 사용하라

### 상속의 장단점
상속은 코드를 재사용할 수 있는 강력한 수단이다. 하지만 최선의 수단은 아니다.

일반적인 `구체 클래스를 패키지 경계를 넘어 다른 패키지의 구체 클래스를 상속`하는 일은 위험하다

> 책에서 `상속`
> 
> 클래스가 다른 클래스를 확장하는 용도 `implement`를 사용한 상속과는 다르다

## 상속은 캡슐화를 깨뜨린다???

클래스가 다른 클래스를 확장 용도로 상속을 이용해서 구현이 된다는 것은 상위 클래스가 어떻게 구현이 되는가에 따라 하위 클래스에도 영향을 줄 수 있다는 것

즉, 상위 클래스를 설계할때는 확장을 충분히 고려하고 문서화 하지 않는다면 이후 큰 문제로 이어질 수 있다는 것이다

### 캡슐화 붕괴 1탄
> HashSet을 사용하는데 원소가 총 몇 번 add() 되었는지 알고 싶은 클래스 생성

```java
public class InstrumentedHashSet<E> extends HashSet<E> {
    private int addCount = 0;

    public InstrumentedHashSet() {}

    public InstrumentedHashSet(int initCap, float loadFactor) {
        super(initCap, loadFactor);
    }

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }

    public int getAddCount() {
        return addCount;
    }
}

public static void main(String[] args) {
    InstrumentedHashSet<String> s = new InstrumentedHashSet<>();
    s.addAll(List.of("a", "b", "c"));

    System.out.println(s.getAddCount()); // 6
}
```
해당 코드는 원소가 몇 번 추가되었는지 알기위해 만들어졌지만 `addAll()`을 사용하면 오히려 2배로 count된 다는 것을 알 수 있다<br/>
문제는 size로 추가하고 `add()`를 다시 부르면서 발생하는 건데 이를 해결하는 방법이 뭐가 있을까?

#### 1. `addAll()` 재정의 없애기
당장은 문제를 해결 할 수 있다. 하지만 이는 HashSet의 `addAll()`이 `add()`를 기본으로 꼭 구현이 되어 있어야 한다는 뒷받침이 필요하다<br/>
이는 상위 클래스의 내부 구현에 의존적으로 개발을 하게 된다 

#### 2. `addAll()` 재정의 방식 수정
컬렉션을 순회하면서 원소 하나당 add 메서드를 한번만 호출하는 것이다.
```java
// 기존 코드
@Override
public boolean addAll(Collection<? extends E> c) {
    addCount += c.size();
    return super.addAll(c);
}

// 수정 코드
@Override
public boolean addAll(Collection<? extends E> c) {
    boolean modified = false;
    for (E e : c) {
        if (add(e)) {
            modified = true;
        }
    }
    return modified;
}
```
하지만 이런 방식은 결국 다시 구현 코드가 복잡해지고 시간도 더들고, 오류를 발생 시킬수 있는 상황도 있다<br/>
심지어 하위 클래스에서 사용할 수 없게 상위 클래스에서 private 필드로 뭐 하나 만들었다면 사용도 못한다.

### 캡슐화 붕괴 2탄
만약 하위 클래스에서 재정의하고자 하는 메소드에 특정 조건이 달린다면 상위 클래스에 데이터를 허용되지 않도록 할 수 있다<br/>
하지만 다른 곳에서 누군가 HashSet클래스를 상속받아 메소드를 구현한다면 특정 조건을 걸 수 없기 때문에 허용되지 않는 데이터가 들어갈 수 있다는 것이다 
이런 문제가 대표적으로 발생한 곳이 바로 Hashtable과 Vector에서 있었다

## 메서드 재정의의 위험성
기존에 `extends`를 사용한 상속에서는 새로운 메서드를 추가하면 괜찮다 라고 생각했지만 결국 이또한 문제가 있을 수 있다<br/>
그럴 확률은 매우매우 낮지만 하위 클래스에서 추가한 메서드가 상위 클래스에서 정말 똑같이 추가된다면 컴파일 오류로까지 이어 질 수 있다.

## 컴포지션을 사용한 해결 방법
기존의 클래스를 확장하지 않고 중간 단계의 새로운 클래스를 만들고 private 필드로 기존 클래스를 인스턴스로써 가지고 있는 것이다<br/>
> 컴포지션 설계
> 
> 기존 클래스가 새로운 클래스의 구성요소로 사용된다

새 클래스의 메서드들을 전달 메서드로 사용하는 `forwarding method`를 사용하면 기존 클래스의 내부 구현 방식을 피할 수 있고 기존 클래스의 메소드 추가에도 영향을 받지 않을 수 있다

```java
// 컴포지션을 사용한 기존 클래스를 바라보는 클래스
public class ForwardingSet<E> implements Set<E> {
    private final Set<E> s;
    
    public ForwardingSet(Set<E> s) {this.s = s;}
    
    @Override
    public int size() {return 0;}

    @Override
    public boolean isEmpty() {return s.isEmpty();}

    @Override
    public boolean contains(Object o) {return s.contains(o);}

    @Override
    public Iterator<E> iterator() {return s.iterator();}

    @Override
    public Object[] toArray() {return s.toArray();}

    @Override
    public <T> T[] toArray(T[] a) {return s.toArray(a);}

    @Override
    public boolean add(E e) {return s.add(e);}

    @Override
    public boolean remove(Object o) {return s.remove(o);}

    @Override
    public boolean containsAll(Collection<?> c) {return s.containsAll(c);}

    @Override
    public boolean addAll(Collection<? extends E> c) {return s.addAll(c);}

    @Override
    public boolean retainAll(Collection<?> c) {return s.retainAll(c);}

    @Override
    public boolean removeAll(Collection<?> c) {return s.removeAll(c);}

    @Override
    public void clear() {s.clear();}
}

// 하위 클래스의 변경
public class InstrumentedHashSet<E> extends ForwardingSet<E> {
    private int addCount = 0;

    public InstrumentedHashSet(Set<E> s) {
        super(s);
    }

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }

    public int getAddCount() {
        return addCount;
    }
}
```
기존의 상속 방식은 쿠페 클래스에서 각각을 따로 확장하고 지원하고 싶은 상위 클래스의 갱성자 각각에 대응하는 생성자를 별도로 정의해줘야 한다.<br/>
하지만 컴포지션 방식은 한 번 정의해두면 어떤 Set의 구현체라도 계측할 수 있고 기존 생성자도 여전히 사용할 수 있다

### 래퍼 클래스
앞서 코드에서 다른 Set 인스턴스를 감싸고 있는 ForwardingSet클래스를 래퍼 클래스라고 한다<br/>
또한 다른 Set에 기능을 덧씌운다는 점에서 `데코레이터 패턴`이라고 한다.

#### 래퍼 클래스의 단점
> 래퍼 클래스는 콜백 프레임워크와 어울리지 않는다<br/>
 
콜백 프레인워크는 보통 상속을 통해 특정 메서드를 오버라이드하고 그 메서드가 프레임워크 내부에서 호출을 기다린다.
즉, 프레임워크가 내부에서 호출하는 콜백 구조에서는 우회되어 실행되기 때문에 우리가 구현한 재정의한 메서드는 무시된다.


## 상속은 버려야 하는가?
상속은 반드시 상위 클래스가 `진짜` 하위 타입인 상황에서만 쓰여야 한다.<br/>
즉, A가 상위 클래스이고 B가 하위 클래스라면 B는 A여야만 한다. 그렇지 않으면 그냥 컴포지션의 방식을 사용하는 것이 좋다

컴포지션을 사용해야할 상황에서 상속을 사용하면 내부 구현을 노출하게 되는 꼴과 같은 말이다. 그 결과 하위 클래스의 성능은 영원히 제한된다.  

> 혹시 상속을 선택했다면...
> 
> 상속을 사용하기 이전 항상 고민해야한다.
> 하위 클래스의 API에 아무런 결함이 없는가?<br/> 
> 만약 있다면 이게 상위까지 올라가도 상관없는가<br/>

이와 같은 고민을 하고 만약 이렇게 됨에도 문제가 없을 경우에는 상속을 사용해도 상관 없다. 
