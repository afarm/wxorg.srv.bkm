package wxorg.textparser;

import java.util.*;

import static wxorg.textparser.TokenType.NEWLINE;
import static wxorg.textparser.TokenType.STRING;

public class TextFileParser {

    private List<Token> fileTokens = new ArrayList<>();

    private int pos = 0;

    /**
     * Parsing result.
     * All entries.
     */
    private List<Entry> entries = new ArrayList<>();

    /**
     * Index by field id;
     * key - Id, value Entry
     */
    private Map<String, Entry> entriesById = new HashMap<>();

    /**
     * Index by field Tags;
     * key - tag, value List<Entry>
     */
    private Map<String, List<Entry>> entriesByTag = new HashMap<>();

    public TextFileParser(List<Token> fileTokens) {
        this.fileTokens = fileTokens;
    }

    public void parse() {
        Entry entry = null;
        Field field = null;
        while (pos < fileTokens.size()) {
            Token token = fileTokens.get(pos);
            if (isFieldHeader()) {
                if (entry == null) {
                    entry = new Entry(fileTokens);
                    entries.add(entry);
                }
                field = new Field(token);
                entry.addField(field);
                pos++;
            } else if (entry != null && token.getType() == STRING && token.getValue().startsWith("---")
                    || pos == (fileTokens.size() - 1)) {
                // Индексы
                String id = entry.getValue("Id");
                if (id != null && !id.isEmpty()) {
                    entriesById.put(id, entry);
                }
                for (String tag : entry.getList("Tags")) {
                    entriesByTag.computeIfAbsent(tag, k -> new ArrayList<>()).add(entry);
                }
                entry = null;
            } else if (entry != null && field != null) {
                field.getValue().add(token);
            }
            // NEWLINE ignored
            pos++;
        }
    }

    private boolean isFieldHeader() {
        if (pos >= fileTokens.size()) {
            return false;
        }
        Token token = fileTokens.get(pos);
        if (token.getType() != STRING) {
            return false;
        }
        //if (!fieldOrder.contains(token.getValue())) return false;
        return pos + 1 < fileTokens.size()
                && fileTokens.get(pos + 1).getType() == TokenType.COLON
                && (pos == 0 || fileTokens.get(pos - 1).getType() == NEWLINE);
    }

    private String tokensToString(List<Token> tokens) {
        StringBuilder sb = new StringBuilder();
        for (Token t : tokens) {
            sb.append(t.getValue());
        }
        return sb.toString();
    }

    /**
     * Generate Id, Cdate,
     * Add tokens to fileTokens
     */
    public Entry addEntry(String type, String name) {
        return null;
    }

    /**
     * Join all fileTokens
     */
    public String join() {
        StringBuilder sb = new StringBuilder();
        for (Token token : fileTokens) {
            sb.append(token.getValue());
        }
        return sb.toString();
    }

    public List<Token> getFileTokens() {
        return fileTokens;
    }

    public void setFileTokens(List<Token> fileTokens) {
        this.fileTokens = fileTokens;
    }

    public Map<String, Entry> getEntriesById() {
        return entriesById;
    }

    public void setEntriesById(Map<String, Entry> entriesById) {
        this.entriesById = entriesById;
    }

    public Map<String, List<Entry>> getEntriesByTag() {
        return entriesByTag;
    }

    public void setEntriesByTag(Map<String, List<Entry>> entriesByTag) {
        this.entriesByTag = entriesByTag;
    }
}
