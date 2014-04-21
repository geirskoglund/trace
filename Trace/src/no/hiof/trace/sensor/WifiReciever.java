package no.hiof.trace.sensor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiReciever extends BroadcastReceiver 
{

	@Override
	public void onReceive(Context context, Intent intent) 
	{
		String action = intent.getAction();
		
		if(action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))
		{
			WifiManager manager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
			NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			NetworkInfo.State state = networkInfo.getState();
			
			if(state == NetworkInfo.State.CONNECTED)
			{
				WifiInfo wifiInfo = manager.getConnectionInfo();
				String ssid = wifiInfo.getSSID();
				Log.d("TRACE-WR","Connected to: " + ssid );
			}
			else
			{
				Log.d("TRACE-WR","No connection"  + " | state: " + manager.getWifiState());
			}
		}
	}

}
