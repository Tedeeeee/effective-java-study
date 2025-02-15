# Item 06 <br /> 불필요한 객체 생성을 피해라

우리는 코드를 작성할때 고민해야하는 중요한 사항이 몇가지 있다
> - 메모리 효율성
> - 성능 향상
> - 데이터의 일관성
> - 유지보수성

이 사항들을 고민하면서 우리가 할 수 있는 노력 중 하나가 바로 재사용성이 높은 코드를 작성하는 것이다<br/>
코드를 무분별하게 생성하여 작성하다보면 메모리 효율성이 낮아지고 그로 인해 성능의 저하까지 우려될 수 있다<br/>
코드를 작성하는 입장에서는 여기저기에서 필요할때마다 해당 객체를 생성하게되고 혹 해당 객체가 상태값을 들고 있다면 데이터의 일관성 마저 무너지게 되는 것이다<br/>

그래서 우리는 코드에서부터 해당 문제점을 해결하기위해 불필요한 객체 생성을 피하려고 한다

정말 간단하게 String을 예시로 들어보자
```java
String str1 = new String("Hi~");

String str2 = "Hi~";
```
첫번째는 Heap영역에 새로운 인스턴스로써 String이 선언될때마다 Heap메모리를 차지한다, 이런 코드가 만약 모든 String에서 사용하고 있다면 인스턴스에는 쓸데없는 인스턴스가 쌓이게 되고 그것을 정리하는 GC의 일만 증가하는 것이다<br/>
반면에 두번째 String은 굳이 Heap영역에 인스턴스를 쌓는 것이 아닌 자체적으로 관리하는 String Pool에 저장되고 이후 다른 곳에서 똑같은 단어를 생성하더라고 새로운 인스턴스로 생성하는 것이 아닌 String Pool에서 가져와서 사용하게 되는 것이다

이는 우리가 item01에서 말했던 정적 팩터리 메소드를 이용한 불필요한 객체 생성을 피할 수 있는 장점에서도 보이게 된다<br/>
기존에는 해당 객체의 기능을 사용하기 위해 무조건 생성을 해야했지만 그 과정을 내부로 돌리는 것이다
```java
Boolean b1 = new Boolean(true);
Boolean b2 = Boolean.valueOf(true);
```
이렇게 사용하면 new 연산자를 제작할 때마다 메모리에 쌓이게 되고 모두 다른 객체이기 때문에 비교하는 방법을 따로 만들어주어야 한다 <br/>
반면에 두번째는 정적 팩토리 메소드를 가지고 만들어진 값이라는 것을 알 수 있다<br/>
Boolean 내부에서 기존에 캐싱된 값을 넣어주어 값을 설정하게 된다 때문에 == 비교값을 통해 쉽게 비교가 가능하고 메모리를 또 사용하는 것이 아니기 때문에 성능또한 무리없이 사용할 수 있다

이에 대한 장점 중 생성 비용이 비싼 객체일수록 더욱 큰 장점으로 번질수 있다는 말을 한적이 있다 이런 비싼 객체라면 충분히 해당 방식을 사용하는 것을 권장하게된다<br/>
우리가 쉽게 만날 수 있는 객체 생성 비용이 비싼 객체는 바로 정규 표현식을 비교하는 Pattern 객체이다 이 객체는 회원가입을 할 때 사용자의 입력값의 유효성을 검증하기 위해 많이 사용하기도 한다<br/>
이는 String 객체에도 존재하는데 예를 보면서 이해해보자
```java
public static void main(String[] args) {
    String str = "Hi~";
    boolean match = str.matches("정규식");
}

// String 객체
public boolean matches(String regex) {
    return Pattern.matches(regex, this);
}

// Pattern 객체
public static boolean matches(String regex, CharSequence input) {
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(input);
    return m.matches();
}

// Pattern 객체
public static Pattern compile(String regex) {
    return new Pattern(regex, 0);
}
```
위에서부터 matches() 가 어떻게 사용되는지 과정을 실제로 들어가보면서 확인을 해본것이다. 결과적으로 정적 팩토리를 많이 사용하지만 결국 Pattern 객체를 새롭게 생성하고 있다는 것을 알 수 있다 <br/>
이말은 곧 String으로 만든 값으로 정규화를 검사한다면 그때마다 Pattern을 생성해서 matches() 시켜주고 있다는 뜻과 같다 그럼 재사용성이 전혀 지켜지지 못하게 되는 것이다<br/>

때문에 이 문제를 해결하는 방법 중에 가장 대표적인것은 역시 String에서 바로 matched하는 것이 아닌 Pattern 자체를 미리 캐싱해놓고 matches를 하는것이다
```java
private static final Pattern PATTERN = Pattern.compile("정규화");

public void cachedStringMatched() {
    String str = "Hi~";

    boolean matches = PATTERN.matcher(str).matches();
}
```
이렇게 사용하게 되면 해당 글자를 검증하는 과정에서 불필요한 객체 생성이 훨씬 줄어드게 되는 것이다

<details>
    <summary>객체 생성 비용이 큰 기준</summary>

#### 복잡한 연산을 수행하는 경우
- Pattern 객체는 글자의 유효성을 검증하는 복잡한 연산을 진행한다

#### Heap 영역을 많이 차지하는 경우
- 이미지를 처리하는 객체의 경우 큰 파일을 모두 메모리에 올리면서 부담이 커지기 때문에 주의

#### I/O 연산이 포함된 객체
- FileReader, Socket, Connection과 같이 IO작업이 다수 포진된 객체의 경우 

#### 네이티브 코드 호출 (JNI)
- 네이티브 코드는 OS와 직접적인 연관이 있기 때문에 JVM 바깥에서 호출이 되는 경우

#### 쓰레드 생성의 경우
- 쓰레드를 해롭게 생성하면 OS 레벨에서 새로운 스레드를 관리하게 된다

#### 내부적인 캐싱 필요
- 객체를 반복적으로 만들어 Heap 메모리에 영역을 많이 차지하게 되면서 GC의 부담이 상승될 경우


> 개인적으로 다른 것과 같은 경우 프레임워크가 스스로 관리해주는 경우가 흔하지만 내부적인 캐싱의 경우는 조금 중요하다고 생각했다 
> 우리가 캐싱이 필요한 경우가 굉장히 잦게 발생할 수 있고 어떻게 발전을 해야하는지도 고민을 해봐야 한다
</details>

<details>
    <summary>불변 객체와 캐싱</summary>

### 소주제. 반복되는 compile 캐싱하기

위에 코드를 보면 만약 다른 정규화가 추가되면 compile이 반복적으로 발생하게 된다는 것을 알 수 있다
```java
private static final Pattern PATTERN1 = Pattern.compile("정규화1");
private static final Pattern PATTERN2 = Pattern.compile("정규화2");

public void cachedStringMatched() {
    String str = "Hi~";

    boolean matches1 = PATTERN1.matcher(str).matches();
    boolean matches2 = PATTERN2.matcher(str).matches();
}
```
이또한 재사용성이 좋은 코드라고 생각하지 않는다. 그렇다면 어떻게 수정할 수 있을까?? 바로 캐싱 기능을 활용하는 것이다
```java
private static final Map<String, Pattern> PATTERNS = new ConcurrentHashMap<>();

private static Pattern getCompile(String str) {
    return PATTERNS.computeIfAbsent(str, Pattern::compile);
}

public static void main(String[] args) {
    String compileStr1 = "정규화1";
    String compileStr2 = "정규화2";

    Pattern compile1 = getCompile(compileStr1);
    Pattern compile2 = getCompile(compileStr2);

    System.out.println(compile1 == getCompile(compileStr1)); // true (캐싱됨)
    System.out.println(compile2 == getCompile(compileStr2)); // true (캐싱됨)
    System.out.println(compile1 == compile2); // false (다른 정규식)
}
```
이렇게 캐싱 기능을 활용하면 compile의 코드를 줄일 수 있는것이다.

하지만 만약 우리가 DB에 있는 대용량의 데이터인 경우 가져온 데이터를 캐싱해놓으면 조회시 성능이 크게 증가할 수 있다 그리고 해당 객체가 불변 객체라면 데이터의 일관성 역시 보장된다는 것을 알 수 있는것이다.

그러면 어떻게 할 수 있을까? 

1. 일급 컬렉션을 사용한 불변성 보장
   - 만약 Member 객체를 만들고 그것의 불변을 보장하고 싶다면 가져온 데이터를 보관하는 장소가 필요할 것이다. 
   - 하지만 이곳에서는 해당 컬렉션이 불변인것이지 안에 있는 객체는 불변이 아니다

2. 불변 객체의 생성
   - 객체의 필드를 final로 만든다
   - 그러면 진짜 불변 객체가 되는 것이다

그러면 짧게 생각해보면 기존의 JPA를 사용하지 않고 데이터가 캐싱된 값을 직접 만들고 싶다면 캐싱 전용 객체를 만들고 해당 값들을 들고 있는 일급컬렉션을 사용하는 방법이 있을것이다 <br/>
그리고 기존의 model값의 필드 값을 모두 final로 만들고 불변을 유지한다.

하지만 이는 JPA에서는 사용할 수 없다. JPA는 리플렉션을 통해 값을 매칭하고 프록시 객체와 dirty checking 등등 고려해야 할 것이 많기 때문에 그 방법이 쉽지 않다<br/>
그래서 캐싱을 지원하는 SpringCache를 사용하게 되는것 같다 거기서 진화하여 Redis를 통한 캐싱까지 진화할 수 있을것이다
</details>

하지만 위에 코드를 보면 우리가 싱글톤에서 보았던 문제 중 하나인 초기에 즉시 로딩의 문제가 생긴다. 물론 이를 수정할 수는 있지만 수정하는 과정에 코드가 굉장히 복잡해질 수 있다<br/>
심지어 그렇게 들인 노력에 비해 큰 성능 개선이 없다면 고민을 해볼 필요가 있다

## 불변이 필수?

객체를 불변으로 만드는 것이 과연 좋은 것일까? 불변의 객체는 객체를 생성할때마다 새로운 객체를 반환한다. 