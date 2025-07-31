package wxorg;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;


public class DataSourceService {

    List<Map<String, String>> allFilesEntries = new ArrayList<>();

    List<Map<String, String>> deletedEntries = new ArrayList<>();

    Map<String, Map<String, String>> mapAllFilesEntries = new HashMap<>();

    // url => id-s
    Map<String, List<Map<String, String>>> idxUrls = new HashMap<>();

    Map<String, List<Map<String, String>>> idxTags = new HashMap<>();

    Map<String, List<Map<String, String>>> idxFiles = new HashMap<>();

    Map<String, Map<String, String>> idxById = new HashMap<>();

    RecursiveParser recursiveParser;

    String dir;

    public DataSourceService(RecursiveParser recursiveParser, String dir) throws IOException {
        this.recursiveParser = recursiveParser;
        this.dir = dir;
        buildAllEntries();
    }

    public List<Map<String, String>> buildAllEntries() throws IOException {
        allFilesEntries = recursiveParser.parse();
        for (Map<String, String> entry : allFilesEntries) {
            String file = entry.get("_file");
            Path path = Path.of(file);
            if (Objects.equals(path.getParent().getFileName().toString(), ".deleted")) {
                deletedEntries.add(entry);
                continue;
            }
            mapAllFilesEntries.put(entry.get("id"), entry);
            String url = entry.get("url");
            idxUrls.computeIfAbsent(url, k -> new ArrayList<>());
            idxUrls.get(url).add(entry);
            idxFiles.computeIfAbsent(file, k -> new ArrayList<>());
            idxFiles.get(file).add(entry);
            idxById.put(entry.get("id"), entry);

            if (entry.get("tags") != null) {
                for (String tag : entry.get("tags").split(" ")) {
                    idxTags.putIfAbsent(tag, new ArrayList<>());
                    idxTags.get(tag).add(entry);
                }
            }
        }
        for (Map<String, String> deletedEntry : deletedEntries) {
            System.out.printf(".deleted " + deletedEntry.get("_file"));
            allFilesEntries.remove(deletedEntry);
        }
        return new ArrayList<>(allFilesEntries);
    }

    public Map<String, List<Map<String, String>>> getIdxUrls() {
        return idxUrls;
    }

    public Map<String, List<Map<String, String>>> idxTags() {
        return idxTags;
    }

    public Map<String, String> getById(String id) {
        return idxById.get(id);
    }

    public void delete(String id) {
        Map<String, String> entry = mapAllFilesEntries.get(id);
        mapAllFilesEntries.remove(id);
        allFilesEntries.remove(entry);
        // idxFiles.get(entry)
        // idxUrls;
        idxById.remove(id);
        try {
            if (entry != null) {
                File file = new File(entry.get("_file"));
                FileUtils.copyFileToDirectory(file, new File(dir + "/.deleted/"));
                FileUtils.forceDelete(file);
            } else {
                System.out.println("id entry is null = " + id);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
