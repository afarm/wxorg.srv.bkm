package wxorg.textparser;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ParserTest {
    public static void main(String[] args) throws Exception {
        String content = Files.readString(Paths.get("RJPCCGPC.txt"));
        Tokenizer tokenizer = new Tokenizer(content);
        List<Token> tokens = tokenizer.tokenize();

        TextFileParser parser = new TextFileParser(tokens);
        parser.parse();

        String joined = parser.join();
        if (!joined.equals(content)) {
            System.out.println("ERROR: join() doesn't match original input.");
        } else {
            System.out.println("PASS: join() matches original input.");
        }

        // Print IDs
        System.out.println("Entries by ID:");
        for (String id : parser.getEntriesById().keySet()) {
            System.out.println(" - " + id);
        }

        // Print Tags
        System.out.println("Tags:");
        for (String tag : parser.getEntriesByTag().keySet()) {
            System.out.println(" - " + tag);
        }
    }
}