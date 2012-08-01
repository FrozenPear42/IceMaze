package com.bugfullabs.icemaze.level;

import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.texturepack.TexturePack;
import org.andengine.util.texturepack.TexturePackTextureRegionLibrary;

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
	
	
	
	public static GameScene createScene(BaseGameActivity a, Level level, TexturePack txPack){	
		
			GameScene levelScene = new GameScene(a, txPack, level);		
			TexturePackTextureRegionLibrary txl = txPack.getTexturePackTextureRegionLibrary();
			

			for(int i = 0; i < level.getWidth(); i++){
		    	  for(int j = 0; j < level.getHeight(); j++){
		    	  
			  		  levelScene.addItem(i, j, level.getItem(i, j));
			  		  
			  		  if(level.getAtts(i, j) != 0){
			  			  levelScene.addAttsItem(i, j, level.getAtts(i, j));
			  		  }
			  		  
		    	}
			 }	
			

  		  	level.createPlayer(a.getVertexBufferObjectManager(), txl.get(GameValues.PLAYER_ID));
  		  	level.getPlayer().attachToScene(levelScene);
			level.getPlayer().setZIndex(2);
			level.setEndsActive(levelScene, false);
			levelScene.sortChildren();
			
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