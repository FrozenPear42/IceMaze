package com.bugfullabs.icemaze.game;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.IModifier.IModifierListener;

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
	private int mColumn;
	private int mRow;
	private boolean modifierFinished = true;
	private int size;
	
	public static final int DIRECTION_UP = 0;
	public static final int DIRECTION_DOWN = 1;
	public static final int DIRECTION_LEFT = 2;
	public static final int DIRECTION_RIGHT = 3;
	

	public PlayerEntity(VertexBufferObjectManager vm, int pX, int pY, TextureRegion tx, int siz) {
	super(pX, pY, tx, vm);	
	initX = pX;
	initY = pY;
	
	this.size = siz;
	
	mColumn = pX/size;
	mRow = pY/size;
	
	}
	
	
	
	
	public void setColumn(int c){
		mColumn = c; 
	}
	
	public int getColumn(){
		return mColumn;
	}
	
	public void setRow(int r){
		mRow = r;
	}
	
	public int getRow(){
		return mRow;
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
	
		
	public void moveTo(float endX, float endY, final IOnFinishListener iOnFinishListener){
	
		modifierFinished = false;

		mM = new MoveModifier(0.2f, this.getX(), endX, this.getY(), endY);
		mM.addModifierListener(new IModifierListener<IEntity>(){

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				mColumn = (int) (getX()/size);
				mRow = (int) (getY()/size);
				modifierFinished = true;
				iOnFinishListener.onFinish();
			}
		});
		mM.setAutoUnregisterWhenFinished(true);
		this.registerEntityModifier(mM);

	}
	
	public void moveTo(float endX, float endY){
		
		modifierFinished = false;

		mM = new MoveModifier(0.2f, this.getX(), endX, this.getY(), endY);
		mM.addModifierListener(new IModifierListener<IEntity>(){

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				mColumn = (int) (getX()/size);
				mRow = (int) (getY()/size);
				modifierFinished = true;
			}
		});
		mM.setAutoUnregisterWhenFinished(true);
		this.registerEntityModifier(mM);

	}
	
	
	public boolean isFinished(){
		return modifierFinished;
	}
	
	public void move(int dir, IOnFinishListener iOnFinishListener){
		
		switch(dir){
		
		case DIRECTION_DOWN:
			
			moveTo(getX(), getY()-size, iOnFinishListener);
			
			break;
		
		case DIRECTION_UP:
			
			moveTo(getX(), getY()+size, iOnFinishListener);
			
			
			break;
			
		case DIRECTION_LEFT:
				
			moveTo(getX()-size, getY(), iOnFinishListener);			
			
			break;
			
		case DIRECTION_RIGHT:
			
			moveTo(getX()+size, getY(), iOnFinishListener);
			
			
			break;
			
		
		}
		
		
	}
	
	
	public void reset(){	
		this.detachSelf();
		this.attachToScene(mScene);
		this.moveToInitPosition();
	}




	public void teleport(final int c, final int r) {
		
		Debug.d("TELEPORT: c: " + Integer.toString(c) + " r: " + Integer.toString(r));
		

		moveTo(c*size, r*size);
		
		mColumn = (int) (getX()/size);
		mRow = (int) (getY()/size);


		
	}
}
