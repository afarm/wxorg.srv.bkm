package wxorg;

import java.time.LocalDateTime;
import java.util.List;

public class Entry {

    public String type;           // Теперь просто строка

    public String header;

    public String uid;

    public LocalDateTime date;

    public String dateStr;

    public List<String> tags;

    public String url;

    public List<String> refs;

    public String body;

    @Override
    public String toString() {
        return String.format("[%s] UID: %s Date: %s\nTags: %s\nUrl: %s\nRefs: %s\nBody:\n%s\n",
                type, uid, date, tags, url, refs, body);
    }
}