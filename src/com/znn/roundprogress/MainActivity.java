package com.znn.roundprogress;


import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	private ProgressImageView progressImageView;
	private Button startButton;
	private Handler handler;
	private Timer timer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		progressImageView = (ProgressImageView) findViewById(R.id.progressAndImage);
		startButton = (Button) findViewById(R.id.startBtn);
		progressImageView.setImageRes(R.drawable.sina);
		startButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				progressImageView.setProgress(0);
				progressImageView.setLevel(ProgressImageView.LEVEL_COLOR_INDEX_NONE);
				progressImageView.setText("");
				TimerTask task = new TimerTask() {
					@Override
					public void run() {
						handler.sendEmptyMessage(0);
					}
				};
				if (timer != null) {
					timer.cancel();
				}
				timer = new Timer();
				timer.schedule(task, 0, 300);
			}
		});
		
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 0:
					int pro = progressImageView.getProgress();
					pro += 5;
					if (pro >= 100) {
						pro = 100;
						progressImageView.setLevel(ProgressImageView.LEVEL_COLOR_INDEX_GREEN);
						progressImageView.setText("完成");
						if (timer != null) {
							timer.cancel();
						}
					}
					progressImageView.setProgress(pro);
					break;

				default:
					break;
				}
			}
		};
		
	}

}
