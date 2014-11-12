package com.roopasoft.util;

import android.content.Context;
import android.graphics.Typeface;

public class Font {

	Typeface typeface;
	Context context;
	
	public Font(Context context)
	{
		this.context=context;
	}
	
	public Typeface typeface_normal_SEGOEUI()
	{
		typeface = Typeface.createFromAsset(context.getAssets(), "Fonts/SEGOEUI.TTF");
		return typeface;
	}
	
	public Typeface typeface_bold_SEGOEUI()
	{
		typeface = Typeface.createFromAsset(context.getAssets(), "Fonts/SEGOEUIB.TTF");
		return typeface;
	}
	
}
