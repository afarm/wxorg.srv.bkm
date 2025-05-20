package wxorg;

import java.io.IOException;
import java.util.*;


public class EntriesService {

    List<Entry> allFilesEntries = new ArrayList<>();

    Set<String> allUrls = new HashSet<>();

    RecursiveParser recursiveParser;

    public EntriesService(RecursiveParser recursiveParser) {
        this.recursiveParser = recursiveParser;
    }

    public List<Entry> allFilesEntries() throws IOException {
        allFilesEntries = recursiveParser.parse();
        for (Entry entry : allFilesEntries) {
            allUrls.add(entry.url);
        }
        return new ArrayList<>(allFilesEntries);
    }

    public Set<String> getAllUrls() {
        return allUrls;
    }
}
