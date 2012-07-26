package com.bugfullabs.icemaze.level;





public class Level{
	
	private int level_pattern[][];
	private int width;
	private int height;		
	private int id;
	private int levelpackId;
	private String levelTexture;
	
public Level(int columns, int rows, int id, int levelpackId, String texture){
		
		width = columns;
		height = rows;
		
		this.levelTexture = texture;
		
		level_pattern = new int[columns][rows];
		
		for (int i = 0; i < columns; i++){
			for (int j = 0; j < rows; j++){				
				level_pattern[i][j] = 0;
			}	
		
		}
		
		this.id = id;
		this.levelpackId = levelpackId;
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

	
	public int getItemNumber(int column, int row)
	{
		
		if(!isCorrect(column, row)){
			return -1;
		}
		return level_pattern[column][row];
	}

	
public int getLevelId() {
		
		return id;
	}
	
	public int getLevelpackId(){
		return levelpackId;
	}
	
	public String getLevelTexture(){
		return this.levelTexture;
	}	
	
	
	private boolean isCorrect(int column, int row){
		
		return !(column < 0 || row < 0 || column >= width || row >= height);
	}
	
	
}