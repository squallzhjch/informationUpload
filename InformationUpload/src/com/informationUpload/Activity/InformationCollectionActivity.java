package com.informationUpload.Activity;


import com.informationUpload.R;
import com.informationUpload.fragments.InformationCollectionFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class InformationCollectionActivity extends FragmentActivity{
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.informationcollectionactivity);
		getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.tab_container,new InformationCollectionFragment(R.id.tab_container)).commit();
	}

}
