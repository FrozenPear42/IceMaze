package com.bugfullabs.icemaze;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.modifier.FadeInModifier;
import org.andengine.entity.modifier.FadeOutModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.content.Intent;
import android.view.Display;




/**
 * 
 * @author Bugful Labs
 * @author Wojciech Gruszka
 * @email  wojciech@bugfullabs.pl
 *
 */


public class Splash extends SimpleBaseGameActivity{

	private int cameraHeight;
	private int cameraWidth;
	
	private Camera mCamera;
	private Scene mScene;
	
	
	private BitmapTextureAtlas mAtlas;
	private ITextureRegion mBugfullabsLogo;
	private ITextureRegion mAndEngineLogo;
	
	private static final float SPLASH_DURATION = 0.5f;
	
	public EngineOptions onCreateEngineOptions() {

		
		
		Display disp = getWindowManager().getDefaultDisplay();
		
		this.cameraWidth = disp.getWidth();
		this.cameraHeight = disp.getHeight();
		
		mCamera = new Camera( 0, 0, this.cameraWidth, this.cameraHeight);
		
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(cameraWidth, cameraHeight), mCamera);
	}




	@Override
	protected void onCreateResources() {
		
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		mAtlas = new BitmapTextureAtlas(getTextureManager(), 512, 256, TextureOptions.BILINEAR);
		mBugfullabsLogo = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mAtlas, this, "logo/bugfullabs.png", 0, 0);
		mAndEngineLogo = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mAtlas, this, "logo/andengine.png", 256, 0);
		mAtlas.load();
		
	}


	@Override
	protected Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());
		
		this.mScene = new Scene();
		mScene.setBackground(new Background(0.0f, 0.0f, 0.0f));
		
      	final float Xpos = (cameraWidth/2)-(mBugfullabsLogo.getWidth()/2);
      	final float Ypos = (cameraHeight/2)-(mBugfullabsLogo.getHeight()/2);
		
		final Sprite mBLlogo = new Sprite(Xpos, Ypos, mBugfullabsLogo, this.getVertexBufferObjectManager());
		mBLlogo.setAlpha(0.0f);
		mScene.attachChild(mBLlogo);
		
		final Sprite mAElogo = new Sprite(Xpos, Ypos, mAndEngineLogo, this.getVertexBufferObjectManager());
		mAElogo.setAlpha(0.0f);
		mScene.attachChild(mAElogo);
		
		
		
		mBLlogo.registerEntityModifier(new FadeInModifier(SPLASH_DURATION));
		
		
		this.getEngine().registerUpdateHandler(new TimerHandler(SPLASH_DURATION, true, new ITimerCallback() {
			int cnt = 0;
			public void onTimePassed(TimerHandler pTimerHandler){
				
				switch(cnt){
				
				case 0:
					
					break;
				
				case 1:
					mBLlogo.registerEntityModifier(new FadeOutModifier(SPLASH_DURATION));
					break;
				
				case 2:
					
					break;
				
				case 3:
					mAElogo.registerEntityModifier(new FadeInModifier(SPLASH_DURATION));
					break;
				
				case 4:
					
					break;
					
				case 5:
					mAElogo.registerEntityModifier(new FadeOutModifier(SPLASH_DURATION));
					break;
					
				case 6:
					Intent intent = new Intent(Splash.this, MainMenuActivity.class);
					Splash.this.startActivity(intent);
					Splash.this.finish();
					break;
				}

				cnt++;
	
			}
		}));
		

		
		
		return this.mScene;
	}
 
	
	
	
}
	