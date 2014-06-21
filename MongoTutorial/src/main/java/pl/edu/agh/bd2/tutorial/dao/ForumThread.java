package pl.edu.agh.bd2.tutorial.dao;

import java.math.BigInteger;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Document(collection = "threads")
public class ForumThread {

    @Id
    private BigInteger id;

    private String threadTitle;

    @DateTimeFormat(iso = ISO.DATE_TIME)
    private Date creationDate;
    private ForumUser user;

    public ForumThread() {
    }

    public ForumThread(String threadTitle, Date creationDate, ForumUser user) {
	this.threadTitle = threadTitle;
	this.creationDate = creationDate;
	this.user = user;
    }

    public BigInteger getId() {
	return id;
    }

    public void setId(BigInteger id) {
	this.id = id;
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
