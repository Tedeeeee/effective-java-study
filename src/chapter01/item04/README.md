# Item 04. 인스턴스화를 막으려거든 private 생성자를 사용해라

우리가 코드를 작성하다보면 정적 필드와 정적 메소드만 필요한 상황이 발생할 수 있다<br/>
예를 들면 `Array, Math, Collections`와 같이 굳이 상태를 유지하거나 값을 저장해놓을 필요가 없다면 사용하는 경우가 존재한다<br/>
또한 상수의 값을 보관하는 경우에도 자주 사용하는 것을 볼 수 있다. 상수는 모두 final static이 붙은 값으로 아마 관리하려고 할 것이다

하지만 단순히 정적 메소드나 정적 필드만을 담은 클래스는 객체 지향적이지 못하다
- 객체 지향적이지 못하다
  - 각 클래스의 상태와 행동을 캡슐화하고 인스턴스를 통해 이를 조작한다
  - 객체를 생성하지 않고 호출이 가능하고 이는 절차적인 코드와 유사해질 가능성이 존재한다
- 테스트가 어려워진다
  - 정적 메서드는 의존성을 주입하기 어렵다. 때문에 단위 테스트의 어려움이 존재한다
- 유연성이 떨어진다
  - 상속과 다형성을 활용하기 쉽지 않고 이 때문에 기능을 확장하거나 대체할 방법이 제한적이다
- 전역 상태로 인한 예기치 못한 문제 발생
  - 여러 클래스에서 공유하여 사용하게 되면 급작스런 데이터 변경, 동시성 문제가 발생할 수 있다
  - 멀티스레드에서는 특히 더욱 조심해서 다뤄야 한다.

위와 같은 단점이 존재하기 때문에 정적 필드 혹은 메서드로만 구성되어 있는 클래스를 만들땐 그 사용처를 고민하여 사용해야한다

정적 클래스는 위에서 말했다시피 유틸 성격을 띄는 클래스로써 제작되는 경우가 많다. 
>### 유틸리티 클래스
> 
> 객체의 상태를 유지하지 않고, 공통적으로 사용되는 기능을 제공하는 클래스
 
위의 설명에서도 보았듯이 중요한 포인트는 상태를 유지하지 않고 공통적으로 사용된다. 때문에 어디서 굳이 인스턴스도 생성하지 않고 사용하는 경우가 많다<br/>
그렇다면 우리는 해당 클래스가 인스턴스화 할 수 없도록 생성자를 막기 위해 꼭 유틸리티 클래스의 생성자를 명시해주어야 한다<br/>
그렇지 않으면 컴파일러가 자동으로 기본 생성자를 만들어 public 생성자가 생성되어 다른 사람들이 자칫 잘못된 사용을 할 수 있다

## 추상 클래스를 통한 인스턴스화 막기?

생성자를 굳이 작성하지 않아도 인스턴스를 막는 방법 중 해당 클래스를 추상클래스로 만드는 방법도 존재한다
```java
abstract class Animal {
    abstract void makeSound();
    
    void sleep() {
        System.out.println("잠자는 중....");
    }
}

public class Main {
    public static void main(String[] args) {
        Animal animal = new Animal() {};
    }
}
```
이런식으로 만들어놓아도 `new Animal()`은 작동하지 않는다. 추상클래스이기 때문이지만 물론 이렇게 단편적으로만 봐서는 그 단점을 모른다

<strong>추상 클래스는 직접적으로는 생성자를 통해 인스턴스를 생성할 수 없지만 하위 클래스에서는 객체를 생성하는 것이 가능하다</strong><br/>
추상 클래스의 본질이 뭘까. 바로 하위 클래스에게 상속을 받게 하여 기능을 확장시키는 것에 본질을 둔다
```java
abstract class Animal {
    abstract void makeSound();

    void sleep() {
        System.out.println("잠자는 중....");
    }
}

class Dog extends Animal {

    @Override
    void makeSound() {
        System.out.println("멍멍!");
    }
}

public class Main {
    public static void main(String[] args) {
        Animal dog = new Dog();
        dog.sleep();
        dog.makeSound();
    }
}
```
결국 그냥 정적으로 쓰이게 만들고 싶고 외부에서 인스턴스화를 막고 싶은 것인데 오히려 마치 상속을 해야하는 클래스 같아지는 것이다

결국 방법은 간단하다. 개발자가 직접 private 생성자를 명시하여 컴파일러가 직접 기본 생성자를 작성하지 못하도록 하면된다
```java
class StringUtils {

    private StringUtils() {};

    public static String removeCommas(String str) {
        return str.replaceAll(",", "");
    }
}

public class Main2 {
    public static void main(String[] args) {
        StringUtils stringUtils = new StringUtils();

        StringUtils.removeCommas("1,2,3,4,5");
    }
}
```
위처럼 Utils로 사용하고자 하는 객체에 private 생성자를 통해 막으면 자연스럽게 사용하는 곳에서 인스턴스를 하려하면 에러로 발생하게 되며 불필요한 객체 생성을 막을 수 있다


- 주의점
  - 개발자의 입장에서는 클래스는 존재하는데 호출이 안되는 것, 이유가 궁금할 수 있으니 주석이 필요할 수 있다
  - private 생성자를 가진 클래스는 하위 클래스가 상속하는 것을 막는다. 이것도 알아두어야 한다. 
