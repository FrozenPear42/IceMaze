package com.bugfullabs.icemaze;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.FadeOutModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.StrokeFont;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.VerticalAlign;
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
import android.view.Display;
import android.view.KeyEvent;

import com.bugfullabs.icemaze.game.GameScene;
import com.bugfullabs.icemaze.game.IOnFinishListener;
import com.bugfullabs.icemaze.game.PlayerEntity;
import com.bugfullabs.icemaze.level.Level;
import com.bugfullabs.icemaze.level.LevelFileReader;
import com.bugfullabs.icemaze.level.LevelSceneFactory;
import com.bugfullabs.icemaze.util.AlignedText;


/**
 * 
 * @author Bugful Labs
 * @author Wojciech Gruszka
 * @email  wojciech@bugfullabs.pl
 *
 */

//TODO: FADE TRANSITION BETWEEN LEVELS


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

	private BitmapTextureAtlas mBigFontTexture;
	private StrokeFont mBigFont;
	
	private Sprite mPauseButton;
	private Sprite mRestartButton;

	private Sprite mScoreBackground;
	private int time;
	private boolean timerStarted = false;
	private boolean nextLevel = false;
	private int tiles;
	private Text mTime; 
	private Text mTiles;
	private Text mTextScore;
	private Text mTextHighScore;
	
	private static Level level;

	private SharedPreferences mSettings;
	
	private SharedPreferences mScore;
	private SharedPreferences.Editor mScoreEditor;
	private int steering;

	private boolean canExit = false;

	private Text mCTiles;
	private int maxTiles;
	private boolean isKey = false;
	private BitmapTextureAtlas bgTextureAtlas;
	private TextureRegion bgTextureRegion;
	
	private boolean isAnim = false;
	
	
	
	private int objSize;
	
	
	/* BASE ENGINE & GAME FUNCTIONS */
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		
		Display disp = getWindowManager().getDefaultDisplay();
		
		cameraWidth = disp.getWidth();
		cameraHeight = disp.getHeight();
		
		objSize = cameraWidth/25; 
		int tmp = cameraHeight/15;
		
		if(objSize <= tmp){
		}else{
		objSize = tmp;
		}
			
			
		
		mCamera = new Camera(0, 0 , cameraWidth, cameraHeight);
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), mCamera);
	}

	@Override
	protected void onCreateResources() {

	TexturePackLoader tpl = new TexturePackLoader(getAssets(), getTextureManager());	
		
	/* FONT */
	mFontTexture = new BitmapTextureAtlas(getTextureManager() ,256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
	Typeface typeface =  Typeface.createFromAsset(getAssets(), "font/FOO.ttf");
    mFont = new StrokeFont(getFontManager(), mFontTexture, typeface, objSize, true, Color.WHITE, 2, Color.BLACK);	
	mFontTexture.load();
    mFont.load();

	mBigFontTexture = new BitmapTextureAtlas(getTextureManager() ,256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
	typeface =  Typeface.createFromAsset(getAssets(), "font/FOO.ttf");
    mBigFont = new StrokeFont(getFontManager(), mBigFontTexture, typeface, objSize*2.2f, true, Color.WHITE, 2, Color.BLACK);	
	mBigFontTexture.load();
    mBigFont.load();
    
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
	
	/* BACKGROUND */
	
	bgTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 32, 32, TextureOptions.REPEATING_BILINEAR_PREMULTIPLYALPHA);
	bgTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bgTextureAtlas, this, "gfx/game/bg.png", 0, 0);
	bgTextureAtlas.load();
	
	
	
	bgTextureRegion.setTextureSize(cameraWidth, cameraHeight);
    
	}

	@Override
	protected Scene onCreateScene() {
		
		if(level == null){
			this.startActivity(new Intent(GameActivity.this, MainMenuActivity.class));
			this.finish();
			overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		}
			
		
		
		/* SHARED PREFS CONFIG */
		mSettings = getSharedPreferences(GameValues.SETTINGS_FILE, 0);
		this.steering = mSettings.getInt("steering", GameValues.STEERING_TOUCH);
		
		mScore = getSharedPreferences(GameValues.SCORE_FILE, 0);
		mScoreEditor = mScore.edit();
		
		isAnim = mSettings.getBoolean("anim", true);
		
		/* STARS COUNTER */
		starCounter = 0;
		
		/* MENU SCENE */
		createMenuScene();	
		
		/* TIMER */
		
		time = 0;
		
		getEngine().registerUpdateHandler(new TimerHandler(1.0f, true, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				if(timerStarted)
				time++;
			}
		}));
		
		
		/* GAME SCENE */
		mGameScene = LevelSceneFactory.createScene(this, level, bgTextureRegion, mGameTexturePack);
		
		//PAUSE BUTTON
		mPauseButton = new Sprite(cameraWidth-objSize, 0, objSize, objSize, mMenuTextures.get(GameValues.PAUSE_ID), getVertexBufferObjectManager()){
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
		mRestartButton = new Sprite(0, 0, objSize, objSize, mMenuTextures.get(GameValues.RESTART_ID), getVertexBufferObjectManager()){
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
		
		maxTiles = level.getTiles();
		mCTiles = new Text(objSize, 0, mFont, getString(R.string.tiles) + ": 0/" + Integer.toString(maxTiles), 15, getVertexBufferObjectManager());
		mCTiles.setZIndex(10);
		mGameScene.attachChild(mCTiles);
		
		mGameScene.setOnSceneTouchListener(this);
		this.mGameScene.setTouchAreaBindingOnActionDownEnabled(true);
	
		
		
		
		/* SCORE INFO */
		
		float height = 20 + objSize*2.2f + objSize*5 + 128 + 32; 
		
		mScoreBackground = new Sprite(60, -cameraHeight, cameraWidth-120, height,  mMenuTextures.get(GameValues.SCOREBG_ID),getVertexBufferObjectManager());
		mScoreBackground.setZIndex(11);

		
		//SCORES	
		
		/* 800x480
		 * BG - 680x380
		 * 
		
		mTime = new Text(40, 120, mFont, getString(R.string.time) + ": XX:XX", 12, getVertexBufferObjectManager());
		mTiles = new Text(350, 120, mFont, getString(R.string.tiles) + ": XXX/XXX", 15, getVertexBufferObjectManager());
		mTextScore = new Text(40, 170, mFont, getString(R.string.score) + ": XXXXX", 14, getVertexBufferObjectManager());
		mTextHighScore = new Text(350, 170, mFont, getString(R.string.high) + ": XXXXX", 19, getVertexBufferObjectManager());
		
		*
		*/
		
		/*
		 * 1024x768
		 * BG - 904x668
		 * 
		 */
		

		
		AlignedText text = new AlignedText(0, 20, mBigFont, getString(R.string.great), HorizontalAlign.CENTER, VerticalAlign.CENTER, cameraWidth-120, 100, this);
		
		mTime = new Text(40, 20 + objSize*3.2f, mFont, getString(R.string.time) + ": XX:XX", 12, getVertexBufferObjectManager());
		mTextScore = new Text(40, 20 + objSize*4.7f, mFont, getString(R.string.score) + ": XXXXX", 14, getVertexBufferObjectManager());
		
		//x = ;
		//y = ;
		mTiles = new Text(mTextScore.getWidth() + 80, 20 + objSize*3.2f, mFont, getString(R.string.tiles) + ": XXX/XXX", 15, getVertexBufferObjectManager());
		mTextHighScore = new Text(mTextScore.getWidth() + 80, 20 + objSize*4.7f, mFont, getString(R.string.high) + ": XXXXX", 19, getVertexBufferObjectManager());
		

		mTime.setZIndex(12);
		mTiles.setZIndex(12);
		mTextScore.setZIndex(12);
		text.setZIndex(13);
		
		
		//BUTTONS	
		Sprite restart = new Sprite((mScoreBackground.getWidth()/2)-264, mScoreBackground.getHeight()-(128+32), mMenuTextures.get(GameValues.RESTART_ID), getVertexBufferObjectManager()){	
			@Override
	        public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionUp()){	
				onGameRestart();
				
				mScoreBackground.registerEntityModifier(new MoveYModifier(1.7f, mScoreBackground.getY(), -(cameraHeight), EaseSineIn.getInstance()));
				}
				return true;
			}
		};
		
		Sprite menu = new Sprite((mScoreBackground.getWidth()/2)-64, mScoreBackground.getHeight()-(128+32), mMenuTextures.get(GameValues.MENU_ID), getVertexBufferObjectManager()){
			@Override
	        public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				
				if(pSceneTouchEvent.isActionUp()){
				
				GameActivity.this.startActivity(new Intent(GameActivity.this, MainMenuActivity.class));
				GameActivity.this.finish();
				overridePendingTransition(R.anim.fadein, R.anim.fadeout);
				
				}
				return true;
			}
		};
		
		Sprite next = new Sprite((mScoreBackground.getWidth()/2)+136, mScoreBackground.getHeight()-(128+32), mMenuTextures.get(GameValues.NEXT_ID), getVertexBufferObjectManager()){
			@Override
	        public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				
				if(pSceneTouchEvent.isActionUp()){

				mScoreBackground.registerEntityModifier(new MoveYModifier(1.7f, mScoreBackground.getY(), -(cameraHeight), new IEntityModifierListener() {
					@Override
					public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
					}
					
					@Override
					public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
						if(!nextLevel){
						nextLevel = true;
						nextLevel();
						}
					}
				}, EaseSineIn.getInstance()));
				
				}
				return true;
			}
		};
		
		
		mScoreBackground.attachChild(restart);
		mScoreBackground.attachChild(menu);
		mScoreBackground.attachChild(next);
		mScoreBackground.attachChild(text);
		mScoreBackground.attachChild(mTime);
		mScoreBackground.attachChild(mTiles);
		mScoreBackground.attachChild(mTextScore);
		mScoreBackground.attachChild(mTextHighScore);
		
		
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

		Sprite next = new Sprite(menuMenuItem.getX() + 128 + 1, menuMenuItem.getY(), mMenuTextures.get(GameValues.NEXT_ID), getVertexBufferObjectManager()){
			
			@Override
	        public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				
				if(pSceneTouchEvent.isActionUp()){
				
					mGameScene.moveItems(new IOnFinishListener() {
						
						@Override
						public void onFinish() {
							nextLevel();	
							
							doResume();	
							
						}
					});

				}
				return true;
			}	
			
		};
		

		next.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mPauseScene.attachChild(next);
		this.mPauseScene.registerTouchArea(next);
		this.mPauseScene.buildAnimations();
		
		
	}
	
	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()){
		
		case MENU_MAIN:
			this.startActivity(new Intent(this, MainMenuActivity.class));
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

		if(level.getPlayer() == null)
			return false;
		
		switch(steering){
		
		
		case GameValues.STEERING_TOUCH:	
		
			if(touchEvent.getAction() == TouchEvent.ACTION_DOWN)
			{	
				
				timerStarted = true;
			
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
		
		//boolean move = false;
		
		final PlayerEntity player = level.getPlayer();
		
		if(!player.isFinished())
			return;
		
		if(player.getColumn() < 0 || player.getRow() < 0 || player.getColumn() >= level.getWidth() || player.getRow() >= level.getHeight())
			return;
		
		
		
		final int col = player.getColumn();
		final int row = player.getRow();
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
			
		case GameValues.END_UN_ID:
			id = GameValues.END_UN_ID;
			break;
		
		case GameValues.END_ID:
			id = GameValues.END_ID;
			break;
			
		case GameValues.TELEPORTGREEN_ID:
			id = GameValues.TELEPORTGREEN_ID;
			break;
			
		case GameValues.TELEPORTRED_ID:
			id = GameValues.TELEPORTRED_ID;
			break;
		
			
		case GameValues.STABLE_ID:
			id = GameValues.STABLE_ID;
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
		
		final int nfCol = nCol;
		final int nfRow = nRow;
		final int nfId = id;
		
		
		if(level.getItem(nCol, nRow) != GameValues.SOLID_ID && 
				   level.getItem(nCol, nRow) != GameValues.BLANK_ID &&
				   level.getAtts(nCol, nRow) != GameValues.LOCK_ID){	
				
				//MOVE
				player.move(dir, new IOnFinishListener() {
					@Override
					public void onFinish() {
						
						//TELEPORT
						if(level.getItem(nfCol, nfRow) == GameValues.TELEPORTGREEN_ID)
						{	
						Debug.d("TELEPORT: nfCol: " + Integer.toString(nfCol) + " nfRow: " + Integer.toString(nfRow));
						
						int telID = level.getTeleportID(GameValues.GREEN_TELEPORT, nfCol, nfRow);
						
						player.teleport(level.getLinikedTeleport(GameValues.GREEN_TELEPORT, telID)[0], level.getLinikedTeleport(GameValues.GREEN_TELEPORT, telID)[1]);
						}
						
						if(level.getItem(nfCol, nfRow) == GameValues.TELEPORTRED_ID)
						{	
						Debug.d("TELEPORT: nfCol: " + Integer.toString(nfCol) + " nfRow: " + Integer.toString(nfRow));
						
						int telID = level.getTeleportID(GameValues.RED_TELEPORT, nfCol, nfRow);
						
						player.teleport(level.getLinikedTeleport(GameValues.RED_TELEPORT, telID)[0], level.getLinikedTeleport(GameValues.RED_TELEPORT, telID)[1]);
						}
						
						
						//END
						if(level.getItem(nfCol, nfRow) == GameValues.END_ID && canExit){
							onEnd();
						}
						

					}});
				
				
				if(level.getAtts(nfCol, nfRow) == GameValues.KEY_ID){
					
					isKey = true;
					
					mGameScene.removeAttsItem(nfCol, nfRow);
					level.setAtts(nfCol, nfRow, 0);
				
				}
				
				if(nfId == GameValues.ONESTEP_ID || nfId == GameValues.BLANK_ID)
					tiles++;
				
				mCTiles.setText(getString(R.string.tiles) + ": " + Integer.toString(tiles) + "/" + Integer.toString(maxTiles));
				
				mGameScene.addItem(col, row, nfId);
				level.setItem(col, row, nfId);
				
				
				
				//STARS
				if(level.getAtts(nfCol, nfRow) == GameValues.FLAME_ID){
				level.setAtts(nfCol, nfRow, 0);
				
				if(isAnim)
				mGameScene.getItem(nfCol, nfRow, 1).registerEntityModifier(new MoveModifier(0.2f, nfCol*objSize, (cameraWidth-objSize)-objSize*(starCounter+1), nfRow*objSize, 0));
				else
				mGameScene.getItem(nfCol, nfRow, 1).setPosition((cameraWidth-objSize)-objSize*(starCounter+1), 0);
					
				mGameScene.getItem(nfCol, nfRow, 1).setZIndex(10);
				mGameScene.sortChildren();
				starCounter++;
				if(starCounter >= 3){
				canExit = true;
				level.setEndsActive(mGameScene, true);
				}
			}
				
				
				}
		
		
				if(level.getAtts(nCol, nRow) != 0){
					
					switch(level.getAtts(nCol, nRow)){

					case GameValues.LOCK_ID:
						if(isKey){
						
						mGameScene.removeAttsItem(nCol, nRow);
						level.setAtts(nCol, nRow, 0);
						
						player.move(dir, new IOnFinishListener() {
							
							@Override
							public void onFinish() {
							}
						});	
						
						if(nfId == GameValues.ONESTEP_ID || nfId == GameValues.BLANK_ID)
							tiles++;
							
						mCTiles.setText(getString(R.string.tiles) + ": " + Integer.toString(tiles) + "/" + Integer.toString(maxTiles));
							
						mGameScene.addItem(col, row, nfId);
						level.setItem(col, row, nfId);
						
						
						}
						break;
						
					}
					
				}
		
				
		
			
			}
		
		
	
	
	private void onEnd(){
	
		timerStarted = false;
		
		calculateScore();
		
		
		
		if(isAnim){
		level.getPlayer().registerEntityModifier(new FadeOutModifier(0.2f, new IEntityModifierListener() {
			
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
			}
			
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {

	
			mGameScene.moveItems(new IOnFinishListener() {
				@Override
				public void onFinish() {
				mScoreBackground.registerEntityModifier(new MoveYModifier(2.0f, mScoreBackground.getY(),  (cameraHeight-mScoreBackground.getHeightScaled())/2, EaseBackOut.getInstance()));
				}
			});
	
			}
		}));
		}else{
			
		mScoreBackground.registerEntityModifier(new MoveYModifier(2.0f, mScoreBackground.getY(), (cameraHeight-mScoreBackground.getHeightScaled())/2, EaseBackOut.getInstance()));

		}

		
		time = 0;
		tiles = 0;
	}
	
	

	
	private void calculateScore(){
		
		String min;
		String sec;
		
		String max;
		String get;
		
		if(time/60 < 10)
			min = "0" + Integer.toString(time/60);
		else
			min = Integer.toString(time/60);
		
		if(time%60 < 10)
			sec = "0" + Integer.toString(time%60);
		else
			sec = Integer.toString(time%60);
		
		mTime.setText(getString(R.string.time) + ": " + min + ":" + sec);
		
		get = Integer.toString(tiles);
		max = Integer.toString(level.getTiles());

		mTiles.setText(getString(R.string.tiles) + ": " + get + "/" + max);
		
		float score;
		score = ((float)tiles/(float)level.getTiles());
		score *= 128000;
		score += (1.0f/(float)time) * 12000;
		
		Debug.i(Integer.toString(tiles));
		Debug.i(Integer.toString(maxTiles));
		
		
		if(tiles == maxTiles){
			mScoreEditor.putBoolean("full" + Integer.toString(level.getLevelpackId()) + "_" + Integer.toString(level.getId()), true);
			mScoreEditor.commit();
			}
			
		
		if(mScore.getInt("score" + Integer.toString(level.getLevelpackId()) + "_" + Integer.toString(level.getId()), 0) < (int) score){
			mScoreEditor.putInt("score" + Integer.toString(level.getLevelpackId()) + "_" + Integer.toString(level.getId()), (int) score);
			mScoreEditor.commit();
			mTextHighScore.setText(getString(R.string.new_high));
		}else{
			mTextHighScore.setText(getString(R.string.high) + " :" + Integer.toString((int) mScore.getInt("score" + Integer.toString(level.getLevelpackId()) + "_" + Integer.toString(level.getId()), 0)));
		}
		
		
		
		mTextScore.setText(getString(R.string.score) + ": " + Integer.toString((int)score));
		
	}
	
	private void onGameRestart(){
		timerStarted = false;
		time = 0;
		tiles = 0;
		canExit = false;
		isKey = false;
		starCounter = 0;
		mCTiles.setText(getString(R.string.tiles) + ": 0/" + Integer.toString(maxTiles));

		
		level.getPlayer().detachSelf();
		
		int id = level.getId();
		int levelpack = level.getLevelpackId();
		level = LevelFileReader.getLevelFromFile(this, "level_" + Integer.toString(levelpack) + "_" + Integer.toString(id));
		
		
		LevelSceneFactory.redraw(this, mGameScene, level, mGameTexturePack);
		
		
	}
	

	private void nextLevel(){
		final int levelID = level.getId();
		final int levelpackID = level.getLevelpackId();
		
		Debug.i("Level ID: " + Integer.toString(levelID));
		Debug.i("Level ID + 1: " + Integer.toString(levelID+1));
		
		timerStarted = false;
		time = 0;
		tiles = 0;
		canExit = false;
		isKey = false;
		starCounter = 0;


		level.getPlayer().detachSelf();
		
		
		if(levelID < 15){
			
			level = LevelFileReader.getLevelFromFile(GameActivity.this, "level_"+ Integer.toString(levelpackID) + "_" +  Integer.toString(levelID+1));	
			
			if(level == null){
				GameActivity.this.startActivity(new Intent(GameActivity.this, MainMenuActivity.class));
				GameActivity.this.finish();
				overridePendingTransition(R.anim.fadein, R.anim.fadeout);
			}
			
			maxTiles = level.getTiles();
			mCTiles.setText(getString(R.string.tiles) + ": 0/" + Integer.toString(maxTiles));
			if(isAnim)
			LevelSceneFactory.redrawWithAnimations(GameActivity.this, mGameScene, level, mGameTexturePack);
			else
			LevelSceneFactory.redraw(GameActivity.this, mGameScene, level, mGameTexturePack);
			
			
			}else{
				
				GameActivity.this.startActivity(new Intent(GameActivity.this, MainMenuActivity.class));
				GameActivity.this.finish();
				overridePendingTransition(R.anim.fadein, R.anim.fadeout);
			}
		nextLevel = false;
		
	}
	
	
	
	public int getCameraWidth(){
		return cameraWidth;
	}
	
	public int getCameraHeight(){
		return cameraHeight;
	}
	
	public int getObjSize(){
		return objSize;
	}
	
}
