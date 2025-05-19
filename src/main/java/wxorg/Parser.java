package wxorg;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.*;

public class Parser {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static List<Entry> parseFile(Path path) throws IOException {
        List<Entry> entries = new ArrayList<>();
        List<String> lines = Files.readAllLines(path);

        String entryPattern = "^(" + String.join("|", App.entryTypes) + "):\\s+(.*?)\\s+(\\w+)\\s+(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2})";
        Pattern startPattern = Pattern.compile(entryPattern);

        Entry current = null;
        StringBuilder bodyBuilder = new StringBuilder();

        for (String line : lines) {
            Matcher matcher = startPattern.matcher(line);
            if (matcher.find()) {
                if (current != null) {
                    current.body = bodyBuilder.toString().trim();
                    entries.add(current);
                }

                current = new Entry();
                current.type = matcher.group(1);
                current.header = matcher.group(2);
                current.uid = matcher.group(3);
                current.dateStr = matcher.group(4);
                current.date = LocalDateTime.parse(matcher.group(4), DATE_FMT);
                current.tags = new ArrayList<>();
                current.refs = new ArrayList<>();
                bodyBuilder = new StringBuilder();
                continue;
            }

            if (current == null) continue;

            if (line.startsWith("Tags:")) {
                current.tags = Arrays.asList(line.replace("Tags:", "").trim().split("\\s+"));
            } else if (line.startsWith("Url:")) {
                current.url = line.replace("Url:", "").trim();
            } else if (line.startsWith("Ref:")) {
                current.refs.add(line.replace("Ref:", "").trim());
            } else {
                bodyBuilder.append(line).append("\n");
            }
        }

        if (current != null) {
            current.body = bodyBuilder.toString().trim();
            entries.add(current);
        }

        return entries;
    }
}