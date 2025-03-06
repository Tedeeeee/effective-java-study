package chapter02.item10;

import java.util.Objects;

class EqualsConsistencyPerson {
    private String name;

    public EqualsConsistencyPerson(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EqualsConsistencyPerson that = (EqualsConsistencyPerson) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        int hashCode = Objects.hashCode(name);
        return Objects.hash(name);
    }
}

public class EqualsConsistency {
    public static void main(String[] args) {
        EqualsConsistencyPerson p1 = new EqualsConsistencyPerson("John");
        EqualsConsistencyPerson p2 = new EqualsConsistencyPerson("John");

        System.out.println(p1.equals(p2)); // true

        p2.setName("John Call");

        System.out.println(p1.equals(p2)); // false
    }
}
