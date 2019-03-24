package edu.fandm.jnguyen.sweeper;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class SweeperGame {

    public static final String TAG = "sweeper.sweepergame";

    // The one and only instance of the game.
    private static SweeperGame sweeperGame;

    // Random generator.
    private Random random = new Random();

    // Time before bomb explodes (in ticks).
    private int bombTime = 10;

    // Time where no bombs can be activated.
    private int safeTime = 6;

    // The values below are reset in newGame().
    private int bombsDefused;
    private int clock;
    private int lives;

    // Other stuff.
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

    boolean isSafeTime() {
        return clock < safeTime;
    }

    int getClock() {
        return clock;
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
        // Reset counters.
        clock = 0;
        lives = 3;
        bombsDefused = 0;

        // Clear the lists.
        bombTimers.clear();
        notExplodedIndices.clear();

        for (int i = 0; i < bombCount; i++) {
            // -1 means not exploded or setTicking, 0 is exploded.
            bombTimers.add(-1);

            // Also, keep track of indices still functional.
            notExplodedIndices.add(i);
        }
    }

    void tick(HashSet<Integer> inputs) {
        // Tick tock.
        clock++;

        // Take no inputs and make no changes to the game during safe time.
        // This is when the user is starting the game.
        if (isSafeTime()) {
            inputs.clear();
            return;
        }

        // Defuse all bombs.
        for (Integer i : inputs) {
            defuseBomb(i);
        }

        // Clear the input set.
        inputs.clear();

        // Make the number of bombs steadily increase.
        int desired = Math.min(notExplodedIndices.size(), (clock / (bombTime * 10)) + 1);

        // Light the fuse on some bombs.
        while (desired > 0) {
            Integer index = notExplodedIndices.get(random.nextInt(notExplodedIndices.size()));
            if (!bombIsActive(index)) {
                bombTimers.set(index, bombTime);
            }
            desired--;
        }

        // Update all bombs still active.
        for (int i = 0; i < notExplodedIndices.size(); i++) {
            int index = notExplodedIndices.get(i);
            int timeRemaining = bombTimers.get(index);
            bombTimers.set(index, timeRemaining - 1);

            // The bomb just exploded.
            if (timeRemaining - 1 == 0) {
                lives--;
                notExplodedIndices.remove(i);
                i--;
            }
        }
    }
}
