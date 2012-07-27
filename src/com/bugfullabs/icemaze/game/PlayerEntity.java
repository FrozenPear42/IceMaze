package com.bugfullabs.icemaze.game;

import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * 
 * @author Bugful Labs
 * @author Wojciech Gruszka
 * @email  wojciech@bugfullabs.pl
 *
 */


public class PlayerEntity extends Sprite{

	private float initX;
	private float initY;
	
	private MoveModifier mM; 
	
	private Scene mScene;
	
	
	public PlayerEntity(VertexBufferObjectManager vm, int pX, int pY, TextureRegion tx) {
	super(pX, pY, tx, vm);	
	initX = pX;
	initY = pY;
	}
	
	
	
	
	
	public void moveToInitPosition(){
		this.unregisterEntityModifier(mM);
		this.setPosition(initX, initY);
		}
	
	
	
	public Scene attachToScene(Scene scene){
		scene.attachChild(this);
		this.mScene = scene;	
		return scene;
	}
	
	
	public void remove(){
		this.detachSelf();
	}
	
		
	public void move(float endX, float endY){
	
		mM = new MoveModifier(0.1f, this.getX(), endX, this.getY(), endY);
	
		this.mM.setAutoUnregisterWhenFinished(true);
		this.registerEntityModifier(mM);
		
	}
	
	public void reset(){	
		this.detachSelf();
		this.attachToScene(mScene);
		this.moveToInitPosition();
	}
}
