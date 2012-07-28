package com.bugfullabs.icemaze.game;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.texturepack.TexturePack;

import com.bugfullabs.icemaze.GameValues;
import com.bugfullabs.icemaze.level.Level;

public class GameScene extends Scene{

	private Sprite[][] mSprites;
	private Sprite[]   mStars;
	
	private BaseGameActivity mActivity;
	private TexturePack mTexturePack;
	
	private int width;
	private int height;
	
	
	
	public GameScene(BaseGameActivity a, TexturePack tx, Level l) {
	super();
	
	mActivity = a;
	mTexturePack = tx;
	
	width = l.getWidth();
	height = l.getHeight();
	
	mSprites = new Sprite[width][height];
	mStars = new Sprite[3];
	
	}
	
	
	public void addItem(int column, int row, int item){
		
		if(mSprites[column][row] != null)
			this.detachChild(mSprites[column][row]);
		
		 mSprites[column][row] = new Sprite(column*32, row*32, mTexturePack.getTexturePackTextureRegionLibrary().get(item), mActivity.getVertexBufferObjectManager());		 
		 this.attachChild(mSprites[column][row]);
		 this.sortChildren();
	
	}
	
	public void addStar(int id, int column, int row){
		
		mStars[id] = new Sprite(column*32, row*32, mTexturePack.getTexturePackTextureRegionLibrary().get(GameValues.FLAME_ID), mActivity.getVertexBufferObjectManager());
		this.attachChild(mStars[id]);
		mStars[id].setZIndex(1);
		this.sortChildren();
	}
	
	public void removeAllItems(){
		
		for(int i = 0; i < 3; i++){
			this.detachChild(mStars[i]);
			mStars[i] = null;
		}
		
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				this.detachChild(mSprites[i][j]);
				mSprites[i][j] = null;
			}
		}
	}
	
	
}
