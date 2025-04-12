# Item 25. 톱레벨 클래스는 한 파일에 하나만 담아라

## ✅ 핵심 내용
자바에서는 하나의 파일에 클래스를 여러개 정의 할 수 있지만, 그렇게 하는 것은 권장되지 않는다.<br/>
그 이유는 예측 불가능한 동작, 유지보수의 어려움, 버그 유발 가능성 때문이다

### 🔍 예
만약 다른 톱레벨 클래스 2개를 같은 파일에 만들면 무슨일이 발생할까?
```java
// Main.java 클래스
public class Main {
  public static void main(String[] args) {
    System.out.println(Utensil.NAME + Dessert.NAME);
  }
}

// Utensil.java 파일
class Utensil {
  static final String NAME = "pan";
}

class Dessert {
  static final String NAME = "cake";
}

// Desert.java 파일
class Dessert {
  static final String NAME = "pot";
}

class Utensil {
  static final String NAME = "pie";
}
```
#### 컴파일 순서와 결과
- `javac Main.java Utensil.java`
  - `Utensil.java` 파일의 정의가 반영됨
  - 결과: `pancake`
- `javac Dessert.java Main.java`
  - `Dessert.java` 파일의 정의가 반영됨
  - 결과: `potpie`

🧠 즉, 컴파일 순서에 따라 어떤 정의가 .class 파일로 저장될지 달라진다!

### 🧾 문제
1. 클래스 중복 정의로 인한 혼란
   - 동일한 이름의 톱레벨 클래스가 여러 파일에 정의되고 컴파일 순서에 따라 어떤 정의가 `.class`에 반영될지 모른다
2. 예측 불가능한 컴파일 결과
   - 어떤 소스가 최종적으로 반영되는지 개발자는 명확히 알기 어려워진다
   - 이는 빌드가 자동화가 되면 더욱 혼란스러워진다
3. 모듈화, 관리, 문서화도 악영향
   - 클래스가 각각 별도 파일에 없으면 IDE나 문서 생성 도구에서 클래스가 누락되거나 탐색하기 어렵다
   - `public`이 아닌 클래스는 숨겨진 의존성이 될 수 있다
4. 협업과 유지보수가 어렵다
   - 여러 개발자가 동시에 파일을 수정하면 충돌의 위험이 있다.
   - 클래스의 정의가 정확히 어떤 파일에 들어있는지 찾기 어려워 디버깅이 어려워진다


### ✅ 해결
- 탑레벨 클래스는 반드시 각각의 `.java`파일에 분리해서 정의
- 한 파일에 담고 싶다면 정적 멤버 클래스를 사용해보자
  - 가독성과 캡슐화가 좋아진다
  - `private static class`로 선언시(`item24`) 접근 범위를 제한할 수 있다 

## 🏁 결론
> `.java`파일 하나에는 톱레벨 클래스 하나만 두고<br/>
> 유지보수, 협업, 예측 가능한 빌드를 위해 반드시 지켜야 할 규칙

