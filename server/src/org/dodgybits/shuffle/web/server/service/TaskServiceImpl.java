package org.dodgybits.shuffle.web.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.dodgybits.shuffle.web.client.service.TaskService;
import org.dodgybits.shuffle.web.common.model.Task;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class TaskServiceImpl extends RemoteServiceServlet 
	implements TaskService {

	public ArrayList<Task> getTasks() {
		ArrayList<Task> result = new ArrayList<Task>(10);
		for(int i = 0; i < 10; i++) {
			result.add(createRandomAction());
		}
		return result;
	}

  private static final long MILLIS_IN_DAY = 1000L * 60 * 60 * 24;
  
  private Task createRandomAction() {
  	Random rnd = new Random();
  	String title = titles[rnd.nextInt(titles.length)];
  	String description = descriptions[rnd.nextInt(descriptions.length)];
  	int dayOffset = rnd.nextInt(11) - 5;
  	Date dueDate = new Date(System.currentTimeMillis() + 
  			MILLIS_IN_DAY * dayOffset);
  	return new Task(
  			null, 
  			"XX" + title, description, 
  			null, null, 
  			dueDate);
  }
  
  private static final String[] titles = new String[] {
    "RE: St0kkMarrkett Picks Trade watch special pr news release",
    "St0kkMarrkett Picks Watch special pr news release news",
    "You are a Winner oskoxmshco", "Encrypted E-mail System (VIRUS REMOVED)",
    "Fw: Malcolm", "Secure Message System (VIRUS REMOVED)",
    "fwd: St0kkMarrkett Picks Watch special pr news releaser",
    "FWD: Financial Market Traderr special pr news release",
    "? s? uma dica r?pida !!!!! leia !!!", "re: You have to heard this",
    "fwd: Watcher TopNews", "VACANZE alle Mauritius", "funny"
  };
  
  private static final String[] descriptions = new String[] {
    "URGENT -[Mon, 24 Apr 2006 02:17:27 +0000]",
    "URGENT TRANSACTION -[Sun, 23 Apr 2006 13:10:03 +0000]",
    "fw: Here it comes", "voce ganho um vale presente Boticario",
    "Read this ASAP", "Hot Stock Talk", "New Breed of Equity Trader",
    "FWD: TopWeeks the wire special pr news release", "[fwd] Read this ASAP",
    "Renda Extra R$1.000,00-R$2.000,00/m?s",
    "re: Make sure your special pr news released",
    "Forbidden Knowledge Conference", "decodificadores os menores pre?os",
    "re: Our Pick", "RE: The hottest pick Watcher",
    "re: You need to review this", "[re:] Our Pick",
    "RE: Before the be11 special pr news release",
    "[re:] Market TradePicks Trade watch news", "No prescription needed",
    "Seu novo site", "[fwd] Financial Market Trader Picker",
    "FWD: Top Financial Market Specialists Trader interest increases",
    "Os cart?es mais animados da web!!", "We will sale 4 you cebtdbwtcv",
    "RE: Best Top Financial Market Specialists Trader Picks"};
  	
}
