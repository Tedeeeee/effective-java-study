package javaOOPStudy.rock_paper_scissors;

public class RockScissorPaper {
    String type;

    public RockScissorPaper(String type) {
        this.type = type;
    }

    public static RockScissorPaper createFromNumberForComputer(int typeNumber) {
        String type = "";
        if (typeNumber == 1) {
            type = "가위";
        } else if (typeNumber == 2) {
            type = "바위";
        } else if (typeNumber == 3) {
            type = "보";
        }

        return new RockScissorPaper(type);
    }

    public String getType() {
        return type;
    }

    public String matches(RockScissorPaper player) {
        if ("가위".equals(type)) {
            return scissorBase(player.type);
        } else if ("바위".equals(type)) {
            return rockBase(player.type);
        }
        return paperBase(player.type);
    }

    public String scissorBase(String playerType) {
        if ("가위".equals(playerType)) {
            return "draw";
        } else if ("바위".equals(playerType)) {
            return "win";
        }
        return "lose";
    }

    public String rockBase(String playerType) {
        if ("바위".equals(playerType)) {
            return "draw";
        } else if ("보".equals(playerType)) {
            return "win";
        }
        return "lose";
    }

    public String paperBase(String playerType) {
        if ("보".equals(playerType)) {
            return "draw";
        } else if ("가위".equals(playerType)) {
            return "win";
        }
        return "lose";
    }
}
