package pl.edu.agh.bd2.tutorial;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.xml.sax.SAXException;

import pl.edu.agh.bd2.tutorial.mongo.SpringMongoConfig;
import pl.edu.agh.bd2.tutorial.parser.Parser;

import com.mongodb.BasicDBObject;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private static MongoOperations mongoOperations;
    private static Forum forum;

    public static void main(String[] args) {
	// try {
	@SuppressWarnings("resource")
	ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig.class);
	mongoOperations = (MongoOperations) ctx.getBean("mongoTemplate");

	// initializeDatabase();

	new PerformanceTest() {

	    @Override
	    public String testOperations() {
		return countThreadsIn2013();
	    }
	}.performTest("Threads created in year 2013");

	new PerformanceTest() {
	    @Override
	    public String testOperations() {
		return getMostPopularThreadInMay();
	    };
	}.performTest("Most popular thread in may title");

	new PerformanceTest() {
	    @Override
	    public String testOperations() {
		return countAveragePostLength();
	    };
	}.performTest("Average post length");

	new PerformanceTest() {
	    @Override
	    public String testOperations() {
		return getUserWithMostThreads();
	    };
	}.performTest("User that posted in the biggest number of threads login");

	new PerformanceTest() {
	    @Override
	    public String testOperations() {
		return getMostCommentingUser();
	    };
	}.performTest("User that posted in the biggest number of unique threads login");

	new PerformanceTest() {
	    @Override
	    public String testOperations() {
		return countPostsWithFrodoWord();
	    };
	}.performTest("Number of posts containing word 'Frodo'");

	new PerformanceTest() {
	    @Override
	    public String testOperations() {
		return countPostsWithUsersFromKCity();
	    };
	}.performTest("Number of posts written by users from city starting with 'K' letter");

	new PerformanceTest() {
	    @Override
	    public String testOperations() {
		return get35thMostPopularUsedWord();
	    };
	}.performTest("35th most popular word in posts");

	// } catch (ParserConfigurationException e) {
	// LOG.error("XML parser configuration error", e);
	// } catch (SAXException e) {
	// LOG.error("XML parsing failed", e);
	// } catch (IOException e) {
	// LOG.error("IO error", e);
	// } catch (ParseException e) {
	// LOG.error("XML parsing failed", e);
	// }

    }

    private static void initializeDatabase() throws ParserConfigurationException, SAXException, IOException,
	    ParseException {
	forum = Parser.parseAndInitialize();

	LOG.info("Cleaning database...");
	mongoOperations.dropCollection("posts");
	mongoOperations.dropCollection("threads");
	mongoOperations.dropCollection("users");
	LOG.info("Database cleaned");

	Date startTime = new Date();
	LOG.info("Loading data into database started");
	mongoOperations.insertAll(forum.getForumUsers());
	mongoOperations.insertAll(forum.getForumThreads());
	mongoOperations.insertAll(forum.getPosts());
	LOG.info("Loading data into database finished. Time: " + (new Date().getTime() - startTime.getTime()) + "ms");

    }

    private static String countThreadsIn2013() {
	return null;
    }

    private static String getMostPopularThreadInMay() {
	return null;
    }

    private static String countAveragePostLength() {
	return null;
    }

    private static String getUserWithMostThreads() {
	return null;
    }

    private static String getMostCommentingUser() {
	return null;
    }

    private static String countPostsWithUsersFromKCity() {
	return null;
    }

    private static String get35thMostPopularUsedWord() {
	return null;
    }

    private static String countPostsWithFrodoWord() {
	BasicDBObject query = new BasicDBObject();
	Pattern regex = Pattern.compile(".*Frodo.*");
	query.put("content", regex);

	return String.valueOf(mongoOperations.getCollection("posts").count(query));
    }

    private static abstract class PerformanceTest {

	public void performTest(String testInfo) {
	    long startTime = new Date().getTime();
	    String result = testOperations();
	    long elapsedTime = new Date().getTime() - startTime;

	    System.out.println("----------Performance test----------");
	    System.out.println(testInfo + ": " + result);
	    System.out.println("time elapsed: " + elapsedTime + "ms");
	    System.out.println("------------------------------------");
	}

	public abstract String testOperations();
    }

}
