package chapter01.item07;

interface Callback {
    void onComplete(String result);
}

class AsyncTask {
    void doSomethingAsync(Callback callback) {
        new Thread(() -> {
            try {
                System.out.println("2초 대기");
                Thread.sleep(2000);
                callback.onComplete("Hello World");
            } catch (InterruptedException e) {
                callback.onComplete(e.getMessage());
            }
        }).start();
    }
}

public class CallbackExam {
    public static void main(String[] args) {
        AsyncTask asyncTask = new AsyncTask();
        asyncTask.doSomethingAsync(result -> System.out.println("콜백 실행 : " + result));
    }
}
