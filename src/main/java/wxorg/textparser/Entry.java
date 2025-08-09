package wxorg.textparser;

import java.util.*;

public class Entry {

    /**
     * All tokens of file.
     */
    private final List<Token> fileTokens;

    /**
     * Порядок полей в entry.
     */
    private List<String> fieldOrder = List.of(new String[]{"Type", "Name", "Id", "Cdate", "Url", "Tags", "File", "Text"});

    /**
     * Result of parsing,
     * Token - token for fieldName
     * List<Token> - token field value
     */
    private Map<Token, List<Token>> fieldMap = new LinkedHashMap<>();

    private List<Field> fields = new ArrayList<>();

    String filePath;

    public Entry(List<Token> fileTokens) {
        this.fileTokens = fileTokens;
    }

    public String getValue(String fieldName) {
        List<Token> val = getFieldTokens(fieldName);
        if (val == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Token t : val) {
            sb.append(t.getValue());
        }
        return sb.toString().trim();
    }

    /**
     * @return All tokens list from first space after ":" to next field
     */
    public List<Token> getFieldTokens(String fieldName) {
        for (Field field : fields) {
            if (field.getKey().getValue().equals(fieldName)) {
                return field.getValue();
            }
        }
        return new ArrayList<>();
    }

    /**
     * @return get list words from field value
     */
    public List<String> getList(String fieldName) {
        List<String> list = new ArrayList<>();
        List<Token> tokens = getFieldTokens(fieldName);
        if (tokens != null) {
            for (Token t : tokens) {
                if (t.getType() == TokenType.STRING) {
                    list.add(t.getValue());
                }
            }
        }
        return list;
    }

    /**
     * For example field "Refs"
     * Refs:
     * 1JPCCG1C Программирование на языке Lua 2
     * BJPCCGPC Программирование на языке Lua 3
     * return sub map with
     * key token 1JPCCG1C => value token Программирование на языке Lua 2
     * key token BJPCCGPC => value token Программирование на языке Lua 3
     */
    public Map<Token, Token> subMap(String fieldName) {
        List<Token> tokens = getFieldTokens(fieldName);
        if (tokens == null) return null;
        Map<Token, Token> result = new LinkedHashMap<>();

        for (int i = 0; i + 1 < tokens.size(); ) {
            Token t1 = tokens.get(i);
            Token t2 = tokens.get(i + 1);
            result.put(t1, t2);
            i += 2;
            // skip optional whitespace/newline
            while (i < tokens.size() &&
                    (tokens.get(i).getType() == TokenType.NEWLINE ||
                            tokens.get(i).getType() == TokenType.WHITESPACE)) {
                i++;
            }
        }
        return result;
    }

    /**
     * @param key new key
     * @param val new value
     * @param pos FIRST, LAST or KEY (previous)
     */
    public void subMapAdd(String key, String val, String pos) {
    }

    public Map<Token, Token> subMapDel(String key) {
        return null;
    }

    /**
     * Добавить токены в fileTokens с fieldName: fieldValue
     * в порядке fieldOrder или в конец
     */
    public void createField(String fieldName, String fieldValue) {
        // Удалить старое поле из fileTokens и fields
        Token existingKey = null;
        for (Token t : fieldMap.keySet()) {
            if (t.getValue().equals(fieldName)) {
                existingKey = t;
                break;
            }
        }
        if (existingKey != null) {
            fieldMap.remove(existingKey);

            int start = fileTokens.indexOf(existingKey);
            int end = start + 1;
            while (end < fileTokens.size()) {
                Token t = fileTokens.get(end);
                if (t.getType() == TokenType.STRING &&
                        fieldOrder.contains(t.getValue())) {
                    break;
                }
                end++;
            }
            fileTokens.subList(start, end).clear();
        }

        // Создаём токены
        Token keyToken = new Token(TokenType.STRING, fieldName);
        List<Token> valueTokens = new ArrayList<>();
        List<Token> fullTokens = new ArrayList<>();

        fullTokens.add(keyToken);
        fullTokens.add(new Token(TokenType.COLON, ":"));
        fullTokens.add(new Token(TokenType.WHITESPACE, "  "));
        Token valToken = new Token(TokenType.STRING, fieldValue);
        valueTokens.add(valToken);
        fullTokens.add(valToken);
        fullTokens.add(new Token(TokenType.NEWLINE, "\n"));

        // Добавляем в fields
        fieldMap.put(keyToken, valueTokens);

        // Найдём позицию для вставки в fileTokens с учётом fieldOrder
        int insertIndex = fileTokens.size();
        for (int i = 0; i < fieldOrder.size(); i++) {
            if (fieldOrder.get(i).equals(fieldName)) {
                for (int j = i - 1; j >= 0; j--) {
                    String prevField = fieldOrder.get(j);
                    for (int k = 0; k < fileTokens.size(); k++) {
                        if (fileTokens.get(k).getType() == TokenType.STRING &&
                                fileTokens.get(k).getValue().equals(prevField)) {
                            insertIndex = k;
                            // идём до конца предыдущего блока
                            while (insertIndex < fileTokens.size()) {
                                Token t = fileTokens.get(insertIndex);
                                if (t.getType() == TokenType.STRING &&
                                        fieldOrder.contains(t.getValue()) &&
                                        !t.getValue().equals(prevField)) {
                                    break;
                                }
                                insertIndex++;
                            }
                            break;
                        }
                    }
                    if (insertIndex != fileTokens.size()) break;
                }
                break;
            }
        }

        // Вставляем токены
        fileTokens.addAll(insertIndex, fullTokens);
    }

    public void createField(String fieldName, Map<String, String> subMap) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : subMap.entrySet()) {
            sb.append("       ") // отступ
                    .append(entry.getKey())
                    .append(" ")
                    .append(entry.getValue())
                    .append("\\n"); // сохраняем перевод строки как текст
        }
        //addField(fieldName, sb.toString());
    }

    public void createField(String fieldName, List<String> subList) {
    }

    public void addField(Field field) {
        fields.add(field);
    }

    // --- getter / setter

    public List<Token> getFileTokens() {
        return fileTokens;
    }

    public List<String> getFieldOrder() {
        return fieldOrder;
    }

    public void setFieldOrder(List<String> fieldOrder) {
        this.fieldOrder = fieldOrder;
    }

    public Map<Token, List<Token>> getFieldMap() {
        return fieldMap;
    }

    public void setFieldMap(Map<Token, List<Token>> fieldMap) {
        this.fieldMap = fieldMap;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

}
