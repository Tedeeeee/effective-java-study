package chapter01.item08;

public class Adult {
    public static void main(String[] args) throws Exception {
        try (Room myRoom = new Room(5)) {
            System.out.println("안녕~");
        }
    }
}
