package com.example.assignment1;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Random;

public class MainxActivity extends AppCompatActivity {

    private TextView scoreTextView;
    private Button[] buttons = new Button[4];
    private int[] buttonIds = {R.id.buttonRed, R.id.buttonBlue, R.id.buttonYellow, R.id.buttonGreen};
    private int currentScore = 0;
    private int currentGreyButtonIndex = -1;
    private boolean buttonPressed = false;
    private Random random = new Random();
    private Handler handler = new Handler();
    private Runnable gameRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainx);

        scoreTextView = findViewById(R.id.scoreTextView);
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = findViewById(buttonIds[i]);
            buttons[i].setOnClickListener(this::buttonClicked);
        }

        startGame();
    }

    private void startGame() {
        currentScore = 0;
        buttonPressed = false;
        scoreTextView.setText("Score: 0");
        scheduleNextGreyButton();

        for (int i = 0; i < buttons.length; i++) {
            revertButtonColor(i);
        }
    }

    private void scheduleNextGreyButton() {
        gameRunnable = new Runnable() {
            @Override
            public void run() {
                if(currentGreyButtonIndex != -1) {
                    revertButtonColor(currentGreyButtonIndex);
                    buttons[currentGreyButtonIndex].setEnabled(true);
                    if (!buttonPressed) {
                        gameOver();
                        return;
                    }
                }
                int newGreyButtonIndex;
                do {
                    newGreyButtonIndex = random.nextInt(buttons.length);
                } while (newGreyButtonIndex == currentGreyButtonIndex);

                currentGreyButtonIndex = newGreyButtonIndex;
                greyOutButton(currentGreyButtonIndex);
                buttonPressed = false;
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(gameRunnable, 1000);
    }


    private void buttonClicked(View view) {
        if (view.getId() == buttons[currentGreyButtonIndex].getId() && !buttonPressed) {
            buttonPressed = true;
            currentScore++;
            scoreTextView.setText("Score: " + currentScore);
            view.setEnabled(false);
        } else {
            gameOver();
        }
    }


    private void greyOutButton(int index) {
        buttons[index].setBackgroundColor(ContextCompat.getColor(this, R.color.grey));
    }

    private void revertButtonColor(int index) {
        int color;
        switch(index) {
            case 0: color = R.color.red; break;
            case 1: color = R.color.blue; break;
            case 2: color = R.color.yellow; break;
            case 3: color = R.color.green; break;
            default: return;
        }
        buttons[index].setBackgroundColor(ContextCompat.getColor(this, color));
    }

    private void gameOver() {
        handler.removeCallbacks(gameRunnable);
        for (Button button : buttons) {
            button.setEnabled(false);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(MainxActivity.this);
        builder.setTitle("Game Over")
                .setMessage("Your score is: " + currentScore)
                .setPositiveButton("RESTART", (dialog, which) -> restartGame())
                .setCancelable(false)
                .show();
    }

    private void restartGame() {
        for (Button button : buttons) {
            button.setEnabled(true);
        }
        currentGreyButtonIndex = -1;
        startGame();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(gameRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentGreyButtonIndex != -1) {
            handler.postDelayed(gameRunnable, 1000);
        }
    }
}

