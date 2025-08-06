package wxorg.xmlparser.test;

import org.junit.Test;
import wxorg.xmlparser.InsertMode;
import wxorg.xmlparser.XmlNode;
import wxorg.xmlparser.XmlParser;

import java.util.List;

public class NewNodeTest {

    @Test
    public void main() {

        XmlParser xmlParser = new XmlParser();
        xmlParser.setSource("<Note id=\"XXX\"></Note>");
        xmlParser.parse();

        List<XmlNode> allNodes = xmlParser.getAllNodes();
        XmlNode parent = allNodes.get(0);
        XmlNode newNode = new XmlNode(xmlParser.getTokens());
        xmlParser.addNode(parent, newNode, InsertMode.AFTER);

        String joined = xmlParser.join();
        System.out.println(joined);
    }
}
