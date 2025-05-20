package wxorg;

import java.io.IOException;
import java.util.*;


public class EntriesService {

    List<Entry> allFilesEntries = new ArrayList<>();

    Set<String> allUrls = new HashSet<>();

    Map<String, Entry> idxById = new HashMap<>();

    RecursiveParser recursiveParser;

    public EntriesService(RecursiveParser recursiveParser) throws IOException {
        this.recursiveParser = recursiveParser;
        initAllFilesEntries();
    }

    public List<Entry> initAllFilesEntries() throws IOException {
        allFilesEntries = recursiveParser.parse();
        for (Entry entry : allFilesEntries) {
            allUrls.add(entry.url);
            idxById.put(entry.uid, entry);
        }
        return new ArrayList<>(allFilesEntries);
    }

    public Set<String> getAllUrls() {
        return allUrls;
    }

    public Entry getById(String uid) {
        return idxById.get(uid);
    }
}
