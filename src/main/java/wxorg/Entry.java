package wxorg;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entry {

    public String _file;

    public String type;

    public String header;

    public String uid;

    public LocalDateTime date;

    public String dateStr;

    public List<String> tags;

    public String url;

    public List<String> refs;

    public String body;

    public List<Map<String, String>> flds = new ArrayList<>();

    @Override
    public String toString() {
        return String.format("[%s] UID: %s Date: %s\nTags: %s\nUrl: %s\nRefs: %s\nBody:\n%s\n",
                type, uid, date, tags, url, refs, body);
    }
}