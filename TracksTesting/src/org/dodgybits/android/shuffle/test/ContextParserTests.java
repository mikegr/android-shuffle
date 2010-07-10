package org.dodgybits.android.shuffle.test;

import java.io.StringReader;

import org.dodgybits.shuffle.android.core.model.Context;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.synchronisation.tracks.parsing.ContextParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

import junit.framework.Assert;
import junit.framework.TestCase;

public class ContextParserTests extends TestCase {

	public ContextParser CreateSUT() {
		return new ContextParser(null);
	}
	public void testContextParserBasicParsingTest() {
	     XmlPullParser xmlParser = Xml.newPullParser();

	     try {
			xmlParser.setInput(new StringReader("<context>"+
   "<created-at type=\"datetime\">2010-01-09T21:20:39+01:00</created-at>"+
   "<hide type=\"boolean\">false</hide>"+
   "<id type=\"integer\">3710</id>"+
   "<name>Online</name>"+
   "<position type=\"integer\">1</position>"+
   "<updated-at type=\"datetime\">2010-02-03T10:37:19+01:00</updated-at>"+
  "</context>"));
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ContextParser parser = CreateSUT();
		Context context = parser.parseSingle(xmlParser).getResult();
		Assert.assertEquals("Online", context.getName());
		Assert.assertEquals(Id.create(3710), context.getTracksId());
		Assert.assertEquals(1265189839000l, context.getModifiedDate());

		
	}
	
	public void testContextParserReturnsNullOnFailedParsing() {
	     XmlPullParser xmlParser = Xml.newPullParser();

	     try {
			xmlParser.setInput(new StringReader("<context>"+
  "<created-at type=\"datetime\">2010-01-09T21:20:39+01:00</created-at>"+
  "<hide type=\"boolean\">false</hide>"+
  "<id type=\"integer\">3710</id>"+
  "<name>Online</name>"+
  "<position type=\"integer\">1</position>"+
  "<updated-at type=\"datetime\">2010-02-03T10:37:19+01:00</updated-at>"+
 "</contxt>"));
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ContextParser parser = CreateSUT();
		Context context = parser.parseSingle(xmlParser).getResult();
		Assert.assertEquals(context, null);

		
	}
	
	
	
}
