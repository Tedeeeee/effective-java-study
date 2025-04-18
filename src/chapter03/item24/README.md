# Item 24. 멤버 클래스는 되도록 static으로 만들라

## ✅ 중첩 클래스
- 클래스 내부에 정의된 또 다른 클래스
- 자신을 감싸고 있는 바깥 클래스 내부에서만 사용되는 것을 의도

### 🔹 중첩 클래스 종류
1. 정적 멤버 클래스 (✔️ static 사용)
2. 비정적 멤버 클래스 (❌ static 없음)
3. 익명 클래스
4. 지역 클래스

> 이중 정적 멤버 클래스를 제외한 나머지 클래스는 모두 inner class(내부 클래스)라고 한다

## 🔍 정적 멤버 클래스와 비정적 멤버 클래스의 차이

### ✅ 1. 대표적인 정의와 기본적인 차이

| 구분             | 정적 멤버 클래스                | 비정적 멤버 클래스                |
|----------------|--------------------------|---------------------------|
| 선언 방식          | `static class Inner {}`  | `class Inner {}`          |
| 바깥 클래스 인스턴스 참조 | ❌ 참조하지 않음                | ✅ 암묵적으로 참조                |
| 생성 방식          | `new Outer.Inner()`      | `new Outer().new Inner()` |
| 메모리 구조         | 독립적인 클래스처럼 존재            | 바깥 클래스 인스턴스에 종속           |

### ✅ 2. 메모리 및 참조 관련
```java
public class Outer {
    private int outerField = 10;

    class Inner {
        final Outer this$0; // 바깥 클래스 인스턴스에 대한 숨은 참조

        Inner(Outer outer) {
            this$0 = outer;
        }

        public void print() {
            System.out.println(this$0.outerField);
        }
    }
}
```
- 비정적 멤버 클래스는 바깥 클래스의 인스턴스에 대한 숨은 참조를 가진다
  - 이로 인해 바깥 클래스 인스턴스가 GC 대상이 되지 않고 이는 메모리 누수의 원인이 될 수 있다(item07에서 다룬 내용)
  - 직접 `new Outer().new Inner()`와 같은 방식으로 만드는 경우도 있지만 관계가 복잡하고 메모리 공간도 차지하고 생성 시간도 오래걸린다
- 정적 멤버 클래스는 그런 참조가 없기 때문에 메모리 누수 위험이 없다

> 비정적 멤버 클래스는 바깥 클래스 인스턴스를 직접 사용하는 경우에만 사용해야 한다

### ✅ 3. 사용 목적
#### 📌 정적 멤버 클래스
- 바깥 클래스의 인스턴스에 의존하지 않는 구성요소
- 바깥 클래스와 논리적으로 관련이 되어있지만 동작은 독립적이다
- 예시 : `Map.Entry`, `Enum`, `Builder class`

#### 📌 비정적 멤버 클래스
- 바깥 클래스의 필드나 메서드에 접근해야 하는 경우
- 예시
  - UI 컴포넌트에서 리스너로 구현
  - 내부 상태 공유가 필요한 경우
  - `어댑터(Adapter)` 역할을 하는 클래스

> 어댑터 패턴
> 
> - 한 클래스의 인터페이스를 클라이언트가 기대하는 다른 인터페이스로 변환해주는 역할
> - 비정적 멤버 클래스는 바깥 클래스의 데이터를 쉽게 활용할 수 있어서 어댑터 구현에 적합
> - `Map의 ketSet()`, `entrySet()`등 컬렉션 뷰

## 🔍 익명 클래스
### 개념
- 이름이 없다
- 선언과 동시에 인스턴스를 생성
- 인터페이스 구현이나 추상 클래스 상속을 일회성으로 구현할 때 사용

### 특징
- 바깥 클래스 인스턴스를 암묵적으로 참조할 수 있다
- 지역 변수는 `final`또는 `effectively final`일때만 접근 가능
  - `effectively final` : 실제로는 변경되지 않는 변수이지만 `final` 키워드는 붙지 않는 변수
- 내부적으로는 바이트코드상 별도 클래스로 컴파일이 된다

### 단점
- 이름이 없기 때문에 재사용이 불가능
- 여러 인터페이스 구현이 불가, 하나의 클래스 상속 또는 하나의 인터페이스만 구현 가능
- 표현식 중간에 등장하기 때문에 길어진 코드는 오히려 가독성 저하때문에 복잡한 로직에는 부적합
- 정식 클래스를 구현하거나 람다로 많이 대체

## 🔍 지역 클래스

### 개념
- 메서드, 생성자, 혹은 초기화 블록 안에서 선언된 이름이 있는 클래스
- 로컬 변수처럼 특정 블록 내에서만 존재하고 사용할 수 있는 클래스

### 📌 특징
- 선언된 블록 내에서만 사용이 가능하다
- 지역 변수를 선언할 수 있는 곳이라면 어디든 사용가능하고 유효 범위도 지역변수와 같다
- 익명 클래스와 다른 점은 이름이 있다
- 정적 멤버는 가질 수 없다.
- 블록 내부의 지역 변수는 `final`또는 `effectively final`일 대만 접근이 가능

> 비정적 멤버 클래스와 마찬가지로, 바깥 클래스 인스턴스가 존재해야 참조가 가능하다<br/>
> 즉, 비정적 문맥에서만 바깥 인스턴스를 참조할 수 있다


## ✅ 결론
- 중첩 클래스 중에서 정적 멤버 클래스는 메모리 누수 위험도 없고 독립성이 뛰어나기 때문에, 가능하면 `static`을 붙여 만들자
- 비정적 멤버 클래스는 바깥 클래스의 인스턴스를 진짜로 필요로 할때만 사용해야한다
- 익명 클래스와 지역 클래스는 간단한 일회성 로직이나 `adapter`, `wrapper`용도로 유용하지만 남용하면 가독성이 저하되고 유지보수의 어려움이 있다
