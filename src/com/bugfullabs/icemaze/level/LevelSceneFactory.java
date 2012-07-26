package com.bugfullabs.icemaze.level;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.texturepack.TexturePack;




public class LevelSceneFactory{
	
	
	
	public static final int BG_ID = 17;

	
	public static Scene createScene(BaseGameActivity a, Level level, TexturePack txPack){	
			
			Scene levelScene = new Scene();	
			
			levelScene.setBackground(new SpriteBackground(new Sprite(0, 0, txPack.getTexturePackTextureRegionLibrary().get(BG_ID), a.getVertexBufferObjectManager())));

			for(int i = 0; i < level.getWidth(); i++){
		    	  for(int j = 0; j < level.getHeight(); j++){
		    	  
		    	  switch(level.getItemNumber(i, j))
		    	  {
		    	  
		    	 // case GameValues.ITEM_SOLID:	//SOLID 	  
		    	//	  levelScene.attachChild(new Sprite(i*32, j*32, txPack.getTexturePackTextureRegionLibrary().get(SOLID_ID)));
		    	//	  break;

		    	 //case GameValues.ITEM_STAR:	//STAR
		    	//	  levelScene.addStar(new Sprite(i*32, j*32, txPack.getTexturePackTextureRegionLibrary().get(STAR_ID)));
		    	//	  break;
		    		  
			  	  default:
			  		
			  		 break;
			  	
		    	  }
		    	  
		    	}
			 }	
			
			
			
			return levelScene;
	}
	
}