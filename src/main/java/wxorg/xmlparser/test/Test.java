package wxorg.xmlparser.test;

import wxorg.xmlparser.XmlNode;
import wxorg.xmlparser.XmlParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class Test {

    public static void main(String[] args) throws IOException {
        String source = Files.readString(Path.of("Note1.xml"));

        XmlParser xmlParser = new XmlParser();
        xmlParser.setSource(source);
        xmlParser.parse();

        XmlNode first = xmlParser.getChildren().get(0);
        //Node second = rootBlock.getChildren().get(1);

        System.out.println(Objects.equals(first.getName(), "Note"));
        //System.out.println(Objects.equals(second.getAttr("id").getValue().getValue(), "ID123"));
        //System.out.println(Objects.equals(rootBlock.getAllNodes().get(2).getName(), "Ref"));
        System.out.println(Objects.equals(xmlParser.join(), xmlParser.getSource()));
        //System.out.println(rootBlock.getSource());

        first = xmlParser.getChildren().get(0);
        first.setName("Xxxx");
        first.addAttr("key", "VAL");
        first.addAttr("page", "123");
        first.addAttr("mdate", "2025");

        String joined = xmlParser.join();
        //System.out.println(joined);
        Files.writeString(Path.of("Note1-res.xml"), joined);
    }
}
