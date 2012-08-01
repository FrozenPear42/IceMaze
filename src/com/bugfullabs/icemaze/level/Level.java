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
	
	
	public static final int ITEM = 0;
	public static final int ATTS = 0;
	
	
	private int level_pattern[][][];
	private int width;
	private int height;		
	private int id;
	private int levelpackId;
	private String levelTexture;
	
	private PlayerEntity player;
	private int playerX;
	private int playerY;
	
	private ArrayList<EndEntity> mEnds;
	private int teleports[][];

	
	
	public int tiles;
	
public Level(int columns, int rows, int id, int levelpackId, String texture){
		
		teleports = new int[2][2];
		mEnds = new ArrayList<EndEntity>();
		
		width = columns;
		height = rows;
		
		this.levelTexture = texture;
		
		level_pattern = new int[columns][rows][2];
		
		for (int i = 0; i < columns; i++){
			for (int j = 0; j < rows; j++){				
				level_pattern[i][j][0] = GameValues.BLANK_ID;
				level_pattern[i][j][1] = 0;		
			}	
		
		}
		
		for(int i = 0; i < 2; i++){
			teleports[i][0] = -1;
		}
		
		this.id = id;
		this.levelpackId = levelpackId;
		tiles = 0;
	}
	
	public void addItem(final int column, final int row, final int id){
		
		if(!isCorrect(column, row)){
			return;
		}
		
		level_pattern[column][row][0] = id;
		
	}
	
	
	public void addItem(final int column, final int row, final int id, final int atts){
		
		if(!isCorrect(column, row)){
			return;
		}
		
		level_pattern[column][row][0] = id;
		level_pattern[column][row][1] = atts;
	}
	
	public void setItem(final int column, final int row, final int id, final int atts){
		
		if(!isCorrect(column, row)){
			return;
		}
		
		level_pattern[column][row][0] = id;
		level_pattern[column][row][1] = atts;
	}
	
	
	public void setItem(final int column, final int row, final int id){
		
		if(!isCorrect(column, row)){
			return;
		}
		
		level_pattern[column][row][0] = id;
		
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
		return level_pattern[column][row][0];
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


	
	public int getAtts(int c, int r){
		return level_pattern[c][r][1];
		}
	
	public void setAtts(int c, int r, int a){
		level_pattern[c][r][1] = a;
	}
	
	
	public void findEnds(){
	
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				
				if(level_pattern[i][j][0] == GameValues.END_ID)
					mEnds.add(new EndEntity(i, j));
				else
				if(level_pattern[i][j][0] == GameValues.ONESTEP_ID)
					tiles++;
				else
				if(level_pattern[i][j][0] == GameValues.TWOSTEP_ID)
					tiles += 2;
				
			}
			
			
		}
		
	}
	
	public int getTiles(){
		return tiles;
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
			level_pattern[c][r][0] = GameValues.END_ID;
		}
		}else{
		for(int i = 0; i < mEnds.size(); i++){
			int c = mEnds.get(i).getColumn();
			int r = mEnds.get(i).getRow();
			s.addItem(c, r, GameValues.END_UN_ID);
			level_pattern[c][r][0] = GameValues.END_UN_ID;
		}		
		}
		
	}
	
	
public int getTeleportId(int nCol, int nRow) {
	
	if(teleports[0][0] == nCol && teleports[0][1] == nRow)
		return 0;
	
	return 1;				
	}
	
	
	public void setTeleport(int c, int r){
		if(teleports[0][0] == -1){
			teleports[0][0] = c;
			teleports[0][1] = r;
		}else{
			teleports[1][0] = c;
			teleports[1][1] = r;		
		}
	
	}
	
	public int[] getTeleport(int id){
		int[] r = new int[2];
		r[0] = teleports[id][0];
		r[1] = teleports[id][1];
		return r;
	}
	
	
		

}