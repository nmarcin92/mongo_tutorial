package pl.edu.agh.bd2.tutorial.dao;

import java.math.BigInteger;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Document(collection = "posts")
public class Post {

    @Id
    private BigInteger id;

    private ForumThread thread;
    private String content;

    @DateTimeFormat(iso = ISO.DATE_TIME)
    private Date creationDate;
    private ForumUser user;

    public Post() {
    }

    public Post(ForumThread thread, String content, Date creationDate, ForumUser user) {
	this.thread = thread;
	this.content = content;
	this.creationDate = creationDate;
	this.user = user;
    }

    public BigInteger getId() {
	return id;
    }

    public void setId(BigInteger id) {
	this.id = id;
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
