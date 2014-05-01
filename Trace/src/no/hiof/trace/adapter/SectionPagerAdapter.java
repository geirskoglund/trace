package no.hiof.trace.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import no.hiof.trace.activity.R;
import no.hiof.trace.contract.DatasetRefresh;
import no.hiof.trace.fragment.AllPlansFragment;
import no.hiof.trace.fragment.CurrentPlanFragment;
import no.hiof.trace.fragment.LatestPlansFragment;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Pair;

/**
 * @author Trace Inc.
 * 
 * Implementation of a FragmentPagerAdapter, used to display fragments in a pager.
 */
public class SectionPagerAdapter extends FragmentPagerAdapter
{
	//List of Pairs, holding 
	// (a) a reference to the id of a text string, holding the display name for the fragment
	// (b) the fragment
	List<Pair<Integer,Fragment>> availableFragments = new ArrayList<Pair<Integer,Fragment>>();
	Context context;
	
	//List of DatasetRefresh-representations of the Fragments. All included fragments implements DatasetRefresh.
	List<DatasetRefresh> refreshFragments = new ArrayList<DatasetRefresh>();
	
	public SectionPagerAdapter(FragmentManager fragmentManager, Context context)
	{
		super(fragmentManager);
		this.context = context;
		
		defineAvailableFragments();
		defineRefreshFragments();
	}
	
	
	/**
	 * Recasts the Fragments as DatasetRefresh and stores them in a list. If recast fails
	 * an exception is thrown. 
	 */
	private void defineRefreshFragments() 
	{
		for(Pair<Integer,Fragment> pair : availableFragments)
		{
			try
			{
				refreshFragments.add((DatasetRefresh)pair.second);
			}
			catch(ClassCastException e)
			{
				throw new ClassCastException(pair.second.toString() + " must implement DatasetRefresh.");
			}
		}
		
	}

	private void defineAvailableFragments()
	{
		availableFragments.add(fragmentData(R.string.title_current_section, new CurrentPlanFragment()));
		availableFragments.add(fragmentData(R.string.title_latest_section, new LatestPlansFragment()));
		availableFragments.add(fragmentData(R.string.title_all_section, new AllPlansFragment()));
	}
	
	//The Pair definition
	private Pair<Integer,Fragment> fragmentData(int fragmentTitleReference, Fragment fragment)
	{
		return new Pair<Integer,Fragment>(fragmentTitleReference,fragment);
	}

	@Override
	public Fragment getItem(int sectionPosition)
	{
		return fragmentOnPosition(sectionPosition);

	}
	
	/**
	 * @param sectionPosition
	 * Calls the refreshData-method on the fragment in the given position.
	 */
	public void updateFragmentOnSection(int sectionPosition)
	{
		if(sectionPosition<0 || sectionPosition >= refreshFragments.size())
			return;
		
		refreshFragments.get(sectionPosition).refreshData();
	}
	
	private Fragment fragmentOnPosition(int position)
	{
		return availableFragments.get(position).second;
	}

	@Override
	public int getCount()
	{
		return numberOfAvailableFragments();
	}
	
	private int numberOfAvailableFragments()
	{
		return availableFragments.size();
	}

	@Override
	public CharSequence getPageTitle(int sectionPosition)
	{
		Locale defaultLocale = Locale.getDefault();
		return fragmentTitleOnPosition(sectionPosition).toUpperCase(defaultLocale);
	}

	private String fragmentTitleOnPosition(int position)
	{
		return context.getString(availableFragments.get(position).first);
	}

}
