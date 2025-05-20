package wxorg;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class RecursiveParser {

    private String dir;

    private final ParserFile parserFile;

    public RecursiveParser(String dir, ParserFile parserFile) {
        this.dir = dir;
        this.parserFile = parserFile;
    }

    public List<Entry> parse() throws IOException {
        Path startDir = Paths.get(dir);
        List<Entry> all = new ArrayList<>();
        Files.walk(startDir)
                .filter(p -> p.toString().endsWith(".txt"))
                .forEach(p -> {
                    try {
                        List<Entry> entries = parserFile.parseFile(p);
                        all.addAll(entries);
                    } catch (IOException e) {
                        System.err.println("Error parsing file: " + p);
                        e.printStackTrace();
                    }
                });
        return all;
    }
}