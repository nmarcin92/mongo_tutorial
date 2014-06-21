package pl.edu.agh.bd2.tutorial.dao;

import java.math.BigInteger;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Document(collection = "users")
public class ForumUser {

    @Id
    private BigInteger id;

    private String login;
    private String city;

    @DateTimeFormat(iso = ISO.DATE_TIME)
    private Date joinDate;

    public ForumUser() {
    }

    public ForumUser(String login, String city, Date joinDate) {
	this.login = login;
	this.city = city;
	this.joinDate = joinDate;
    }

    public BigInteger getId() {
	return id;
    }

    public void setId(BigInteger id) {
	this.id = id;
    }

    public String getLogin() {
	return login;
    }

    public void setLogin(String login) {
	this.login = login;
    }

    public String getCity() {
	return city;
    }

    public void setCity(String city) {
	this.city = city;
    }

    public Date getJoinDate() {
	return joinDate;
    }

    public void setJoinDate(Date joinDate) {
	this.joinDate = joinDate;
    }

}
