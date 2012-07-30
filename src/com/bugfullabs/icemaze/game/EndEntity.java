package com.bugfullabs.icemaze.game;

public class EndEntity {

	private int col;
	private int row;
	
	public EndEntity(int c, int r){
		col = c;
		row = r;
	}
	
	public int getColumn(){
		return col;
	}
	
	public int getRow(){
		return row;
	}
	
	public void setColumn(int c){
		col = c;
	}
	
	public void setRow(int r){
		row = r;
	}
}
