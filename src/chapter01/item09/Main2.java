package chapter01.item09;

import java.io.IOException;
import java.io.InputStream;

public class Main2 {
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
