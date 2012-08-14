package com.bugfullabs.icemaze.level;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.modifier.FadeInModifier;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.texturepack.TexturePack;
import org.andengine.util.texturepack.TexturePackTextureRegionLibrary;

import android.content.SharedPreferences;

import com.bugfullabs.icemaze.GameValues;
import com.bugfullabs.icemaze.game.GameScene;

/**
 * 
 * @author Bugful Labs
 * @author Wojciech Gruszka
 * @email  wojciech@bugfullabs.pl
 *
 */


public class LevelSceneFactory{
	
	
	
	public static GameScene createScene(final BaseGameActivity a, final Level level, TexturePack txPack){	
		
			final GameScene levelScene = new GameScene(a, txPack, level);		
			final TexturePackTextureRegionLibrary txl = txPack.getTexturePackTextureRegionLibrary();
			
			float interval = 0;
			
			SharedPreferences settings = a.getSharedPreferences(GameValues.SETTINGS_FILE, 0);
			
			boolean anim = settings.getBoolean("anim", false);
			
			
			
			
			for(int i = 0; i < level.getWidth(); i++){
		    	  for(int j = 0; j < level.getHeight(); j++){
		    	  
		    		  
		    		  interval = 0.15f + 0.005f * (i * level.getHeight() + j);
	
		    		  if(anim)
		    			  levelScene.addItem(i, j, level.getItem(i, j), interval);
		    			 else
			  			  levelScene.addItem(i, j, level.getItem(i, j));
			    		 	  
		    		  
		    		  
			  		  if(level.getAtts(i, j) != 0){
			  			  
			  			  if(anim)
			  			  levelScene.addAttsItem(i, j, level.getAtts(i, j), interval);
			  			  else
			  			  levelScene.addAttsItem(i, j, level.getAtts(i, j));
			  			  
			  			  
			  		  }
			  		  
			  		  
			  		  
		    	}
			 }	
			

			a.getEngine().registerUpdateHandler(new TimerHandler(interval, false, new ITimerCallback() {
				
				@Override
				public void onTimePassed(TimerHandler pTimerHandler) {
					level.setEndsActive(levelScene, false);
					
					level.createPlayer(a.getVertexBufferObjectManager(), txl.get(GameValues.PLAYER_ID));
		  		  	level.getPlayer().setAlpha(0.0f);
		  		  	level.getPlayer().attachToScene(levelScene);
					level.getPlayer().setZIndex(2);
					levelScene.sortChildren();
					
					level.getPlayer().registerEntityModifier(new FadeInModifier(0.2f));
					
				}
			}));

  		  
			return levelScene;
	}
	
	
	public static void redraw(BaseGameActivity a,GameScene s, Level l, TexturePack tx){
		s.removeAllItems();
		
		for(int i = 0; i < l.getWidth(); i++){
	    	  for(int j = 0; j < l.getHeight(); j++){
	    	  
		  		  s.addItem(i, j, l.getItem(i, j));

		  		  if(l.getAtts(i, j) != 0){
			  			
		  			  s.addAttsItem(i, j, l.getAtts(i, j));
		  			  
		  		  }
	    	}
		 }	
		
		
		
		  	l.createPlayer(a.getVertexBufferObjectManager(), tx.getTexturePackTextureRegionLibrary().get(GameValues.PLAYER_ID));
		  	l.getPlayer().attachToScene(s);
		  	l.getPlayer().setZIndex(2);
		  	
		  	l.setEndsActive(s, false);
		  	
		  	s.sortChildren();
	}
	
	
	
	
	
}