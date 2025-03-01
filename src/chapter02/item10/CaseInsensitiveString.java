package chapter02.item10;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class CaseInsensitiveString {
    private final String s;

    public CaseInsensitiveString(String s) {
        this.s = Objects.requireNonNull(s);
    }

//    @Override
//    public boolean equals(Object o) {
//        if (o instanceof CaseInsensitiveString) {
//            return s.equalsIgnoreCase(((CaseInsensitiveString) o).s);
//        }
//
//        if (o instanceof String) {
//            return s.equalsIgnoreCase((String) o);
//        }
//
//        return false;
//    }


    @Override
    public boolean equals(Object o) {
        return o instanceof CaseInsensitiveString &&
                ((CaseInsensitiveString) o).s.equalsIgnoreCase(s);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(s);
    }
}

class CaseInsensitiveStringTest {
    public static void main(String[] args) {
        CaseInsensitiveString cis = new CaseInsensitiveString("abc");
        String s = "abc";

        System.out.println(cis.equals(s)); // false
        System.out.println(s.equals(cis)); // false

        List<CaseInsensitiveString> list = new ArrayList<>();
        list.add(cis);

        System.out.println(list.contains(s));
    }
}
