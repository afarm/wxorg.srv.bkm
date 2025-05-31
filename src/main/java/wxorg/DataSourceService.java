package wxorg;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;


public class DataSourceService {

    List<Entry> allAllFilesEntries = new ArrayList<>();

    List<Entry> deletedEntries = new ArrayList<>();

    Map<String, Entry> mapAllFilesEntries = new HashMap<>();

    // url => uid-s
    Map<String, List<Entry>> idxUrls = new HashMap<>();

    Map<String, List<Entry>> idxFiles = new HashMap<>();

    Map<String, Entry> idxById = new HashMap<>();

    RecursiveParser recursiveParser;
    private final String dir;

    public DataSourceService(RecursiveParser recursiveParser, String dir) throws IOException {
        this.recursiveParser = recursiveParser;
        this.dir = dir;
        buildAllEntries();
    }

    public List<Entry> buildAllEntries() throws IOException {
        allAllFilesEntries = recursiveParser.parse();
        for (Entry entry : allAllFilesEntries) {
            String file = entry._file;
            Path path = Path.of(file);
            if (Objects.equals(path.getParent().getFileName().toString(), ".deleted")) {
                deletedEntries.add(entry);
                continue;
            }
            mapAllFilesEntries.put(entry.uid, entry);
            String url = entry.url;
            idxUrls.computeIfAbsent(url, k -> new ArrayList<>());
            idxUrls.get(url).add(entry);
            idxFiles.computeIfAbsent(file, k -> new ArrayList<>());
            idxFiles.get(file).add(entry);
            idxById.put(entry.uid, entry);
        }
        for (Entry deletedEntry : deletedEntries) {
            System.out.printf(".deleted " + deletedEntry._file);
            allAllFilesEntries.remove(deletedEntry);
        }
        return new ArrayList<>(allAllFilesEntries);
    }

    public Map<String, List<Entry>> getIdxUrls() {
        return idxUrls;
    }

    public Entry getById(String uid) {
        return idxById.get(uid);
    }

    public void delete(String uid) {
        Entry entry = mapAllFilesEntries.get(uid);
        mapAllFilesEntries.remove(uid);
        allAllFilesEntries.remove(entry);
        // idxFiles.get(entry)
        // idxUrls;
        idxById.remove(uid);
        try {
            if(entry != null) {
                File file = new File(entry._file);
                FileUtils.copyFileToDirectory(file, new File(dir + "/.deleted/"));
                FileUtils.forceDelete(file);
            } else {
                System.out.println("uid entry is null = " + uid);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
