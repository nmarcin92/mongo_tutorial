package pl.edu.agh.bd2.tutorial.dao;

import java.util.Date;

public class ForumThread {

    private String threadTitle;
    private Date creationDate;
    private ForumUser user;

    public ForumThread() {
    }

    public ForumThread(String threadTitle, Date creationDate, ForumUser user) {
	this.threadTitle = threadTitle;
	this.creationDate = creationDate;
	this.user = user;
    }

    public String getThreadTitle() {
	return threadTitle;
    }

    public void setThreadTitle(String threadTitle) {
	this.threadTitle = threadTitle;
    }

    public Date getCreationDate() {
	return creationDate;
    }

    public void setCreationDate(Date creationDate) {
	this.creationDate = creationDate;
    }

    public ForumUser getUser() {
	return user;
    }

    public void setUser(ForumUser user) {
	this.user = user;
    }

}
