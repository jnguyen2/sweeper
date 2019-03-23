package edu.fandm.jnguyen.sweeper;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "sweeper.MainActivity";

    private ArrayList<Button> bombs = new ArrayList<>();

    // Game ticks.
    private int count = 0;
    private boolean isTicking = false;
    private Handler handler = new Handler();
    private TickTask tickTask = new TickTask();

    public void startGame(View v) {
        if (isTicking) {
            isTicking = false;
            handler.removeCallbacks(tickTask);
            count = 0;
        } else {
            handler.removeCallbacks(tickTask);
            handler.postDelayed(tickTask, 1000);
            isTicking = true;
        }
    }

    class TickTask implements Runnable {

        @Override
        public void run() {
            updateUI();
            long start = SystemClock.uptimeMillis();
            handler.postAtTime(this, start + 1000);
        }
    }

    private void updateUI() {
        Button bomb = bombs.get(0);
        bomb.setText(Integer.toString(count));
        count++;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButtonGrid buttonGrid = findViewById(R.id.buttonGrid);

        Integer bombCount = getResources().getInteger(R.integer.bomb_count);
        for (int i = 0; i < bombCount; i++) {
            final int idx = i;
            Button button = buttonGrid.getButton(i);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), String.format("Button %d pressed.", idx), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
