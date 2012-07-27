package com.bugfullabs.icemaze.level;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * 
 * @author Bugful Labs
 * @author Wojciech Gruszka
 * @email  wojciech@bugfullabs.pl
 *
 */


public class LevelHandler extends DefaultHandler{

		// ===========================================================
		// Fields
		// ===========================================================
		
		private boolean in_rowtag = false;
		private int current_row = 0;
		private Level level;
		

		
		public Level getLevel(){
		return this.level;
		}
		

		@Override
		public void startDocument() throws SAXException {

		}

		@Override
		public void endDocument() throws SAXException {

		}


		@Override
		public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
			
			if (localName.equals("level")) {
				
			level = new Level(Integer.parseInt(atts.getValue("columns")), Integer.parseInt(atts.getValue("rows")), Integer.parseInt(atts.getValue("id")), Integer.parseInt(atts.getValue("levelpackid")), atts.getValue("texture"));
			
			}else if (localName.equals("row")) {
				
			current_row = Integer.parseInt(atts.getValue("number"));
			this.in_rowtag = true;

			}else if (localName.equals("item")) {	
			
			if(in_rowtag == true)
			{
			
			level.addItem(Integer.parseInt(atts.getValue("column")), current_row, Integer.parseInt(atts.getValue("id")));
				
			}
				
			}
		}
		

		@Override
		public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
			
			if (localName.equals("level")) {
			}else if (localName.equals("row")) {
				this.in_rowtag = false;
			}	
		}

		@Override
	    public void characters(char ch[], int start, int length) {

			
	    }	
		
}