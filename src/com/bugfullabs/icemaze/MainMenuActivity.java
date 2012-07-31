package com.bugfullabs.icemaze;

import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
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

//TODO: REDRAW GRID AFTER RESET & GAME

public class MainMenuActivity extends SimpleBaseGameActivity{

	public static int cameraWidth;
	public static int cameraHeight;
	
	private SmoothCamera mCamera;

	private Scene mScene;
	
	private BitmapTextureAtlas mFontTexture;
	private StrokeFont mFont;
	
	private BitmapTextureAtlas mSFontTexture;
	private StrokeFont mSFont;
	

	private TexturePackTextureRegionLibrary mTextures;
	
	
	SharedPreferences mSettings;
	SharedPreferences.Editor mEditor;
	
	private boolean mToogleSound = false;
	private boolean mToogleMusic = false;
	
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
	
	public static final float offsetX = 84;
	public static final float offsetY = 72;
	
	public static float marginX;
	public static float marginY;
	

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
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		
		cameraWidth = 800;
		cameraHeight = 480;
		
		marginX = cameraWidth/2 - ((offsetX)*(NUMBER_OF_ITEMS_IN_ROW/2));
		marginY = cameraHeight/2 - (((offsetY)*((NUMBER_OF_ITEMS/NUMBER_OF_ITEMS_IN_ROW)/2))) + offsetY + 16;
		
		mSettings = getSharedPreferences(GameValues.SETTINGS_FILE, 0);
		mEditor = mSettings.edit();
		
		mToogleSound = mSettings.getBoolean("sound", false);
		mToogleMusic = mSettings.getBoolean("music", false);		

		this.mCamera = new SmoothCamera(0, 0, MainMenuActivity.cameraWidth, MainMenuActivity.cameraHeight, 800, 800, 1.0f);

		
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(MainMenuActivity.cameraWidth, MainMenuActivity.cameraHeight), this.mCamera);
	}

	@Override
	protected void onCreateResources() {

		
		this.mFontTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        Typeface typeface =  Typeface.createFromAsset(getAssets(), "font/FOO.ttf");
        mFont = new StrokeFont(this.getFontManager(), mFontTexture, typeface, 30, true, Color.WHITE, 2, Color.BLACK);
        
        this.mSFontTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        typeface =  Typeface.createFromAsset(getAssets(), "font/segoeprb.ttf");
        mSFont = new StrokeFont(this.getFontManager(), mSFontTexture, typeface, 40, true, Color.WHITE, 2, Color.BLACK);
        
        mSFontTexture.load();
        mSFont.load();
        
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
        
        
     
        mFontTexture.load();
		mFont.load();
        
	}

	@Override
	protected Scene onCreateScene() {
		
		mScore = getSharedPreferences(GameValues.SCORE_FILE, 0);
		mScoreEditor = mScore.edit();
		
		
		cameraInitX = mCamera.getCenterX();
		cameraInitY = mCamera.getCenterY();
		
		
		this.mScene = new Scene();
		mScene.setBackground(new SpriteBackground(new Sprite(0, 0, mTextures.get(GameValues.BG_ID), getVertexBufferObjectManager())));
		
		mScene.attachChild(new Sprite(0, 0, mTextures.get(GameValues.TITLE_ID), getVertexBufferObjectManager()));
		mScene.attachChild(new Sprite(0, cameraHeight, mTextures.get(GameValues.SETTINGS_ID), getVertexBufferObjectManager()));
		
		
		
		/* PLAY */
		new Button(this, mScene, (cameraWidth/2)-125, (cameraHeight/2)-37.5f, 250, 75, getString(R.string.newgame), mButtonLongRegion, mFont){
			@Override
			public boolean onButtonPressed(){	
			inStart = false;

			mCamera.setCenter(cameraInitX+cameraWidth, cameraInitY);
			mHud.setVisible(true);
			return true;
			}
		};
		
		/* SETTINGS */
		new Button(this, mScene, (cameraWidth/2)-125, (cameraHeight/2)-37.5f+75, 250, 75, getString(R.string.options), mButtonLongRegion, mFont){
			@Override
			public boolean onButtonPressed(){
			mCamera.setCenter(cameraInitX, cameraInitY+cameraHeight);
			inStart = false;
			return true;
			}
		};
		
		/* CREDITS */
		new Button(this, mScene, (cameraWidth/2)-125, (cameraHeight/2)-37.5f+150, 250, 75, getString(R.string.credits), mButtonLongRegion, mFont){
			@Override
			public boolean onButtonPressed(){	
				mCamera.setCenter(cameraInitX-cameraWidth, cameraInitY);
				inStart = false;
				return true;
			}
		};
		
		
		
		//FIXME:
		
		mMusicButton = new Button(this, mScene, 110, cameraHeight + 180, 250, 75, getString(R.string.music) + ": " + getString(R.string.yes), mButtonLongRegion, mFont){
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
		
		
		mSoundButton = new Button(this, mScene, 110, cameraHeight+275, 250, 75, getString(R.string.sound) + ": " + getString(R.string.yes), mButtonLongRegion, mFont){
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
		
		new Button(this, mScene, 440, cameraHeight+180, 250, 75, getString(R.string.reset), mButtonLongRegion, mFont){
			@Override
			public boolean onButtonPressed(){	
				mScoreEditor.clear();
				mScoreEditor.commit();
				
				return true;
			}
		};
		
		
		new Button(this, mScene, 440, cameraHeight+275, 250, 75, "Steering", mButtonLongRegion, mFont){
			@Override
			public boolean onButtonPressed(){	

				return true;
			}
		};
		
		Sprite mBackButton = new Sprite(0, cameraHeight *2 - 72 , mTextures.get(GameValues.BACK_M_ID), getVertexBufferObjectManager()){
			
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionUp()){
					
					mCamera.setCenter(cameraInitX, cameraInitY);
					
					return true;
				}
			return false;
			}
			
		};
		
		mScene.attachChild(mBackButton);
		mScene.registerTouchArea(mBackButton);


		mScene.attachChild(new Sprite(-cameraWidth, 0, mTextures.get(GameValues.CREDITS_ID), getVertexBufferObjectManager()));
		Text credits = new Text(-cameraWidth + 20, cameraHeight/2-32, this.mSFont, getString(R.string.creditstext), getVertexBufferObjectManager());
		credits.setTextOptions(new TextOptions(HorizontalAlign.LEFT));
		mScene.attachChild(credits);
		
		
		
		/* LEVEL SELECT GRID */
	  	
	  	mHud = new HUD();
	  	
	  	levelpack = new AlignedText(0,  marginY - 96, mFont, "LEVELPACK: " + Integer.toString(levelpackId), HorizontalAlign.CENTER, VerticalAlign.CENTER, cameraWidth, 24, this);
	  	
  
	  	back = new Button(this, mHud, marginX - (36 + 128) , marginY + offsetY -36, 72, 72, "-", mButtonShortRegion, mFont){
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
	  	
	  	
	  	next = new Button(this, mHud,  cameraWidth - marginX - 36 + 128 , marginY + offsetY -36, 72, 72, "+", mButtonShortRegion, mFont){
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
	  	mHud.attachChild(new Sprite(0, 0, mTextures.get(GameValues.LEVELSELECT_ID), getVertexBufferObjectManager()));
	  	
	  	mHud.setVisible(false);
	  	mCamera.setHUD(mHud);
	  	
	  	
	  	
	  	/* CREATE GRID */
	  	
	  	  int i = 0;
	  	  
	  	  for(int z = 0; z < GameValues.LEVELPACKS; z++){
	  	  i = 0;
	  	  for(int j = 0; j < (NUMBER_OF_ITEMS/NUMBER_OF_ITEMS_IN_ROW); j++){
				for(int k = 0; k < NUMBER_OF_ITEMS_IN_ROW; k++){
				final int id = i + 1;
				//TODO: DONE LEVELS
				
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
				
					
				new Button(this, mScene, (z+1)*cameraWidth + marginX + (k * offsetX) - 36, marginY + (j * offsetY)- 36, 72, 72, Integer.toString(id),buttonRegion , mFont){
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
		
		
		
		
		
		if(mToogleMusic != true)
			this.mMusicButton.setText(getString(R.string.music)+  ": " + getString(R.string.no));
		
		if(mToogleSound != true)
			this.mSoundButton.setText(getString(R.string.sound) + ": " + getString(R.string.no));
		
		
		return mScene;
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

	

	
}