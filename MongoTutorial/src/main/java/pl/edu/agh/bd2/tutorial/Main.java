package pl.edu.agh.bd2.tutorial;

import java.io.IOException;
import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.xml.sax.SAXException;

import pl.edu.agh.bd2.tutorial.dao.ForumThread;
import pl.edu.agh.bd2.tutorial.dao.ForumUser;
import pl.edu.agh.bd2.tutorial.dao.Post;
import pl.edu.agh.bd2.tutorial.mongo.SpringMongoConfig;
import pl.edu.agh.bd2.tutorial.parser.Parser;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private static MongoOperations mongoOperations;
    private static Forum forum;

    public static void main(String[] args) {
	try {
	    @SuppressWarnings("resource")
	    ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig.class);
	    mongoOperations = (MongoOperations) ctx.getBean("mongoTemplate");

	    initializeDatabase();

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

	    System.out.println("Summary testing time: ");
	    System.out.println("standard: " + PerformanceTest.getSummaryTestingTime(false) + "ms");
	    System.out.println("mongo: " + PerformanceTest.getSummaryTestingTime(true) + "ms");

	} catch (ParserConfigurationException e) {
	    LOG.error("XML parser configuration error", e);
	} catch (SAXException e) {
	    LOG.error("XML parsing failed", e);
	} catch (IOException e) {
	    LOG.error("IO error", e);
	} catch (ParseException e) {
	    LOG.error("XML parsing failed", e);
	}

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
	Date startDate = new DateTime("2013-01-01T00:00:00Z").toDate();
	Date endDate = new DateTime("2013-12-30T23:59:59Z").toDate();

	if (PerformanceTest.mongoMode) {
	    Query query = new Query();
	    query.addCriteria(new Criteria().andOperator(
	    //
		    Criteria.where("creationDate").gte(startDate), Criteria.where("creationDate").lte(endDate)));

	    return String.valueOf(mongoOperations.count(query, ForumThread.class));
	} else {
	    List<ForumThread> threads = mongoOperations.findAll(ForumThread.class);
	    int counter = 0;
	    for (ForumThread t : threads) {
		if (t.getCreationDate().after(startDate) && t.getCreationDate().before(endDate))
		    ++counter;
	    }

	    return String.valueOf(counter);
	}
    }

    // Czasami wychodz¹ inne wyniki - prawdopodobnie kilka tematow ma tê sam¹
    // liczbê postów
    // i zale¿y jak siê posortuje
    private static String getMostPopularThreadInMay() {
	Date startDate = new DateTime("2013-05-01T00:00:00Z").toDate();
	Date endDate = new DateTime("2013-05-31T23:59:59Z").toDate();

	if (PerformanceTest.mongoMode) {
	    DBObject match = new BasicDBObject("$match", new BasicDBObject("creationDate", BasicDBObjectBuilder
		    .start("$gte", startDate).append("$lte", endDate).get()));

	    DBObject groupFields = new BasicDBObject("_id", "$thread");
	    groupFields.put("postsInThread", new BasicDBObject("$sum", 1));
	    DBObject group = new BasicDBObject("$group", groupFields);

	    DBObject sort = new BasicDBObject("$sort", new BasicDBObject("postsInThread", -1));

	    AggregationOutput output = mongoOperations.getCollection("posts").aggregate(match, group, sort);
	    return ((DBObject) output.results().iterator().next().get("_id")).get("threadTitle").toString();
	} else {
	    List<Post> posts = mongoOperations.findAll(Post.class);
	    Map<ForumThread, Integer> map = new HashMap<>();
	    for (Post p : posts) {
		if (p.getCreationDate().after(startDate) && p.getCreationDate().before(endDate)) {
		    Integer v = map.get(p.getThread());
		    if (v == null) {
			map.put(p.getThread(), 1);
		    } else {
			map.put(p.getThread(), v + 1);
		    }
		}
	    }
	    Entry<ForumThread, Integer> maxEntry = null;
	    for (Entry<ForumThread, Integer> entry : map.entrySet()) {
		if (maxEntry == null || entry.getValue() >= maxEntry.getValue()) {
		    maxEntry = entry;
		}
	    }

	    return maxEntry.getKey().getThreadTitle();
	}

    }

    private static String countAveragePostLength() {
	if (PerformanceTest.mongoMode) {
	    /*
	     * Unfortunately the aggregation framework doesn't support a "len"
	     * operator to automatically convert strings to their length while
	     * you do a query
	     */
	    AggregationOperation project = Aggregation.project("content");
	    Aggregation aggregation = Aggregation.newAggregation(project);
	    AggregationResults<Post> result = mongoOperations.aggregate(aggregation, "posts", Post.class);
	    // System.out.println(result.getMappedResults().get(100).getContent());

	    Query query = new Query();
	    long len = mongoOperations.count(query, Post.class);

	    double avg = 0;
	    for (Post p : result.getMappedResults()) {

		avg += p.getContent().length();
	    }

	    avg /= len;
	    return Double.toString(avg);
	} else {
	    List<Post> posts = mongoOperations.findAll(Post.class);
	    double len = 0;
	    for (Post p : posts) {
		len += p.getContent().length();
	    }

	    return String.valueOf(len / posts.size());
	}

    }

    private static String getUserWithMostThreads() {
	if (PerformanceTest.mongoMode) {

	    AggregationOperation group = Aggregation.group("user").sum("amount").as("am").count().as("count");
	    AggregationOperation sort = Aggregation.sort(Direction.DESC, "count");
	    AggregationOperation limit = Aggregation.limit(1);
	    Aggregation aggregation = Aggregation.newAggregation(group, sort, limit);
	    AggregationResults<ForumUser> result = mongoOperations.aggregate(aggregation, "posts", ForumUser.class);

	    return result.getMappedResults().get(0).getLogin();
	} else {
	    List<Post> posts = mongoOperations.findAll(Post.class);
	    Map<ForumUser, Set<String>> userMap = new HashMap<>();
	    for (Post p : posts) {
		Set<String> set = userMap.get(p.getUser());
		if (set == null) {
		    set = new TreeSet<String>();
		    // ForumThread is not comparable
		    set.add(p.getThread().getThreadTitle());
		    userMap.put(p.getUser(), set);
		} else {
		    set.add(p.getThread().getThreadTitle());
		}
	    }

	    Entry<ForumUser, Set<String>> maxEntry = null;
	    for (Entry<ForumUser, Set<String>> entry : userMap.entrySet()) {
		if (maxEntry == null || entry.getValue().size() > maxEntry.getValue().size()) {
		    maxEntry = entry;
		}
	    }

	    return maxEntry.getKey().getLogin();
	}
    }

    private static String getMostCommentingUser() {
	return null;
    }

    private static String countPostsWithUsersFromKCity() {
	if (PerformanceTest.mongoMode) {
	    BasicDBObject query = new BasicDBObject();
	    Pattern regex = Pattern.compile("^K.*");
	    query.put("user.city", regex);

	    return String.valueOf(mongoOperations.getCollection("posts").count(query));
	} else {
	    List<Post> posts = mongoOperations.findAll(Post.class);
	    int count = 0;
	    for (Post p : posts) {
		if (p.getUser().getCity() != null && p.getUser().getCity().startsWith("K")) {
		    ++count;
		}
	    }

	    return String.valueOf(count);
	}
    }

    private static String get35thMostPopularUsedWord() {

	if (PerformanceTest.mongoMode) {
	    // String map = "function Map() {" //
	    // + "var content = this.content; " //
	    // + "if (content) {" //
	    // + "content = content.toLowerCase().split(\" \"); "//
	    // + "for (var i=content.length-1; i>=0; i--) {" //
	    // + "if (content[i]) {" //
	    // + "emit(content[i],1);}}}}";
	    //
	    // String reduce = "function Reduce(key, values) {" //
	    // + "var count = 0; " //
	    // + "values.forEach(function(v) {" //
	    // + "count += v; });" //
	    // + "return count;}";
	    // DBCollection postsCollection =
	    // mongoOperations.getCollection("posts");
	    // mongoOperations.dropCollection("map_reduce_result");
	    // MapReduceCommand cmd = new MapReduceCommand(postsCollection, map,
	    // reduce, "map_reduce_result",
	    // MapReduceCommand.OutputType.REPLACE, null);
	    // MapReduceOutput out = postsCollection.mapReduce(cmd);
	    //
	    // int i = 1;
	    // for (DBObject o :
	    // mongoOperations.getCollection("map_reduce_result").find()
	    // .sort(new BasicDBObject("value", -1))) {
	    // ++i;
	    // if (i == 35) {
	    // return o.get("_id").toString();
	    // }
	    // }
	    return null;
	} else {
	    List<Post> posts = mongoOperations.findAll(Post.class);
	    Map<String, Integer> wordsMap = new HashMap<>();
	    ValueComparator vc = new ValueComparator(wordsMap);
	    Map<String, Integer> sortedWordsMap = new TreeMap<>(vc);

	    for (Post p : posts) {
		for (String word : p.getContent().toLowerCase().split(" ")) {
		    Integer count = wordsMap.get(word);
		    if (count == null) {
			wordsMap.put(word, 1);
		    } else {
			wordsMap.put(word, count + 1);
		    }
		}
	    }

	    int i = 1;
	    sortedWordsMap.putAll(wordsMap);
	    for (String s : sortedWordsMap.keySet()) {
		if (i == 35) {
		    return s;
		}
		++i;
	    }

	    return null;
	}
    }

    private static String countPostsWithFrodoWord() {
	if (PerformanceTest.mongoMode) {
	    BasicDBObject query = new BasicDBObject();
	    Pattern regex = Pattern.compile(".*Frodo.*");
	    query.put("content", regex);

	    return String.valueOf(mongoOperations.getCollection("posts").count(query));
	} else {
	    List<Post> posts = mongoOperations.findAll(Post.class);
	    int count = 0;
	    for (Post p : posts) {
		if (p.getContent().contains("Frodo")) {
		    ++count;
		}
	    }

	    return String.valueOf(count);
	}
    }

    private static abstract class PerformanceTest {

	private static long summaryJavaTestingTime = 0;
	private static long summaryMongoTestingTime = 0;
	public static boolean mongoMode = true;

	public void performTest(String testInfo) {
	    mongoMode = true;
	    long startTime = new Date().getTime();
	    String mongoResult = testOperations();
	    long mongoElapsedTime = new Date().getTime() - startTime;

	    mongoMode = false;
	    startTime = new Date().getTime();
	    String javaResult = testOperations();
	    long javaElapsedTime = new Date().getTime() - startTime;

	    System.out.println("----------Performance test----------");
	    System.out.println(testInfo);
	    System.out.println("- standard methods: " + javaResult + ", time " + javaElapsedTime + "ms");
	    System.out.println("- mongo methods: " + mongoResult + ", time " + mongoElapsedTime + "ms");
	    System.out.println("------------------------------------");

	    summaryJavaTestingTime += javaElapsedTime;
	    summaryMongoTestingTime += mongoElapsedTime;
	}

	public static long getSummaryTestingTime(boolean mongoMode) {
	    if (mongoMode)
		return summaryMongoTestingTime;
	    return summaryJavaTestingTime;
	}

	public abstract String testOperations();
    }

    private static class ValueComparator implements Comparator<String> {
	private final Map<String, Integer> baseMap;

	public ValueComparator(Map<String, Integer> baseMap) {
	    this.baseMap = baseMap;
	}

	@Override
	public int compare(String o1, String o2) {
	    if (baseMap.get(o1) < baseMap.get(o2)) {
		return 1;
	    } else {
		return -1;
	    }
	}
    }

}
