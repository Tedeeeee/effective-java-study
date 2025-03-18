package chapter02.item14;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class PhoneNumber implements Comparable<PhoneNumber> {

    private final int areaCode;
    private final int prefix;
    private final int lineNumber;

    public PhoneNumber(int areaCode, int prefix, int lineNumber) {
        this.areaCode = areaCode;
        this.prefix = prefix;
        this.lineNumber = lineNumber;
    }

    private static final Comparator<PhoneNumber> COMPARATOR =
            Comparator.comparingInt((PhoneNumber pn) -> pn.areaCode)
                    .thenComparingInt(pn -> pn.prefix)
                    .thenComparingInt(pn -> pn.lineNumber);
    @Override
    public int compareTo(PhoneNumber o) {
        return COMPARATOR.compare(this, o);
    }

    public static void main(String[] args) {
        Set<String> s = new HashSet<>();
        Collections.addAll(s, args);
        System.out.println(s);
    }
}
