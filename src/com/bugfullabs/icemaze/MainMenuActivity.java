package com.bugfullabs.icemaze;

import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.StrokeFont;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.VerticalAlign;
import org.andengine.util.texturepack.TexturePack;
import org.andengine.util.texturepack.TexturePackLoader;
import org.andengine.util.texturepack.TexturePackTextureRegion;
import org.andengine.util.texturepack.TexturePackTextureRegionLibrary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.Display;
import android.view.KeyEvent;

import com.bugfullabs.icemaze.level.Level;
import com.bugfullabs.icemaze.level.LevelFileReader;
import com.bugfullabs.icemaze.util.AlignedText;
import com.bugfullabs.icemaze.util.Button;



/**
 * 
 * @author Bugful Labs
 * @author Grushenko
 * @email  wojciech@bugfullabs.pl
 *
 */
         

public class MainMenuActivity extends SimpleBaseGameActivity{

	public static int cameraWidth;
	public static int cameraHeight;
	
	private SmoothCamera mCamera;

	private Scene mScene;
	
	private BitmapTextureAtlas mFontTexture;
	private StrokeFont mFont;
	
	private BitmapTextureAtlas mSFontTexture;
	private StrokeFont mSFont;
	
	private BitmapTextureAtlas mBigFontTexture;
	private StrokeFont mBigFont;
	
	private TexturePackTextureRegionLibrary mTextures;

	
	SharedPreferences mSettings;
	SharedPreferences.Editor mEditor;
	
	private boolean mToogleSound = false;
	private boolean mToogleMusic = false;
	private boolean mToogleAnim = true;
	
	private Button mAnimButton;
	private Button mMusicButton;
	private Button mSoundButton;
	
	private TexturePack mTexturePack;
	private TiledTextureRegion mButtonLongRegion;
	private TiledTextureRegion mButtonShortRegion;
	private TiledTextureRegion mButtonDoneRegion;
	private TiledTextureRegion mButtonFullRegion;
	
	/* LEVEL GRID */
	public static final int NUMBER_OF_ITEMS = 15;
	public static final int NUMBER_OF_ITEMS_IN_ROW = 5;
	
	public float offsetX;
	public float offsetY;
	
	public float marginX;
	public float marginY;
	
	public float gridButtonSize;
	
	private Button mGrid[][];
	

	private int levelpackId = 1;

	private boolean inStart = true;
	
  	private Button next;
  	private Button back;
	
  	private HUD mHud;

  	private float cameraInitX;
  	private float cameraInitY;
  	
	private SharedPreferences mScore;
	private SharedPreferences.Editor mScoreEditor;

	private AlignedText levelpack;
	private BitmapTextureAtlas mBig2FontTexture;
	private StrokeFont mBig2Font;

	
	@Override
	public EngineOptions onCreateEngineOptions() {
		
		Display disp = getWindowManager().getDefaultDisplay();
		
		cameraWidth = disp.getWidth();
		cameraHeight = disp.getHeight();
		
		gridButtonSize = 0.09f * cameraWidth;
		
		offsetX = gridButtonSize;
		offsetY = gridButtonSize*1.1f;
		
		
		marginX = cameraWidth/2 - ((gridButtonSize)*(NUMBER_OF_ITEMS_IN_ROW/2));
		marginY = cameraHeight/2 - (((gridButtonSize)*((NUMBER_OF_ITEMS/NUMBER_OF_ITEMS_IN_ROW)/2))) + offsetY + 16;
		
		mSettings = getSharedPreferences(GameValues.SETTINGS_FILE, 0);
		mEditor = mSettings.edit();

		mToogleAnim = mSettings.getBoolean("anim", true);
		mToogleSound = mSettings.getBoolean("sound", false);
		mToogleMusic = mSettings.getBoolean("music", false);		

		this.mCamera = new SmoothCamera(0, 0, MainMenuActivity.cameraWidth, MainMenuActivity.cameraHeight, 1000, 1000, 1.0f);

		
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), this.mCamera);
	}

	@Override
	protected void onCreateResources() {

		
		this.mFontTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        Typeface typeface =  Typeface.createFromAsset(getAssets(), "font/FOO.ttf");
        mFont = new StrokeFont(this.getFontManager(), mFontTexture, typeface, cameraWidth*0.04f, true, Color.WHITE, 2, Color.BLACK);
        
        
        mFontTexture.load();
		mFont.load();
        
        this.mSFontTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        typeface =  Typeface.createFromAsset(getAssets(), "font/segoeprb.ttf");
        mSFont = new StrokeFont(this.getFontManager(), mSFontTexture, typeface, cameraWidth/22, true, Color.WHITE, 2, Color.BLACK);
        
        
        mSFontTexture.load();
        mSFont.load();
        
        this.mBigFontTexture = new BitmapTextureAtlas(this.getTextureManager(), 1024, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        typeface =  Typeface.createFromAsset(getAssets(), "font/segoeprb.ttf");
        mBigFont = new StrokeFont(this.getFontManager(), mBigFontTexture, typeface, cameraWidth/6.4f, true, Color.argb(255, 210, 236, 255), 2, Color.BLACK);
        
        
        mBigFontTexture.load();
        mBigFont.load();
        
        
        this.mBig2FontTexture = new BitmapTextureAtlas(this.getTextureManager(), 1024, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        typeface =  Typeface.createFromAsset(getAssets(), "font/segoeprb.ttf");
        mBig2Font = new StrokeFont(this.getFontManager(), mBig2FontTexture, typeface, cameraWidth/8.5f, true, Color.argb(255, 210, 236, 255), 2, Color.BLACK);
        
        
        mBig2FontTexture.load();
        mBig2Font.load();
        
        TexturePackLoader tpl = new TexturePackLoader(getAssets(), getTextureManager());
        try {
		
        mTexturePack = tpl.loadFromAsset("gfx/menu/menu.xml", "gfx/menu/");

        mTexturePack.loadTexture();
        mTextures = mTexturePack.getTexturePackTextureRegionLibrary();
        } catch (Exception e) {
			e.printStackTrace();
			
		}
        
       
        TexturePackTextureRegion shortTextureRegion = mTextures.get(GameValues.BUTTONSHORT_ID);
        mButtonShortRegion = TiledTextureRegion.create(mTexturePack.getTexture(), (int) shortTextureRegion.getTextureX(), (int)shortTextureRegion.getTextureY(), shortTextureRegion.getSourceWidth(), shortTextureRegion.getSourceHeight(), 2, 1);
       
        TexturePackTextureRegion textureRegion = mTextures.get(GameValues.BUTTONLONG_ID);
        mButtonLongRegion = TiledTextureRegion.create(mTexturePack.getTexture(), (int) textureRegion.getTextureX(), (int) textureRegion.getTextureY(), textureRegion.getSourceWidth(), textureRegion.getSourceHeight(), 2, 1);
        
        textureRegion = mTextures.get(GameValues.BUTTONSHORTDONE_ID);
        mButtonDoneRegion = TiledTextureRegion.create(mTexturePack.getTexture(), (int) textureRegion.getTextureX(), (int) textureRegion.getTextureY(), textureRegion.getSourceWidth(), textureRegion.getSourceHeight(), 2, 1);
        
        textureRegion = mTextures.get(GameValues.BUTTONSHORTFULL_ID);
        mButtonFullRegion = TiledTextureRegion.create(mTexturePack.getTexture(), (int) textureRegion.getTextureX(), (int) textureRegion.getTextureY(), textureRegion.getSourceWidth(), textureRegion.getSourceHeight(), 2, 1);
        
       
        
	}

	@Override
	protected Scene onCreateScene() {
		
		mEngine.registerUpdateHandler(new FPSLogger());
		
		mGrid = new Button[GameValues.LEVELPACKS][15];
		
		mScore = getSharedPreferences(GameValues.SCORE_FILE, 0);
		mScoreEditor = mScore.edit();
		
		
		cameraInitX = mCamera.getCenterX();
		cameraInitY = mCamera.getCenterY();
		
		
		this.mScene = new Scene();
		
		Sprite bg = new Sprite(0, 0, mTextures.get(GameValues.BG_ID), getVertexBufferObjectManager());
		//bg.setSize(cameraWidth, cameraHeight);
		//bg.setSize(cameraWidth, (cameraWidth/800)*bg.getHeight());
		bg.setWidth(cameraWidth);
		bg.setHeight(cameraWidth/1.33333f);
		mScene.setBackground(new SpriteBackground(bg));
		
		
		mScene.attachChild(new AlignedText(0, 0, mBigFont, getString(R.string.app_name), HorizontalAlign.CENTER, VerticalAlign.TOP, cameraWidth, 120, this));
		
		mScene.attachChild(new AlignedText(0, cameraHeight, mBigFont, getString(R.string.options), HorizontalAlign.CENTER, VerticalAlign.TOP, cameraWidth, 120, this));
		
		mScene.attachChild(new AlignedText(0, cameraHeight-cameraWidth/22-10, mSFont, getString(R.string.version), HorizontalAlign.RIGHT, VerticalAlign.BOTTOM, cameraWidth-20, cameraWidth/22, this));
		
		
		/* MIAN MENU */
		
		
		
		
		/* PLAY */
		new Button(this, mScene, (cameraWidth*0.69f)/2, (cameraHeight*0.84f)/2, cameraWidth*0.31f, cameraHeight*0.16f, getString(R.string.newgame), mButtonLongRegion, mFont){
			@Override
			public boolean onButtonPressed(){	
			inStart = false;

			mCamera.setCenter(cameraInitX+cameraWidth, cameraInitY);
			mHud.setVisible(true);

			return true;
			}
		};
		
		
		
		/* SETTINGS */
		new Button(this, mScene, (cameraWidth*0.69f)/2, (cameraHeight*1.16f)/2, cameraWidth*0.31f, cameraHeight*0.16f, getString(R.string.options), mButtonLongRegion, mFont){
			@Override
			public boolean onButtonPressed(){
			mCamera.setCenter(cameraInitX, cameraInitY+cameraHeight);
			inStart = false;
			return true;
			}
		};
		
		/* CREDITS */
		new Button(this, mScene, (cameraWidth*0.69f)/2, (cameraHeight*1.48f)/2, cameraWidth*0.31f, cameraHeight*0.16f, getString(R.string.credits), mButtonLongRegion, mFont){
			@Override
			public boolean onButtonPressed(){	
				mCamera.setCenter(cameraInitX-cameraWidth, cameraInitY);
				inStart = false;
				return true;
			}
		};
		
		
		

		
		mMusicButton = new Button(this, mScene, cameraWidth*0.14f, cameraHeight*1.38f, cameraWidth*0.31f, cameraHeight*0.16f, getString(R.string.music) + ": " + getString(R.string.yes), mButtonLongRegion, mFont){
			@Override
			public boolean onButtonPressed(){	
				mToogleMusic = !mToogleMusic;
				
				mEditor.putBoolean("music", mToogleMusic);
				mEditor.commit();
				if(mToogleMusic == true){
				this.setText(getString(R.string.music)+  ": " + getString(R.string.yes));
				}else{
				this.setText(getString(R.string.music)+  ": " + getString(R.string.no));
				}
				return true;
			}
		};
		
		
		mSoundButton = new Button(this, mScene, cameraWidth*0.14f, cameraHeight*1.57f, cameraWidth*0.31f, cameraHeight*0.16f, getString(R.string.sound) + ": " + getString(R.string.yes), mButtonLongRegion, mFont){
			@Override
			public boolean onButtonPressed(){	
				mToogleSound = !mToogleSound;
				
				mEditor.putBoolean("sound", mToogleSound);
				mEditor.commit();
				if(mToogleSound == true){
				this.setText(getString(R.string.sound) + ": " + getString(R.string.yes));
				}else{
				this.setText(getString(R.string.sound) + ": " + getString(R.string.no));
				}
				return true;
			}
		};

		
		mAnimButton = new Button(this, mScene, cameraWidth*0.55f, cameraHeight*1.38f, cameraWidth*0.31f, cameraHeight*0.16f, getString(R.string.anim) + ": " + getString(R.string.yes), mButtonLongRegion, mFont){
			@Override
			public boolean onButtonPressed(){	
				mToogleAnim = !mToogleAnim;
				
				mEditor.putBoolean("anim", mToogleAnim);
				mEditor.commit();
				if(mToogleAnim == true){
				this.setText(getString(R.string.anim) + ": " + getString(R.string.yes));
				}else{
				this.setText(getString(R.string.anim) + ": " + getString(R.string.no));
				}
				return true;
			}
		};
		
		
		new Button(this, mScene, cameraWidth*0.55f, cameraHeight*1.57f, cameraWidth*0.31f, cameraHeight*0.16f, getString(R.string.reset), mButtonLongRegion, mFont){
			@Override
			public boolean onButtonPressed(){	


				
			        	   mScoreEditor.clear();
							
			        	   mScoreEditor.commit();
							
							/* REDRAW GRID */
							
							for(int i = 0; i < GameValues.LEVELPACKS; i++){
								for(int j = 0; j < 15; j++){
								
								mGrid[i][j].detachSelf();	
								mGrid[i][j] = null;
								}
							}
							
							drawGrid();
			           			     
				return true;
			}
		};
		
		
		
		Sprite mBackButton = new Sprite(0, cameraHeight *2 - cameraWidth*0.1f , mTextures.get(GameValues.BACK_M_ID), getVertexBufferObjectManager()){
			
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionUp()){
					
					mCamera.setCenter(cameraInitX, cameraInitY);
					
					return true;
				}
			return false;
			}
			
		};
		
		mBackButton.setSize(cameraWidth*0.1f, cameraWidth*0.1f);
		mScene.attachChild(mBackButton);
		mScene.registerTouchArea(mBackButton);


		mBackButton = new Sprite(-cameraWidth, cameraHeight - cameraWidth*0.1f , mTextures.get(GameValues.BACK_M_ID), getVertexBufferObjectManager()){
			
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionUp()){
					
					mCamera.setCenter(cameraInitX, cameraInitY);
					
					return true;
				}
			return false;
			}
			
		};
		mBackButton.setSize(cameraWidth*0.1f, cameraWidth*0.1f);
		mScene.attachChild(mBackButton);
		mScene.registerTouchArea(mBackButton);

		/* XXX:CREDITS */
		
		mScene.attachChild(new AlignedText(-cameraWidth, 0, mBigFont, getString(R.string.credits), HorizontalAlign.CENTER, VerticalAlign.TOP, cameraWidth, 120, this));

		Text credits = new Text(-cameraWidth + 20, cameraHeight/2-32, this.mSFont, getString(R.string.creditstext), getVertexBufferObjectManager());
		credits.setTextOptions(new TextOptions(HorizontalAlign.LEFT));
		mScene.attachChild(credits);
		
		
		new Button(this, mScene, -(cameraWidth*0.38f)-30, cameraHeight - cameraHeight*0.13f - 20, cameraWidth*0.38f, cameraHeight*0.13f, getString(R.string.our_apps), mButtonLongRegion, mFont){
			@Override
			public boolean onButtonPressed(){	
				
		        Intent intent = new Intent(Intent.ACTION_VIEW);
		        intent.setData(Uri.parse("market://search?q=pub:BugfulLabs.pl"));
		        startActivity(intent);

				overridePendingTransition(R.anim.fadein, R.anim.fadeout);
				return true;
			}
		};
		
		
		
		/* LEVEL SELECT GRID */
	  	
	  	mHud = new HUD();
	  	
	  	levelpack = new AlignedText(0,  marginY - 96, mFont, "LEVELPACK: " + Integer.toString(levelpackId), HorizontalAlign.CENTER, VerticalAlign.CENTER, cameraWidth, 24, this);
	  	
  
	  	back = new Button(this, mHud, marginX - (gridButtonSize/2 + 128) , marginY + offsetY - gridButtonSize/2, gridButtonSize, gridButtonSize, "-", mButtonShortRegion, mFont){
	  		@Override
	  		public boolean onButtonPressed(){
				
	  			if(levelpackId > 1){
	  			levelpackId--;	
	  			levelpack.setText("LEVELPACK: " + Integer.toString(levelpackId));
	  			next.setVisible(true);	
	  			
	  			mCamera.setCenter(cameraInitX + cameraWidth*levelpackId, mCamera.getCenterY());
	  			
	  			if(levelpackId <= 1){
	  			back.setVisible(false);
	  			}
	  			}
				return true;
			  }
	  	};
	  	
	  	
	  	next = new Button(this, mHud,  cameraWidth - marginX - gridButtonSize/2 + 128 , marginY + offsetY - gridButtonSize/2, gridButtonSize, gridButtonSize, "+", mButtonShortRegion, mFont){
	  		@Override
	  		public boolean onButtonPressed(){
	  			
				if(levelpackId < GameValues.LEVELPACKS){
	  			levelpackId++;
	  			levelpack.setText("LEVELPACK: " + Integer.toString(levelpackId));
	  			back.setVisible(true);

	  			mCamera.setCenter(cameraInitX + cameraWidth*levelpackId, mCamera.getCenterY());
	  			
	  			if(levelpackId >= GameValues.LEVELPACKS){
	  			this.setVisible(false);
	  			}
				}
				return true;
			  }
	  	};
	  	
	  	back.setVisible(false);
	  	
	  	
	  	
	  	mHud.attachChild(levelpack);
	  	mHud.attachChild(new AlignedText(0, 0, mBig2Font, getString(R.string.select_level), HorizontalAlign.CENTER, VerticalAlign.TOP, cameraWidth, 120, this));
		
		mBackButton = new Sprite(0, cameraHeight - cameraWidth*0.1f , mTextures.get(GameValues.BACK_M_ID), getVertexBufferObjectManager()){
			
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionUp()){
					mCamera.setCenter(cameraInitX, cameraInitY);
					mHud.setVisible(false);
					levelpackId = 1;
					next.setVisible(true);
					back.setVisible(false);
					levelpack.setText("LEVELPACK: " + Integer.toString(levelpackId));
					return true;
				}
			return false;
			}
			
		};
		mBackButton.setSize(cameraWidth*0.1f, cameraWidth*0.1f);
		mHud.attachChild(mBackButton);
		mHud.registerTouchArea(mBackButton);
	  	
	  	
	  	mHud.setVisible(false);
	  	mCamera.setHUD(mHud);
	  	
	  	
	  	
	  	/* CREATE GRID */

		drawGrid();
		
		
		if(mToogleMusic != true)
			this.mMusicButton.setText(getString(R.string.music)+  ": " + getString(R.string.no));
		
		if(mToogleSound != true)
			this.mSoundButton.setText(getString(R.string.sound) + ": " + getString(R.string.no));
		
		if(mToogleAnim != true)
			this.mAnimButton.setText(getString(R.string.anim) + ": " + getString(R.string.no));
				
		
		return mScene;
	}
	
	


		private void drawGrid(){
		  	  int i = 0;
		  	  
		  	  for(int z = 0; z < GameValues.LEVELPACKS; z++){
		  	  i = 0;
		  	  for(int j = 0; j < (NUMBER_OF_ITEMS/NUMBER_OF_ITEMS_IN_ROW); j++){
					for(int k = 0; k < NUMBER_OF_ITEMS_IN_ROW; k++){
					final int id = i + 1;

					TiledTextureRegion buttonRegion;
					
					if(mScore.getInt("score" + Integer.toString(z+1) + "_" + Integer.toString(id), 0) > 0){
					if(mScore.getBoolean("full" + Integer.toString(z+1) + "_" + Integer.toString(id), false)){
					buttonRegion = mButtonFullRegion;
					}else{
					buttonRegion = mButtonDoneRegion;
					}
					}else{
					buttonRegion = mButtonShortRegion;
					}
					
						
					mGrid[z][i] = new Button(this, mScene, (z+1)*cameraWidth + marginX + (k * offsetX) - gridButtonSize/2, marginY + (j * offsetY)- gridButtonSize/2, gridButtonSize, gridButtonSize, Integer.toString(id),buttonRegion , mFont){
		    			  @Override
		    			  public boolean onButtonPressed(){
		    				MainMenuActivity.this.onLevelSelected(id, levelpackId);
		    				return true;
		    			  }
		    		  };
	 
		    		  i++;
					}
				}
		  	  
		  	  
		  	  }
		}
	


	    private void onLevelSelected(int id, int level_pack)
	    {


	    	try {
			  	final Level level = LevelFileReader.getLevelFromFile(this, "level_"+ Integer.toString(level_pack) + "_" + Integer.toString(id));

			  	GameActivity.setLevel(level);
			  	
			  	this.startActivity(new Intent(this, GameActivity.class));
			  	this.finish();
			  	overridePendingTransition(R.anim.fadein, R.anim.fadeout);
			  	
			} catch (Exception e) {
				e.printStackTrace();
			}

	    }
	
	
	    @Override
		public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
			if(pKeyCode == KeyEvent.KEYCODE_BACK  && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
				
				if(!inStart){
				mCamera.setCenter(cameraInitX, cameraInitY);
				mHud.setVisible(false);
				levelpackId = 1;
				next.setVisible(true);
				back.setVisible(false);
				levelpack.setText("LEVELPACK: " + Integer.toString(levelpackId));
				inStart = true;
				}else{
				MainMenuActivity.this.finish();
				}
				
				
				
				return true;
			}
			return super.onKeyDown(pKeyCode, pEvent); 
		}

	    
/*
	    @Override
	    public void onResumeGame(){
	    	
	    	super.onResumeGame();
	    	
	    	if(mGrid[0][0] != null && !isDrawingGrid)
	    	{
			
	    	isDrawingGrid = true;
	    		
	    	for(int i = 0; i < GameValues.LEVELPACKS; i++){
				for(int j = 0; j < 15; j++){
				
				mGrid[i][j].detachSelf();	
				mGrid[i][j] = null;
				
				
				}
			}
			
			drawGrid();
	    	isDrawingGrid = false;
	    	}
	    	
	    	
	    	
	    	Debug.i("MainMEnuActivity", "onResumeGame()");
	    	
	    */
	    	
}