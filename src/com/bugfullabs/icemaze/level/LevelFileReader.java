package com.bugfullabs.icemaze.level;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.andengine.ui.activity.BaseGameActivity;

/**
 * 
 * @author Bugful Labs
 * @author Wojciech Gruszka
 * @email  wojciech@bugfullabs.pl
 *
 */


public class LevelFileReader{
	

	
	
	public static Level getLevelFromFile(BaseGameActivity activity, String file)
	{
		try {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = spf.newSAXParser(); 
		//XMLReader xr = sp.getXMLReader();
	
		LevelHandler handler = new LevelHandler();
		
		sp.parse(activity.getAssets().open("levels/"+file+".xml"), handler);
		
		return handler.getLevel();
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		return null;
	}
	
}