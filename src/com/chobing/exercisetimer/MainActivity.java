package com.chobing.exercisetimer;

import java.io.IOException;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	private boolean timerStarted = false;
	private boolean timerReset = true;
	
	private int runningTime;
	private int restTime;
	private int repeatTime;
	
	private MediaPlayer shortBeep;
	private MediaPlayer longBeep;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button startButton = (Button)findViewById(R.id.startButton);
		startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (timerStarted) {
					pauseTimer();
				} else {
					startTimer();
				}
			}
		});
		
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		shortBeep = MediaPlayer.create(this, R.raw.short_beep);
		shortBeep.setLooping(false);
		shortBeep.setVolume(1, 1);
		longBeep = MediaPlayer.create(this, R.raw.long_beep);
		longBeep.setLooping(false);
		longBeep.setVolume(1, 1);
		try {
			shortBeep.prepare();
			longBeep.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Button resetButton = (Button)findViewById(R.id.resetButton);
		resetButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				resetTimer();
			}
		});
		makeResetButtonClickable(false);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		pauseTimer();
	}
	
	private void startTimer() {
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		// Set Buttons
		Button startButton = (Button)findViewById(R.id.startButton);
		
		startButton.setText(getResources().getString(R.string.pause));
		timerStarted = true;
		
		makeResetButtonClickable(false);
		makeEditFieldsEditable(false);
		
		if (timerReset) {
			// Set Timer
			EditText runningTimer = (EditText)findViewById(R.id.runningTimer);
			runningTime = Integer.parseInt(runningTimer.getText().toString());
			
			EditText restTimer = (EditText)findViewById(R.id.restTimer);
			restTime = Integer.parseInt(restTimer.getText().toString());
			
			EditText repeatTimer = (EditText)findViewById(R.id.repeatTimer);
			repeatTime = Integer.parseInt(repeatTimer.getText().toString());
			
			Thread t = new Thread(new TimerRunnable());
			t.start();
		}
	}

	private void pauseTimer() {
	    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    
		Button startButton = (Button)findViewById(R.id.startButton);
		
		startButton.setText(getResources().getString(R.string.resume));
		timerStarted = false;
		
		makeResetButtonClickable(true);
	}
	
	private void resetTimer() {
	    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    
		// Set Timer
		displayRunningTime(runningTime);
		displayRestTime(restTime);
		displayRepeatTime(repeatTime);
		
		// Set Buttons
		Button startButton = (Button)findViewById(R.id.startButton);
		
		startButton.setText(getResources().getString(R.string.start));
		timerStarted = false;
		
		makeEditFieldsEditable(true);
		makeResetButtonClickable(true);
		
		timerReset = true;
	}
	
	private void makeEditFieldsEditable(boolean editable) {
		EditText runningTimer = (EditText)findViewById(R.id.runningTimer);
		EditText restTimer = (EditText)findViewById(R.id.restTimer);
		EditText repeatTimer = (EditText)findViewById(R.id.repeatTimer);
		
		makeTextViewClickable(runningTimer, editable);
		makeTextViewClickable(restTimer, editable);
		makeTextViewClickable(repeatTimer, editable);
		
		runningTimer.setEnabled(editable);
		restTimer.setEnabled(editable);
		repeatTimer.setEnabled(editable);
	}
	
	private void makeResetButtonClickable(boolean clickable) {
		Button resetButton = (Button)findViewById(R.id.resetButton);
		
		makeTextViewClickable(resetButton, clickable);
		
		resetButton.setEnabled(clickable);
	}
	
	private void makeTextViewClickable(TextView tv, boolean clickable) {
		if (clickable) {
			tv.setTextColor(getResources().getColor(android.R.color.black));
		} else {
			tv.setTextColor(getResources().getColor(android.R.color.darker_gray));
		}
	}
	
	private void displayRunningTime(int t) {
		EditText timer = (EditText)findViewById(R.id.runningTimer);
		timer.setText("" + t);
	}
	private void displayRestTime(int t) {
		EditText timer = (EditText)findViewById(R.id.restTimer);
		timer.setText("" + t);
	}
	private void displayRepeatTime(int t) {
		EditText timer = (EditText)findViewById(R.id.repeatTimer);
		timer.setText("" + t);
	}
	
	private class TimerRunnable implements Runnable {

		private int currentRunningTime;
		private int currentRestTime;
		private int currentRepeatTime;
		
		private Handler handler = new Handler();
		private Runnable displayTimeRunnable = new Runnable() {
			@Override
			public void run() {
				displayRunningTime(currentRunningTime);
				displayRestTime(currentRestTime);
				displayRepeatTime(currentRepeatTime);
			}
		};
		
		@Override
		public void run() {
			timerReset = false;
			
			currentRunningTime = runningTime;
			currentRestTime = restTime;
			currentRepeatTime = repeatTime;
			
			shortBeep.start();
			
			while (true) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				if (timerReset) {
					break;
				}
				
				if (timerStarted) {
					calculateCurrentTime();
				}
				handler.post(displayTimeRunnable);
			}
		}
		
		private void calculateCurrentTime() {
			if (currentRunningTime > 0) {
				currentRunningTime--;
				
				if (currentRunningTime == 0) {
					longBeep.start();
				}
				return;
			}
			
			if (currentRestTime > 0) {
				currentRestTime--;
				
				if (currentRestTime == 0) {
					shortBeep.start();
				}
				
				return;
			}
			
			if (currentRepeatTime > 0) {
				currentRunningTime = runningTime;
				currentRestTime = restTime;
				currentRepeatTime--;
			}
		}
	}
	
}
