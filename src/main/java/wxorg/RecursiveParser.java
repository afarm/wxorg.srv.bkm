package wxorg;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class RecursiveParser {
    public List<Entry> parse() throws IOException {
        Path startDir = Paths.get("/home/f/xorg/xorg");
        List<String> entryTypes = Arrays.asList("Note", "Bookmark", "Task", "Reminder"); // ← можно добавлять свои
        List<Entry> all = new ArrayList<>();
        Files.walk(startDir)
                .filter(p -> p.toString().endsWith(".txt"))
                .forEach(p -> {
                    try {
                        List<Entry> entries = Parser.parseFile(p, entryTypes);
                        all.addAll(entries);
                    } catch (IOException e) {
                        System.err.println("Error parsing file: " + p);
                        e.printStackTrace();
                    }
                });
        return all;
    }
}