package org.dodgybits.android.shuffle.test;

import java.io.StringReader;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.synchronisation.tracks.parsing.IContextLookup;
import org.dodgybits.shuffle.android.synchronisation.tracks.parsing.IProjectLookup;
import org.dodgybits.shuffle.android.synchronisation.tracks.parsing.TaskParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class TaskParserTests extends TestCase {
	public TaskParser CreateSUT() {
		return new TaskParser(new IContextLookup() {
			
			@Override
			public Id findContextIdByTracksId(Id tracksId) {
				// TODO Auto-generated method stub
				return Id.create(1234);
			}
		}, new IProjectLookup() {
			
			@Override
			public Id findProjectIdByTracksId(Id tracksId) {
				// TODO Auto-generated method stub
				return Id.create(2345);
			}
		}, null);
	}
	public void testTaskParserBasicParsingTest() {
	     XmlPullParser xmlParser = Xml.newPullParser();

	     try {
			xmlParser.setInput(new StringReader("<todo>"+
			"<completed-at type=\"datetime\" nil=\"true\"/>"+
			"<context-id type=\"integer\">3711</context-id>"+
			"<created-at type=\"datetime\">2009-10-26T22:23:42+01:00</created-at>"+
			"<description>Läs getting things done igen</description>"+
			"<due type=\"datetime\" nil=\"true\"/>"+
			"<id type=\"integer\">25076</id>"+
			"<ip-address>90.232.35.15</ip-address>"+
			"<notes>Primärt kring idéer och projekt</notes>"+
			"<project-id type=\"integer\">4558</project-id>"+
			"<recurring-todo-id type=\"integer\" nil=\"true\"/>"+
			"<show-from type=\"datetime\" nil=\"true\"/>"+
			"<state>active</state>"+
			"<updated-at type=\"datetime\">2010-02-03T10:37:19+01:00</updated-at>"+
			"</todo>"));
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TaskParser parser = CreateSUT();
		Task task = parser.parseSingle(xmlParser).getResult();
		Assert.assertEquals("Läs getting things done igen", task.getDescription());
		Assert.assertEquals(Id.create(25076), task.getTracksId());
		Assert.assertEquals(1265189839000l, task.getModifiedDate());
		assertEquals("Primärt kring idéer och projekt", task.getDetails());
		assertEquals("context id was wrong",Id.create(1234), task.getContextId() );
		assertEquals("project id was wrong",Id.create(2345), task.getProjectId() );
		assertEquals("due date was wrong",0,task.getDueDate());
		assertEquals("start date was wrong",0,task.getStartDate());
		
		
	}
	
}
