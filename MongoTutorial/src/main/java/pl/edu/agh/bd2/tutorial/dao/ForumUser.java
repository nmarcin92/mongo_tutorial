package pl.edu.agh.bd2.tutorial.dao;

import java.util.Date;

public class ForumUser {

    private String login;
    private String city;
    private Date joinDate;

    public ForumUser() {
    }

    public ForumUser(String login, String city, Date joinDate) {
	this.login = login;
	this.city = city;
	this.joinDate = joinDate;
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
