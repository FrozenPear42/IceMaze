package com.bugfullabs.icemaze;

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
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.font.StrokeFont;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.VerticalAlign;
import org.andengine.util.modifier.IModifier;
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


public class MainMenuActivity extends SimpleBaseGameActivity{

	public static int cameraWidth;
	public static int cameraHeight;
	private Camera mCamera;

	private Scene mMainScene;
	private Scene mOptionsScene; 
	
	private BitmapTextureAtlas mFontTexture;
	private StrokeFont mFont;

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
	
	
	/* LEVEL GRID */
	public static final int NUMBER_OF_ITEMS = 15;
	public static final int NUMBER_OF_ITEMS_IN_ROW = 5;
	
	public static final float offsetX = 72;
	public static final float offsetY = 72;
	
	public static float marginX;
	public static float marginY;
	
	private Scene mGridScene;

	private int levelpackId = 1;

	private boolean inStart = true;
	
  	private Button next;
  	private Button back;
	
	
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		
		//Display disp = getWindowManager().getDefaultDisplay();
		
		//MainMenuActivity.cameraWidth = disp.getWidth();
		//MainMenuActivity.cameraHeight = disp.getHeight();
		
		
		
		cameraWidth = 800;
		cameraHeight = 480;
		
		marginX = cameraWidth/2 - ((offsetX)*(NUMBER_OF_ITEMS_IN_ROW/2));
		marginY = cameraHeight/2 - (((offsetY)*((NUMBER_OF_ITEMS/NUMBER_OF_ITEMS_IN_ROW)/2))) + offsetY + 16;
		
		mSettings = getSharedPreferences(GameValues.SETTINGS_FILE, 0);
		mEditor = mSettings.edit();
		
		mToogleSound = mSettings.getBoolean("sound", false);
		mToogleMusic = mSettings.getBoolean("music", false);		

		this.mCamera = new Camera(0, 0, MainMenuActivity.cameraWidth, MainMenuActivity.cameraHeight);
		
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(MainMenuActivity.cameraWidth, MainMenuActivity.cameraHeight), this.mCamera);
	}

	@Override
	protected void onCreateResources() {

		
		this.mFontTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        Typeface typeface =  Typeface.createFromAsset(getAssets(), "font/FOO.ttf");
        mFont = new StrokeFont(this.getFontManager(), mFontTexture, typeface, 30, true, Color.WHITE, 2, Color.BLACK);
        
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
        
     
        mFontTexture.load();
		mFont.load();
        
	}

	@Override
	protected Scene onCreateScene() {
		this.mMainScene = new Scene();
		mMainScene.setBackground(new SpriteBackground(new Sprite(0, 0, mTextures.get(GameValues.BG_ID), getVertexBufferObjectManager())));
		
		this.mOptionsScene = new Scene();
		mOptionsScene.setBackground(new SpriteBackground(new Sprite(0, 0, mTextures.get(GameValues.BG_ID), getVertexBufferObjectManager())));
		
		mMainScene.attachChild(new Sprite(0, 0, mTextures.get(GameValues.TITLE_ID), getVertexBufferObjectManager()));
		mOptionsScene.attachChild(new Sprite(0, 0, mTextures.get(GameValues.SETTINGS_ID), getVertexBufferObjectManager()));
		
		
		setLevelSelectGrid();
		
		
		new Button(this, mMainScene, (cameraWidth/2)-125, (cameraHeight/2)-37.5f, 250, 75, getString(R.string.newgame), mButtonLongRegion, mFont){
			@Override
			public boolean onButtonPressed(){	
			inStart = false;
			changeSceneWithFade(mGridScene, 0.2f);
			return true;
			}
		};
		
		new Button(this, mMainScene, (cameraWidth/2)-125, (cameraHeight/2)-37.5f+75, 250, 75, getString(R.string.options), mButtonLongRegion, mFont){
			@Override
			public boolean onButtonPressed(){	
			inStart = false;
			changeSceneWithFade(mOptionsScene, 0.3f);	
				
			return true;
			}
		};
		
		new Button(this, mMainScene, (cameraWidth/2)-125, (cameraHeight/2)-37.5f+150, 250, 75, getString(R.string.exit), mButtonLongRegion, mFont){
			@Override
			public boolean onButtonPressed(){	
				MainMenuActivity.this.finish();
				return true;
			}
		};
		
		
		
		
		mMusicButton = new Button(this, mOptionsScene, (cameraWidth/2)-125, (cameraHeight/2)-37.5f, 250, 75, getString(R.string.music) + ": " + getString(R.string.yes), mButtonLongRegion, mFont){
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
		
		
		mSoundButton = new Button(this, mOptionsScene, (cameraWidth/2)-125, (cameraHeight/2)-37.5f+75, 250, 75, getString(R.string.sound) + ": " + getString(R.string.yes), mButtonLongRegion, mFont){
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
		
		new Button(this, mOptionsScene, (cameraWidth/2)-125, (cameraHeight/2)-37.5f+150, 250, 75, getString(R.string.reset), mButtonLongRegion, mFont){
			@Override
			public boolean onButtonPressed(){	
				inStart = true;
				changeSceneWithFade(mMainScene, 0.3f);	
				return true;
			}
		};
		
		
		if(mToogleMusic != true)
		{
		this.mMusicButton.setText(getString(R.string.music)+  ": " + getString(R.string.no));
		}
		
		if(mToogleSound != true)
		{
			this.mSoundButton.setText(getString(R.string.sound) + ": " + getString(R.string.no));
		}
		
		
		return mMainScene;
	}
	
	
	
	private void setLevelSelectGrid() {
    	
	
	  	mGridScene = new Scene();

	  	
	  	
	  	//final Sprite bg = new Sprite(0, 0, mBackgroundTextureRegion, getVertexBufferObjectManager());
	  	//bg.setWidth(cameraWidth);
	  	//bg.setHeight(cameraHeight);
	  	//mGridScene.setBackground(new SpriteBackground(bg));
	  	mGridScene.setBackground(new SpriteBackground(new Sprite(0, 0, mTextures.get(GameValues.BG_ID), getVertexBufferObjectManager())));
		
		mGridScene.attachChild(new Sprite(0, 0, mTextures.get(GameValues.LEVELSELECT_ID), getVertexBufferObjectManager()));
		

	  	final AlignedText levelpack = new AlignedText(0,  marginY - 72, mFont, "LEVELPACK: " + Integer.toString(levelpackId), HorizontalAlign.CENTER, VerticalAlign.CENTER, cameraWidth, 24, this);
	  	
	  	mGridScene.attachChild(levelpack);	  

	  	back = new Button(this, mGridScene, marginX - (36 + 128) , marginY + offsetY -36, 72, 72, "-", mButtonShortRegion, mFont){
	  		@Override
	  		public boolean onButtonPressed(){
				
	  			if(levelpackId > 1){
	  			levelpackId--;	
	  			levelpack.setText("LEVELPACK: " + Integer.toString(levelpackId));
	  			next.setVisible(true);	
	  			if(levelpackId <= 1){
	  			back.setVisible(false);
	  			}
	  			}
				return true;
			  }
	  	};
	  	
	  	back.setVisible(false);
	  	
	  	next = new Button(this, mGridScene,  cameraWidth - marginX - 36 + 128 , marginY + offsetY -36, 72, 72, "+", mButtonShortRegion, mFont){
	  		@Override
	  		public boolean onButtonPressed(){
	  			
				if(levelpackId < GameValues.LEVELPACKS){
	  			levelpackId++;
	  			levelpack.setText("LEVELPACK: " + Integer.toString(levelpackId));
	  			back.setVisible(true);
	  			if(levelpackId >= GameValues.LEVELPACKS){
	  			this.setVisible(false);
	  			}
				}
				return true;
			  }

	  	};
	  	
	
	  	  int i = 0;

	  	  
	  	  for(int j = 0; j < (NUMBER_OF_ITEMS/NUMBER_OF_ITEMS_IN_ROW); j++){

				for(int k = 0; k < NUMBER_OF_ITEMS_IN_ROW; k++){

				final int id = i + 1;
				
				//TODO: DONE LEVELS
				
				new Button(this, mGridScene,  marginX + (k * offsetX) - 36, marginY + (j * offsetY)- 36, 72, 72, Integer.toString(id),  mButtonShortRegion, mFont){
	    			  @Override
	    			  public boolean onButtonPressed(){
	    				MainMenuActivity.this.onLevelSelected(id, levelpackId);
	    				return true;
	    			  }
	    		  };
	    		  
	    		  i++;
				}
			}


	  	  this.mEngine.setScene(mGridScene);

	    }



	    private void onLevelSelected(int id, int level_pack)
	    {


	    	try {
			  	final Level level = LevelFileReader.getLevelFromFile(this, "level_"+ Integer.toString(level_pack) + "_" + Integer.toString(id));

			  	GameActivity.setLevel(level);

			  	this.startActivity(new Intent(this, GameActivity.class));
			  	overridePendingTransition(R.anim.fadein, R.anim.fadeout);
			  	
			} catch (Exception e) {
				e.printStackTrace();
			}

	    }
	
	
	    @Override
		public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
			if(pKeyCode == KeyEvent.KEYCODE_BACK  && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
				
				if(!inStart){

				changeSceneWithFade(mMainScene, 0.2f);	
				inStart = true;
				}else{
				MainMenuActivity.this.finish();
				}
				
				
				return true;
			}
			return super.onKeyDown(pKeyCode, pEvent); 
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
			MainMenuActivity.this.getEngine().setScene(s);
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