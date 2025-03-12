package chapter02.item13;

class DeepCopyExam implements Cloneable {
    int[] data = {1, 2, 3};

    @Override
    protected Object clone() throws CloneNotSupportedException {
        try {
            DeepCopyExam clone = (DeepCopyExam) super.clone();
            clone.data = this.data.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new CloneNotSupportedException(e.getMessage());
        }
    }
}

public class Main {
    public static void main(String[] args) throws CloneNotSupportedException {
        DeepCopyExam origin = new DeepCopyExam();
        DeepCopyExam clone = (DeepCopyExam) origin.clone();

        clone.data[0] = 2;

        System.out.println(origin.data[0]); // 1
        System.out.println(clone.data[0]); // 2
    }
}
