package com.bugfullabs.icemaze.game;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.texturepack.TexturePack;

import com.bugfullabs.icemaze.GameValues;
import com.bugfullabs.icemaze.level.Level;

public class GameScene extends Scene{

	private Sprite[][][] mSprites;
	private Sprite[]   mStars;

	@SuppressWarnings("unused")
	private Level level;
	
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
	
	level = l;
	
	mSprites = new Sprite[width][height][2];
	mStars = new Sprite[3];
	
	}
	
	
	public void addItem(int column, int row, int item){
		
		if(mSprites[column][row] != null)
			this.detachChild(mSprites[column][row][0]);
		
		 mSprites[column][row][0] = new Sprite(column*32, row*32, mTexturePack.getTexturePackTextureRegionLibrary().get(item), mActivity.getVertexBufferObjectManager());		 
		 this.attachChild(mSprites[column][row][0]);
		 this.sortChildren();
	
	}
	
	public void addStar(int id, int column, int row){
		
		mStars[id] = new Sprite(column*32, row*32, mTexturePack.getTexturePackTextureRegionLibrary().get(GameValues.FLAME_ID), mActivity.getVertexBufferObjectManager());
		this.attachChild(mStars[id]);
		mStars[id].setZIndex(1);
		this.sortChildren();
	}
	
	public void addAttsItem(int c, int r, int item){
		
		 mSprites[c][r][1] = new Sprite(c*32, r*32, mTexturePack.getTexturePackTextureRegionLibrary().get(item), mActivity.getVertexBufferObjectManager());		 
		 mSprites[c][r][1].setZIndex(1);
		 this.attachChild(mSprites[c][r][1]);
		 this.sortChildren();
		
	}
	
	public void removeAttsItem(int c, int r){
		this.detachChild(mSprites[c][r][1]);
		mSprites[c][r][1] = null;
		}
	
	public void removeStar(int id){
		this.detachChild(mStars[id]);
	}
	
	public void removeAllItems(){
		
		//level.getPlayer().detachSelf();
		
		for(int i = 0; i < 3; i++){
			this.detachChild(mStars[i]);
			mStars[i] = null;
		}
		
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				for(int k = 0; k < 2; k ++){
				
				this.detachChild(mSprites[i][j][k]);
				mSprites[i][j][k] = null;
			
				}
			}
		}
	}
	
	
}
