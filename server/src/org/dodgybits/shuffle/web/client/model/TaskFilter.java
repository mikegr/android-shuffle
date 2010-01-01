package org.dodgybits.shuffle.web.client.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class TaskFilter implements Serializable {
    public ValueSelection<ContextValue> contexts;
    public ValueSelection<ProjectValue> projects;
    public BooleanSelection isComplete;
    public boolean onlyTopActionPerProject;
}
