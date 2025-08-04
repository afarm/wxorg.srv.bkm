package wxorg.xmlparser.test;

import org.junit.Test;
import wxorg.xmlparser.RootBlock;
import wxorg.xmlparser.XmlParser;

public class NewNodeTest {

    @Test
    public void main() {

        RootBlock rootBlock = new RootBlock();
        rootBlock.setSource("");
        XmlParser xmlParser = new XmlParser();
        xmlParser.parse(rootBlock);
        xmlParser.addNode("NNN");

        String joined = rootBlock.join();
        System.out.println(joined);
    }
}
