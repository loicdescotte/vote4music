import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import play.Logger;
import play.mvc.Http.Response;
import play.test.FunctionalTest;

public class ApplicationTest extends FunctionalTest {

    @Test
    public void testThatIndexPageWorks() {
        Response response = GET("/");
        assertIsOk(response);
        assertContentType("text/html", response);
        assertCharset("utf-8", response);
    }
    
    @Test
    public void testArtistisUnique() {
        String album1 = "<album><artist>joe</artist><name>album1</name><release-date>2010</release-date><genre>ROCK</genre></album>";
        POST("/api/albums","application/xml",album1);
        //Other album, same artist name
        String album2 = "<album><artist>joe</artist><name>album2</name><release-date>2010</release-date><genre>ROCK</genre></album>";
        POST("/api/albums","application/xml",album2);
        //check artist is unique (name must be unique)        
        Response response = GET("/api/artists");
        String xmlTree = response.out.toString();
        //parse response and assert there is only one artist
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document document = null;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(new ByteArrayInputStream(xmlTree.getBytes()));
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		Element rootNode = document.getDocumentElement();
		assertTrue(rootNode.getElementsByTagName("artist").getLength()==1);
		
		//add an artist
		String album3 = "<album><artist>bob</artist><name>album3</name><release-date>2010</release-date><genre>ROCK</genre></album>";
        POST("/api/albums","application/xml",album3);
        
        response = GET("/api/artists");
        xmlTree = response.out.toString();
        
        //parse response and assert there is only one artist
        factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(new ByteArrayInputStream(xmlTree.getBytes()));
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		rootNode = document.getDocumentElement();
		assertTrue(rootNode.getElementsByTagName("artist").getLength()==2);
        
        
		
    }
    
}