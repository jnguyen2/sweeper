package edu.fandm.jnguyen.sweeper;

import android.content.Context;

import java.util.ArrayList;
import java.util.Random;

public class SweeperGame {

    public static final String TAG = "sweeper.sweepergame";

    private static SweeperGame sweeperGame;
    private Random random = new Random();
    private int lives;
    private int bombsDefused;
    private int bombCount;
    private ArrayList<Integer> bombTimers = new ArrayList<>();
    private ArrayList<Integer> notExplodedIndices = new ArrayList<>();

    private SweeperGame(Context context) {
        bombCount = context.getResources().getInteger(R.integer.bomb_count);
    }

    // Return the one and only instance of SweeperGame.
    static SweeperGame getInstance(Context context) {
        if (sweeperGame == null) {
            sweeperGame = new SweeperGame(context);
        }

        return sweeperGame;
    }

    boolean isGameOver() {
        return notExplodedIndices.isEmpty() || lives <= 0;
    }

    int getBombCount() {
        return bombCount;
    }

    int getScore() {
        return bombsDefused;
    }

    boolean bombIsActive(int index) {
        return bombTimers.get(index) > 0;
    }

    boolean bombExploded(int index) {
        return !notExplodedIndices.contains(index);
    }

    void defuseBomb(int index) {
        if (bombExploded(index)) {
            return;
        }

        if (bombIsActive(index)) {
            bombTimers.set(index, -1);
            bombsDefused++;
        } else if (!bombExploded(index)) {
            bombTimers.set(index, 0);
            lives--;
            notExplodedIndices.remove(Integer.valueOf(index));
        }
    }

    void newGame() {
        // Reset lives and score.
        lives = 3;
        bombsDefused = 0;

        // Clear the lists.
        bombTimers.clear();
        notExplodedIndices.clear();

        for (int i = 0; i < bombCount; i++) {
            // -1 means not exploded or ticking, 0 is exploded.
            bombTimers.add(-1);

            // Also, keep track of indices still functional.
            notExplodedIndices.add(i);
        }
    }

    void tick() {
        int desired = random.nextInt(notExplodedIndices.size() / 2);
        // Activate an inactive bomb.
        while (desired > 0) {
            Integer index = notExplodedIndices.get(random.nextInt(notExplodedIndices.size()));
            if (!bombIsActive(index)) {
                bombTimers.set(index, 5);
            }
            desired--;
        }

        for (int i = 0; i < bombCount; i++) {
            int timeRemaining = bombTimers.get(i);
            bombTimers.set(i, timeRemaining - 1);

            // The bomb just exploded.
            if (timeRemaining - 1 == 0) {
                lives--;
                notExplodedIndices.remove(Integer.valueOf(i));
            }
        }
    }
}
