package pl.edu.agh.bd2.tutorial.parser;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import pl.edu.agh.bd2.tutorial.Forum;
import pl.edu.agh.bd2.tutorial.dao.ForumThread;
import pl.edu.agh.bd2.tutorial.dao.ForumUser;
import pl.edu.agh.bd2.tutorial.dao.Post;

public class Parser {

    private static final Logger LOG = LoggerFactory.getLogger(Parser.class);
    private static final String XML_FILE_PATH = "src/main/resources/tolkien.xml";
    // private static final DateFormat THREAD_DATE_FORMAT = new
    // SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final DateFormat USER_DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag("pl"));
    private static final DateFormat POST_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    private static final Pattern USER_DATE_PATTERN = Pattern.compile("Do³¹czy³\\(a\\): (.*)");
    private static final Pattern USER_CITY_PATTERN = Pattern.compile("Sk¹d: (.*)");
    private static final Pattern POST_DATE_PATTERN = Pattern.compile("Wys³any: (.*)");

    public static Forum parseAndInitialize() throws ParserConfigurationException, SAXException, IOException,
	    ParseException {

	LOG.info("XML parsing from file " + XML_FILE_PATH + " started");

	Forum forum = Forum.getInstance();

	File xmlFile = new File(XML_FILE_PATH);
	DocumentBuilder dBuilder;
	dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	Document doc = dBuilder.parse(xmlFile);
	doc.getDocumentElement().normalize();

	NodeList nList = doc.getElementsByTagName("task_result");
	for (int i = 0; i < nList.getLength(); ++i) {
	    Node node = nList.item(i);
	    Element element = (Element) node;
	    // Date threadCreationDate =
	    // THREAD_DATE_FORMAT.parse(element.getElementsByTagName("date").item(0)
	    // .getTextContent());

	    NodeList threadList = element.getElementsByTagName("rule");

	    String threadTitle = threadList.item(0).getTextContent().split(":")[1].substring(1).trim();
	    String userData = threadList.item(1).getTextContent();
	    String userLogin = threadList.item(2).getTextContent().trim();
	    String postContent = threadList.item(3).getTextContent();
	    String postDetails = threadList.item(4).getTextContent();

	    Date userJoinDate = null;
	    Matcher m = USER_DATE_PATTERN.matcher(userData);
	    if (m.find()) {
		userJoinDate = USER_DATE_FORMAT.parse(m.group(1));
	    }
	    String userCity = null;
	    m = USER_CITY_PATTERN.matcher(userData);
	    if (m.find()) {
		userCity = m.group(1);
	    }
	    Date postCreationDate = null;
	    m = POST_DATE_PATTERN.matcher(postDetails);
	    if (m.find()) {
		String dateText = m.group(1);
		if (dateText.startsWith("Dzisiaj")) {
		    postCreationDate = getDateWithHours(true, dateText.substring(dateText.length() - 5));
		} else if (dateText.startsWith("Wczoraj")) {
		    postCreationDate = getDateWithHours(false, dateText.substring(dateText.length() - 5));
		} else {
		    postCreationDate = POST_DATE_FORMAT.parse(m.group(1));
		}
	    }

	    ForumUser user = new ForumUser();
	    user.setLogin(userLogin);
	    user.setJoinDate(userJoinDate);
	    user.setCity(userCity);

	    ForumThread thread = new ForumThread();
	    thread.setThreadTitle(threadTitle);
	    thread.setUser(user);
	    thread.setCreationDate(postCreationDate);

	    Post post = new Post();
	    post.setContent(postContent);
	    post.setThread(thread);
	    post.setUser(user);
	    post.setCreationDate(postCreationDate);

	    forum.addPost(post, thread, user);
	}

	LOG.info("XML parsing from file " + XML_FILE_PATH + " finished");
	LOG.info("Imported users: " + forum.getForumUsers().size() + ", threads: " + forum.getForumThreads().size()
		+ ", posts: " + forum.getPosts().size());

	return forum;
    }

    private static Date getDateWithHours(boolean today, String hour) {
	Calendar cal = Calendar.getInstance();
	cal.add(Calendar.HOUR, Integer.parseInt(hour.split(":")[0]));
	cal.add(Calendar.MINUTE, Integer.parseInt(hour.split(":")[1]));
	if (!today) {
	    cal.add(Calendar.DATE, -1);
	}

	return cal.getTime();
    }
}
