package org.dodgybits.shuffle.web.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.dodgybits.shuffle.web.client.model.TaskFilter;
import org.dodgybits.shuffle.web.client.model.TaskOrdering;
import org.dodgybits.shuffle.web.client.model.TaskValue;
import org.dodgybits.shuffle.web.client.service.NotLoggedInException;
import org.dodgybits.shuffle.web.client.service.TaskService;
import org.dodgybits.shuffle.web.server.model.Task;
import org.dodgybits.shuffle.web.server.persistence.JdoUtils;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class TaskServiceImpl extends RemoteServiceServlet implements
        TaskService {

    public ArrayList<TaskValue> getMockTasks() {
        ArrayList<TaskValue> result = new ArrayList<TaskValue>(10);
        for (int i = 0; i < 10; i++) {
            result.add(createRandomAction());
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<TaskValue> getTasks(TaskFilter filter, TaskOrdering order) throws NotLoggedInException {
        checkLoggedIn();

        ArrayList<TaskValue> taskValues = new ArrayList<TaskValue>();
        PersistenceManager pm = JdoUtils.getPm();
        try {
            Query query = pm.newQuery(Task.class);
            setFilter(query, filter);
            setOrdering(query, order);
            List<Task> tasks = (List<Task>) query.execute(getUser());
            for (Task task : tasks) {
                taskValues.add(task.toTaskValue());
            }
        } finally {
            JdoUtils.closePm();
        }
        return taskValues;
    }

    public TaskValue saveTask(TaskValue taskValue) throws NotLoggedInException {
        checkLoggedIn();
        
        PersistenceManager pm = JdoUtils.getPm();
        Task task = Task.fromTaskValue(getUser(), taskValue);
        try {
            task = pm.makePersistent(task);
        } finally {
            JdoUtils.closePm();
        }
        return task.toTaskValue();
    }

    private void setFilter(Query query, TaskFilter filter) {
        query.setFilter("user == u");
        query.declareParameters("com.google.appengine.api.users.User u");

        // TODO
    }

    private void setOrdering(Query query, TaskOrdering ordering) {
        // TODO
    }
    
    private void checkLoggedIn() throws NotLoggedInException {
        if (getUser() == null) {
          throw new NotLoggedInException("Not logged in.");
        }
      }

      private User getUser() {
        UserService userService = UserServiceFactory.getUserService();
        return userService.getCurrentUser();
      }
    

    private static final long MILLIS_IN_DAY = 1000L * 60 * 60 * 24;

    private TaskValue createRandomAction() {
        Random rnd = new Random();
        String title = titles[rnd.nextInt(titles.length)];
        String description = descriptions[rnd.nextInt(descriptions.length)];
        int dayOffset = rnd.nextInt(11) - 5;
        Date dueDate = new Date(System.currentTimeMillis() + MILLIS_IN_DAY
                * dayOffset);
        return new TaskValue(null, "XX" + title, description, null, null,
                dueDate);
    }

    private static final String[] titles = new String[] {
            "RE: St0kkMarrkett Picks Trade watch special pr news release",
            "St0kkMarrkett Picks Watch special pr news release news",
            "You are a Winner oskoxmshco",
            "Encrypted E-mail System (VIRUS REMOVED)", "Fw: Malcolm",
            "Secure Message System (VIRUS REMOVED)",
            "fwd: St0kkMarrkett Picks Watch special pr news releaser",
            "FWD: Financial Market Traderr special pr news release",
            "? s? uma dica r?pida !!!!! leia !!!",
            "re: You have to heard this", "fwd: Watcher TopNews",
            "VACANZE alle Mauritius", "funny" };

    private static final String[] descriptions = new String[] {
            "URGENT -[Mon, 24 Apr 2006 02:17:27 +0000]",
            "URGENT TRANSACTION -[Sun, 23 Apr 2006 13:10:03 +0000]",
            "fw: Here it comes", "voce ganho um vale presente Boticario",
            "Read this ASAP", "Hot Stock Talk", "New Breed of Equity Trader",
            "FWD: TopWeeks the wire special pr news release",
            "[fwd] Read this ASAP", "Renda Extra R$1.000,00-R$2.000,00/m?s",
            "re: Make sure your special pr news released",
            "Forbidden Knowledge Conference",
            "decodificadores os menores pre?os", "re: Our Pick",
            "RE: The hottest pick Watcher", "re: You need to review this",
            "[re:] Our Pick", "RE: Before the be11 special pr news release",
            "[re:] Market TradePicks Trade watch news",
            "No prescription needed", "Seu novo site",
            "[fwd] Financial Market Trader Picker",
            "FWD: Top Financial Market Specialists Trader interest increases",
            "Os cart?es mais animados da web!!",
            "We will sale 4 you cebtdbwtcv",
            "RE: Best Top Financial Market Specialists Trader Picks" };

}
