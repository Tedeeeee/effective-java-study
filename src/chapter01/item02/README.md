# Item 02 <br /> 생성자에 매개변수가 많다면 빌더를 고려하라

정적 팩토리와 생성자는 모두 매개변수가 많아지는 것에 대한 적절한 대응을 하기 어렵다 <br />
```java
class Person {
    private String name;
    private int age;
    private String gender;
    private String address;
    private String phoneNumber;
    private boolean alive;
}
```
만약 위와 같이 여러가지의 필드를 가지고 있는 클래스를 사용하려면 어떻게 해야할까?

객체를 생성할 때 필수로 받는 매개변수와 필수가 아닌 매개변수가 존재할 수 있다 <br />
즉, 조건에 따라 다양한 생성자 종류가 나타날 수 있다는 것이다.

## 대응법 1. 점층적 생성자 패턴

이를 위해 개발자는 <u><strong>점층적 생성자 패턴</strong></u>을 즐겨 사용하고는 했다. <br />
점층적 생성자 패턴은 기존의 모든 인자를 받아 생성하는 생성자를 만들고 특정 매개변수만을 받아 제작하는 생성자를 모두 만들어준다
```java
class Person {
    private String name;
    private int age;
    private String gender;
    private String address;
    private String phoneNumber;
    private boolean alive;

    public Person(String name, int age, String gender, String address, String phoneNumber, boolean alive) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.alive = alive;
    }

    public Person(String name, int age) {
        this(name, age, "unknown", "unknown", "unknown", true);
    }

    public Person(String name, int age, String gender) {
        this(name, age, gender, "unknown", "unknown", true);
    }

    public Person(String name, int age, String gender, String address) {
        this(name, age, gender, address, "unknown", true);
    }

    public Person(String name, int age, String gender, String address, String phoneNumber) {
        this(name, age, gender, address, phoneNumber, true);
    }
}
```
이렇게 일일히 하나씩 만들어 놓으면 나중에는 원하는 매개변수만을 사용해 기본값을 셋팅할 수 있다
```java
Person person = new Person("테스트", 12, "남", "주소");
```
이렇게 만들어주면 되지만 이때도 결국 문제는 발생한다

### 단점
> 만약 매개변수가 수없이 많아지는 상황이 오게된다면 단점이 더욱 극명하게 나타난다
> 1. 각 값의 의미를 알아야한다
> 2. 매개변수가 몇 개인지 주의
> 3. 타입이 같은 매개변수를 연달아 받는 경우
> 4. 순서의 문제
>
> 이런 다양한 문제로 인해 잘못된 생성에도 불구하고 이를 런타임시에 다 만들어지고 오작동시에 알 수 있게 된다는 것이다 

## 대응법 2. 자바 빈즈 패턴

선택적 매개변수가 많을 경우를 대비하기 위한 방법 중 두번째로 <u><strong>자바 빈즈 패턴</strong></u>이 있다<br />
이것은 초기에 아무것도 없는 생성자를 만들고 만들어진 값에 setter라는 메소드를 통해 해당 객체의 값을 만들어주는 것이다

```java
class Person {
    private String name;
    private int age;
    private String gender;
    private String address;
    private String phoneNumber;
    private boolean alive;

    public Person() {}

    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setGender(String gender) { this.gender = gender; }
    public void setAddress(String address) { this.address = address; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setAlive(boolean alive) { this.alive = alive; }
}
public class BuilderTest {
    public static void main(String[] args) {
        Person person = new Person();
        person.setName("테스트");
        person.setAge(18);
        person.setGender("남");
        person.setAddress("주소");
        person.setPhoneNumber("01012345678");
        person.setAlive(true);
    }
}
```
이렇게 main에서 값이 없는 빈 생성자를 만들고 모든 값을 그때그때 값을 셋팅(setter)하는 것으로 점층적 생성자 패턴의 단점들을 극복할 수 있다

하지만 역시나 이도 분명한 단점은 존재한다

### 단점
> setter 메소드를 호출하여 객체를 완성시키면 <u><strong>객체의 일관성</strong></u>이 무너지게 된다 <br/>
객체가 생성될 당시에는 아무값도 설정되지 않아서 사용할 수 없어야 하지만 그 제재를 하기 쉽지 않다 <br/>
때문에 잘못된 코드 작업으로 인해 오류가 발생하면 런타임시에 디버깅을 사용해도 찾기 쉽지 않을 수 있다
>
> 또한 일관성이 무너지는 것은 곧 클래스를 불변으로 만들 수 없다는 뜻과도 같다 <br/>
> 변할 수 있는 객체는 스레드 안전성을 보장할 수 없기 때문에 프로그래머의 추가적인 작업이 필요하다 

물론 이를 극복하기 위한 얼리고(freezing)기법이 존재한다

<details>
  <summary>얼리고(FreeZing) 기법</summary>

```java
class FreeZenPerson {
    private String name;
    private int age;
    private String gender;
    private String address;
    private String phoneNumber;
    private boolean alive;
    private boolean frozen = false;

    public void setName(String name) {
        checkFrozen();
        this.name = name;
    }

    public void setAge(int age) {
        checkFrozen();
        this.age = age;
    }

    public void setGender(String gender) {
        checkFrozen();
        this.gender = gender;
    }

    public void setAddress(String address) {
        checkFrozen();
        this.address = address;
    }

    public void setPhoneNumber(String phoneNumber) {
        checkFrozen();
        this.phoneNumber = phoneNumber;
    }

    public void setAlive(boolean alive) {
        checkFrozen();
        this.alive = alive;
    }

    public void freeze() {
        this.frozen = true;
    }

    private void checkFrozen() {
        if (frozen) {
            throw new UnsupportedOperationException("객체가 이미 얼려져 수정할 수 없습니다!");
        }
    }
}
```
</details>
얼리고 기법은 값이 완성되기 전까지 다른곳에서 사용할 수 없게 하고 값이 완성이 되면 해당 객체를 얼려서 다른곳에서 사용만 하고 변경에 대해서는 막아놓는 기법인 것이다 이를 통해 불변을 보장할 수 있다

하지만 이또한 결국 freeze 메소드를 호출해줬는지 확인하는 것이 오롯이 개발자의 몫이기 때문에 런타임 오류시 설정을 안해주면 문제가 발생한다

## 대응법 3. 빌더 패턴

점층적 생성자 패턴의 안전성과 자바 빈즈 패턴의 가독성을 모두 겸비한 패턴 <u><strong>빌더 패턴</strong></u>을 소개하려고 한다

먼저 기존의 로직이 빌더 패턴을 통해 어떻게 생성되는지 확인을 해보자

```java
class BuilderPerson {
    private final String name;
    private final int age;
    private final String gender;
    private final String address;
    private final String phoneNumber;
    private final boolean alive;

    public BuilderPerson(Builder builder) {
        this.name = builder.name;
        this.age = builder.age;
        this.gender = builder.gender;
        this.address = builder.address;
        this.phoneNumber = builder.phoneNumber;
        this.alive = builder.alive;
    }

    public static class Builder {
        private String name;
        private int age;
        private String gender;
        private String address;
        private String phoneNumber;
        private boolean alive;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setAge(int age) {
            this.age = age;
            return this;
        }

        public Builder setGender(String gender) {
            this.gender = gender;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder setAlive(boolean alive) {
            this.alive = alive;
            return this;
        }

        public BuilderPerson build() {
            return new BuilderPerson(this);
        }
    }
}

public class BuilderTest2 {
    public static void main(String[] args) {
        BuilderPerson builderPerson = new BuilderPerson.Builder()
                .setName("테스트")
                .setAge(18)
                .setGender("남")
                .setAddress("주소")
                .setPhoneNumber("01012345678")
                .setAlive(true)
                .build();
    }
}
```
빌더 패턴을 통해 객체를 생성하는 것을 간단히 설명하면
1. 만들고자 하는 클래스에 Builder라는 내부 정적 클래스를 선언한다
    - 정적 클래스는 우리가 사용하고자 하는 클래스를 완성하고 반환하면서 일관성을 유지한다
    - 또한 일관성이 유지된 객체를 기존의 모든 매개변수를 가지고 있는 생성자를 통해 생성하기 때문에 불변성까지 적용할 수 있다
2. 각 필드들을 셋팅하는 것은 모두 메소드(setter)로 전환하여 해당 값을 설정하도록 한다
   - 이를 통해 순서가 중요해지지 않는다
   - 또한 각각의 메소드만 불러내기때문에 연쇄적인 호출이 가능하다
3. build 메소드를 호출하여 마침표를 찍어 객체 제작을 완료한다
   - 위에서 말했다시피 실제로 반환하고자 하는 클래스의 AllArgsConstructor와 같은 역할을 하는 생성자로 반환한다

### 빌더 패턴은 계층적으로 설계된 클래스와 함께 사용하기에 좋다

자바는 상속 구조를 사용하여 계층적 설계를 진행한다 하지만 이 과정에서 자식들은 본인들이 만들어질때마다 부모의 생성자도 신경을 써야하기 때문에 오히려 코드가 복잡해질 수 있다 <br />
또한 만약 부모 혹은 자식이 매개변수가 많다면 그것또한 굉장히 복잡한 코드가 길게 늘어지게 되는 것이다 

이를 해결하기 위해 자식은 부모의 Builder를 상속받아 구현한다
```java
abstract class Person3 {
    private final String name;
    private final int age;

    protected Person3(Builder<?> builder) {
        this.name = builder.name;
        this.age = builder.age;
    }

    abstract static class Builder<T extends Builder<T>> {
        private String name;
        private int age;

        public T setName(String name) {
            this.name = name;
            return self();
        }

        public T setAge(int age) {
            this.age = age;
            return self();
        }

        // 하위 클래스에서 반드시 구현
        protected abstract T self();

        public abstract Person3 build();
    }
}

class Student extends Person3 {
    private final String school;

    public Student(Builder builder) {
        super(builder);
        this.school = builder.school;
    }

    public static class Builder extends Person3.Builder<Builder> {
        private String school;

        public Builder setSchool(String school) {
            this.school = school;
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public Student build() {
            return new Student(this);
        }
    }
}
```
이런식으로 부모는 값을 받을 준비를 하는 것이다 물론 해당 코드는 부모를 직접 생성할 일이 없다는 전제로 만들어진것이다
혹여 같은 부모를 통해 무엇을 만들더라도 그 사람의 기본적인 특성(부모)는 변하지 않기 때문에 모두 만들어주어야 하는 것이다

그렇다면 또다른 아주 중요한 코드가 생긴다
```java
  Student student = new Student.Builder()
          .setName("김철수")  // Person 반환
          .setAge(18)        // Person 반환
          .setSchool("서울고등학교") // Student 반환
          .build();
```
자세히 보면 코드는 Student의 Builder를 사용해서 Student라는 타입으로 변환되는 것을 확인할 수 있다 <br />
하지만 위의 코드에서 Student에게 name과 age는 찾아볼 수 없고 부모에게 찾아볼 수 있다

그것은 위의 setName과 setAge의 반환타입이 T로써 즉, 여기선 Person이라서 Student의 Builder는 부모의 Builder와 본인의 Builder모두 반환할 수 있던 것이다

이를 <u><strong>공변 반환 타입</strong></u>이라고 한다
- 하위 클래스의 메서드가 부모 클래스의 반환 타입보다 더욱 구체적인 타입으로 반환할 수 있도록 하는 것
- 이로써 빌더는 더욱 자연스러운 메서드 체이닝이 진행된다

덕분에 불변적이고 가독성이 좋은 코드가 또다시 탄생하게 되는 것이다

## 빌더의 단점
빌더는 매개변수가 별로 없다면 점층적 생성자 패턴보다도 코드가 장황해져서 오히려 가독성을 떨어뜨릴수 있다 하지만 실제로 코드를 작성하다보면 4개로 끝나는 객체는 생각보다 많지 않다 그래서 왠만하면 그냥 빌더로 시작하는 것이 좋다
