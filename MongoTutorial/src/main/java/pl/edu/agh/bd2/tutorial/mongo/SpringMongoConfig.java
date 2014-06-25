package pl.edu.agh.bd2.tutorial.mongo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.WriteConcern;

@Configuration
public class SpringMongoConfig {

    public @Bean
    MongoDbFactory mongoDbFactory() throws Exception {

	MongoClientOptions options = MongoClientOptions.builder().connectionsPerHost(10)
		.writeConcern(WriteConcern.NONE).build();
	// return new SimpleMongoDbFactory(new MongoClient("localhost",
	// options), "forum-db");

	return new SimpleMongoDbFactory(new MongoClient(), "forum-db");
    }

    public @Bean
    MongoTemplate mongoTemplate() throws Exception {
	MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
	return mongoTemplate;
    }

}