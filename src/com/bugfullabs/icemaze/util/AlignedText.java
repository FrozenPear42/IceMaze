package com.bugfullabs.icemaze.util;

import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.VerticalAlign;


public class AlignedText extends Text {
	
    private HorizontalAlign 	mAlignmentH;
    private VerticalAlign		mAlignmentV;
    
    private float offsetX = 0.0f;
    private float offsetY = 0.0f;
    
    private int screenWidth;
    private int screenHeight;
    
    
    public AlignedText(float pOffsetX, float pOffsetY, Font pFont, String pText, HorizontalAlign pHorizontalAlign, VerticalAlign pVerticalAlign, float width, float height, BaseGameActivity a) {
            
    		super(pOffsetX, pOffsetY, pFont, pText, a.getVertexBufferObjectManager());
            this.mAlignmentH = pHorizontalAlign;
            this.mAlignmentV = pVerticalAlign;
            
            this.offsetX = pOffsetX;
            this.offsetY = pOffsetY;
            
            this.screenWidth = (int) width;
            this.screenHeight = (int) height;
        
            this. alignText();
    }
    
    private void alignText(){
            float textwidth 	= getWidth();
            float textheight	= getHeight();
            
            float x = offsetX;
            float y = offsetY;
                
            if(mAlignmentH == HorizontalAlign.CENTER)  {
            	x += ((this.screenWidth / 2) - (textwidth / 2)); 	
            }
            
            if(mAlignmentH == HorizontalAlign.RIGHT)  {
            	x += (this.screenWidth - textwidth); 	
            }
            
            if(mAlignmentV == VerticalAlign.CENTER) {
            	y += ((this.screenHeight / 2) - (textheight / 2)); 	
            }
            
            if(mAlignmentV == VerticalAlign.BOTTOM) {
            	y += (this.screenHeight - textheight);
            }

            
            setPosition(x, y);
    }

    @Override
    public void setText(CharSequence pText) {
            super.setText(pText);
            alignText();
    }
}
