package javaOOPStudy.rock_paper_scissors;

public class Player {
    private RockScissorPaper rockScissorPaper;

    public Player() {
    }

    public void readyForPlay(String type) {
        validation(type);
        this.rockScissorPaper = new RockScissorPaper(type);
    }

    public void validation(String type) {
        if (!("가위".equals(type) || "바위".equals(type) || "보".equals(type))) {
            throw new IllegalArgumentException("[ERROR] 올바른 값을 입력하세요! (가위, 바위, 보)");
        }
    }

    public RockScissorPaper getRockScissorPaper() {
        return rockScissorPaper;
    }
}
