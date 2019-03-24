package edu.fandm.jnguyen.sweeper;

import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "sweeper.MainActivity";

    private ArrayList<Button> bombs = new ArrayList<>();
    private SweeperGame sweeperGame;

    // Game ticks.
    private boolean isTicking = false;
    private Handler handler = new Handler();
    private TickTask tickTask = new TickTask();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sweeperGame = SweeperGame.getInstance(getApplicationContext());

        ButtonGrid buttonGrid = findViewById(R.id.buttonGrid);
        for (int i = 0; i < sweeperGame.getBombCount(); i++) {
            final int idx = i;
            Button button = buttonGrid.getButton(i);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sweeperGame.defuseBomb(idx);
                    v.setBackgroundResource(R.drawable.bomb_inactive);
                }
            });
            bombs.add(button);
        }

        // Start a new game.
        sweeperGame.newGame();

        // Disable all buttons until a game starts.
        buttonsEnabled(false);
    }

    public void startGame(View v) {
        // Invert ticking.
        ticking(!isTicking);

        // If the clock is ticking, buttons should be on I guess.
        buttonsEnabled(isTicking);
    }

    public void ticking(boolean on) {
        isTicking = on;
        if (on) {
            handler.removeCallbacks(tickTask);
            handler.postDelayed(tickTask, 500);
            sweeperGame.newGame();
        } else {
            handler.removeCallbacks(tickTask);
        }
    }

    class TickTask implements Runnable {

        @Override
        public void run() {
            if (isTicking) {
                tick();
                long start = SystemClock.uptimeMillis();
                handler.postAtTime(this, start + 500);
            }
        }
    }

    private void tick() {
        updateUI();
        sweeperGame.tick();

        if (sweeperGame.isGameOver()) {
            ticking(false);
            buttonsEnabled(false);
            indicateLoss();
        }
    }

    private void updateUI() {
        TextView scoreTV = findViewById(R.id.textView_clock);
        scoreTV.setText(Integer.toString(sweeperGame.getScore()));

        for (int i = 0; i < bombs.size(); i++) {
            Button bomb = bombs.get(i);
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
        for (int i = 0; i < bombs.size(); i++) {
            Button bomb = bombs.get(i);

            // Update color.
            if (!on) {
                if (!sweeperGame.bombExploded(i)) {
                    bomb.setBackgroundResource(R.drawable.bomb_blank);
                } else {
                    bomb.setBackgroundResource(R.drawable.bomb_dead);
                }
            } else {
                bomb.setBackgroundResource(R.drawable.bomb_inactive);
            }

            bomb.setEnabled(on);
        }
    }

    private void indicateLoss() {
        Toast.makeText(this, "You lost!", Toast.LENGTH_SHORT).show();
    }
}
