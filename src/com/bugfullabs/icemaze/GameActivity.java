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
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
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
import org.andengine.util.modifier.ease.EaseBackOut;
import org.andengine.util.modifier.ease.EaseSineIn;
import org.andengine.util.texturepack.TexturePack;
import org.andengine.util.texturepack.TexturePackLoader;
import org.andengine.util.texturepack.TexturePackTextureRegionLibrary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.KeyEvent;

import com.bugfullabs.icemaze.game.GameScene;
import com.bugfullabs.icemaze.game.PlayerEntity;
import com.bugfullabs.icemaze.level.Level;
import com.bugfullabs.icemaze.level.LevelFileReader;
import com.bugfullabs.icemaze.level.LevelSceneFactory;


/**
 * 
 * @author Bugful Labs
 * @author Wojciech Gruszka
 * @email  wojciech@bugfullabs.pl
 *
 */


public class GameActivity extends SimpleBaseGameActivity implements IOnMenuItemClickListener, IOnSceneTouchListener{

	private static final int MENU_RESET = 0;
	private static final int MENU_MAIN = 1;
	private static final int MENU_RESUME = 2;
	
	private int cameraHeight = 480;
	private int cameraWidth = 800;
	
	private Camera mCamera;
	private GameScene mGameScene;
	private MenuScene mPauseScene;
	
	private int starCounter;
	
	private TexturePack mMenuTexturePack; 
	private TexturePack mGameTexturePack; 
	
	private TexturePackTextureRegionLibrary mMenuTextures;
	
	private BitmapTextureAtlas mFontTexture;
	private StrokeFont mFont;
	
	private Sprite mPauseButton;
	private Sprite mRestartButton;

	private Sprite mScoreBackground;
	
	private static Level level;

	private SharedPreferences mSettings;
	//private SharedPreferences.Editor mEditor;
	private int steering;
	
	private Sprite[] stars;
	
	private boolean canExit = false;
	
	/* BASE ENGINE & GAME FUNCTIONS */
	
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
		
		/* SHARED PREFS CONFIG */
		mSettings = getSharedPreferences(GameValues.SETTINGS_FILE, 0);
		//mEditor = mSettings.edit();
		this.steering = mSettings.getInt("steering", GameValues.STEERING_TOUCH);
		
		
		/* STARS COUNTER */
		starCounter = 0;
		stars = new Sprite[3];
		
		/* MENU SCENE */
		createMenuScene();	
		
		
		/* GAME SCENE */
		mGameScene = LevelSceneFactory.createScene(this, level, mGameTexturePack);
		
		//PAUSE BUTTON
		mPauseButton = new Sprite(cameraWidth-32, 0, 32, 32, mMenuTextures.get(GameValues.PAUSE_ID), getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionUp()){
					doPause();
					return true;
				}
			return false;
			}
		};
		mPauseButton.setZIndex(10);
		mGameScene.registerTouchArea(mPauseButton);
		mGameScene.attachChild(mPauseButton);
		
		//RESTART BUTTON
		mRestartButton = new Sprite(0, 0, 32, 32, mMenuTextures.get(GameValues.RESTART_ID), getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionUp()){
					//ON Restart
					onGameRestart();
				}
			return false;
			}
		};
		mRestartButton.setZIndex(10);
		mGameScene.registerTouchArea(mRestartButton);
		mGameScene.attachChild(mRestartButton);
		
		mGameScene.setOnSceneTouchListener(this);
		
		this.mGameScene.setTouchAreaBindingOnActionDownEnabled(true);
		
		
		/* SCORE INFO */
		mScoreBackground = new Sprite(100, -(cameraHeight-100), cameraWidth-200, cameraHeight-100,  mMenuTextures.get(GameValues.SCOREBG_ID),getVertexBufferObjectManager());
		mScoreBackground.setZIndex(11);

			Sprite restart = new Sprite(92, mScoreBackground.getHeight()-(128+32), mMenuTextures.get(GameValues.RESTART_ID), getVertexBufferObjectManager()){	
			@Override
	        public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				
				onGameRestart();
				
				mScoreBackground.registerEntityModifier(new MoveYModifier(1.7f, mScoreBackground.getY(), -(cameraHeight-100), EaseSineIn.getInstance()));
				
				return true;
			}
		};
		
		Sprite menu = new Sprite(236, mScoreBackground.getHeight()-(128+32), mMenuTextures.get(GameValues.MENU_ID), getVertexBufferObjectManager()){
			@Override
	        public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				
				GameActivity.this.setIntent(new Intent(GameActivity.this, MainMenuActivity.class));
				GameActivity.this.finish();
				overridePendingTransition(R.anim.fadein, R.anim.fadeout);
				
				
				return true;
			}
		};
		
		Sprite next = new Sprite(380, mScoreBackground.getHeight()-(128+32), mMenuTextures.get(GameValues.NEXT_ID), getVertexBufferObjectManager()){
			@Override
	        public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				
				return true;
			}
		};
		
		
		mScoreBackground.attachChild(restart);
		mScoreBackground.attachChild(menu);
		mScoreBackground.attachChild(next);
		
		mGameScene.registerTouchArea(next);
		mGameScene.registerTouchArea(menu);
		mGameScene.registerTouchArea(restart);
		
		mGameScene.sortChildren();
		mGameScene.attachChild(mScoreBackground);
		
		
		
		
		return mGameScene;
	}
	
	
	
	
	/* MENU */
	
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
			onGameRestart();
			doResume();
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
			if(mGameScene != null){
			
			if(this.mGameScene.hasChildScene()) {
				doResume();
			} else {
				doPause();
			}
			return true;
		}
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
	
	
	/* STATICS */
	
	
	public static void setLevel(Level lvl){
		level = lvl;
	}
	
	
	
	
	/* GAME STEERING */
	
	
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent touchEvent) {

		switch(steering){
		
		
		case GameValues.STEERING_TOUCH:	
		
			if(touchEvent.getAction() == TouchEvent.ACTION_DOWN)
			{	
			
				float xDown = touchEvent.getX();
				float yDown = touchEvent.getY();
		
				float xPlayer = level.getPlayer().getX();
				float yPlayer = level.getPlayer().getY();
				
				
				if ((xDown + yPlayer) < (xPlayer + yDown)) {
					if ((xDown - yPlayer) < (xPlayer - yDown)){
						//LEFT
						checkCollision(PlayerEntity.DIRECTION_LEFT);
					}else if ((xDown - yPlayer) > (xPlayer - yDown)){
						//DOWN
						checkCollision(PlayerEntity.DIRECTION_UP);
					}
				}
				else if ((xDown + yPlayer) > (xPlayer + yDown)) {
					if ((xDown - yPlayer) > (xPlayer - yDown)) {
						//RIGHT
						checkCollision(PlayerEntity.DIRECTION_RIGHT);
					}else if ((xDown - yPlayer) < (xPlayer - yDown)){
						//UP
						checkCollision(PlayerEntity.DIRECTION_DOWN);
					}
				}

			}
			
			break;
		
			
		case GameValues.STEERING_SLIDE:
			
			
			
			
			break;
		
		}
		
		
		return true;
	}
	
	
	
	private void checkCollision(int dir){
		
		PlayerEntity player = level.getPlayer();
		
		if(!player.isFinished())
			return;
		
		if(player.getColumn() < 0 || player.getRow() < 0 || player.getColumn() >= level.getWidth() || player.getRow() >= level.getHeight())
			return;
		
		
		
		int col = player.getColumn();
		int row = player.getRow();
		int nCol = col;
		int nRow = row;
		
		
		int id = GameValues.BLANK_ID;
		
		switch(level.getItem(col, row)){
		case GameValues.ONESTEP_ID:
			id = GameValues.BLANK_ID;
			break;
		
		case GameValues.TWOSTEP_ID:
			id = GameValues.ONESTEP_ID;
			break;
		
		}
		
		
		switch(dir){
		
		case PlayerEntity.DIRECTION_UP:
			nRow = row+1;
			break;

		case PlayerEntity.DIRECTION_DOWN:
			nRow = row-1;
			break;	
			
		case PlayerEntity.DIRECTION_LEFT:
			nCol = col-1;
			break;	
		
		case PlayerEntity.DIRECTION_RIGHT:
			nCol = col+1;
			break;	
		
			
		}
		
		
		
		if(level.getItem(nCol, nRow) != GameValues.SOLID_ID && 
				   level.getItem(nCol, nRow) != GameValues.BLANK_ID){	
				
				player.move(dir);
				
				mGameScene.addItem(col, row, id);
				
				level.setItem(col, row, id);
				
				if(level.getItem(nCol, nRow) == GameValues.END_ID && canExit){
					onEnd();
				
				}
				if(level.isStar(nCol, nRow)){
				mGameScene.removeStar(level.getStarId(nCol, nRow));
				starCounter++;
				addStar();
				if(starCounter >= 3){
				canExit = true;
				
				level.setEndsActive(mGameScene, true);
				}
				}
				}
	
	}
		
	
	private void addStar(){
		stars[starCounter-1] = new Sprite((cameraWidth-32)-32*starCounter,0, mGameTexturePack.getTexturePackTextureRegionLibrary().get(GameValues.FLAME_ID), getVertexBufferObjectManager());
		mGameScene.attachChild(stars[starCounter-1]);
		stars[starCounter-1].setZIndex(10);
		mGameScene.sortChildren();
	}
		
	
	
	private void onEnd(){
	
	//TODO: SHOW SCORE SCENE
		mScoreBackground.registerEntityModifier(new MoveYModifier(1.5f, mScoreBackground.getY(), 50, EaseBackOut.getInstance()));
		
		
	}
	
	private void onGameRestart(){
		canExit = false;
		starCounter = 0;
		
		for(int i = 0; i < 3; i++){
			if(stars[i] != null){
			stars[i].detachSelf();
			stars[i] = null;
			}
			
		}
		
		level.getPlayer().detachSelf();
		
		int id = level.getId();
		int levelpack = level.getLevelpackId();
		level = LevelFileReader.getLevelFromFile(this, "level_" + Integer.toString(levelpack) + "_" + Integer.toString(id));
		LevelSceneFactory.redraw(this, mGameScene, level, mGameTexturePack);
		
		
	}
	
	
	@SuppressWarnings("unused")
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
