package wxorg.xmlparser.test;

import org.junit.Test;
import wxorg.xmlparser.XmlParser;

public class NewNodeTest {

    @Test
    public void main() {

        XmlParser xmlParser = new XmlParser();
        xmlParser.setSource("");
        xmlParser.parse();
        xmlParser.addNode("NNN");

        String joined = xmlParser.join();
        System.out.println(joined);
    }
}
