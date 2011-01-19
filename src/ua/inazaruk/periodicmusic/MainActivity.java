package ua.inazaruk.periodicmusic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity 
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button startBtn = (Button)findViewById(R.id.startBtn);
        Button stopBtn = (Button)findViewById(R.id.stopBtn);
        
        startBtn.setOnClickListener(new OnClickListener()
		{			
			@Override
			public void onClick(View arg0)
			{
				onStartClicked();				
			}
		});
        
        stopBtn.setOnClickListener(new OnClickListener()
		{			
			@Override
			public void onClick(View arg0)
			{				
				onStopClicked();
			}
		});
    }
    
    private void onStartClicked()
    {
    	Intent intent = new Intent(this, PlaybackService.class);
    	startService(intent);
    }
    
    private void onStopClicked()
    {
    	Intent intent = new Intent(this, PlaybackService.class);
    	stopService(intent);
    }
}