package chapter01.item02;

class Person2 {
    private String name;
    private int age;
    private String gender;
    private String address;
    private String phoneNumber;
    private boolean alive;

    public Person2() {}

    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setGender(String gender) { this.gender = gender; }
    public void setAddress(String address) { this.address = address; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setAlive(boolean alive) { this.alive = alive; }
}

class FreeZenPerson {
    private String name;
    private int age;
    private String gender;
    private String address;
    private String phoneNumber;
    private boolean alive;
    private boolean frozen = false;

    public void setName(String name) {
        checkFrozen();
        this.name = name;
    }

    public void setAge(int age) {
        checkFrozen();
        this.age = age;
    }

    public void setGender(String gender) {
        checkFrozen();
        this.gender = gender;
    }

    public void setAddress(String address) {
        checkFrozen();
        this.address = address;
    }

    public void setPhoneNumber(String phoneNumber) {
        checkFrozen();
        this.phoneNumber = phoneNumber;
    }

    public void setAlive(boolean alive) {
        checkFrozen();
        this.alive = alive;
    }

    public void freeze() {
        this.frozen = true;
    }

    private void checkFrozen() {
        if (frozen) {
            throw new UnsupportedOperationException("객체가 이미 얼려져 수정할 수 없습니다!");
        }
    }
}

class BuilderPerson {
    private final String name;
    private final int age;
    private final String gender;
    private final String address;
    private final String phoneNumber;
    private final boolean alive;

    public BuilderPerson(Builder builder) {
        this.name = builder.name;
        this.age = builder.age;
        this.gender = builder.gender;
        this.address = builder.address;
        this.phoneNumber = builder.phoneNumber;
        this.alive = builder.alive;
    }

    public static class Builder {
        private String name;
        private int age;
        private String gender;
        private String address;
        private String phoneNumber;
        private boolean alive;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setAge(int age) {
            this.age = age;
            return this;
        }

        public Builder setGender(String gender) {
            this.gender = gender;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder setAlive(boolean alive) {
            this.alive = alive;
            return this;
        }

        public BuilderPerson build() {
            return new BuilderPerson(this);
        }
    }
}

public class BuilderTest2 {
    public static void main(String[] args) {
        BuilderPerson builderPerson = new BuilderPerson.Builder()
                .setName("테스트")
                .setAge(18)
                .setGender("남")
                .setAddress("주소")
                .setPhoneNumber("01012345678")
                .setAlive(true)
                .build();
    }
}
