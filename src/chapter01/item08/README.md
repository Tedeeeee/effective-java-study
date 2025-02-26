# Item 08. finalizer와 cleaner 사용은 피해라

앞서 메모리와 자원 정리를 개발자가 해주어야만 메모리 누수의 문제가 발생하지 않는다고 전했다<br/>
물론 GC가 정리를 해주지만 그것만으로는 부족한 경우(리소스 정리 등)가 있었다.<br/>

> #### fianlizer(JDK 1.0 ~ 1.2)
> 
> finalizer는 객체가 더이상 사용되지 않을 때 정리 작업을 수행할 수 있도록 설계되었다
 
### finalizer의 문제점
- GC 실행 시점을 예측할 수 없다 -> 이로 인해 리소스 정리 시간이 너무 늦어질 수 있다
- Finalizer Queue의 이유로 GC에서 한번 더 처리하고 별도의 스레드처리로 불필요한 메모리 점유 시간 증가
- `finalize()` 내부의 예외 발생을 JVM이 무시함

> #### cleaner(JDK 9)
> 
> Cleaner는 Runnable을 등록하여 사용하기 때문에 별도의 스레드로 정리 작업을 수행하고 register()를 통해 정리 로직을 등록하기 때문에 명확한 방식으로 정리

## finalizer와 cleaner의 공통적인 문제
### 1. 수행 시점의 문제
```java
public class CleanerEx {
    private static Cleaner cleaner = Cleaner.create();

    static class ManagedFile {
        private final File file;
        private final FileWriter writer;
        private final Cleaner.Cleanable cleanable;

        private static class State implements Runnable {
            private final FileWriter writer;
            private final File file;

            State(FileWriter writer, File file) {
                this.writer = writer;
                this.file = file;
            }

            @Override
            public void run() {
                try {
                    System.out.println("파일 정리 중....");
                    writer.close();

                    if (file.exists()) {
                        file.delete();
                        System.out.println("파일 삭제 완료 : " + file.getName());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public ManagedFile(String fileName) throws IOException {
            this.file = new File(fileName);
            this.writer = new FileWriter(file);
            this.cleanable = cleaner.register(this, new State(writer, file));
        }

        public void write(String data) throws IOException {
            writer.write(data);
            writer.flush();
        }
    }

    public static void main(String[] args) throws IOException {
        new ManagedFile("test.txt").write("Hello, Cleaner!");
        System.gc();
    }
}
```
`System.gc()`가 GC를 바로 실행할 것이라고 예상했지만 실제로 실행되지 않았다 <br/>
그래서 run은 실행되지 않고 리소스(File)의 정리도 이뤄지지 않는다. 물론 이뤄지겠지만 정확히는 언제 이뤄질지 모른다는 것이다

### 2.수행 여부의 문제
finalizer와 cleaner는 실행 시점 뿐 아니라 실행 여부도 보장하지 못한다.<br/>
GC가 언제 실행될지 모른다는건 실행이 안되고 종료될 가능성도 존재한다는 것이다
- 파일의 삭제 : 중요한 파일이 삭제되지 못하고 남을 수 있다
- DB 변경 사항 : DB의 변경 사항이 반영되지 않을 수 있다 
  - lock을 해제하는 작업을 맡겼을때 해제를 보장할 수 없다.

이렇게 상태가 불안정하게 수정되면 다른 스레드가 접근했을때 문제가 발생 할 수 있다.<br/>
상태를 영구적으로 수정하는 작업에서는 절대 finalizer나 cleaner를 의존하면 안된다.<br/>
GC를 실행 시켜줄수도 있는 메소드는 있지만 말그대로 실행 시켜 줄수도 있는 메소드일뿐 실제로 호출하는 것은 아니다.

##### finalizer 동작 중 발생한 예외 처리
`해당 예외는 무시된다`<br/>
만약 리소스를 정리하는 과정에서 예외가 발생하더라도 JVM은 이를 무시한다. 예외를 무시한다는건 아주 큰 문제로 번진다<br/>
물론 cleaner는 자신의 스레드를 따로 사용하기 때문에 영향이 덜하지만 그래도 완벽한 해결은 되지 못한다

### 3. 심각한 성능 문제
AutoClosable을 구현한 객체보다 finalizer를 사용하면 객체를 생성하고 파괴하는데 훨씬 오래 걸린다.<br/>
물론 finalizer보다 좀 더 나은 cleaner는 비교적 빠른 시간이 걸리지만 결국 느린것은 변함없다

결국 성능 문제가 발생한다

### 해결방법?
결국 수많은 문제가 있는 finalizer와 cleaner를 대신 해줄 묘안은 있다.<br/>
AutoCloseable을 구현하고 실제 클라이언트에서 사용 종료시 close() 메소드를 호출하는 것이다.
`자세한 내용은 item09 에서...`

### 왜 있을까?
1. 자원의 소유자가 close메서드를 호출하지 않는 것에 대비한 안전망 역할
   - 비록 해당 메소드가 바로 호출될지는 알 수 없지만 그래서 위험하지만 아예 안하는것보다는 낫다
2. 네이티브 피어와 연결된 객체
   - 네이티브 피어는 자바 객체가 아니라서 GC가 회수하지 못한다. 때문에 개발자가 직접 회수 할 수 있도록 설정해줘야 한다.


## 결과
사실 이 단원은 나는 어렵고 이해하기 쉽지 않았다. 하지만 결국 중요한것은 리소스를 사용하고 그를 정리하여 메모리 누수를 잡아주어야 한다<br/>
이를 위해 리소스 정리하는 로직이 필요하고 Java는 이를 finalizer와 cleaner를 통해 전달해왔다

하지만 둘은 모두 불안정하고 심지어 finalizer는 아예 없어져버렸다<br/>
그나마 cleaner는 자신의 스레드가 따로 있기 때문에 finalizer에 비해 나은 성능을 보이기에 아직 존재한다<br/>
또한 네이티브 피어를 사용하는 곳에서는 사용해주면 좋다

하지만 결국 모두 try-with-resource에서 정리된다.



사용한 리소스를 효과적으로 정리하기 위해서는 다 사용한뒤 <u>바로 정리한다는 것</u>이 중요하다.<br/>
하지만 Finalizer와 Cleaner는 모두 즉시 정리되는 것이 아니기에 자바에서는 `try-with-resources`나 `try-finally`를 사용하여 해결한다
