package edu.fandm.jnguyen.sweeper;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "sweeper.MainActivity";

    private Button bomb;
    private BombTimer bombTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bomb = findViewById(R.id.button_bomb);
    }

    public void bombClick(View v) {
        if (bombTimer == null || bombTimer.getStatus() == AsyncTask.Status.FINISHED) {
            activateBomb((Button) v);
        } else if (bombTimer.getStatus() == AsyncTask.Status.RUNNING) {
            bombTimer.cancel(true);
            bomb.setBackgroundColor(getResources().getColor(R.color.miscBlack));
            toastBombStatus();
        }
    }

    public void activateBomb(Button bomb) {
        bombTimer = new BombTimer();
        bombTimer.execute();
    }

    private void toastBombStatus() {
        if (bomb.getText().equals("X")) {
            Toast.makeText(this, "Bomb defused.", Toast.LENGTH_SHORT).show();
            bomb.setText("");
        } else {
            Toast.makeText(getApplicationContext(), "Bomb exploded!", Toast.LENGTH_SHORT).show();
        }
    }

    class BombTimer extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            bomb.setBackgroundColor(getResources().getColor(R.color.miscRed));
            bomb.setText("X");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                int i = 0;
                while (i < 50) {
                    if (isCancelled()) {
                        break;
                    }
                    Thread.sleep(1);
                    i++;
                }
            } catch (Exception e) {
                Log.d(TAG, "I guess the bomb ticks forever....");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            bomb.setBackgroundColor(getResources().getColor(R.color.miscOrange));
            bomb.setText("");
            toastBombStatus();
        }
    }
}
