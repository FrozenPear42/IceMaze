package com.bugfullabs.icemaze.level;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.modifier.FadeInModifier;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.texturepack.TexturePack;
import org.andengine.util.texturepack.TexturePackTextureRegionLibrary;

import android.content.SharedPreferences;

import com.bugfullabs.icemaze.GameActivity;
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
	
	
	
	public static GameScene createScene(final GameActivity a, final Level level, TextureRegion bgTextureRegion, TexturePack txPack){	
		
			final GameScene levelScene = new GameScene(a, txPack, level);		
			final TexturePackTextureRegionLibrary txl = txPack.getTexturePackTextureRegionLibrary();
			
			//Sprite bg = new Sprite(0, 0, bgTextureRegion, a.getVertexBufferObjectManager());
			Sprite bg = new Sprite(0, 0, bgTextureRegion, a.getVertexBufferObjectManager());
			
			
			
			levelScene.setBackground(new SpriteBackground(bg));
			
			float interval = 0;
			
			SharedPreferences settings = a.getSharedPreferences(GameValues.SETTINGS_FILE, 0);
			
			boolean anim = settings.getBoolean("anim", true);
			

			
			for(int i = 0; i < level.getWidth(); i++){
		    	  for(int j = 0; j < level.getHeight(); j++){
		    	  
		    		  
		    		  interval = 0.3f + 0.005f * (i * level.getHeight() + j);
	
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
			
			if(anim)
			{
				
			a.getEngine().registerUpdateHandler(new TimerHandler(interval, false, new ITimerCallback() {
				
				@Override
				public void onTimePassed(TimerHandler pTimerHandler) {
					level.setEndsActive(levelScene, false);
					
					level.createPlayer(a.getVertexBufferObjectManager(), txl.get(GameValues.PLAYER_ID), a.getObjSize());
		  		  	level.getPlayer().setAlpha(0.0f);
		  		  	level.getPlayer().attachToScene(levelScene);
					level.getPlayer().setZIndex(2);
					levelScene.sortChildren();
					
					level.getPlayer().registerEntityModifier(new FadeInModifier(0.2f));
					
				}
			}));
			}else{

				level.setEndsActive(levelScene, false);
				
				level.createPlayer(a.getVertexBufferObjectManager(), txl.get(GameValues.PLAYER_ID), a.getObjSize());
	  		  	level.getPlayer().attachToScene(levelScene);
				level.getPlayer().setZIndex(2);
				levelScene.sortChildren();
				
			}
  		  
			return levelScene;
	}
	
	
	public static void redraw(final GameActivity a, final GameScene s, final Level l, final TexturePack tx){
		s.removeAllItems();
		

		for(int i = 0; i < l.getWidth(); i++){
	    	  for(int j = 0; j < l.getHeight(); j++){
	    	  
	    		  if(l.getItem(i, j) == GameValues.CLEAN_ID)
	    			  continue;
	    		  
		  			  s.addItem(i, j, l.getItem(i, j));
		  			
		  			  if(l.getAtts(i, j) != 0){
		  			  s.addAttsItem(i, j, l.getAtts(i, j));
		  			}
		  		  }
		  		  
		}
		
		l.setEndsActive(s, false);
		
		l.createPlayer(a.getVertexBufferObjectManager(), tx.getTexturePackTextureRegionLibrary().get(GameValues.PLAYER_ID), a.getObjSize());
		  	l.getPlayer().attachToScene(s);
		l.getPlayer().setZIndex(2);
		s.sortChildren();
			
	}
		
		public static void redrawWithAnimations(final GameActivity a, final GameScene s, final Level l, final TexturePack tx){
			s.removeAllItems();
			
			float interval = 0;

			
			for(int i = 0; i < l.getWidth(); i++){
		    	  for(int j = 0; j < l.getHeight(); j++){
		    	  
		    		  if(l.getItem(i, j) == GameValues.CLEAN_ID)
		    			  continue;
		    		  
		    		  interval = 0.5f + 0.005f * (i * l.getHeight() + j);

		    			  s.addItem(i, j, l.getItem(i, j), interval);

			  		  if(l.getAtts(i, j) != 0){
			  			  
			  			  s.addAttsItem(i, j, l.getAtts(i, j), interval);

			  		  }
 
		    	}
			 }	
		
		
		a.getEngine().registerUpdateHandler(new TimerHandler(interval, false, new ITimerCallback() {
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				l.setEndsActive(s, false);
				
				l.createPlayer(a.getVertexBufferObjectManager(), tx.getTexturePackTextureRegionLibrary().get(GameValues.PLAYER_ID), a.getObjSize());
	  		  	l.getPlayer().setAlpha(0.0f);
	  		  	l.getPlayer().attachToScene(s);
				l.getPlayer().setZIndex(2);
				s.sortChildren();
				
				l.getPlayer().registerEntityModifier(new FadeInModifier(0.2f));
				
			}
		}));

	}
	
	
	
	
	
}