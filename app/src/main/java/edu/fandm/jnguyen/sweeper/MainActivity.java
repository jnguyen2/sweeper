package edu.fandm.jnguyen.sweeper;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "sweeper.MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void bombClick(View v) {
        activateBomb((Button) v);
    }

    public void activateBomb(Button bomb) {
        BombTimer bombTimer = new BombTimer(bomb);
        bombTimer.execute();
    }

    class BombTimer extends AsyncTask<Void, Void, Void> {
        private Button bomb;

        BombTimer(Button bomb) {
            super();
            this.bomb = bomb;
        }

        @Override
        protected void onPreExecute() {
            bomb.setBackgroundColor(getResources().getColor(R.color.miscRed));
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                Log.d(TAG, "I guess the bomb ticks forever....");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            bomb.setBackgroundColor(getResources().getColor(R.color.miscBlack));
        }
    }
}
