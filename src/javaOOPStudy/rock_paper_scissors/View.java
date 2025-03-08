package javaOOPStudy.rock_paper_scissors;

public class View {
    public void playerInput() {
        System.out.println("가위, 바위, 보 중 하나를 입력하세요 (종료: 끝):");
    }

    public void computerType(Computer computer) {
        System.out.printf("컴퓨터 : %s", computer.getRockScissorPaper());
        System.out.println();
    }

    public void checkPlayResult(String result) {
        if ("draw".equals(result)) {
            System.out.println("😐 비겼습니다!");
        } else if ("lose".equals(result)) {
            System.out.println("😂 컴퓨터가 이겼습니다!");
        } else if ("win".equals(result)) {
            System.out.println("😎 당신이 이겼습니다!");
        }
    }

    public void resultGameStats(Refree refree) {
        System.out.println("게임을 종료합니다");
        System.out.print("총 게임 수 : ");
        System.out.print(refree.getTotalCount() + "판 ");
        System.out.print("(");
        System.out.printf("승리 : %d, ", refree.getWinCount());
        System.out.printf("패배 : %d, ", refree.getLoseCount());
        System.out.printf("무승부 : %d", refree.getDrawCount());
        System.out.print(")");
    }
}
