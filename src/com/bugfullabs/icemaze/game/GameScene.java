package com.bugfullabs.icemaze.game;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.modifier.FadeOutModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.debug.Debug;
import org.andengine.util.modifier.ease.EaseStrongIn;
import org.andengine.util.texturepack.TexturePack;

import com.bugfullabs.icemaze.level.Level;

public class GameScene extends Scene{

	private Sprite[][][] mSprites;

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

	
	}
	
	
	public void addItem(int column, int row, int item){
		
		if(mSprites[column][row] != null)
			this.detachChild(mSprites[column][row][0]);
		
		 mSprites[column][row][0] = new Sprite(column*32, row*32, mTexturePack.getTexturePackTextureRegionLibrary().get(item), mActivity.getVertexBufferObjectManager());		 
		 this.attachChild(mSprites[column][row][0]);
		 this.sortChildren();
	
	}

	
	public void addItem(int column, int row, int item, float interval){
		
		if(mSprites[column][row] != null)
			this.detachChild(mSprites[column][row][0]);
		
		 mSprites[column][row][0] = new Sprite(0, row*32, mTexturePack.getTexturePackTextureRegionLibrary().get(item), mActivity.getVertexBufferObjectManager());		 
		 this.attachChild(mSprites[column][row][0]);
		 this.sortChildren();
		 mSprites[column][row][0].registerEntityModifier(new MoveXModifier(interval, 800 + column*32, column*32, EaseStrongIn.getInstance()));
		 
		 
	}

	
	public void addAttsItem(int c, int r, int item){
		
		 mSprites[c][r][1] = new Sprite(c*32, r*32, mTexturePack.getTexturePackTextureRegionLibrary().get(item), mActivity.getVertexBufferObjectManager());		 
		 mSprites[c][r][1].setZIndex(1);
		 this.attachChild(mSprites[c][r][1]);
		 this.sortChildren();
		
	}

	public void addAttsItem(int c, int r, int item, float interval){
		
		 mSprites[c][r][1] = new Sprite(0, r*32, mTexturePack.getTexturePackTextureRegionLibrary().get(item), mActivity.getVertexBufferObjectManager());		 
		 mSprites[c][r][1].setZIndex(1);
		 this.attachChild(mSprites[c][r][1]);
		 this.sortChildren();
		 mSprites[c][r][1].registerEntityModifier(new MoveXModifier(interval, 800+c*32, c*32, EaseStrongIn.getInstance()));
		 
		 
	}
	
	public void removeAttsItem(int c, int r){
		this.detachChild(mSprites[c][r][1]);
		mSprites[c][r][1] = null;
		}
	
	
	
	public Sprite getItem(int c, int r, int l){
		return mSprites[c][r][l];
	}
	
	public void removeAllItems(){

		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				for(int k = 0; k < 2; k ++){
				
				this.detachChild(mSprites[i][j][k]);
				mSprites[i][j][k] = null;
			
				}
			}
		}
	}


	public void moveItems(final IOnFinishListener iOnFinishListener) {
		
		
		Debug.i("MOVE");
		
		
		
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				

				if(mSprites[i][j][0] != null){
					mSprites[i][j][0].registerEntityModifier(new FadeOutModifier(1.0f));
				}
				if(mSprites[i][j][1] != null){
					mSprites[i][j][1].registerEntityModifier(new FadeOutModifier(1.0f));
				}
				
			}
		}
		
		mActivity.getEngine().registerUpdateHandler(new TimerHandler(1.0f, false, new ITimerCallback() {
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				System.gc();
				iOnFinishListener.onFinish();
			}
		}));

	}
	
	
	
	
}
