package no.hiof.trace.fragment;

import no.hiof.trace.activity.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AllPlansFragment extends Fragment
{
	public AllPlansFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_all_plans, container, false);
		return rootView;
	}
}
