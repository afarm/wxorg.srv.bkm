package wxorg;

import java.time.LocalDateTime;
import java.util.*;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entry entry = (Entry) o;
        return Objects.equals(_file, entry._file) && Objects.equals(type, entry.type)
                && Objects.equals(header, entry.header) && Objects.equals(uid, entry.uid)
                && Objects.equals(date, entry.date) && Objects.equals(dateStr, entry.dateStr)
                && Objects.equals(tags, entry.tags) && Objects.equals(url, entry.url) && Objects.equals(refs, entry.refs)
                && Objects.equals(body, entry.body) && Objects.equals(flds, entry.flds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_file, type, header, uid, date, dateStr, tags, url, refs, body, flds);
    }
}