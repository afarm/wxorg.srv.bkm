package wxorg;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class RecursiveParser {

    private String dir;

    private final ParserXmlFile parserFile;

    public RecursiveParser(String dir, ParserXmlFile parserFile) {
        this.dir = dir;
        this.parserFile = parserFile;
    }

    public List<Map<String, String>> parse() throws IOException {
        Path startDir = Paths.get(dir);
        List<Map<String, String>> all = new ArrayList<>();
        Files.walk(startDir)
                .filter(p -> p.toString().endsWith(".xml"))
                .forEach(p -> {
                    try {
                        List<Map<String, String>> entries = parserFile.parseFile(p);
                        all.addAll(entries);
                    } catch (IOException e) {
                        System.err.println("Error parsing file: " + p);
                        e.printStackTrace();
                    }
                });
        return all;
    }
}