package edu.fandm.jnguyen.sweeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "sweeper.MainActivity";

    private ArrayList<Button> buttons = new ArrayList<>();
    private SweeperGame sweeperGame;

    // Input handling.
    private HashSet<Integer> inputsBetweenTicks = new HashSet<>();

    // Game ticks.
    private boolean isTicking = false;
    private Handler handler = new Handler();
    private TickTask tickTask = new TickTask();
    private int millis = 300;

    // Shared preferences to store high score.
    private SharedPreferences sharedPreferences;
    private int highScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start a new game.
        sweeperGame = SweeperGame.getInstance(getApplicationContext());
        sweeperGame.newGame();

        // Wire up the buttons.
        ButtonGrid buttonGrid = findViewById(R.id.buttonGrid);
        for (int i = 0; i < sweeperGame.getBombCount(); i++) {
            final int idx = i;
            Button button = buttonGrid.getButton(i);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    inputsBetweenTicks.add(idx);
                    if (sweeperGame.bombIsActive(idx)) {
                        increaseScore();
                        v.setBackgroundResource(R.drawable.bomb_blank);
                    } else if (!sweeperGame.bombExploded(idx)) {
                        v.setBackgroundResource(R.drawable.bomb_dead);
                    }
                }
            });

            // Keep track of all the buttons.
            buttons.add(button);
        }

        // Disable all buttons until a game starts.
        buttonsEnabled(false);

        // Load best score.
        sharedPreferences = this.getSharedPreferences(getString(R.string.preferences_file_key),
                MODE_PRIVATE);
        highScore = sharedPreferences.getInt(getString(R.string.high_score_key), 0);

        // Show splash
        boolean intro = sharedPreferences.getBoolean("intro", false);
        if (!intro) {
            showSplash(null);
        }

        updateUI();
    }

    public void startGame(View v) {
        // Invert setTicking.
        setTicking(!isTicking);

        // If the clock is setTicking, buttons should be on I guess.
        buttonsEnabled(isTicking);

        // Update icon for start button.
        updateStartButton();
    }

    public void increaseScore() {
        TextView scoreTv = findViewById(R.id.textView_score);
        int score = Integer.parseInt(scoreTv.getText().toString());
        scoreTv.setText(String.format("%d", score + 1));
    }

    private void updateStartButton() {
        Button startButton = findViewById(R.id.button_start);

        // Set icon appropriately.
        if (isTicking) {
            startButton.setBackgroundResource(R.drawable.close_white);
        } else {
            startButton.setBackgroundResource(R.drawable.refresh_white);
        }
    }

    public void setTicking(boolean on) {
        isTicking = on;
        if (on) {
            handler.removeCallbacks(tickTask);
            handler.postDelayed(tickTask, millis);
            sweeperGame.newGame();
        } else {
            handler.removeCallbacks(tickTask);

            // Final tick to update score.
            sweeperGame.tick(null);
            updateScore();
            updateUI();
        }
    }

    class TickTask implements Runnable {

        @Override
        public void run() {
            if (isTicking) {
                tick();
                long start = SystemClock.uptimeMillis();
                handler.postAtTime(this, start + millis);
            }
        }
    }

    private void updateAllButtonDrawables(int drawableId) {
        for (Button b : buttons) {
            b.setBackgroundResource(drawableId);
        }
    }

    private void tick() {
        // Update game.
        sweeperGame.tick(inputsBetweenTicks);

        // Check if game ended.
        if (sweeperGame.isGameOver()) {
            setTicking(false);
            buttonsEnabled(false);
            updateStartButton();
            updateScore();
            updateUI();
            return;
        }

        // Update UI.
        if (sweeperGame.isSafeTime()) {
            if (sweeperGame.getClock() % 2 == 0) {
                updateAllButtonDrawables(R.drawable.bomb_blank);
            } else {
                updateAllButtonDrawables(R.drawable.bomb_active);
            }
        } else {
            updateUI();
        }
    }

    private void updateScore() {
        if (sweeperGame.getScore() > highScore) {
            SharedPreferences.Editor editor = sharedPreferences.edit();

            // Update the user's best score.
            editor.putInt(getString(R.string.high_score_key), sweeperGame.getScore());

            // Update that the user has played one game and no longer needs the splash screen.
            editor.putBoolean("intro", true);

            editor.apply();
            highScore = sweeperGame.getScore();
        }
    }

    private void updateUI() {
        TextView bestScore = findViewById(R.id.textView_record);
        bestScore.setText(String.format("BEST: %d", highScore));

        TextView scoreTV = findViewById(R.id.textView_score);
        scoreTV.setText(Integer.toString(sweeperGame.getScore()));

        for (int i = 0; i < buttons.size(); i++) {
            Button bomb = buttons.get(i);
            if (sweeperGame.bombIsActive(i)) {
                bomb.setBackgroundResource(R.drawable.bomb_active);
            } else if (sweeperGame.bombExploded(i)) {
                bomb.setBackgroundResource(R.drawable.bomb_dead);
            } else {
                bomb.setBackgroundResource(R.drawable.bomb_blank);
            }
        }
    }

    private void buttonsEnabled(boolean on) {
        for (int i = 0; i < buttons.size(); i++) {
            Button bomb = buttons.get(i);

            // Update color.
            if (on) {
                bomb.setBackgroundResource(R.drawable.bomb_blank);
            } else {
                if (!sweeperGame.bombExploded(i)) {
                    bomb.setBackgroundResource(R.drawable.bomb_blank);
                } else {
                    bomb.setBackgroundResource(R.drawable.bomb_dead);
                }
            }

            bomb.setEnabled(on);
        }
    }

    public void showSplash(View v) {
        Intent intent = new Intent(this, Splash.class);
        startActivity(intent);
    }
}
