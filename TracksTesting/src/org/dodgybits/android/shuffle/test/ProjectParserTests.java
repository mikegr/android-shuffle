package org.dodgybits.android.shuffle.test;

import java.io.StringReader;


import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.Project;
import org.dodgybits.shuffle.android.synchronisation.tracks.parsing.IContextLookup;
import org.dodgybits.shuffle.android.synchronisation.tracks.parsing.ProjectParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;
import junit.framework.Assert;
import junit.framework.TestCase;

public class ProjectParserTests extends TestCase {
	public ProjectParser CreateSUT() {
		return new ProjectParser(new IContextLookup() {
			
			@Override
			public Id findContextIdByTracksId(Id tracksId) {
				// TODO Auto-generated method stub
				return Id.create(1234);
			}
		}, null);
	}
	public void testProjectParserBasicParsingTest() {
	     XmlPullParser xmlParser = Xml.newPullParser();

	     try {
			xmlParser.setInput(new StringReader("<project>"+
			"<completed-at type=\"datetime\" nil=\"true\"/>"+
			"<created-at type=\"datetime\">2010-01-09T21:21:13+01:00</created-at>"+
			"<default-context-id type=\"integer\" nil=\"true\"/>"+
			"<default-tags nil=\"true\"/>"+
			"<description nil=\"true\"/>"+
			"<id type=\"integer\">4532</id>"+
			"<name>Homework</name>"+
			"<position type=\"integer\">1</position>"+
			"<state>active</state>"+
			"<updated-at type=\"datetime\">2010-02-03T10:37:19+01:00</updated-at>"+
			"</project>"));
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ProjectParser parser = CreateSUT();
		Project project = parser.parseSingle(xmlParser).getResult();
		Assert.assertEquals("Homework", project.getName());
		Assert.assertEquals(Id.create(4532), project.getTracksId());
		Assert.assertEquals(1265189839000l, project.getModifiedDate());
		assertEquals(Id.NONE, project.getDefaultContextId());
		assertEquals(false, project.isArchived());

		
	}
	public void testProjectParserBasicParsingTestWithDefaultContext() {
	     XmlPullParser xmlParser = Xml.newPullParser();

	     try {
			xmlParser.setInput(new StringReader("<project>"+
			"<completed-at type=\"datetime\" nil=\"true\"/>"+
			"<created-at type=\"datetime\">2010-01-09T21:21:13+01:00</created-at>"+
			"<default-context-id type=\"integer\">2222</default-context-id>"+
			"<default-tags nil=\"true\"/>"+
			"<description nil=\"true\"/>"+
			"<id type=\"integer\">4532</id>"+
			"<name>Homework</name>"+
			"<position type=\"integer\">1</position>"+
			"<state>active</state>"+
			"<updated-at type=\"datetime\">2010-02-03T10:37:19+01:00</updated-at>"+
			"</project>"));
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ProjectParser parser = CreateSUT();
		Project project = parser.parseSingle(xmlParser).getResult();
		Assert.assertEquals("Homework", project.getName());
		Assert.assertEquals(Id.create(4532), project.getTracksId());
		Assert.assertEquals(1265189839000l, project.getModifiedDate());
		assertEquals(Id.create(1234), project.getDefaultContextId());
		assertEquals(false, project.isArchived());

		
	}
}
