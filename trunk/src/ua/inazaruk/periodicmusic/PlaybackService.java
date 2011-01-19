package ua.inazaruk.periodicmusic;

import java.io.FileInputStream;
import java.io.IOException;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

public class PlaybackService extends Service
{
	static final private String TAG = PlaybackService.class.getSimpleName(); 
	static final private String PLAY_ACTION = "PlayMusic";
	
	PendingIntent m_pendingIntent;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{		
		startPeriodicUpdates();
		registerReceiver(new PlayActionReceiver(), new IntentFilter(PLAY_ACTION));
		return START_STICKY;
	}
	
	@Override
	public void onCreate()
	{
		Intent playIntent = new Intent(PLAY_ACTION);              
		m_pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1,  
													 playIntent, 
													 PendingIntent.FLAG_UPDATE_CURRENT);
		
		super.onCreate();
	}
	
	@Override
	public void onDestroy()
	{		
		stopPeriodicUpdates();
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent arg0)
	{
		return null;
	}
	
	class PlayActionReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context ctx, Intent intent)
		{
			PowerManager powerManager = (PowerManager)getSystemService(POWER_SERVICE);
			final WakeLock wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Music");
			wakeLock.acquire();
						
			Log.d(TAG, "Broadcast received!");
			Thread thread = new Thread(new Runnable()
			{				
				@Override
				public void run()
				{															
					onPlay();
					wakeLock.release();					
				}
			});
			
			thread.start();
		}
	}
	
	private void startPeriodicUpdates()
	{
		Log.d(TAG, "Start periodic updates");
		AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, 60 * 1000, m_pendingIntent);	
	}
	
	private void stopPeriodicUpdates()
	{
		Log.d(TAG, "Stop periodic updates");
		AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
		alarmManager.cancel(m_pendingIntent);
	}
	
	private void onPlay()
	{
		try
		{
			Log.d(TAG, "Playback started!");
			MediaPlayer player = new MediaPlayer();
			player.setOnErrorListener(new OnErrorListener()
			{				
				@Override
				public boolean onError(MediaPlayer arg0, int arg1, int arg2)
				{
					Log.e(TAG, "Player failed!");
					return false;
				}
			});
			
			player.setOnCompletionListener(new OnCompletionListener()
			{				
				@Override
				public void onCompletion(MediaPlayer arg0)
				{
					Log.d(TAG, "Playback stopped!");					
				}
			});			
			
			FileInputStream stream = new FileInputStream("/mnt/sdcard/note.wav");
			player.setDataSource(stream.getFD());
			player.prepare();
			player.start();		
		}
		catch(IOException ex)
		{
			Log.e(TAG, "IOException", ex);
		}
	}
	
}
