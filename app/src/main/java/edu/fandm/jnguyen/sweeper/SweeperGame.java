package edu.fandm.jnguyen.sweeper;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

public class SweeperGame {

    public static final String TAG = "sweeper.sweepergame";

    private static SweeperGame sweeperGame;
    private Random random = new Random();
    private int timeUntilNextBomb;
    private int bombCount;
    private int bombsRemaining;
    private HashSet<Integer> bombsExploded;
    private ArrayList<Integer> bombTimers = new ArrayList<>();
    private int mainFuseTime = 10;
    private int currentLevel = 1;

    private SweeperGame(Context context) {
        bombCount = context.getResources().getInteger(R.integer.bomb_count);
    }

    // Return the one and only instance of SweeperGame.
    public static SweeperGame getInstance(Context context) {
        if (sweeperGame == null) {
            sweeperGame = new SweeperGame(context);
        }

        return sweeperGame;
    }

    public boolean isGameOver() {
        return bombsRemaining <= 0;
    }

    public int getBombCount() {
        return bombCount;
    }

    public boolean bombIsActive(int index) {
        return bombTimers.get(index) > 0;
    }

    public boolean bombExploded(int index) {
        return bombTimers.get(index) == 0;
    }

    public void defuseBomb(int index) {
        if (bombTimers.get(index) == 0) {
            return;
        }

        if (bombIsActive(index)) {
            bombTimers.set(index, -1);
        } else if (!bombExploded(index)){
            bombTimers.set(index, 0);
            bombsRemaining--;
        }
    }

    public void newGame() {
        bombsRemaining = bombCount;
        timeUntilNextBomb = 0;

        // -1 means not exploded or ticking, 0 is exploded.
        bombTimers = new ArrayList<>();
        for (int i = 0; i < bombCount; i++) {
            bombTimers.add(-1);
        }
    }

    public void tick() {
        timeUntilNextBomb--;

        if (timeUntilNextBomb <= 0) {
            // Find an inactive bomb.
            Integer index = 0;
            if (bombsRemaining > 1) {
                index = random.nextInt(bombCount);
                while (bombsRemaining > 0 && (bombIsActive(index) || bombExploded(index))) {
                    index = random.nextInt(bombCount);
                }
            } else {
                for (int i = 0; i < bombCount; i++) {
                    if (!bombExploded(i)) {
                        index = i;
                        break;
                    }
                }
            }

            // Activate!
            bombTimers.set(index, mainFuseTime - currentLevel);
            timeUntilNextBomb = random.nextInt(mainFuseTime / 2);
        }

        for (int i = 0; i < bombCount; i++) {
            if (bombIsActive(i)) {
                bombTimers.set(i, bombTimers.get(i) - 1);
                if (bombExploded(i)) {
                    bombsRemaining--;
                }
            }
        }
    }
}
