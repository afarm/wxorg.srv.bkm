package wxorg;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.*;

public class ParserFile {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private List<String> entryTypes;

    public ParserFile(List<String> entryTypes) {
        this.entryTypes = entryTypes;
    }

    public List<Map<String, String>> parseFile(Path path) throws IOException {
        List<Map<String, String>> entries = new ArrayList<>();
        List<String> lines = Files.readAllLines(path);

        String entryPatternStr = "^(" + String.join("|", entryTypes) + "):\\s+(.*?)\\s+(\\w+)\\s+(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2})";
        Pattern entryPattern = Pattern.compile(entryPatternStr);
        String fldPatternStr = "^(.+?):\\s+(.*?)";
        Pattern fldPattern = Pattern.compile(fldPatternStr);

        Map<String, String> current = null;
        StringBuilder bodyBuilder = new StringBuilder();

        boolean inFlds = false;

        for (String line : lines) {
            Matcher matcher = entryPattern.matcher(line);
            if (matcher.find()) {
                if (current != null) {
                    current.put("body", bodyBuilder.toString().trim());
                    entries.add(current);
                }

                current = new HashMap<>();
                current.put("_file", path.toString());
                current.put("type", matcher.group(1));
                current.put("header", matcher.group(2));
                current.put("id", matcher.group(3));
                current.put("dateStr", matcher.group(4));
                //current.date", LocalDateTime.parse(matcher.group(4), DATE_FMT);
                current.put("tags", "");
                current.put("refs", "new ArrayList<>()");
                bodyBuilder = new StringBuilder();
                inFlds = true;
                continue;
            }

            if (current == null) {
                continue;
            }

            Matcher matcherFlds = fldPattern.matcher(line);

            if (line.matches("^\s*^")) {
                inFlds = false;
            }

            if (inFlds) {
                if (line.startsWith("Tags:")) {
                    //current.tags = Arrays.asList(line.replace("Tags:", "").trim().split("\\s+"));
                } else if (line.startsWith("Url:")) {
                    //current.url = line.replace("Url:", "").trim();
                } else if (line.startsWith("Ref:")) {
                    //current.refs.add(line.replace("Ref:", "").trim());
                } else if (matcherFlds.find()) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put(matcherFlds.group(1), matcherFlds.group(2));
                    //current.flds.add(map);
                } else {
                    inFlds = false;
                }
            } else {
                bodyBuilder.append(line).append("\n");
            }
        }

        if (current != null) {
            //current.body = bodyBuilder.toString().trim();
            entries.add(current);
        } else {
            System.out.println("Skipped = " + path);
        }

        return entries;
    }
}