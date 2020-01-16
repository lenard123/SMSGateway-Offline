package com.iml.smsgateway;

import android.net.wifi.*;
import android.telephony.gsm.*;
import java.io.*;
import java.net.*;
import java.util.*;
import android.support.v4.app.NotificationCompat;
import android.app.*;
import android.content.*;

public class HttpServer implements Runnable
{
	
	public final static int STATUS_RUNNING = 0;
	public final static int STATUS_STOP = 1;
	public final static int STATUS_STARTING = 2;
	
	public final static int RESPONSE_INVALID_PARAMETERS = 1;
	public final static int RESPONSE_SUCCESS = 2;
	
	public final static int PORT = 8080;
	//public static String ip_address = "127.0.0.1";	
	
	private static int status = 1;
	public static MainActivity activity;
	private static ServerSocket server;
	private Socket client;
	private OutputStream os;
	private InputStream is;
	private static NotificationManager nm;
	
	public HttpServer(Socket socket)
	{
		client = socket;
		try
		{
			os = socket.getOutputStream();
			is = socket.getInputStream();
		}
		catch (IOException e)
		{
			System.err.println(e);
		}
	}
	
	public static String getHost()
	{
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
						return inetAddress.getHostAddress();
					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
		return "127.0.0.1";
	}
	
	public static void showNotification()
	{
		NotificationCompat.Builder mbuilder = new NotificationCompat.Builder(activity);
		mbuilder.setContentTitle("SMSGateway");
		mbuilder.setContentText("Server is running on port: 8080");
		mbuilder.setSmallIcon(R.drawable.ic_launcher);
		
		Intent x = new Intent(activity, MainActivity.class);
		
		TaskStackBuilder tsb = TaskStackBuilder.create(activity);
		tsb.addParentStack(MainActivity.class);
		tsb.addNextIntent(x);
		
	
		PendingIntent pi = tsb.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		mbuilder.setContentIntent(pi);
	
		
		nm = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(100, mbuilder.build());
	}
	
	public static int getStatus()
	{
		return status;
	}
	
	
	public static void stopServer()
	{
		activity.addLogs("Stopping server");
		activity.showIP();
		status = STATUS_STOP;
		nm.cancelAll();
		try
		{
			server.close();
		}
		catch (IOException e)
		{
			System.err.println(e);
		}
	}
	
	public static void startServer(final MainActivity app)
	{
		
		
		Thread t = new Thread(new Runnable(){

				@Override
				public void run()
				{
					// TODO: Implement this method
					HttpServer.activity = app;
					app.addLogs("Starting Server");
					showNotification();
					HttpServer.status = STATUS_STARTING;
					try {
						
						HttpServer.server = new ServerSocket(PORT);
						app.showIP();
						HttpServer.status = STATUS_RUNNING;
						app.addLogs("Server running on port "+PORT);
						app.stopButton();
						while(true){
							if (HttpServer.status == STATUS_RUNNING){
								Thread request = new Thread(new HttpServer(server.accept()));
								request.start();
							} else {break;}
						}
					}catch(IOException ioe){
						if (HttpServer.status == STATUS_STOP) return;
						HttpServer.status = STATUS_STOP;
						app.addLogs("Can't start server on port "+PORT);
						app.startButton();
						System.err.println(ioe);
					}
				}
			});
		t.start();
	}
	
	@Override
	public void run()
	{
		// TODO: Implement this method
		activity.addLogs("Incoming Request");
		try
		{
			Map<String, String> query = getQuery();
			if(query.containsKey("number") && query.containsKey("message")){
				activity.addLogs("Sending message to "+query.get("number"));
				sendMessage(query);
			} else {
				activity.addLogs("Invalid Parameters");
				writeResponse("{\"status\":"+RESPONSE_INVALID_PARAMETERS+",\"message\":\"Failed: Invalid parameters\"}");
			}
		}
		catch (IOException e)
		{
			System.err.println(e);
		} finally {
			try
			{
				client.close();
			}
			catch (IOException e)
			{
				System.err.println(e);
			}
		}
	}
	
	private void sendMessage(Map<String,String> query){
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(query.get("number"), null, query.get("message"), null, null);
		writeResponse("{\"status\":"+RESPONSE_SUCCESS+",\"message\":\"Success: Message sent successfully\"}");
		activity.addLogs("Message sent");
	}
	
	private void writeResponse(String body){
		PrintWriter pw = new PrintWriter(os);
		pw.println("HTTP/1.0 200 OK");
		pw.println("Content-Type: application/json");
		pw.println("Content-Length: "+body.length());
		pw.println("");
		pw.print(body);
		pw.flush();
	}
	
	private Map<String,String> getQuery() throws IOException{
		Map<String, String> res= new HashMap<String, String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String[] query = br.readLine().split(" ")[1].split("\\?");
		if(query.length > 1){
			String[] params = query[1].split("\\&");
			for(int i = 0; i < params.length; i++){
				String key = params[i].split("\\=")[0];
				String value = "";
				if (params[i].split("\\=").length > 1){
					value = decodeValue(params[i].split("\\=")[1]);
				}
				res.put(key, value);
			}
		}
		return res;
	}
	public static String decodeValue(String value) {
        return URLDecoder.decode(value);
    }
}
