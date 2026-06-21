
package model;

public class Task {
    private String title;
    private String description;
    private String status;
    private String assignedTo;
    private String priority;
    private String dueDate;

    public Task(String title, String description, String status, String assignedTo, String priority, String dueDate) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.assignedTo = assignedTo;
        this.priority = priority;
        this.dueDate = dueDate;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public String getPriority() {
        return priority;
    }

    public String getDueDate() {
        return dueDate;
    }
}