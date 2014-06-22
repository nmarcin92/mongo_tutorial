package pl.edu.agh.bd2.tutorial;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jetty.jndi.local.localContextRoot;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.xml.sax.SAXException;

import pl.edu.agh.bd2.tutorial.dao.ForumThread;
import pl.edu.agh.bd2.tutorial.dao.ForumUser;
import pl.edu.agh.bd2.tutorial.dao.Post;
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

	System.out.println("Summary testing time: " + PerformanceTest.getSummaryTestingTime() + "ms");

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

    @SuppressWarnings("deprecation")
    private static String countThreadsIn2013() {
	Query query = new Query();
	Date startDate = new DateTime("2013-01-01T00:00:00Z").toDate();
	Date endDate = new DateTime("2013-12-30T23:59:59Z").toDate();
	query.addCriteria(new Criteria().andOperator(
	//
		Criteria.where("creationDate").gte(startDate), Criteria.where("creationDate").lte(endDate)));

	return String.valueOf(mongoOperations.count(query, ForumThread.class));
    }

    private static String getMostPopularThreadInMay() {

//    	Query query = new Query();
//    	Date startDate = new DateTime("2013-05-01T00:00:00Z").toDate();
//    	Date endDate = new DateTime("2013-05-31T23:59:59Z").toDate();
//    	query.addCriteria(new Criteria().andOperator(
//    	//
//    		Criteria.where("creationDate").gte(startDate), Criteria.where("creationDate").lte(endDate)));
//
//    	return String.valueOf(mongoOperations.count(query, ForumThread.class));
    	return null;
    }

    private static String countAveragePostLength() {
    	/*
    	 * Unfortunately the aggregation framework doesn't support a "len" operator 
    	 * to automatically convert strings to their length while you do a query
    	 */
//    	
//    	AggregationOperation project = Aggregation.project("login");
//    	AggregationOperation sort = Aggregation.sort(Direction.DESC, "login");//("user").sum("amount").as("amount_sum").count().as("tran_count");
//    	Aggregation aggregation = Aggregation.newAggregation(project, sort);
//    	 AggregationResults<Post> result = mongoOperations.aggregate(aggregation, "posts", Post.class);
//    	 
//    	 return String.valueOf(result.getMappedResults().size());
    	return null;
    }

    private static String getUserWithMostThreads() {

    	AggregationOperation group = Aggregation.group("user").sum("amount").as("am").count().as("count");
    	AggregationOperation sort = Aggregation.sort(Direction.DESC, "count");
    	AggregationOperation limit = Aggregation.limit(1);
    	Aggregation aggregation = Aggregation.newAggregation(group, sort, limit);
		AggregationResults<ForumUser> result = mongoOperations.aggregate(aggregation, "posts", ForumUser.class);
	// TypedAggregation<Post> agg = newAggregation(Post.class, group("user",
	// "thread").count().as("threadNr"),
	// sort(Direction.DESC, "threadNr"));
	// AggregationResults<Post> res = mongoOperations.aggregate(agg,
	// Post.class);
	//
	// System.out.println(res.getMappedResults());
	return result.getMappedResults().get(0).getLogin();
    }

    private static String getMostCommentingUser() {
	return null;
    }

    private static String countPostsWithUsersFromKCity() {
	BasicDBObject query = new BasicDBObject();
	Pattern regex = Pattern.compile("K.*");
	query.put("user.city", regex);

	return String.valueOf(mongoOperations.getCollection("posts").count(query));
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

	private static long summaryTestingTime = 0;

	public void performTest(String testInfo) {
	    long startTime = new Date().getTime();
	    String result = testOperations();
	    long elapsedTime = new Date().getTime() - startTime;

	    System.out.println("----------Performance test----------");
	    System.out.println(testInfo + ": " + result);
	    System.out.println("time elapsed: " + elapsedTime + "ms");
	    System.out.println("------------------------------------");

	    summaryTestingTime += elapsedTime;
	}

	public static long getSummaryTestingTime() {
	    return summaryTestingTime;
	}

	public abstract String testOperations();
    }

}
