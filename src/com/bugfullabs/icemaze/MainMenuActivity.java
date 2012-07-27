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
import org.andengine.entity.scene.background.Background;
import org.andengine.opengl.font.StrokeFont;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;
import org.andengine.util.modifier.IModifier;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Display;

import com.bugfullabs.icemaze.level.LevelFileReader;
import com.bugfullabs.icemaze.util.Button;


/**
 * 
 * @author Bugful Labs
 * @author Grushenko
 * @email  wojciech@bugfullabs.pl
 *
 */


public class MainMenuActivity extends SimpleBaseGameActivity{

	private int cameraWidth;
	private int cameraHeight;
	private Camera mCamera;
	//private TextureRegion mValveRegion;
	private BitmapTextureAtlas mTextureAtlas;
	
	private Scene mMainScene;
	private Scene mOptionsScene; 
	
	private BitmapTextureAtlas mFontTexture;
	private StrokeFont mFont;
	//private Sprite mValve;
	private TiledTextureRegion mButtonRegion;
	
	SharedPreferences mSettings;
	SharedPreferences.Editor mEditor;
	private static final String SETTINGS_FILE = "Settings";
	
	private boolean mToogleSound = false;
	private boolean mToogleMusic = false;
	
	private Button mMusicButton;
	private Button mSoundButton;
	


	@Override
	public EngineOptions onCreateEngineOptions() {
		
		Display disp = getWindowManager().getDefaultDisplay();
		
		this.cameraWidth = disp.getWidth();
		this.cameraHeight = disp.getHeight();
		
		mSettings = getSharedPreferences(SETTINGS_FILE, 0);
		mEditor = mSettings.edit();
		
		mToogleSound = mSettings.getBoolean("sound", false);
		mToogleMusic = mSettings.getBoolean("music", false);		

		this.mCamera = new Camera(0, 0, this.cameraWidth, this.cameraHeight);
		
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(this.cameraWidth, this.cameraHeight), this.mCamera);
	}

	@Override
	protected void onCreateResources() {

		this.mTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 512, 512, TextureOptions.BILINEAR);
		
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		this.mButtonRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mTextureAtlas, this, "button.png", 0, 0, 2, 1);
		
		this.mFontTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        Typeface typeface =  Typeface.createFromAsset(getAssets(), "font/FOO.ttf");
        mFont = new StrokeFont(this.getFontManager(), mFontTexture, typeface, 30, true, Color.WHITE, 2, Color.BLACK);
        
        mTextureAtlas.load();
        mFontTexture.load();
		mFont.load();
        
	}

	@Override
	protected Scene onCreateScene() {
		this.mMainScene = new Scene();
		mMainScene.setBackground(new Background(0.34f, 0.42f, 0.73f));
		
		this.mOptionsScene = new Scene();
		mOptionsScene.setBackground(new Background(0.54f, 0.92f, 0.33f));
		
		
		
		
		new Button(this, mMainScene, (cameraWidth/2)-125, (cameraHeight/2)-37.5f, 250, 75, getString(R.string.newgame), mButtonRegion, mFont){
			@Override
			public boolean onButtonPressed(){	

			try {

			GameActivity.setLevel(LevelFileReader.getLevelFromFile(MainMenuActivity.this, "level_1_1"));
			} catch (Exception e) {
				e.printStackTrace();
			}
				
			Intent intent = new Intent(MainMenuActivity.this, GameActivity.class);
			MainMenuActivity.this.startActivity(intent);
			overridePendingTransition(R.anim.fadein, R.anim.fadeout);
			
			return true;
			}
		};
		
		new Button(this, mMainScene, (cameraWidth/2)-125, (cameraHeight/2)-37.5f+75, 250, 75, getString(R.string.options), mButtonRegion, mFont){
			@Override
			public boolean onButtonPressed(){	
			
			changeSceneWithFade(mOptionsScene, 0.3f);	
				
			return true;
			}
		};
		
		new Button(this, mMainScene, (cameraWidth/2)-125, (cameraHeight/2)-37.5f+150, 250, 75, getString(R.string.exit), mButtonRegion, mFont){
			@Override
			public boolean onButtonPressed(){	
				MainMenuActivity.this.finish();
				return true;
			}
		};
		
		
		
		
		
		
		mMusicButton = new Button(this, mOptionsScene, (cameraWidth/2)-125, (cameraHeight/2)-37.5f, 250, 75, getString(R.string.music) + ": " + getString(R.string.yes), mButtonRegion, mFont){
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
		
		
		mSoundButton = new Button(this, mOptionsScene, (cameraWidth/2)-125, (cameraHeight/2)-37.5f+75, 250, 75, getString(R.string.sound) + ": " + getString(R.string.yes), mButtonRegion, mFont){
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
		
		new Button(this, mOptionsScene, (cameraWidth/2)-125, (cameraHeight/2)-37.5f+150, 250, 75, getString(R.string.reset), mButtonRegion, mFont){
			@Override
			public boolean onButtonPressed(){	
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