package com.bugfullabs.icemaze;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
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
import android.view.KeyEvent;

import com.bugfullabs.icemaze.game.GameScene;
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
	
	private Sprite[] stars;
	
	private boolean canExit = false;

	private Text mCTiles;
	private int maxTiles;
	
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

	mBigFontTexture = new BitmapTextureAtlas(getTextureManager() ,256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
	typeface =  Typeface.createFromAsset(getAssets(), "font/FOO.ttf");
    mBigFont = new StrokeFont(getFontManager(), mBigFontTexture, typeface, 60, true, Color.WHITE, 2, Color.BLACK);	
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
		
		
		/* STARS COUNTER */
		starCounter = 0;
		stars = new Sprite[3];
		
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
		
		maxTiles = level.getTiles();
		mCTiles = new Text(32, 0, mFont, "TILES: 0/" + Integer.toString(maxTiles), 15, getVertexBufferObjectManager());
		mCTiles.setZIndex(10);
		mGameScene.attachChild(mCTiles);
		
		mGameScene.setOnSceneTouchListener(this);
		this.mGameScene.setTouchAreaBindingOnActionDownEnabled(true);
	
		
		
		
		/* SCORE INFO */
		mScoreBackground = new Sprite(100, -(cameraHeight-100), cameraWidth-200, cameraHeight-100,  mMenuTextures.get(GameValues.SCOREBG_ID),getVertexBufferObjectManager());
		mScoreBackground.setZIndex(11);

		
		//SCORES	
		mTime = new Text(40, 120, mFont, "TIME: XX:XX", 12, getVertexBufferObjectManager());
		mTiles = new Text(320, 120, mFont, "TILES: XXX/XXX", 15, getVertexBufferObjectManager());
		mTextScore = new Text(40, 170, mFont, "SCORE: XXXXX", 14, getVertexBufferObjectManager());
		mTextHighScore = new Text(320, 170, mFont, "HIGH: XXXXX", 16, getVertexBufferObjectManager());
		
		mTime.setZIndex(12);
		mTiles.setZIndex(12);
		mTextScore.setZIndex(12);
		
		AlignedText text = new AlignedText(0, 20, mBigFont, "GREAT!", HorizontalAlign.CENTER, VerticalAlign.CENTER, cameraWidth-200, 100, this);
		
		text.setZIndex(13);
		
		
		//BUTTONS	
		Sprite restart = new Sprite(92, mScoreBackground.getHeight()-(128+32), mMenuTextures.get(GameValues.RESTART_ID), getVertexBufferObjectManager()){	
			@Override
	        public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionUp()){	
				onGameRestart();
				
				mScoreBackground.registerEntityModifier(new MoveYModifier(1.7f, mScoreBackground.getY(), -(cameraHeight-100), EaseSineIn.getInstance()));
				}
				return true;
			}
		};
		
		Sprite menu = new Sprite(236, mScoreBackground.getHeight()-(128+32), mMenuTextures.get(GameValues.MENU_ID), getVertexBufferObjectManager()){
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
		
		Sprite next = new Sprite(380, mScoreBackground.getHeight()-(128+32), mMenuTextures.get(GameValues.NEXT_ID), getVertexBufferObjectManager()){
			@Override
	        public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				
				if(pSceneTouchEvent.isActionUp()){

				mScoreBackground.registerEntityModifier(new MoveYModifier(1.7f, mScoreBackground.getY(), -(cameraHeight-100), new IEntityModifierListener() {
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

	}
	
	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()){
		
		case MENU_MAIN:
			this.startActivity(new Intent(this ,MainMenuActivity.class));
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
			
		case GameValues.END_UN_ID:
			id = GameValues.END_UN_ID;
			break;
		
		case GameValues.END_ID:
			id = GameValues.END_ID;
			break;
			
		case GameValues.TELEPORT_ID:
			id = GameValues.TELEPORT_ID;
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
				
				
				if(id == GameValues.ONESTEP_ID || id == GameValues.BLANK_ID)
					tiles++;
				
				mCTiles.setText("TILES: " + Integer.toString(tiles) + "/" + Integer.toString(maxTiles));
				
				mGameScene.addItem(col, row, id);
				level.setItem(col, row, id);
				
				//TELEPORT
				if(level.getItem(nCol, nRow) == GameValues.TELEPORT_ID)
				{	
				Debug.d("TELEPORT: nCol: " + Integer.toString(nCol) + " nRow: " + Integer.toString(nRow));
				int telID = level.getTeleportId(nCol, nRow);
				if(telID == 0)
					telID = 1;
				else
					telID = 0;
					
				Debug.d("TELEPORT: telCol: " + Integer.toString(level.getTeleport(telID)[0]) + " telRow: " + level.getTeleport(telID)[1]);
				
				player.teleport(level.getTeleport(telID)[0], level.getTeleport(telID)[1]);
				}
				
				//END
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
	
		timerStarted = false;
		
		calculateScore();
		
		mScoreBackground.registerEntityModifier(new MoveYModifier(2.0f, mScoreBackground.getY(), 50, EaseBackOut.getInstance()));
		
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
		
		mTime.setText("TIME: " + min + ":" + sec);
		
		get = Integer.toString(tiles);
		max = Integer.toString(level.getTiles());

		mTiles.setText("TILES: " + get + "/" + max);
		
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
			mTextHighScore.setText("NEW HIGH SCORE");
		}else{
			mTextHighScore.setText("HIGH :" + Integer.toString((int) mScore.getInt("score" + Integer.toString(level.getLevelpackId()) + "_" + Integer.toString(level.getId()), 0)));
		}
		
		
		
		mTextScore.setText("SCORE: " + Integer.toString((int)score));
		
	}
	
	private void onGameRestart(){
		timerStarted = false;
		time = 0;
		tiles = 0;
		canExit = false;
		starCounter = 0;
		mCTiles.setText("TILES: 0/" + Integer.toString(maxTiles));
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
	

	private void nextLevel(){
		final int levelID = level.getId();
		final int levelpackID = level.getLevelpackId();
		
		Debug.i("Level ID: " + Integer.toString(levelID));
		Debug.i("Level ID + 1: " + Integer.toString(levelID+1));
		
		canExit = false;
		starCounter = 0;
		
		for(int i = 0; i < 3; i++){
			if(stars[i] != null){
			stars[i].detachSelf();
			stars[i] = null;
			}

		}
		
		level.getPlayer().detachSelf();
		
		
		if(levelID < 15){
			
			level = LevelFileReader.getLevelFromFile(GameActivity.this, "level_"+ Integer.toString(levelpackID) + "_" +  Integer.toString(levelID+1));	
			maxTiles = level.getTiles();
			mCTiles.setText("TILES: 0/" + Integer.toString(maxTiles));
			LevelSceneFactory.redraw(GameActivity.this, mGameScene, level, mGameTexturePack);
		
	
			
			}else{
				
				GameActivity.this.startActivity(new Intent(GameActivity.this, MainMenuActivity.class));
				GameActivity.this.finish();
				overridePendingTransition(R.anim.fadein, R.anim.fadeout);
			}
		nextLevel = false;
		
	}
	
	
	
}
