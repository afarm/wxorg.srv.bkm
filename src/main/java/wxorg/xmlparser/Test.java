package wxorg.xmlparser;

import java.util.Objects;

public class Test {
    public static void main(String[] args) {
        Block block = new Block();
        block.setSource("" +
                "  some text \n\n" +
                "  --- \n\n" +
                "<Book id=\"ID123\" cdate=\"2025-01-01\" \n" +
                "    url=\"http://www.com\"> \n" +
                "    text \n" +
                "    <Ref id=\"ID123123\" /> \n" +
                "    <Ref id=\"ID123dddd\" /> \n" +
                "</Book>\n" +
                "  \n" +
                "<Node id=\"ID22\" cdate=\"2025-02-01\" \n" +
                "    url=\"http://www.qqq\"> \n" +
                "    text \n" +
                "    <Ref id=\"ID125551\" /> \n" +
                "    <Ref id=\"ID123555\" /> \n" +
                "</Note>\n" +
                "text \n");

        Parser parser = new Parser();
        parser.parse(block);

//        Tokenizer tokenizer = new Tokenizer(block.getSource());
//        tokenizer.tokenize().forEach(System.out::println);

        // tokens = {"  ", "some text", " \n\n  ", "---", " \n\n", "<", "Book", " ", "id", "=", "\"", "ID123", "\"", " " ...}
        System.out.println(Objects.equals(block.getChildren().get(0).getName(), "Book"));
        System.out.println(Objects.equals(block.getChildren().get(1).getAttr("id").getValue().getValue(), "ID22"));
        // System.out.println(Objects.equals(block.getChildren().get(1).getTokens().get(OPEN_LT).getValue(), "<"));
        System.out.println(Objects.equals(block.getAllNodes().get(1).getName(), "Ref"));

        System.out.println(Objects.equals(block.join(), block.getSource()));

        System.out.println(block.getSource());

        block.getAllNodes().get(5).addAttr("key", "VAL");
        block.getAllNodes().get(5).addAttr("page", "123");
        block.getAllNodes().get(5).addAttr("mdate", "2025");

        System.out.println(block.join());

    }
}
