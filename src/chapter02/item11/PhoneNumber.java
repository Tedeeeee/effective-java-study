package chapter02.item11;

import java.util.*;

public class PhoneNumber {
    private int first;
    private int second;
    private int third;

    public PhoneNumber(int first, int second, int third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PhoneNumber that = (PhoneNumber) o;
        return first == that.first && second == that.second && third == that.third;
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(first);
        result = 31 * result + Integer.hashCode(second);
        result = 31 * result + Integer.hashCode(third);
        return result;
        // return Objects.hash(first, second, third);
    }
}

class Main {
    public static void main(String[] args) {
        Map<PhoneNumber, String> map = new HashMap<>();
        map.put((new PhoneNumber(707, 867, 5309)), "제니");

        System.out.println(map.get((new PhoneNumber(707, 867, 5309))));

        List<PhoneNumber> list = new ArrayList<>();
        list.add(new PhoneNumber(707, 867, 5309));

        System.out.println(list.contains(new PhoneNumber(707, 867, 5309)));
    }
}
