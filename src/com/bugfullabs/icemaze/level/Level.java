package com.bugfullabs.icemaze.level;

import java.util.ArrayList;

import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import com.bugfullabs.icemaze.GameValues;
import com.bugfullabs.icemaze.game.EndEntity;
import com.bugfullabs.icemaze.game.GameScene;
import com.bugfullabs.icemaze.game.PlayerEntity;

/**
 * 
 * @author Bugful Labs
 * @author Wojciech Gruszka
 * @email  wojciech@bugfullabs.pl
 *
 */


public class Level{
	
	private int level_pattern[][];
	private int width;
	private int height;		
	private int id;
	private int levelpackId;
	private String levelTexture;
	
	private PlayerEntity player;
	private int playerX;
	private int playerY;
	
	private int stars[][];
	private ArrayList<EndEntity> mEnds;
	
	
public Level(int columns, int rows, int id, int levelpackId, String texture){
		
		stars = new int[3][3];
		mEnds = new ArrayList<EndEntity>();
		
		width = columns;
		height = rows;
		
		this.levelTexture = texture;
		
		level_pattern = new int[columns][rows];
		
		for (int i = 0; i < columns; i++){
			for (int j = 0; j < rows; j++){				
				level_pattern[i][j] = GameValues.BLANK_ID;
			}	
		
		}
		
		this.id = id;
		this.levelpackId = levelpackId;
	}
	
	public void addItem(final int column, final int row, final int id){
		
		if(!isCorrect(column, row)){
			return;
		}
		
		level_pattern[column][row] = id;
		
	}
	
	public void setItem(final int column, final int row, final int id){
		
		if(!isCorrect(column, row)){
			return;
		}
		
		level_pattern[column][row] = id;
		
	}
	
	public int getWidth(){
		
	return this.width;
	}
	
	public int getHeight(){
	
		return this.height;
}

	
	public int getItem(int column, int row)
	{
		
		if(!isCorrect(column, row)){
			return -1;
		}
		return level_pattern[column][row];
	}

	
	public int getId() {
		
		return id;
	}
	
	public int getLevelpackId(){
		return levelpackId;
	}
	
	public String getLevelTexture(){
		return this.levelTexture;
	}	
	
	public void setPlayer(int x, int y){
		this.playerX = x;
		this.playerY = y;
	}
	
	
	public void createPlayer(VertexBufferObjectManager vm, TextureRegion tx){
		player = new PlayerEntity(vm, playerX*32, playerY*32, tx);

		Debug.i(Integer.toString(player.getZIndex()));
	}
	
	public PlayerEntity getPlayer(){
		return player;
	}
	
	
	private boolean isCorrect(int column, int row){
		
		return !(column < 0 || row < 0 || column >= width || row >= height);
	}

	public void addStar(int id, int column, int row) {
		stars[id][0] = column;
		stars[id][1] = row;
		stars[id][2] = 0;
	}
	
	
	public int getStarColumn(int id){
		return stars[id][0];
	}
	
	public int getStarRow(int id){
		return stars[id][1];
	}
	
	public boolean getStarCollected(int id){
		return stars[id][2] == 1;
	}
	
	public int getStarId(int c, int r){
		for(int i = 0; i < 3 ; i++){
			if(stars[i][0] == c && stars[i][1] == r){
				return i;
				}
			}
		return -1;
	}
	
	public void findEnds(){
	
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				if(level_pattern[i][j] == GameValues.END_ID){
					mEnds.add(new EndEntity(i, j));
				}
			}
		}
		
	}
	
	public EndEntity getEnd(int id){
		return mEnds.get(id);
	}
	
	public void setEndsActive(GameScene s, boolean t){
		
		if(t){	
		for(int i = 0; i < mEnds.size(); i++){
			int c = mEnds.get(i).getColumn();
			int r = mEnds.get(i).getRow();
			s.addItem(c, r, GameValues.END_ID);
		}
		}else{
		for(int i = 0; i < mEnds.size(); i++){
			int c = mEnds.get(i).getColumn();
			int r = mEnds.get(i).getRow();
			s.addItem(c, r, GameValues.END_UN_ID);

		}		
		}
		
	}
	
	public void setStarCollected(int id, boolean col){
		if(col)
			stars[id][2] = 1;
		else
			stars[id][2] = 0;
	}
	
	public boolean isStar(int c, int r){
		
		for(int i = 0; i < 3 ; i++){
			if(stars[i][0] == c && stars[i][1] == r){
				
				if(!getStarCollected(i)){
				setStarCollected(i, true);
				return true;
				}
			}
		}
		
		return false;
	}
	
	
	
	
}