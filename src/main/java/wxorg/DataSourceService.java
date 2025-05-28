package wxorg;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class DataSourceService {

    List<Entry> allAllFilesEntries = new ArrayList<>();

    Map<String, Entry> mapAllFilesEntries = new HashMap<>();

    // url => uid-s
    Map<String, List<String>> idxUrls = new HashMap<>();

    Map<String, Entry> idxById = new HashMap<>();

    RecursiveParser recursiveParser;

    public DataSourceService(RecursiveParser recursiveParser) throws IOException {
        this.recursiveParser = recursiveParser;
        initAllFilesEntries();
    }

    public List<Entry> initAllFilesEntries() throws IOException {
        allAllFilesEntries = recursiveParser.parse();
        for (Entry entry : allAllFilesEntries) {
            mapAllFilesEntries.put(entry.uid, entry);
            if(idxUrls.get(entry.url) == null) {
                idxUrls.put(entry.url, new ArrayList<>());
            }
            idxUrls.get(entry.url).add(entry.uid);
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

    public void delete(String uid) {
//        mapAllFilesEntries.remove();
//        allAllFilesEntries.remove();
//        idxUrls;
//        idxById;
//        new File().renameTo(".deleted/");
    }
}
