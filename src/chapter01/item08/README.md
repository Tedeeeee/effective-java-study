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


사용한 리소스를 효과적으로 정리하기 위해서는 다 사용한뒤 <u>바로 정리한다는 것</u>이 중요하다.<br/>
하지만 Finalizer와 Cleaner는 모두 즉시 정리되는 것이 아니기에 자바에서는 `try-with-resources`나 `try-finally`를 사용하여 해결한다
