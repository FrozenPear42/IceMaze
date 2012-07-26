package com.bugfullabs.icemaze.level;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;



public class LevelHandler extends DefaultHandler{

		// ===========================================================
		// Fields
		// ===========================================================
		
		private boolean in_rowtag = false;
		
		private int current_row = 0;
		
		
		Level level;
		

		public Level getLevel(){
			
			
			return this.level;
		}
		

		@Override
		public void startDocument() throws SAXException {

		}

		@Override
		public void endDocument() throws SAXException {

		}

		/** Gets be called on opening tags like: 
		 * <tag> 
		 * Can provide attribute(s), when xml was like:
		 * <tag attribute="attributeValue">*/
		@Override
		public void startElement(String namespaceURI, String localName,
				String qName, Attributes atts) throws SAXException {
			if (localName.equals("level")) {

				String rowsValue = atts.getValue("rows");
				String columnsValue = atts.getValue("columns");
				
				
			}else if (localName.equals("row")) {
				
				current_row = Integer.parseInt(atts.getValue("number"));
				this.in_rowtag = true;

			}else if (localName.equals("item")) {
			
			if(in_rowtag == true)
			{
			
			}
			}else if (localName.equals("cubes")){
				//level.setNumberOfCubes(Integer.parseInt(atts.getValue("number")));
			}else if(localName.equals("cube")){

				
				//level.setCube(Integer.parseInt(atts.getValue("column")), Integer.parseInt(atts.getValue("row")), Integer.parseInt(atts.getValue("color")), Integer.parseInt(atts.getValue("dir")), Integer.parseInt(atts.getValue("id"))-1);	
				
				
			}
		}
		
		/** Gets be called on closing tags like: 
		 * </tag> */
		@Override
		public void endElement(String namespaceURI, String localName, String qName)
				throws SAXException {
			if (localName.equals("level")) {
			}else if (localName.equals("row")) {
				this.in_rowtag = false;
			}else if (localName.equals("cubes")) {
			}
			
			
		}
		
		/** Gets be called on the following structure: 
		 * <tag>characters</tag> */
		@Override
	    public void characters(char ch[], int start, int length) {

			
	    	}	
		
}