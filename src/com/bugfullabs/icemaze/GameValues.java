package com.bugfullabs.icemaze;

/**
 * 
 * @author Bugful Labs
 * @author Grushenko
 * @email  wojciech@bugfullabs.pl
 *
 */


public interface GameValues{
	
	/* GAME MENU */
	public static final int BACK_ID = 0;
	public static final int MENU_ID = 1;
	public static final int NEXT_ID = 2;
	public static final int PAUSE_ID = 3;
	public static final int RESTART_ID = 4;
	public static final int SCOREBG_ID = 5;
	
	/* GAME */
	public static final int PLAYER_ID = 0;
	public static final int SOLID_ID = 1;
	public static final int BLANK_ID = 2;
	public static final int END_ID = 3;
	public static final int END_UN_ID = 4;
	public static final int FLAME_ID = 5;
	public static final int ONESTEP_ID = 6;
	public static final int TWOSTEP_ID = 7;
	public static final int LOCK_ID = 8;
	public static final int KEY_ID = 9;
	public static final int TELEPORTGREEN_ID = 10;
	public static final int TELEPORTRED_ID = 11;
	public static final int CLEAN_ID = 12;
	public static final int STABLE_ID = 13;
	/* MAIN MENU */

	public static final int BACK_M_ID = 0;
	public static final int BG_ID = 1;
	public static final int BUTTONLONG_ID = 2;
	public static final int BUTTONSHORT_ID = 3;
	public static final int BUTTONSHORTDONE_ID = 4;
	public static final int BUTTONSHORTFULL_ID = 5;


	
	/* SHARED PREFS */
	public static final String SETTINGS_FILE = "Settings";
	public static final int STEERING_TOUCH = 0;
	public static final int STEERING_SLIDE = 1;
	
	public static final String SCORE_FILE = "Scores";
	
	/* GAME VALUES */
	
	public static final int LEVELPACKS = 3;
	public static final int TELEPORTS = 2;
	
	public static final int GREEN_TELEPORT = 0;
	public static final int RED_TELEPORT = 1;

	
}