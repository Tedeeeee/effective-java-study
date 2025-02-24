package chapter01.item08;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.Cleaner;

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
