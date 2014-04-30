package no.hiof.trace.utils;

import java.util.ArrayList;
import java.util.List;

import no.hiof.trace.application.TraceApp;
import no.hiof.trace.db.DatabaseManager;
import no.hiof.trace.db.model.Plan;
import no.hiof.trace.db.values.ColumnName;
import android.util.Log;

public class PlanAutomationHelper {
	
	Plan ssidPlan = null;
	List<Plan> ssidPlans = new ArrayList<Plan>();
	String ssid = "";
	String wifiTrigger="WiFi";

	public PlanAutomationHelper() 
	{
	}
	
	public Plan getPlanForSSID(String ssid)
	{	
		if(!ssidIsQueried(ssid))
			getPlansForSSID(ssid);
		
		return getFirstPlanFrom(ssidPlans);
	}
	
	public List<Plan> getPlansForSSID(String ssid)
	{
		if(!ssidIsQueried(ssid))
		{
			DatabaseManager database = new DatabaseManager(TraceApp.getAppContext());
			ssidPlans = database.getOpenAutoLoadingPlans(ColumnName.SSID, ssid, wifiTrigger);
		}
		
		return ssidPlans;
	}
	
	public boolean autoLoadingSetForSSID(String ssid)
	{
		if(!ssidIsQueried(ssid))
			getPlansForSSID(ssid);
		Log.d("TRACE-DM","Helper: Lengde på listen: "+ssidPlans.size());
		return !ssidPlans.isEmpty();
	}
	
	private Plan getFirstPlanFrom(List<Plan> plans)
	{
		if(plans.isEmpty())
			return new Plan();
		else
			return plans.get(0);
	}
	
	public Plan getCurrentPlan()
	{
		DatabaseManager database = new DatabaseManager(TraceApp.getAppContext());
		return database.getActivePlan();
	}
	
	private boolean ssidIsQueried(String ssid)
	{
		return this.ssid.equals(ssid);
	}
	

}
