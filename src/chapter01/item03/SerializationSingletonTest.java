package chapter01.item03;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializationSingletonTest {
    public static void main(String[] args) throws Exception {
        SingletonBillPugh instance = SingletonBillPugh.getInstance();

        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("singleton.obj"));
        out.writeObject(instance);
        out.close();

        ObjectInputStream in = new ObjectInputStream(new FileInputStream("singleton.obj"));
        SingletonBillPugh newInstance = (SingletonBillPugh) in.readObject();
        in.close();

        // 결과 : false == 싱글톤 깨짐
        System.out.println(instance == newInstance);
    }
}
