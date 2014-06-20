package pl.edu.agh.bd2.tutorial;

import java.io.IOException;
import java.text.ParseException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import pl.edu.agh.bd2.tutorial.parser.Parser;

public class Main {

    private static final Logger LOG = Logger.getLogger(Main.class);

    public static void main(String[] args) {
	try {
	    Forum forum = Parser.parseAndInitialize();
	} catch (ParserConfigurationException e) {
	    LOG.error("XML parser configuration error", e);
	    System.exit(1);
	} catch (SAXException e) {
	    LOG.error("XML parsing failed", e);
	    System.exit(1);
	} catch (IOException e) {
	    LOG.error("IO error", e);
	    System.exit(1);
	} catch (ParseException e) {
	    LOG.error("XML parsing failed", e);
	    System.exit(1);
	}
    }

}
