package javaOOPStudy.rock_paper_scissors;

public class Refree {
    private int totalCount;
    private int winCount;
    private int loseCount;
    private int drawCount;

    public String checkPlay(Computer computer, Player player) {
        String matchResult = computer.match(player);
        playResultCheck(matchResult);
        return matchResult;
    }

    private void playResultCheck(String matchResult) {
        totalCount++;

        if ("draw".equals(matchResult)) {
            drawCount++;
        }
        if ("win".equals(matchResult)) {
            winCount++;
        }
        if ("lose".equals(matchResult)) {
            loseCount++;
        }
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getWinCount() {
        return winCount;
    }

    public int getLoseCount() {
        return loseCount;
    }

    public int getDrawCount() {
        return drawCount;
    }
}
