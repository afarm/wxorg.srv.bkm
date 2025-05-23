package wxorg;

import java.io.IOException;
import java.util.*;


public class EntriesService {

    List<Entry> allAllFilesEntries = new ArrayList<>();

    Map<String, Entry> mapAllFilesEntries = new HashMap<>();

    // url => uid-s
    Map<String, List<String>> idxUrls = new HashMap<>();

    Map<String, Entry> idxById = new HashMap<>();

    RecursiveParser recursiveParser;

    public EntriesService(RecursiveParser recursiveParser) throws IOException {
        this.recursiveParser = recursiveParser;
        initAllFilesEntries();
    }

    public List<Entry> initAllFilesEntries() throws IOException {
        allAllFilesEntries = recursiveParser.parse();
        for (Entry entry : allAllFilesEntries) {
            mapAllFilesEntries.put(entry.uid, entry);
            if(idxUrls.get(entry.uid) == null) {
                idxUrls.put(entry.url, new ArrayList<>());
            }
            idxUrls.get(entry.uid).add(entry.uid);
            idxById.put(entry.uid, entry);
        }
        return new ArrayList<>(allAllFilesEntries);
    }

    public Map<String, List<String>> getIdxUrls() {
        return idxUrls;
    }

    public Entry getById(String uid) {
        return idxById.get(uid);
    }
}
