package com.bugfullabs.icemaze.util;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.VerticalAlign;
import org.andengine.util.debug.Debug;



/**
 * 
 * @author Bugful Labs
 * @author Grushenko
 * @email  wojciech@bugfullabs.pl
 *
 */


public class Button{
	
	private TiledSprite bgButton;
	private AlignedText textButton;
	private TiledTextureRegion txRegion;
	
	private float mX;
	private float mY;
	
	public Button(final BaseGameActivity a, final Scene scene, final float x, final float y, final float width, final float height, final String text, final TiledTextureRegion tx, final Font font){	
		
			mX = x;
			mY = y;
		
			txRegion = tx.deepCopy();
			
			textButton = new AlignedText(x, y, font, text, HorizontalAlign.CENTER, VerticalAlign.CENTER, width, height, a);	
			
			bgButton = new	TiledSprite(x, y, txRegion, a.getVertexBufferObjectManager()){
			@Override
	        public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {


			switch(pSceneTouchEvent.getAction()){
			
			case TouchEvent.ACTION_UP:
				this.setCurrentTileIndex(0);
				return onButtonPressed();
			
			case TouchEvent.ACTION_DOWN:
				this.setCurrentTileIndex(1);
				Debug.i("DOWN");
				break;
			
			default:
				this.setCurrentTileIndex(0);
				break;

			}
	       	        
		return true;
	    }
		};
		bgButton.setWidth(width);
		bgButton.setHeight(height);
		
	    scene.registerTouchArea(bgButton);
		scene.attachChild(bgButton);
		scene.attachChild(textButton);
		}
	
	
	
	
	
	public void setText(String text){
		textButton.setText(text);
	}
	
	public void setPosition(final float x, final float y){
		
		textButton.setPosition(x, y);
		bgButton.setPosition(x, y);
	}
	
	public boolean onButtonPressed(){
		return false;
	}

	
	public int getHeight() {
		
		return (int) this.bgButton.getHeight();
	}

	public float getX(){
		return this.mX;
	}

	public float getY(){
		return this.mY;
	}
	
	public int getWidth() {
		
		return (int) this.bgButton.getWidth();
	}
	
	
	void detachSelf(){
		this.bgButton.detachSelf();
		this.textButton.detachSelf();
	}
	
}