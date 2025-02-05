package chapter01.item02;

abstract class Person3 {
    private final String name;
    private final int age;

    protected Person3(Builder<?> builder) {
        this.name = builder.name;
        this.age = builder.age;
    }

    abstract static class Builder<T extends Builder<T>> {
        private String name;
        private int age;

        public T setName(String name) {
            this.name = name;
            return self();
        }

        public T setAge(int age) {
            this.age = age;
            return self();
        }

        // 하위 클래스에서 반드시 구현
        protected abstract T self();

        public abstract Person3 build();
    }
}

class Student extends Person3 {
    private final String school;

    public Student(Builder builder) {
        super(builder);
        this.school = builder.school;
    }

    public static class Builder extends Person3.Builder<Builder> {
        private String school;

        public Builder setSchool(String school) {
            this.school = school;
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public Student build() {
            return new Student(this);
        }
    }
}

public class BuilderTest3 {
    public static void main(String[] args) {
        Student student = new Student.Builder()
                .setName("김철수")
                .setAge(18)
                .setSchool("서울고등학교")
                .build();

    }
}
