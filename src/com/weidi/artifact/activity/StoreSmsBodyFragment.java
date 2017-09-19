package com.weidi.artifact.activity;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.weidi.artifact.R;

//只是显示短信内容
public class StoreSmsBodyFragment extends Fragment{
	private Context mContext;
	private View view;
	public TextView iv_addresser_content;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		Bundle bundle = getArguments();
		String body = bundle.getString("body");
		view = inflater.inflate(com.weidi.artifact.R.layout.storesmsbody_view, null);
		iv_addresser_content = (TextView) view.findViewById(R.id.iv_addresser_content);
		iv_addresser_content.setText(body);
		return view;
	}

}

