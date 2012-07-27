package com.bugfullabs.icemaze.level;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.texturepack.TexturePack;
import org.andengine.util.texturepack.TexturePackTextureRegionLibrary;

import com.bugfullabs.icemaze.GameValues;

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
			TexturePackTextureRegionLibrary txl = txPack.getTexturePackTextureRegionLibrary();
			

			for(int i = 0; i < level.getWidth(); i++){
		    	  for(int j = 0; j < level.getHeight(); j++){
		    	  
			  		  levelScene.attachChild(new Sprite(i*32, j*32, txl.get(level.getItem(i, j)), a.getVertexBufferObjectManager()));
		    	  
		    	}
			 }	
			
  		  	level.createPlayer(a.getVertexBufferObjectManager(), txl.get(GameValues.PLAYER_ID));
  		  	level.getPlayer().attachToScene(levelScene);
			
			
			
			
			return levelScene;
	}
	
}