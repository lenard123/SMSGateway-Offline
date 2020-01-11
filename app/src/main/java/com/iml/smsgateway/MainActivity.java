package com.iml.smsgateway;

import android.app.*;
import android.os.*;
import android.widget.*;
import java.util.*;
import java.text.*;
import android.view.View.*;
import android.view.*;
import android.content.*;
import android.graphics.*;

public class MainActivity extends Activity 
{
	public Button start;
	public TextView status;
	private static String logs = "App Started";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		addLogs(logs);
		showIP();
		
		start = (Button) findViewById(R.id.start);
		status = (TextView) findViewById(R.id.status);
		
		if (HttpServer.getStatus() == HttpServer.STATUS_RUNNING){
			stopButton();
		}
		final MainActivity app = this;
		start.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					// TODO: Implement this method
					if (HttpServer.getStatus() == HttpServer.STATUS_STOP){
						HttpServer.startServer(app);
						//stopButton();
					} else if (HttpServer.getStatus() == HttpServer.STATUS_RUNNING){
						HttpServer.stopServer();
						startButton();
					}
				}
			});
    }
	
	public void startButton()
	{
		runOnUiThread(new Runnable(){

				@Override
				public void run()
				{
					// TODO: Implement this method
					start.setText("Start server");
					start.setBackgroundResource(R.drawable.green_button);
					start.setTextColor(Color.rgb(0, 255, 0));
					status.setText("Not running");
					status.setTextColor(Color.rgb(255,0,0));
				}
			});
	}	
	
	public void stopButton()
	{
		runOnUiThread(new Runnable(){

				@Override
				public void run()
				{
					start.setText("Stop server");
					start.setBackgroundResource(R.drawable.pink_button);
					start.setTextColor(Color.rgb(250, 113, 205));
					status.setText("Running");
					status.setTextColor(Color.rgb(0,255,0));
					
					// TODO: Implement this method
				}
		});
	}
	
	public void addLogs(final String log1)
	{
		
		runOnUiThread(new Runnable(){

				@Override
				public void run()
				{
					// TODO: Implement this method
					Date now = new Date();
					SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss");
					String log = ft.format(now) + " " + log1 + "\n";
					TextView logs = (TextView) findViewById(R.id.logs);
					log += logs.getText().toString();
					logs.setText(log);
					MainActivity.logs = log;
				}
			});
		
		
	}
	
	public void showIP()
	{
		//final MainActivity app = this;
		runOnUiThread(new Runnable(){

				@Override
				public void run()
				{
					// TODO: Implement this method
					String ip = HttpServer.getHost();
					int port = HttpServer.PORT;
					String sample = "http://"+ip+":"+port+"?number=1234&message=test";
					TextView host = (TextView) findViewById(R.id.host);
					host.setText(ip);
					TextView example = (TextView) findViewById(R.id.sample);
					example.setText(sample);
					
				}
			});
	}
}
