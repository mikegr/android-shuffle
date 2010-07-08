package org.dodgybits.android.shuffle.test;

import java.text.ParseException;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.dodgybits.shuffle.android.core.util.DateUtils;

import junit.framework.Assert;
import junit.framework.TestCase;

public class DateParsingAssumptions extends TestCase {

	
	public void testAssumptionParsingWorksForGTDIFYTracks() {
		try 
		{
			
			
			GregorianCalendar expected = new GregorianCalendar( 2010, 0, 9, 21, 20, 39);
			expected.setTimeZone(TimeZone.getTimeZone("CET"));
			
			long date = DateUtils.parseIso8601Date("2010-01-09T21:20:39+01:00");
			
			Assert.assertEquals(expected.getTimeInMillis(), date);
			
		} catch (ParseException e) {
		Assert.fail("could not parse gtdify");
		}
		
	}
	public void testAssumptionParsingWorksForStephansTracks() {
		try 
		{
			
			
			GregorianCalendar expected = new GregorianCalendar( 2010, 3, 11, 23, 32, 20);
			expected.setTimeZone(TimeZone.getTimeZone("EET"));
			
			long date = DateUtils.parseIso8601Date("2010-04-11T22:32:20+02:00");
			
			Assert.assertEquals(expected.getTimeInMillis(), date);
			
		} catch (ParseException e) {
		Assert.fail("could not parse gtdify");
		}
		
	}

}
