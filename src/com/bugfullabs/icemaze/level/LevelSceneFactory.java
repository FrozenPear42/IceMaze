package com.bugfullabs.icemaze.level;

import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.texturepack.TexturePack;

/**
 * 
 * @author Bugful Labs
 * @author Wojciech Gruszka
 * @email  wojciech@bugfullabs.pl
 *
 */


public class LevelSceneFactory{
	
	
	
	public static Scene createScene(BaseGameActivity a, Level level, TexturePack txPack){	
			
			Scene levelScene = new Scene();		
			
			for(int i = 0; i < level.getWidth(); i++){
		    	  for(int j = 0; j < level.getHeight(); j++){
		    	  
		    	  switch(level.getItem(i, j))
		    	  {

		    	  //TODO: DRAWINGSCENE
		    	  
			  	  default:
			  		
			  		 break;
			  	
		    	  }
		    	  
		    	}
			 }	
			
			
			
			return levelScene;
	}
	
}