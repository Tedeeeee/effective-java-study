package javaOOPStudy.rock_paper_scissors;

import java.util.Scanner;

public class Controller {
    private final Scanner console;
    private final View view;

    public Controller(Scanner console, View view) {
        this.console = console;
        this.view = view;
    }

    public void play() {
        Computer computer = new Computer();
        Player player = new Player();
        Refree refree = new Refree();

        while(playerReady(player)) {
            playingSituation(computer);
            checkPlay(refree, computer, player);
        }
        view.resultGameStats(refree);
    }

    private boolean playerReady(Player player) {
        view.playerInput();
        String playerInput = console.nextLine();

        if ("끝".equals(playerInput)) {
            return false;
        }

        player.readyForPlay(playerInput);
        return true;
    }

    private void playingSituation(Computer computer) {
        computer.readyForGame();
        view.computerType(computer);
    }

    private void checkPlay(Refree refree, Computer computer, Player player) {
        String result = refree.checkPlay(computer, player);
        view.checkPlayResult(result);
    }
}
