package no.hiof.trace.sensor;

import java.util.ArrayList;
import java.util.List;

import no.hiof.trace.application.TraceApp;
import no.hiof.trace.service.TraceService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * @author Trace Inc.
 * 
 * A broadcast receiver, monitoring changes in SSID for the device. Broadcasts the ssid when it is set or lost.
 * 
 * The class also provides a static method for listing registered SSIDs on the device.
 */
public class WifiReciever extends BroadcastReceiver 
{
	/**
	 * The broadcast string used for all broadcasts from this class
	 */
	public final static String SSID_CHANGED = "no.hiof.trace.SSID_CHANGED";

	/**
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 * 
	 * Transmits the status of the ssid when it changes
	 */
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		String action = intent.getAction();
		
		if(action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))
		{
			WifiManager manager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
			NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			NetworkInfo.State state = networkInfo.getState();
			
			//make sure we have the service up and running
			Intent startServiceIntent = new Intent(context, TraceService.class);
			context.startService(startServiceIntent);
			
			if(state == NetworkInfo.State.CONNECTED)
			{
				WifiInfo wifiInfo = manager.getConnectionInfo();
				String ssid = wifiInfo.getSSID();
				ssid = ssid.substring(1,ssid.length()-1); //remove quotes
				
				Intent newSSID = new Intent(SSID_CHANGED);
				newSSID.putExtra("connected", true);
				newSSID.putExtra("ssid", ssid);
				TraceApp.getAppContext().sendBroadcast(newSSID);
			}
			else if(state == NetworkInfo.State.DISCONNECTED)
			{
				Intent newSSID = new Intent(SSID_CHANGED);
				newSSID.putExtra("connected", false);
				TraceApp.getAppContext().sendBroadcast(newSSID);
			}
		}
	}
	
	
	/**
	 * @return A List containing string representations of all the SSIDs stored on the device.
	 */
	public static List<String> getAllStoredSsid()
	{
		WifiManager manager = (WifiManager)TraceApp.getAppContext().getSystemService(Context.WIFI_SERVICE);
		List<WifiConfiguration> networks = manager.getConfiguredNetworks();
		List<String> ssids = new ArrayList<String>();
		
		for(WifiConfiguration network : networks)
			ssids.add(network.SSID.substring(1, network.SSID.length()-1)); //removes quotes
		
		return ssids;
	}

}
