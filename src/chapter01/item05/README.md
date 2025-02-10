# Item 05 <br /> 자원을 직접 명시하지 말고 의존 객체 주입을 사용해라

만약 어떠한 클래스가 자신이 필요한 객체를 의존하는 경우 안좋은 예시를 보자
```java
class EncryptPassword {
   public String encrypt(String password) {
      return "hash_" + password;
   }
}

public class UserService {
   private EncryptPassword encryptPassword;

   public UserService() {
      this.encryptPassword = new EncryptPassword();
   }

   public void register(String username, String password) {
      String hashedPassword = encryptPassword.encrypt(password);
      System.out.println(username + ": " + hashedPassword);
   }
}
```
해당 코드는 비밀번호를 암호화 진행하여 회원가입을 하는 로직이다 <br/> 
이 코드의 문제점이 뭘까??
> 유연성과 테스트가 어렵다는 문제
> 
> 만약 EncryptPassword가 아닌 다른 EncryptPassword로 변경을 하고 싶어도 할 수 없다 <br />
> 이는 나아가 테스트시 만약 다른 EncryptPassword를 넣어서 진행하고 싶어도 진행할 수 없다 <br />
> 결국 단 하나만의 객체로 모든 것을 할 수 있을것이라는 확신이 존재해야 하는 것이다.

즉, 사용하는 자원에 따라 동작이 달라지는 클래스는 정적 유틸리티 클래스나 싱글턴 방식이 적합하지 않다 <br/>
그렇다면 어떻게 변경하면 유연한 클래스가 될 수 있을까?

```java
public class FlexibleUserService {
   private final EncryptPassword encryptPassword;

   public FlexibleUserService(EncryptPassword encryptPassword) {
      this.encryptPassword = encryptPassword;
   }

   public void register(String username, String password) {
      String hashedPassword = encryptPassword.encrypt(password);
      System.out.println(username + ": " + hashedPassword);
   }
}
```
차이점은 생성자에 인자로 받고 있다는 차이점이 있다 이렇게 필요한 자원을 인스턴스 생성 시 전달해줌으로써 테스트가 편한 유연한 클래스가 되는 것이다



