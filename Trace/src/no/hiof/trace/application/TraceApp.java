package no.hiof.trace.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.hiof.trace.db.DatabaseManager;
import no.hiof.trace.db.model.Plan;
import android.app.Application;
import android.content.Context;
import android.util.Log;

public class TraceApp extends Application 
{
	private static Context context;

    public void onCreate()
    {
        super.onCreate();
        TraceApp.context = getApplicationContext();
    }

    public static Context getAppContext() 
    {
        return TraceApp.context;
    }
	
//	private List<Plan> allPlans = new ArrayList<Plan>();
//
//	public List<Plan> getRefreshedPlans()
//	{
//		DatabaseManager database = new DatabaseManager(this.getApplicationContext());
//		allPlans = database.getAllPlans();
//		database.closeDatabase();
//		return allPlans;
//	}
//	
//	public List<Plan> getAllPlans()
//	{
//		if(allPlans.isEmpty())
//			return getRefreshedPlans();
//		else
//			return allPlans;
//	}
//	
//	public List<Plan> getLatestPlans()
//	{
//		ArrayList<Plan> plans = new ArrayList<Plan>(getAllPlans());
//		if(plans.isEmpty()) return plans;
//		
//		int returnThisManyRecords = 3;
//		
//		if (returnThisManyRecords > plans.size())
//			returnThisManyRecords = plans.size();
//		
//		Collections.reverse(plans);
//		
//		return plans.subList(0, returnThisManyRecords);
//	}
//	
//	public Plan getPlanAt(int index)
//	{
//		if(index<allPlans.size() && index>=0 )
//			return allPlans.get(index);
//		else
//			return new Plan();
//	}
//	
//	public Plan getActivePlan()
//	{
//		List<Plan> plans = getLatestPlans();
//		if(plans.isEmpty())
//			return new Plan();
//		else
//			return (Plan) plans.get(0);
//	}
//	
//	public void addPlan(Plan plan)
//	{
//		allPlans.add(plan);
//	}

}
