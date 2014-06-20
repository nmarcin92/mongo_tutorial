package pl.edu.agh.bd2.tutorial.dao;

import java.util.Date;

public class Post {

    private ForumThread thread;
    private String content;
    private Date creationDate;
    private ForumUser user;

    public Post() {
    }

    public Post(ForumThread thread, String content, Date creationDate,
	    ForumUser user) {
	this.thread = thread;
	this.content = content;
	this.creationDate = creationDate;
	this.user = user;
    }

    public ForumThread getThread() {
	return thread;
    }

    public void setThread(ForumThread thread) {
	this.thread = thread;
    }

    public String getContent() {
	return content;
    }

    public void setContent(String content) {
	this.content = content;
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
