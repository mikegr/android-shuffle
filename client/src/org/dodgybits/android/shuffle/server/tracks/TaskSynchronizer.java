package org.dodgybits.android.shuffle.server.tracks;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.database.Cursor;
import android.util.Xml;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Context;
import org.dodgybits.android.shuffle.model.Project;
import org.dodgybits.android.shuffle.model.Task;
import org.dodgybits.android.shuffle.provider.Shuffle;
import org.dodgybits.android.shuffle.util.BindingUtils;
import org.dodgybits.android.shuffle.util.ModelUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Morten Nielsen
 */
public final class TaskSynchronizer extends Synchronizer<Task> {
    private final String tracksUrl;

    public TaskSynchronizer(ContentResolver contentResolver, Resources resources, WebClient client, ContextWrapper activity, TracksSynchronizer tracksSynchronizer, String tracksUrl, int basePercent) {
        super(contentResolver, tracksSynchronizer, client, resources, activity, basePercent);

        this.tracksUrl = tracksUrl;
    }


    @Override
    protected String readingRemoteText() {
        return resources.getString( R.string.readingRemoteTasks);
    }

    @Override
    protected String processingText() {
        return resources.getString( R.string.processingTasks);
    }

    @Override
    protected String readingLocalText() {
        return resources.getString( R.string.readingLocalTasks);
    }

    @Override
    protected String stageFinishedText() {
        return resources.getString( R.string.doneWithTasks);
    }

    @Override
    protected void saveLocalEntityFromRemote(Task task) {
        ModelUtils.insertTask(activity, task);
    }

    protected Task createMergedLocalEntity(Task localTask, Task newTask) {
        return new Task(localTask.id, newTask.description, newTask.details, newTask.context, newTask.project, localTask.created, newTask.modified,
                newTask.startDate, newTask.dueDate, localTask.timezone,
                localTask.allDay, localTask.hasAlarms, localTask.calEventId, localTask.order, localTask.complete,
                newTask.tracksId);
    }

    protected String createDocumentForEntity(Task task) {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try {
            serializer.setOutput(writer);
            //serializer.startDocument("UTF-8", true);


            serializer.startTag("", "todo");
            Date date = new Date(task.created);
            if(task.complete)
            serializer.startTag("", "completed-at").attribute("", "type", "datetime").text(simpleDateFormat.format(new Date(task.modified))).endTag("", "completed-at");
            
            String contextId = findContextRemoteId(task);
            if(contextId != null)            serializer.startTag("", "context-id").attribute("", "type", "integer").text(contextId).endTag("", "context-id");

            serializer.startTag("", "created-at").attribute("", "type", "datetime").text(simpleDateFormat.format(date)).endTag("", "created-at");
            serializer.startTag("", "description").text(task.description).endTag("", "description");
            if(task.dueDate != 0)
            serializer.startTag("", "due").attribute("", "type", "datetime").text(simpleDateFormat.format(new Date(task.dueDate))).endTag("", "due");
         

            serializer.startTag("", "notes").text(task.details != null? task.details:"").endTag("", "notes");

            String projectId = findProjectRemoteId(task);
            if(projectId != null)            serializer.startTag("", "project-id").attribute("", "type", "integer").text(projectId).endTag("", "project-id");
            serializer.startTag("", "state").text((task.complete == null || !task.complete) ? "active": "completed").endTag("", "state");
            serializer.startTag("", "updated-at").attribute("", "type", "datetime").text(simpleDateFormat.format(new Date(task.modified))).endTag("", "updated-at");


            serializer.endTag("", "todo");
            // serializer.endDocument();
            serializer.flush();
        } catch (IOException ignored) {

        }


        return writer.toString();
    }

    private String findProjectRemoteId(Task task) {
                if(task.project != null && task.project.tracksId != null)
            return task.project.tracksId.toString();
        else
            return null;
    }

    private String findContextRemoteId(Task task) {
        if(task.context != null && task.context.tracksId != null)
            return task.context.tracksId.toString();
        else
            return null;
    }


    protected Task parseSingleEntity(XmlPullParser parser) throws ParseException {
        try {
            int eventType = parser.getEventType();
            String taskDescription = null;
            String taskNotes = null;
            Long trackId = null;
            Long trackModifiedDate = null;
            
            Context context=null;
            Project project=null;


                        long created = 0;
            long showAt = 0;
                            long due = 0;
            
            SimpleDateFormat format = simpleDateFormat;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = parser.getName();

                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:

                        break;
                    case XmlPullParser.START_TAG:


                        if (name.equalsIgnoreCase("description")) {
                            taskDescription = parser.nextText();
                        } else if (name.equalsIgnoreCase("id")) {
                            trackId = Long.parseLong(parser.nextText());
                        } else if (name.equalsIgnoreCase("updated-at")) {
                            trackModifiedDate = format.parse(parser.nextText()).getTime();
                        } else if (name.equalsIgnoreCase("context-id")) {
                            String tokenValue = parser.nextText();
                            if(tokenValue != null && !tokenValue.equals(""))
                            context = findContextByRemoteId( Long.parseLong(tokenValue));
                        } else if (name.equalsIgnoreCase("project-id")) {
                            String tokenValue = parser.nextText();
                            if(tokenValue != null && !tokenValue.equals(""))
                            project = findProjectByRemoteId( Long.parseLong(tokenValue));
                        }else if (name.equalsIgnoreCase("notes")) {
                            taskNotes = parser.nextText();
                        }else if (name.equalsIgnoreCase("created-at")) {
                            created = format.parse(parser.nextText()).getTime();
                        }else if (name.equalsIgnoreCase("due")) {

                            String textToken = parser.nextText();
                            if( textToken != null && !textToken.equals(""))
                            due = format.parse(textToken).getTime();
                        }else if (name.equalsIgnoreCase("show-from")) {

                            String textToken = parser.nextText();
                            if( textToken != null && !textToken.equals(""))
                            showAt = format.parse(textToken).getTime();
                        }




                        break;
                    case XmlPullParser.END_TAG:
                        if (name.equalsIgnoreCase("todo") && taskDescription != null) {


                            return new Task(null, taskDescription, taskNotes,
                                    context, project, created,  trackModifiedDate, showAt, due, "UTC",true,false,null,0,false,
                                    trackId);
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (IOException e) {
            throw new ParseException("Unable to parse task", 0);
        } catch (XmlPullParserException e) {
            throw new ParseException("Unable to parse task", 0);
        }
        return null;
    }

    private Project findProjectByRemoteId(long tracksId) {
        Project context = null;

			Cursor projectCursor = contentResolver.query(
					Shuffle.Projects.CONTENT_URI, Shuffle.Projects.cFullProjection, "tracks_id = "+tracksId, null, null);
			if (projectCursor.moveToFirst()) {
				context = BindingUtils.readProject(projectCursor);
			}
        projectCursor.close();
		return context;
    }

    private Context findContextByRemoteId(long tracksId) {
       		Context context = null;

			Cursor contextCursor = contentResolver.query(
					Shuffle.Contexts.CONTENT_URI, Shuffle.Contexts.cFullProjection, "tracks_id = "+tracksId, null, null);
			if (contextCursor.moveToFirst()) {
				context = BindingUtils.readContext(contextCursor, activity.getResources());
			}
        contextCursor.close();
		return context;
    }

    @Override
    protected String createEntityUrl(Task project) {
        return tracksUrl+"/todos/" + project.tracksId + ".xml";
    }


    @Override
    protected String endIndexTag() {
        return "todos";
    }

    @Override
    protected String entityIndexUrl() {
        return tracksUrl+"/todos.xml";
    }

    protected boolean removeLocalEntity(Task task) {
        return 1 == contentResolver.delete(
                Shuffle.Tasks.CONTENT_URI, Shuffle.Tasks._ID + " = " + task.id,
                null);
    }

    protected void saveLocalEntity(Task task) {
        ContentValues values = new ContentValues();
        BindingUtils.writeTask(values, task);
        contentResolver.update(Shuffle.Tasks.CONTENT_URI, values, Shuffle.Tasks._ID + "=?", new String[]{String.valueOf(task.id)});
    }

    protected Map<String, Task> getShuffleEntities(ContentResolver contentResolver, Resources resources) {


        Cursor cursor = contentResolver.query(
                Shuffle.Tasks.CONTENT_URI, Shuffle.Tasks.cExpandedProjection,
                null, null, null);


        Map<String, Task> list = new HashMap<String, Task>();

        while (cursor.moveToNext()) {
            Task task = BindingUtils.readTask(cursor, resources);

            list.put(task.description, task);


        }
        cursor.close();
        return list;
    }
}