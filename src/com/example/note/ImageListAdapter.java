package com.example.note;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class ImageListAdapter extends ArrayAdapter<String> {

	public ImageListAdapter(Context context, int textViewResourceId,
			String[] objects) {
		
		super(context, textViewResourceId, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		return inflater.inflate(R.layout.image_listrow, null);
	}	
}
