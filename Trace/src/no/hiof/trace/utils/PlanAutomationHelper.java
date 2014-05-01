package no.hiof.trace.utils;

import java.util.ArrayList;
import java.util.List;

import no.hiof.trace.application.TraceApp;
import no.hiof.trace.db.DatabaseManager;
import no.hiof.trace.db.model.Plan;
import no.hiof.trace.db.values.ColumnName;
import android.util.Log;

/**
 * @author Trace Inc.
 *Helper class for automation of selection and registration
 */
public class PlanAutomationHelper {
	
	Plan ssidPlan = null;
	List<Plan> ssidPlans = new ArrayList<Plan>();
	String ssid = "";
	String wifiTrigger="WiFi";

	/**
	 * Gets the first plan in a collection with a registered SSID
	 * @param ssid the SSID to check for
	 * @return the first Plan with correct SSID
	 */
	public Plan getPlanForSSID(String ssid)
	{	
		if(!ssidIsQueried(ssid))
			getPlansForSSID(ssid);
		
		return getFirstPlanFrom(ssidPlans);
	}
	
	/**
	 * Gets all the plans that are registered with a given SSID
	 * @param ssid to check for
	 * @return a collection of Plans with the registered SSID
	 */
	public List<Plan> getPlansForSSID(String ssid)
	{
		if(!ssidIsQueried(ssid))
		{
			DatabaseManager database = new DatabaseManager(TraceApp.getAppContext());
			ssidPlans = database.getOpenAutoLoadingPlans(ColumnName.SSID, ssid, wifiTrigger);
		}
		
		return ssidPlans;
	}
	
	/**
	 * Checks if a SSID is registered to a plan for auto loading. True if it is registered.
	 * @param ssid the SSID to check
	 * @return the result of the SSID check
	 */
	public boolean autoLoadingSetForSSID(String ssid)
	{
		if(!ssidIsQueried(ssid))
			getPlansForSSID(ssid);
		Log.d("TRACE-DM","Helper: Lengde på listen: "+ssidPlans.size());
		return !ssidPlans.isEmpty();
	}
	
	/*Gets the first Plan-object from a collection of Plan-objects or an empty plan if the collection is empty
	 * @param plans the collection of plans
	 * @return the first Plan in the input collection
	 */
	private Plan getFirstPlanFrom(List<Plan> plans)
	{
		if(plans.isEmpty())
			return new Plan();
		else
			return plans.get(0);
	}
	
	/**
	 * Gets the current plan
	 * @return the current Plan
	 */
	public Plan getCurrentPlan()
	{
		DatabaseManager database = new DatabaseManager(TraceApp.getAppContext());
		return database.getActivePlan();
	}
	
	/*
	 * Used to check if the ssid field is equal to another SSID
	 * @param ssid the SSID used to check against the 
	 * @return the result of the SSID check
	 */
	private boolean ssidIsQueried(String ssid)
	{
		return this.ssid.equals(ssid);
	}
	

}
