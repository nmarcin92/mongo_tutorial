package pl.edu.agh.bd2.tutorial;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import pl.edu.agh.bd2.tutorial.dao.ForumThread;
import pl.edu.agh.bd2.tutorial.dao.ForumUser;
import pl.edu.agh.bd2.tutorial.dao.Post;

public class Forum {

    private final static Forum _instance = new Forum();

    private final Map<String, ForumThread> threadsMap = new HashMap<String, ForumThread>();
    private final Map<String, ForumUser> usersMap = new HashMap<String, ForumUser>();
    private final List<Post> posts = new LinkedList<Post>();

    public static Forum getInstance() {
	return _instance;
    }

    private Forum() {
    }

    public void addPost(Post post, ForumThread thread, ForumUser user) {
	if (usersMap.containsKey(user.getLogin())) {
	    user = usersMap.get(user.getLogin());
	    thread.setUser(user);
	    post.setUser(user);
	} else {
	    usersMap.put(user.getLogin(), user);
	}
	if (threadsMap.containsKey(thread.getThreadTitle())) {
	    Date newDate = thread.getCreationDate();
	    thread = threadsMap.get(thread.getThreadTitle());
	    if (thread.getCreationDate().after(newDate)) {
		thread.setCreationDate(newDate);
		thread.setUser(user);
	    }
	    post.setThread(thread);
	} else {
	    threadsMap.put(thread.getThreadTitle(), thread);
	}

	posts.add(post);
    }

    public List<ForumUser> getForumUsers() {
	return new LinkedList<ForumUser>(usersMap.values());
    }

    public List<ForumThread> getForumThreads() {
	return new LinkedList<ForumThread>(threadsMap.values());
    }

    public List<Post> getPosts() {
	return posts;
    }

}
