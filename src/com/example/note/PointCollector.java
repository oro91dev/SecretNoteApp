package com.example.note;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
//pointcollector samler inn punktene og legger i en arraylist
public class PointCollector implements OnTouchListener {

	public static final int NUM_POINTS = 4;
	private PointCollecterListener listener;
	private List<Point> points = new ArrayList<Point>();

	public boolean onTouch(View v, MotionEvent event) {

		int x = Math.round(event.getX());
		int y = Math.round(event.getY());

		points.add(new Point(x, y));
		
		if(points.size() == NUM_POINTS) {
			if(listener != null) {
				listener.pointsCollected(points);
			}
		}

		return false;
	}

	public void setListener(PointCollecterListener listener) {
		this.listener = listener;
	}
	
	public void clear() {
		points.clear();
	}
}


