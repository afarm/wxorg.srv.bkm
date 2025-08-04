package wxorg;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class RecursiveParser {

    private String dir;

    private final ParserXmlFile parserXmlFile;

    public RecursiveParser(String dir, ParserXmlFile parserXmlFile) {
        this.dir = dir;
        this.parserXmlFile = parserXmlFile;
    }

    public List<Map<String, String>> parse() throws IOException {
        Path startDir = Paths.get(dir);
        List<Map<String, String>> all = new ArrayList<>();
        Files.walk(startDir)
                .filter(p -> p.toString().endsWith(".xml"))
                .forEach(p -> {
                    try {
                        List<Map<String, String>> entries = parserXmlFile.parseFile(p);
                        all.addAll(entries);
                    } catch (IOException e) {
                        System.err.println("Error parsing file: " + p);
                        e.printStackTrace();
                    }
                });
        return all;
    }
}