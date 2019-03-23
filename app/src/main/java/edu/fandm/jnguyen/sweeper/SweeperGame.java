package edu.fandm.jnguyen.sweeper;

import android.content.Context;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class SweeperGame {

    private static SweeperGame sweeperGame;
    private Random random = new Random();
    private int clock;
    private int timeUntilNextBomb;
    private int bombCount;
    private ArrayList<Integer> bombTimers = new ArrayList<>();
    private int mainFuseTime = 10;
    private int currentLevel = 0;

    private SweeperGame(Context context) {
        bombCount = context.getResources().getInteger(R.integer.bomb_count);

        // -1 means not exploded or ticking, 0 is exploded.
        for (int i = 0; i < bombCount; i++) {
            bombTimers.add(-1);
        }
    }

    // Return the one and only instance of SweeperGame.
    public SweeperGame getInstance(Context context) {
        if (sweeperGame == null) {
            sweeperGame = new SweeperGame(context);
        }

        return sweeperGame;
    }

    public int getBombTimer(int index) {
        return bombTimers.get(index);
    }

    public void newGame() {
        clock = 0;
        timeUntilNextBomb = 0;
    }

    public void tickOnce() {
        clock++;
        timeUntilNextBomb--;

        if (timeUntilNextBomb == 0) {
            // Find an inactive bomb.
            int index = random.nextInt(bombCount);
            while (bombTimers.get(index) != -1) {
                index = random.nextInt(bombCount);
            }

            // Activate!
            bombTimers.set(index, mainFuseTime - currentLevel);
        }
    }
}
