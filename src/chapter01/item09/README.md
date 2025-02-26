# Item 09. try-finally 보다 try-with-resources를 사용하라

`InputStream, OutputStream, DBConnection`등 자바의 라이브러리에는 close()메소드를 통해 닫아야하는 자원이 많다<br/>
이런 자원들은 메모리를 사용하기 때문에 사용을 완료하면 모두 정리해주어야 한다<br/>
하지만 GC는 실행 시점이 보장되지 않기 때문에 자원이 언제까지 열려있을지 모르기 때문에 개발자가 바로 닫아주는 것이 좋다.

그렇다면 자원을 사용하고 바로 닫아주는 방법에는 무엇이 있을까??

### 1. try-finally 를 사용한 자원 정리
```java
public static void main(String[] args) {
    Connection conn = null;

    try {
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/c", "root", "");
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        if (conn != null) {
            try {
                // close에서 발생하는 예외 잡기
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
```
DB를 연결하는 자원은 GC가 직접 닫아주지 못한다 그래서 finally를 사용하여 개발자가 직접 닫아주어야 한다<br/>
Java의 GC는 Heap 메모리를 정리하는 것이지 OS 자원(파일, 소켓, DB 커넥션 등)은 해제하지 못하기 때문이다

또한 여기서 다른 문제는 자원을 두개 이상 다룬다면 어떻게 되는 걸까 
```java
static void copy(String src, String dest) throws IOException {
    InputStream in = new FileInputStream(src);
    try {
        OutputStream out = new FileOutputStream(dest);
        try {
            byte[] buffer = new byte[1024];
            int n;
            while ((n = in.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
        } finally {
            out.close();
        }
    } finally {
        in.close();
    }
}
```
파일을 복사하는 로직이다. 보이는대로 2개 이상의 리소스를 사용할 수 있지만 이는 큰 문제가 있다
>FileOutputStream(dest); 에서 문제가 발생하면 out.close()가 실행되고 밖에 있는 in.close()도 실행되지 않는다<br/>
>심지어 FileOutputStream이 제대로 생성되지 않기 때문에 out.close()도 실행되지 않는다

<details>
    <summary>finally 안에서 예외 발생 시</summary>

```java
public class FinallyTest {
    public static void main(String[] args) {
        try {
            System.out.println("try 시작");
            try {
                System.out.println("안쪽 try 실행");
            } finally {
                System.out.println("안쪽 finally 실행");
            }
        } finally {
            System.out.println("바깥쪽 finally 실행");
        }
    }
}
```
>try 시작 <br/>
안쪽 try 실행 <br/>
안쪽 finally 실행 <br/>
바깥쪽 finally 실행 <br/>
 
만약 여기서 첫번째 finally가 예외가 발생한다면 어떻게 될까? 

>try 시작 <br/>
안쪽 try 실행 <br/>
안쪽 finally 실행 <br/>
Exception in thread "main" java.lang.RuntimeException: 안쪽 finally에서 예외 발생! <br/>

이렇게 바깥 finally가 실행되지 않는것을 알 수 있다.<br/>
물론 catch해서 해결할 수 있다 하지만 코드의 가독성이 현저하게 떨어지는 것을 볼 수 있다
```java
public class FinallyTest {
    public static void main(String[] args) {
        try {
            System.out.println("try 시작");
            try {
                System.out.println("안쪽 try 실행");
            } finally {
                System.out.println("안쪽 finally 실행");
                try {
                    throw new RuntimeException("안쪽 finally에서 예외 발생!");
                } catch (Exception e) {
                    System.out.println("예외를 잡아서 바깥쪽 finally 실행을 보장!");
                }
            }
        } finally {
            System.out.println("바깥쪽 finally 실행");
        }
    }
}
```
>try 시작 <br/>
안쪽 try 실행 <br/>
안쪽 finally 실행 <br/>
예외를 잡아서 바깥쪽 finally 실행을 보장! <br/>
바깥쪽 finally 실행 <br/>
 
결국 굉장히 안좋은 코드로 이어진다 
</details>
그래서 우리는 try-with-resources를 사용해야 한다. 해당 구조는 사용한 자원을 스스로 사용한뒤 close한다<br/> 
그래서 해당 구조는 사용되는 자원이 AutoCloseable가 구현 되어 있어야 한다. 물론 이미 자바 라이브러리를 포함한 대부분 클래스, 인터페이스는 구현되어있다<br/>

```java
static void copySecond(String src, String dest) throws IOException {
    try (
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dest)
    ) {
        byte[] buffer = new byte[1024];
        int n;
        while ((n = in.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
    }
}
```
코드가 훨씬 간결해진다 만약 이쪽에서 read와 close 모두 예외가 발생한다면 첫번째 예외가 먼저 유지되고 그 뒤의 예외들은 모두 `Suppressed Exception`에 저장되어 출력되는 것을 볼 수 있다<br/>
또한 흥미롭게도 Throwable의 getSuppressed() 메소드를 활용하면 try-with-resources 에서 발생한 모든 예외도 확인 할 수 있다

```java
public class Main {
    public static void main(String[] args) {
        try {
            copyWithTryWithResource();
        } catch (IOException e) {
            e.printStackTrace();

            for (Throwable throwable : e.getSuppressed()) {
                System.out.println("⚠️ 추가된 예외: " + throwable);
            }
        }
    }

    static void copyWithTryWithResource() throws IOException {
        try (MyBrokenInputStream mi = new MyBrokenInputStream();) {
            mi.read();
        }
    }

    static class MyBrokenInputStream extends InputStream {
        @Override
        public int read() throws IOException {
            throw new IOException("! 읽기 중 예외 발생!");
        }

        @Override
        public void close() throws IOException {
            throw new IOException("! 닫기 중 예외 발생!");
        }
    }
}

```
>java.io.IOException: ! 읽기 중 예외 발생!<br/>
>at chapter01.item09.Main2\&MyBrokenInputStream.read(Main2.java:28) <br/>
>at chapter01.item09.Main2.copyWithTryWithResource(Main2.java:21) <br/>
>at chapter01.item09.Main2.main(Main2.java:9) <br/>
>Suppressed: java.io.IOException: ! 닫기 중 예외 발생! <br/>
>at chapter01.item09.Main2$MyBrokenInputStream.close(Main2.java:33) <br/>
>at chapter01.item09.Main2.copyWithTryWithResource(Main2.java:20) <br/>
>... 1 more
> 
> // 여긴 System.out.print로 찍힌 console<br/>
>⚠️ 추가된 예외: java.io.IOException: ! 닫기 중 예외 발생!

이렇게 발생한다면 가장 먼저 발생한 예외를 제외한 추가된 예외를 확인할 수 있다.

<details>
    <summary>InputStream의 AutoCloseable</summary>

```java
public abstract class InputStream implements Closeable {
    // ...
    public void close() throws IOException {}
}

public interface Closeable extends AutoCloseable {
    public void close() throws IOException;
}

public interface AutoCloseable {
    void close() throws Exception;
}
```
실제로 AutoCloseable을 구현하도록 만든 InputStream을 상속받은 라이브러리 중 `FIleInputStream`을 보면 `close()`를 구현한 것을 볼 수 있다

```java
public class FileInputStream extends InputStream {
    
    // ...
    
    public void close() throws IOException {
        if (closed) {
            return;
        }
        synchronized (closeLock) {
            if (closed) {
                return;
            }
            closed = true;
        }

        FileChannel fc = channel;
        if (fc != null) {
            // possible race with getChannel(), benign since
            // FileChannel.close is final and idempotent
            fc.close();
        }

        fd.closeAll(new Closeable() {
            public void close() throws IOException {
                fd.close();
            }
        });
    }
}
```

</details>

#### try-with-resources 의 예외 처리
try-with-resources에서 발생한 예외를 내부에서 처리하기 위해 catch역시 사용할 수 있다

```java
public static void main(String[] args) {
    try {
        copyWithTryWithResource();
    } catch (IOException e) {
        System.out.println("🔥 첫 번째 예외 발생!");
        e.printStackTrace();

        for (Throwable throwable : e.getSuppressed()) {
            System.out.println("⚠️ 추가된 예외: " + throwable);
        }
    }
}

static void copyWithTryWithResource() throws IOException {
    try (MyBrokenInputStream mi = new MyBrokenInputStream();) {
        mi.read();
    } catch (IOException e) {
        System.out.println("⚠️ catch 블록에서 예외 처리!");
        throw e;
    }
}
```
>⚠️ catch 블록에서 예외 처리! <br/>
>🔥 첫 번째 예외 발생! <br/>
>⚠️ 추가된 예외: java.io.IOException: ! 닫기 중 예외 발생! <br/>
>java.io.IOException: ! 읽기 중 예외 발생! <br/>
>at chapter01.item09.Main2$MyBrokenInputStream.read(Main2.java:32) <br/>
>at chapter01.item09.Main2.copyWithTryWithResource(Main2.java:22) <br/>
>at chapter01.item09.Main2.main(Main2.java:9) <br/>
>Suppressed: java.io.IOException: ! 닫기 중 예외 발생! <br/>
>at chapter01.item09.Main2\$MyBrokenInputStream.close(Main2.java:37) <br/>
>at chapter01.item09.Main2.copyWithTryWithResource(Main2.java:21) <br/>
>... 1 more

기존의 코드를 살짝 손봤지만 가장 먼저 발생한 예외인 IOException을 잡는것을 보면 catch를 활용할 수 있다는 것도 알 수 있다

## 정리

이렇게 try-finally 보다 try-with-resources를 사용하는게 자원을 관리하는 면에서 훨씬 유리하다는 것을 볼 수 있다<br/>
대표적인 장점을 정리해보았다
- 두 개의 자원을 사용한다면 복잡해질 수 있는 코드가 훨씬 간단
- AutoCloseable를 구현해서 즉시 닫는것도 가능
- 예외가 두개 발생하면 try-finally는 따로 작업을 해야 모든 예외를 볼 수 있지만 try-with-resources는 그냥 볼 수 있다. 


당연히 try-finally를 사용하지말자는게 아니라 회수해야하는 자원을 사용할때는 try-with-resources가 좋다는 것을 말하고 싶다
