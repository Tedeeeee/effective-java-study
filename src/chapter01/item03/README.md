# Item 03 <br /> 생성자나 열거 타입으로 싱글턴임을 보증하라

앞서 정적 팩토리 메소드의 장점에 대해서 알아볼때 싱글톤 얘기가 잠깐 나왔었다
> p9 중
> 
> 2번째 장점 호출할 때마다 인스턴스를 새롭게 생성하지 않아도된다
> 
> 반복되는 요청에 같은 객체를 반환하여 특정 인스턴스를 살아 있게 할 수 있는 인스턴스 통제 클래스를 만들면 해당 클래스를 싱글톤으로 만들수 있다

말이 조금 어렵지만 결국 인스턴스 통제 클래스란 넓은 의미의 클래스이다 `특정 인스턴스를 만든 후 유지시킬 수 있는 클래스` 인것이다. <br/>
하나의 프로세스에 하나의 인스턴스만 존재하도록하는 싱글톤 역시 그 의미는 인스턴스 통제 클래스라는 말이다

그렇다면 싱글톤을 만드는 방법은 뭐가 있을까?

## 싱글톤을 만드는 방식 (feat. 진화 과정)
싱글톤을 아주 간단하게 만드는 방법부터 어떤 단점이 있고 해당 단점을 극복하기 위해 어떻게 수정되어왔는지 알아보자

### 1. public static final을 통한 싱글톤 (Eager Initialization)
```java
public class Singleton {
    public static final Singleton INSTANCE = new Singleton();

    private Singleton() {}
}
```
해당 클래스가 로드되는 시점에 바로 생성된다 <br/>
외부에서는 new 연산자를 통한 생성이 불가능 하기에 <code>Singleton.INSTANCE</code>를 사용해서 직접 참조한다 <br/>

> 단점 <br/>
> - 해당 클래스가 무조건 사용된다는 것이 확신되어야 한다
> - 생성 비용이 크다면 오히려 무거운 성능이 발생할 수 있다
> - 메소드가 아닌 직접 접근이기 때문에 추후 변경 가능성을 고려 할 수 없다 

코드는 추후에 변경될 가능성이 매우 크다. 그렇다면 이를 먼저 해결해보자 
### 2. private static final을 통한 싱글톤 (Eager Initialization)
```java
public class SingletonPrivate {
    private static final SingletonPrivate INSTANCE = new SingletonPrivate();

    private SingletonPrivate() {}

    public static SingletonPrivate getInstance() {
        return INSTANCE;
    }
}
```
기존에는 직접적인 참조를 통해 싱글톤임을 보장하고 진행했지만 이번엔 메소드를 통해 참조를 진행한다 <br/>
메소드를 통하면 추후에 해당 클래스를 싱글톤이 아니도록 하거나 혹은 다른 객체를 반환하거나와 같은 변경 사항에도 쉽게 대처할 수 있다 <br/>

하지만 단점은 여전히 존재한다
> 단점 <br/>
> - 해당 클래스가 무조건 사용된다는 것이 확신되어야 한다
> - 생성 비용이 크다면 오히려 무거운 성능이 발생할 수 있다

생성 비용이 크다면 클래스가 로드되는 시점에도 굉장히 큰 비용이 소모되고 또한 메모리도 많이 소비된다 해당 단점을 해결해보자
### 3. 느린 초기화을 통한 싱글톤 (Lazy Initialization)
```java
public class SingletonLazy {
    private static SingletonLazy instance;

    private SingletonLazy() {}

    public static SingletonLazy getInstance() {
        if (instance == null) {
            instance = new SingletonLazy();
        }
        return instance;
    }
}
```
이번엔 final로 작성하지 않고 초기에 값을 할당해주지 않고 해당 클래스를 메소드로 호출을 해야만 인스턴스가 생성하게 되도록 만들었다<br/>
메소드를 자세히 보면 null 체크를 한번 함으로써 싱글톤을 보장한다 <br/>
이렇게 클래스가 로드될 당시가 아닌 누군가 메소드를 통해 참조를 했을때 클래스가 메모리에 올라가기 때문에 훨씬 비용적인 부담이 적어지는 것을 알 수 있다

> 단점
>
> - 멀티 스레드에 불안정하다

초기에 봤던 단점은 모두 사라진것을 알 수 있다 하지만 굉장히 중요한 멀티스레드 환경에서 문제가 발생했다
왜냐하면 우리는 멀티스레드의 환경에서 코딩을 하는데 멀티스레드에서 안전하지 못한 클래스가 있다는 것은 위험하다 

그래서 해당 객체를 멀티 스레드 환경에서 안전하게 하기 위해 Java의 synchronized를 사용하려고 한다
```java
public class SingletonLazy {
    private static SingletonLazy instance;

    private SingletonLazy() {}

    public static synchronized SingletonLazy getInstance() {
        if (instance == null) {
            instance = new SingletonLazy();
        }
        return instance;
    }
}
```
코드만 보면 매우매우 간단하다 그저 메소드 앞에 synchronized를 사용해서 동기 방식을 도입시켜 멀티 스레드로 부터 안전하게 작업하게 된다 <br/>
하지만 이또한 문제는 존재한다. 
바로 비효율적(성능 저하)이라는 것이다. 앞으로 정말 많은 멀티스레드가 작업을 하기위해 접근하지만 앞의 대기 순서가 있다면 꼼짝없이 이곳에 갇혀서 작업을 기다려야 하기 때문이다.<br/>
어떻게 해결 할 수 있을까?
### 4. Double-Checked Locking
```java
public class SingletonDoubleCheck {
    private static volatile SingletonDoubleCheck instance;
    
    private SingletonDoubleCheck() {}
    
    public static SingletonDoubleCheck getInstance() {
        if (instance == null) {
            synchronized (SingletonDoubleCheck.class) {
                if (instance == null) {
                    instance = new SingletonDoubleCheck();
                }
            }
        }
        return instance;
    }
}
```
권장되고 있는 싱글톤 제작 방식이다 <br/>
메소드 자체적으로 걸린 동기 작업을 메소드안에서 꼭 필요한 작업만 동기작업을 거는 방식으로 변환
<details>
    <summary>코드 진행 방식</summary>

1. <code>if (instance == null)</code>
   - 모든 스레드가 해당 메소드에 들어온다면 일단 체크를 한다
2. <code>synchronized (Singleton.class)</code>
   - 가장 먼저 들어온 스레드가 동기화블록에 들어가고 나머지는 모두 차단
      - 여기서 혹여 같이 들어온 스레드가 있더라도 대기를 한다 
3. <code>if (instance == null)</code>
   - 다시 한번 더 확인하여 실제로 없는가를 확인하여 기존에 동시에 들어온 애들은 또 한번 걸러진다
   - 여기서 Double-Checking 의 특징이 나온다
4. instance쪽에 volatile 사용
   - volatile을 통해 인스턴스가 초기화 되기 전까지 다른 스레드가 읽지 못하게 한다
   - volatile이란?
     - Java의 키워드 중 하나, 변수의 값을 여러 스레드에서 안전하게 읽고 쓸 수 있도록 보장하는 역할을 한다
</details>

이로써 코드로만 본다면 멀티스레드의 안정성과 코드의 유연함, 무조건 생성되어야 한다는 모든 단점은 해결했다<br/>
하지만 이또한 단점이 존재한다
> 단점
> 
> - 코드가 복잡하다 -> 두번의 if, volatile 키워드 학습
> - 만약 버전이 낮다면 volatile은 존재하지 않는다

그래서 또다른 해결 방법을 찾아보자
### 5. 정적 내부 클래스
사실 이 방법을 가장 권장하고 있다
```java
public class SingletonBillPugh {
    private SingletonBillPugh() {}
    
    private static class Holder {
        private static final SingletonBillPugh INSTANCE = new SingletonBillPugh();
    }
    
    public static SingletonBillPugh getInstance() {
        return Holder.INSTANCE;
    }
}
```
얼핏봐도 갑자기 간단해진 코드에 당황할 수 있다 <br/>
놀랍게도 정적 내부 클래스 하나로 모든 장점을 가져갈 수 있다 어떻게 이 코드만으로 해결할 수 있었을까?

- 코드의 간결함 
- 정적 내부 클래스 선언을 통한 Lazy Initialization 자동 지원
  - 정적 내부 클래스가 메모리에 올라가는 순간은 `SingletonBillPugh.getInstance()` 를 했을때이다 결국 필요할때 딱 올라간다는 말이다
- 멀티 스레드에 안전
  - 2번째 예시로 든 private static final을 보면 해당 클래스가 로드되는 시점에 new를 하는 것을 볼 수 있는데 내부 클래스는 참조되기 전까지는 클래스 로드가 실행되지 않는다
  - 결국 `return Holder.INSTANCE`를 하는 당시에 클래스 로드가 되고 이때 new하여 만들어진다는 것이다
  - 그럼 결국 메소드를 여러개의 스레드가 접근하는건 똑같지 않나? 하겠지만 이는 JVM이 해결해준다. JVM은 클래스 로딩을 할때 단 한번 생성되는 것을 보장해주기 때문이다.
- volatile 해결
  - 해당 키워드가 필요없어진다 어차피 딱 한개의 클래스만 생성되는 것을 JVM이 보장하기에 접근해도 문제가 없다
### 6. Enum 방식을 사용한 싱글톤 제작
이제 조금 심오해지는 방식으로 진행된다. 
여기서 문제는 바로 Reflection 공격과 Serialization(직렬화) 공격이다
#### 1. Reflection 공격
```java
public class ReflectionSingletonTest {
    public static void main(String[] args) throws Exception {
        SingletonBillPugh instance = SingletonBillPugh.getInstance();

        Constructor<SingletonBillPugh> constructor = SingletonBillPugh.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        SingletonBillPugh reflectionInstance = constructor.newInstance();

        // 결과 : false == 싱글톤 깨짐
        System.out.println(instance == reflectionInstance);
    }
}
```
위의 코드는 reflection을 통해 private에 접근하고 생성자를 통해 아예 새로운 인스턴스를 뽑아내게 되는 것이다 이렇게 싱글톤은 깨지게 된다<br/>
#### 2. Serialization(직렬화) 공격
```java
public class SerializationSingletonTest {
    public static void main(String[] args) throws Exception {
        SingletonBillPugh instance = SingletonBillPugh.getInstance();

        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("singleton.obj"));
        out.writeObject(instance);
        out.close();

        ObjectInputStream in = new ObjectInputStream(new FileInputStream("singleton.obj"));
        SingletonBillPugh newInstance = (SingletonBillPugh) in.readObject();
        in.close();

        // 결과 : false == 싱글톤 깨짐
        System.out.println(instance == newInstance);
    }
}
```
위의 reflection 보다는 비교적 알기 쉬운 코드이다. 그냥 해당 객체를 직렬화를 진행하고 다시 바로 역직렬화를 하여 객체를 새롭게 읽어내면 새로운 객체가 생성되고 역시 기존에 만들어놓은 객체와는 다르게 된다 <br/>
물론 해결방법이 없는것은 아니다 직렬화를 하고 역직렬화를 하면서 새로운 객체를 내보내지 않도록 하기 위해 `readResolve()`라는 메소드가 존재한다 이 메소드를 싱글톤 클래스에 넣어주면 된다
```java
public class SingletonBillPugh implements Serializable {
    private SingletonBillPugh() {}

    private static class Holder {
        private static final SingletonBillPugh INSTANCE = new SingletonBillPugh();
    }

    public static SingletonBillPugh getInstance() {
        return Holder.INSTANCE;
    }

    protected Object readResolve() {
        return getInstance();
    }
}
```
이렇게 만들어주면 되지만 역시 이는 개발자가 깜빡하고 놓쳐서 작성하지 않으면 나중에 직렬화 공격에 따른 치명적인 오류까지 연결될 수 있다<br/>

이렇게 두 개의 치명적인 단점이 발생하는 것을 확인할 수 있는데 이를 한방에 해결 할 수 있는 방법이 바로 Enum 클래스 이다

```java
public enum SingletonEnum {
    INSTANCE;
}
```
끝이다. 이렇게 작성하면 해결이 된다.
이는 Enum의 특징을 이용한 해결 방법이다. 
- Reflection을 이용해서 생성자를 호출할 수 없다
- 직렬화, 역직렬화에도 항상 같은 인스턴스를 유지한다
- JVM이 클래스 로딩에 자동으로 싱글톤을 보장한다 때문에 동기작업이 따로 필요하지 않는다

하지만 Enum은 무조건 초기에 클래스로써 로딩되기 때문에 Lazy 작업은 진행되지 않는다. 때문에 생성 비용이 크면 메모리 문제는 발생 할 수 있다 <br/>
또한 Enum 클래스는 Java에서 내부적으로 java.lang.Enum<T> 라는 클래스를 상속받고 있기 때문에 다른 클래스를 상속 받을 수 없다.<br/>

## 결론
결국 싱글톤을 안전하게 만드는 방법은 정적 내부 클래스와 Enum, 두가지로 추려질 수 있었다 <br/>
둘 중 어느것을 사용하느냐는 상황에 맞게 사용하는 것이 매우 중요할 것 같다<br/>
각각의 장단점을 보고 상황에 맞게 골라 작업을 하는 것이 좋을 것 같다

| 문제                 | 정적 내부 클래스 방식       | Enum 방식          |
|--------------------|--------------------|------------------|
| 멀티스레드 안전성     | ✅ JVM이 보장          | ✅ JVM이 보장        |
| Lazy Initialization | ✅ 메소드 호출시 생성       | ❌ 클래스 로딩 시 즉시 생성 |
| Reflection 공격 방어 | ❌ private 접근 가능    | ✅ reflection 불가  |
| Serialization 안전성 | ❌ readResolve() 필수 | ✅ 자동 직렬화 진행      |
| 코드의 간결성         | ✅ 아주 간단            | ✅ 더 간단           |
<details>
    <summary>여담</summary>
어차피 Spring 쓰면 빈이 싱글톤을 관리하긴해...
그래도 알고 쓰는거랑 모르고 쓰는건 다르니께...ㅎㅎ
</details>
