package org.dodgybits.shuffle.android.synchronisation.tracks;

import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.EntityBuilder;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.core.model.Task.Builder;
import org.dodgybits.shuffle.android.core.model.persistence.EntityPersister;
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

/**
 * @author Morten Nielsen
 */
public final class TaskSynchronizer extends Synchronizer<Task> {
    private static final String cTag = "TaskSynchronizer";

    
    private final String mTracksUrl;

    public TaskSynchronizer( 
            TracksSynchronizer tracksSynchronizer, 
            WebClient client, 
            Context context, 
            int basePercent,
            String tracksUrl) {
        super(tracksSynchronizer, client, context, basePercent);

        this.mTracksUrl = tracksUrl;
    }

    @Override
    protected EntityBuilder<Task> createBuilder() {
        return Task.newBuilder();
    }

    @Override
    protected EntityPersister<Task> createPersister() {
        return new TaskPersister(mContext.getContentResolver());
    }
    
    @Override
    protected void verifyLocalEntities(Map<Id, Task> localEntities) {

        LinkedList<Id> tasksWithoutContext = new LinkedList<Id>();
        for(Task t : localEntities.values()) {
            if(!t.getContextId().isInitialised()) {
                tasksWithoutContext.add(t.getLocalId());
            }
        }
        if (tasksWithoutContext.size() > 0) {
            mTracksSynchronizer.postSyncMessage(R.string.cannotSyncTasksWithoutContext);
            for(Id id : tasksWithoutContext) {
                localEntities.remove(id);
            }
        }
    }

    @Override
    protected String readingRemoteText() {
        return mContext.getString(R.string.readingRemoteTasks);
    }

    @Override
    protected String processingText() {
        return mContext.getString(R.string.processingTasks);
    }

    @Override
    protected String readingLocalText() {
        return mContext.getString(R.string.readingLocalTasks);
    }

    @Override
    protected String stageFinishedText() {
        return mContext.getString(R.string.doneWithTasks);
    }


    protected Task createMergedLocalEntity(Task localTask, Task newTask) {
        Builder builder = Task.newBuilder();
        builder.mergeFrom(newTask);
        builder
            .setLocalId(localTask.getLocalId())
            .setCreatedDate(localTask.getCreatedDate())
            .setTimezone(localTask.getTimezone())
            .setAllDay(localTask.isAllDay())
            .setHasAlarm(localTask.hasAlarms())
            .setCalendarEventId(localTask.getCalendarEventId())
            .setOrder(localTask.getOrder())
            .setComplete(localTask.isComplete());
        return builder.build();
    }

    protected String createDocumentForEntity(Task task) {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try {
            serializer.setOutput(writer);
            //serializer.startDocument("UTF-8", true);


            serializer.startTag("", "todo");
            Date creationDate = new Date(task.getCreatedDate());
            if (task.isComplete()) {
                Date completedDate = new Date(task.getModifiedDate());
                serializer.startTag("", "completed-at").attribute("", "type", "datetime").text(mDateFormat.format(completedDate)).endTag("", "completed-at");
            }
            
            Id contextId = findTracksIdByContextId(task.getContextId());
            if (contextId.isInitialised()) {
                serializer.startTag("", "context-id").attribute("", "type", "integer").text(contextId.toString()).endTag("", "context-id");
            }
            serializer.startTag("", "created-at").attribute("", "type", "datetime").text(mDateFormat.format(creationDate)).endTag("", "created-at");
            serializer.startTag("", "description").text(task.getDescription()).endTag("", "description");
            if (task.getDueDate() != 0)
                serializer.startTag("", "due").attribute("", "type", "datetime").text(mDateFormat.format(new Date(task.getDueDate()))).endTag("", "due");


            serializer.startTag("", "notes").text(task.getDetails() != null ? task.getDetails() : "").endTag("", "notes");

            Id projectId = findTracksIdByProjectId(task.getProjectId());
            if (projectId.isInitialised()) {
                serializer.startTag("", "project-id").attribute("", "type", "integer").text(projectId.toString()).endTag("", "project-id");
            }
            serializer.startTag("", "state").text(task.isComplete() ? "completed" : "active").endTag("", "state");
            serializer.startTag("", "updated-at").attribute("", "type", "datetime").text(mDateFormat.format(new Date(task.getModifiedDate()))).endTag("", "updated-at");


            serializer.endTag("", "todo");
            // serializer.endDocument();
            serializer.flush();
        } catch (IOException ignored) {
            Log.d(cTag, "Failed to serialize task", ignored);
        }

        return writer.toString();
    }

    protected Task parseSingleEntity(XmlPullParser parser) throws ParseException {
        final DateFormat format = mDateFormat;
        Task task = null;
        
        try {
            int eventType = parser.getEventType();
            
            while (eventType != XmlPullParser.END_DOCUMENT && task == null) {
                final String name = parser.getName();
                
                final Builder builder = Task.newBuilder();
                builder.setTimezone("UTC");
                long startDate = 0L;
                long dueDate = 0L;

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (name.equalsIgnoreCase("description")) {
                            builder.setDescription(parser.nextText());
                        } else if (name.equalsIgnoreCase("id")) {
                            Id tracksId = Id.create(Long.parseLong(parser.nextText()));
                            builder.setTracksId(tracksId);
                        } else if (name.equalsIgnoreCase("updated-at")) {
                            long modifiedDate = format.parse(parser.nextText()).getTime();
                            builder.setModifiedDate(modifiedDate);
                        } else if (name.equalsIgnoreCase("context-id")) {
                            String tokenValue = parser.nextText();
                            if (tokenValue != null && !tokenValue.equals("")) {
                                Id tracksId = Id.create(Long.parseLong(tokenValue));
                                Id contextId = findContextIdByTracksId(tracksId);
                                builder.setContextId(contextId);
                            }
                        } else if (name.equalsIgnoreCase("project-id")) {
                            String tokenValue = parser.nextText();
                            if (tokenValue != null && !tokenValue.equals("")) {
                                Id tracksId = Id.create(Long.parseLong(tokenValue));
                                Id projectId = findProjectIdByTracksId(tracksId);
                                builder.setProjectId(projectId);
                            }
                        } else if (name.equalsIgnoreCase("notes")) {
                            builder.setDetails(parser.nextText());
                        } else if (name.equalsIgnoreCase("created-at")) {
                            long created = format.parse(parser.nextText()).getTime();
                            builder.setCreatedDate(created);
                        } else if (name.equalsIgnoreCase("due")) {
                            String textToken = parser.nextText();
                            if (textToken != null && !textToken.equals("")) {
                                dueDate = format.parse(textToken).getTime();
                                builder.setDueDate(dueDate);
                            }
                        } else if (name.equalsIgnoreCase("show-from")) {
                            String textToken = parser.nextText();
                            if (textToken != null && !textToken.equals("")) {
                                startDate = format.parse(textToken).getTime();
                                builder.setStartDate(startDate);
                            }
                        }
                        break;
                        
                    case XmlPullParser.END_TAG:
                        if (name.equalsIgnoreCase("todo")) {
                            boolean allDay = startDate > 0L || dueDate > 0L;
                            builder.setAllDay(allDay);
                            task = builder.build();
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
        return task;
    }

    @Override
    protected String createEntityUrl(Task task) {
        return mTracksUrl + "/todos/" + task.getTracksId() + ".xml";
    }

    @Override
    protected String endIndexTag() {
        return "todos";
    }

    @Override
    protected String entityIndexUrl() {
        return mTracksUrl + "/todos.xml";
    }

}