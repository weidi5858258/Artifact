package com.weidi.artifact.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;

public class FocusedTextView extends TextView {

	public FocusedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}


	public FocusedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public FocusedTextView(Context context) {
		super(context);
	}
	
	/**
	 * 当前并没有焦点，我只是欺骗了Android系统
	 */
	@Override                              
	@ExportedProperty(category = "focus")  //可以不要的
	public boolean isFocused() {
		return true;
	}
}
