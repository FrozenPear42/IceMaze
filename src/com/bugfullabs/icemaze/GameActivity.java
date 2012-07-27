package com.bugfullabs.icemaze;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.FadeInModifier;
import org.andengine.entity.modifier.FadeOutModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.StrokeFont;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.texturepack.TexturePack;
import org.andengine.util.texturepack.TexturePackLoader;
import org.andengine.util.texturepack.TexturePackTextureRegionLibrary;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.KeyEvent;

import com.bugfullabs.icemaze.level.Level;
import com.bugfullabs.icemaze.level.LevelSceneFactory;


/**
 * 
 * @author Bugful Labs
 * @author Wojciech Gruszka
 * @email  wojciech@bugfullabs.pl
 *
 */


public class GameActivity extends SimpleBaseGameActivity implements IOnMenuItemClickListener{

	private static final int MENU_RESET = 0;
	private static final int MENU_MAIN = 1;
	private static final int MENU_RESUME = 2;
	
	private int cameraHeight = 480;
	private int cameraWidth = 800;
	
	private Camera mCamera;
	private Scene mGameScene;
	private Scene mScoreScene;
	private MenuScene mPauseScene;
	
	
	private TexturePack mMenuTexturePack; 
	private TexturePack mGameTexturePack; 
	
	private TexturePackTextureRegionLibrary mMenuTextures;
	
	private BitmapTextureAtlas mFontTexture;
	private StrokeFont mFont;
	
	private Sprite mPauseButton;
	private Sprite mRestartButton;

	
	private static Level level;

	
	@Override
	public EngineOptions onCreateEngineOptions() {
		
		mCamera = new Camera(0, 0 , cameraWidth, cameraHeight);
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(cameraWidth, cameraHeight), mCamera);
	}

	@Override
	protected void onCreateResources() {

	TexturePackLoader tpl = new TexturePackLoader(getAssets(), getTextureManager());	
		
	/* FONT */
	mFontTexture = new BitmapTextureAtlas(getTextureManager() ,256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
	Typeface typeface =  Typeface.createFromAsset(getAssets(), "font/FOO.ttf");
    mFont = new StrokeFont(getFontManager(), mFontTexture, typeface, 26, true, Color.WHITE, 2, Color.BLACK);	
	mFontTexture.load();
    mFont.load();
    /* MENU ITEMS */
	try {
	mMenuTexturePack = tpl.loadFromAsset("gfx/menu/gamemenu.xml", "gfx/menu/");
	mMenuTexturePack.loadTexture();
	mMenuTextures = mMenuTexturePack.getTexturePackTextureRegionLibrary();
	Debug.i("DONE");
	} catch (Exception e) {
		e.printStackTrace();
	}
	/* GAME ITEMS */
	try{
	mGameTexturePack = tpl.loadFromAsset("gfx/game/" + level.getLevelTexture(), "gfx/game/");
	mGameTexturePack.loadTexture();
	}catch(Exception e){
	e.printStackTrace();
	}
	
	
    
    
	}

	@Override
	protected Scene onCreateScene() {
		
		/* GAME SCENE */
		mGameScene = LevelSceneFactory.createScene(this, level, mGameTexturePack);
		
		createMenuScene();
		
		
		
		//PAUSE BUTTON
		mPauseButton = new Sprite(cameraWidth-24, 0, 24, 24, mMenuTextures.get(GameValues.PAUSE_ID), getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionUp()){
					doPause();
					return true;
				}
			return false;
			}
		};
		mGameScene.registerTouchArea(mPauseButton);
		mGameScene.attachChild(mPauseButton);
		
		//RESTART BUTTON
		mRestartButton = new Sprite(0, 0, 24, 24, mMenuTextures.get(GameValues.RESTART_ID), getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionUp()){
					//ON Restart
					
				}
			return false;
			}
		};
		mGameScene.registerTouchArea(mRestartButton);
		mGameScene.attachChild(mRestartButton);
		
		
		return mGameScene;
	}
	
	
	
	
	
	

	
	
	
	protected void createMenuScene() {

		this.mPauseScene = new MenuScene(this.mCamera);


		final SpriteMenuItem resetMenuItem = new SpriteMenuItem(MENU_RESET, mMenuTextures.get(GameValues.RESTART_ID), getVertexBufferObjectManager());
		resetMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mPauseScene.addMenuItem(resetMenuItem);

		final SpriteMenuItem menuMenuItem = new SpriteMenuItem(MENU_MAIN,  mMenuTextures.get(GameValues.MENU_ID), getVertexBufferObjectManager());
		menuMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mPauseScene.addMenuItem(menuMenuItem);

		final SpriteMenuItem resumeMenuItem = new SpriteMenuItem(MENU_RESUME,  mMenuTextures.get(GameValues.BACK_ID), getVertexBufferObjectManager());
		resumeMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mPauseScene.addMenuItem(resumeMenuItem);
		this.mPauseScene.buildAnimations();
		this.mPauseScene.setBackgroundEnabled(false);
		this.mPauseScene.setOnMenuItemClickListener(this);

	}
	
	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()){
		
		case MENU_MAIN:
			this.setIntent(new Intent(this ,MainMenuActivity.class));
			this.finish();
			overridePendingTransition(R.anim.fadein, R.anim.fadeout);
			
			break;
		case MENU_RESET:
			
			break;
			
		case MENU_RESUME:
			doResume();
			return true;

		}
		return false;
	}
	
	
	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		if((pKeyCode == KeyEvent.KEYCODE_MENU || pKeyCode == KeyEvent.KEYCODE_BACK ) && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			if(this.mGameScene.hasChildScene()) {
				doResume();
			} else {
				doPause();
			}
			return true;
		}
		return super.onKeyDown(pKeyCode, pEvent); 
	}

	
	public void doPause() {
		
		this.mGameScene.setChildScene(this.mPauseScene, false, true, true);
	}

	public void doResume() {
		this.mGameScene.clearChildScene();
		
		this.mPauseScene.reset();
	}
	
	public static void setLevel(Level lvl){
		level = lvl;
	}
	
	
	private void changeSceneWithFade(final Scene s, final float time){
		
		Scene cs = this.getEngine().getScene();

		final Rectangle black = new Rectangle(0, 0, cameraWidth, cameraHeight, this.getVertexBufferObjectManager());
		black.setColor(0.0f, 0.0f, 0.0f);
		black.setAlpha(0.0f);
		
		cs.attachChild(black);
		
		black.registerEntityModifier(new FadeInModifier(time, new IEntityModifierListener() {
			
			@Override
			public void onModifierStarted(IModifier<IEntity> arg0, IEntity arg1) {	
			}
			
			@Override
			public void onModifierFinished(IModifier<IEntity> arg0, IEntity arg1) {
			
				black.detachSelf();
				s.attachChild(black);
				GameActivity.this.getEngine().setScene(s);
				black.registerEntityModifier(new FadeOutModifier(time, new IEntityModifierListener() {
					
					@Override
					public void onModifierStarted(IModifier<IEntity> arg0, IEntity arg1) {
					}
					
					@Override
					public void onModifierFinished(IModifier<IEntity> arg0, IEntity arg1) {
						black.detachSelf();
					}
				}));
			}
		}));
		
		
		}

	
}
