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
    private int clock = 0;
    private boolean isTicking = false;
    private Handler handler = new Handler();
    private TickTask tickTask = new TickTask();

    public void startGame(View v) {
        ticking(!isTicking);
    }

    public void ticking(boolean on) {
        if (on) {
            handler.removeCallbacks(tickTask);
            handler.postDelayed(tickTask, 1000);
            isTicking = true;
            sweeperGame.newGame();
        } else {
            isTicking = false;
            handler.removeCallbacks(tickTask);
            clock = 0;
        }
    }

    class TickTask implements Runnable {

        @Override
        public void run() {
            tick();
            long start = SystemClock.uptimeMillis();
            handler.postAtTime(this, start + 1000);
        }
    }

    private void tick() {
        updateUI();
        sweeperGame.tick();
        clock++;
    }

    private void updateUI() {
        if (sweeperGame.isGameOver()) {
            ticking(false);
            indicateLoss();
        }

        TextView clockTV = findViewById(R.id.textView_clock);
        clockTV.setText(Integer.toString(clock));

        for (int i = 0; i < bombs.size(); i++) {
            Button bomb = bombs.get(i);
            if (sweeperGame.bombIsActive(i)) {
                bomb.setBackgroundColor(getResources().getColor(R.color.miscRed));
            } else if (sweeperGame.bombExploded(i)) {
                bomb.setBackgroundColor(getResources().getColor(R.color.miscBlack));
            } else {
                bomb.setBackgroundColor(getResources().getColor(R.color.miscWhite));
            }
        }
    }

    private void indicateLoss() {
        Toast.makeText(this, "You lost!", Toast.LENGTH_SHORT).show();
    }

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
                    updateUI();
                }
            });
            bombs.add(button);
        }
    }
}
