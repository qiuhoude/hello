package com.example.surfaceviewdome;

import java.io.File;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	private EditText et_path;
	private Button bt_play, bt_pause, bt_replay, bt_stop;
	private SurfaceView sv;
	private MediaPlayer mediaPlayer;
	private SeekBar sb;
	private int currentPosition;
	private boolean isplay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
	}

	private void init() {
		et_path = (EditText) findViewById(R.id.et_path);
		bt_play = (Button) findViewById(R.id.play);
		bt_pause = (Button) findViewById(R.id.pause);
		bt_replay = (Button) findViewById(R.id.replay);
		bt_stop = (Button) findViewById(R.id.stop);
		sb = (SeekBar) findViewById(R.id.sb);
		sv = (SurfaceView) findViewById(R.id.sv);
		// ���÷ֱ���
		sv.getHolder().setFixedSize(110, 160);
		// �Ͱ汾�ֻ���4.0���µİ汾�����ϲ���ָ�����Լ���ά�������������ǵȴ���Ļ��Ⱦ�����������͵��û���ǰ
		sv.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// ����holder�ص�
		sv.getHolder().addCallback(new Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				System.out.println("holder������:"
						+ mediaPlayer.getCurrentPosition());
				if (mediaPlayer != null && mediaPlayer.isPlaying()) {
					currentPosition = mediaPlayer.getCurrentPosition();// ��ȡ��ǰλ��
					isplay = false;
					mediaPlayer.stop();
					mediaPlayer.release();
					mediaPlayer = null;
				}
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				System.out.println("holder������");
				System.out.println("currentPosition=" + currentPosition);
				if (currentPosition > 0) {
					play(currentPosition);
				}

			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				System.out.println("holder���ı�");

			}
		});

		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				isplay = true;
				int process = sb.getProgress();
				mediaPlayer.seekTo(process);
				if (mediaPlayer != null && mediaPlayer.isPlaying()) {
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				System.out.println("��ʼseek");
				isplay =false;
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
			}
		});

		bt_play.setOnClickListener(this);
		bt_pause.setOnClickListener(this);
		bt_replay.setOnClickListener(this);
		bt_stop.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.play:
			play(0);
			break;
		case R.id.pause:
			pause();
			break;
		case R.id.replay:
			replay();
			break;
		case R.id.stop:
			stop();
			break;
		}
	}

	private void replay() {
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.seekTo(0);// ���Ŷ���λ��
		} else {
			play(0);
		}
	}

	private void pause() {
		if ("����".equals(bt_pause.getText().toString().trim())) {
			mediaPlayer.start();
			bt_pause.setText("��ͣ");
			return;
		}
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
			bt_pause.setText("����");
		}
	}

	private void stop() {
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			currentPosition = 0;
			isplay = false;
			sb.setProgress(0);
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
			bt_play.setEnabled(true);
		}
	}

	private void play(final int currentPosition) {
		String path = et_path.getText().toString().trim();
		File file = new File(path);
		if (file.exists() && file.length() > 0) {
			try {
				if (mediaPlayer == null) {
					mediaPlayer = new MediaPlayer();
					mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); // ������Ƶ��������
					// ������ʾ�ĵط�
					mediaPlayer.setDisplay(sv.getHolder());
					mediaPlayer.setDataSource(path);// ����·��
					mediaPlayer.prepareAsync();
					// mediaPlayer.prepare();// ׼��
					mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
						@Override
						public void onPrepared(MediaPlayer mp) {
							mediaPlayer.start();
							mediaPlayer.seekTo(currentPosition);
							int max = mediaPlayer.getDuration();
							// seekbar�����ֵ
							sb.setMax(max);

							new Thread() {
								public void run() {
									isplay = true;
									while (isplay) {
										int postion = mediaPlayer
												.getCurrentPosition();
										sb.setProgress(postion);
										try {
											sleep(100);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}

									}
								}
							}.start();

						}
					});
					bt_play.setEnabled(false);
					mediaPlayer
							.setOnCompletionListener(new OnCompletionListener() {

								@Override
								public void onCompletion(MediaPlayer mp) {
									stop();
								}
							});
				}
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), "����ʧ��", 0).show();
				e.printStackTrace();
			}
		} else {
			Toast.makeText(getApplicationContext(), "��Ƶ�ļ�������", 0).show();
		}
	}
}
