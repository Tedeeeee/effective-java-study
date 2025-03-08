package javaOOPStudy.rock_paper_scissors;

import java.util.Random;

public class Computer {
    private RockScissorPaper rockScissorPaper;

    public Computer() {
    }

    // 랜덤 안에서 아쉽...
    public void readyForGame() {
        Random random = new Random();
        int typeNumber = random.nextInt(1, 4);

        validation(typeNumber);
        this.rockScissorPaper = RockScissorPaper.createFromNumberForComputer(typeNumber);
    }

    public void validation(int typeNumber) {
        if (typeNumber < 1 || typeNumber > 3) {
            throw new IllegalArgumentException("[ERROR] 컴퓨터가 올바른 값을 만들지 못했습니다");
        }
    }

    // get 사용 아쉽...
    public String match(Player person) {
        return rockScissorPaper.matches(person.getRockScissorPaper());
    }

    public String getRockScissorPaper() {
        return rockScissorPaper.getType();
    }
}
