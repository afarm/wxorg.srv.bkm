package wxorg.xmlparser;

import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static wxorg.xmlparser.Node.*;

/**
 * Результирующая структураю парсинга.
 */
public class Block {
    /**
     * Source text.
     */
    String source;

    /**
     * List tokens.
     * Shared with all objects.
     */
    List<Token> allTokens;

    /**
     * Result parsed all node, include inner.
     */
    List<Node> allNodes;

    /**
     * Only children nodes.
     */
    List<Node> children;

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public List<Token> getAllTokens() {
        return allTokens;
    }

    public List<Node> getAllNodes() {
        return allNodes;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setAllTokens(List<Token> allTokens) {
        this.allTokens = allTokens;
    }

    public void setAllNodes(List<Node> allNodes) {
        this.allNodes = allNodes;
    }

    public Object join() {
        StringBuilder res = new StringBuilder();
        for (Token token : allTokens) {
            res.append(token.getValue());
        }
        return res.toString();
    }
}

class Node {

    static String OPEN_LT = "OPEN_LT";

    static String OPEN_GT = "OPEN_GT";

    static String CLOSE_LT = "CLOSE_LT";

    static String CLOSE_GT = "CLOSE_GT";

    /**
     * Children nodes.
     */
    List<Node> children;

    /**
     * Node attributes list.
     */
    List<Attr> attributes;

    /**
     * Node (some) attributes map.
     */
    Map<String, Attr> attrs = new HashMap<>();

    /**
     * Link for token of open tag name.
     */
    Token openTagName;

    /**
     * Link for token of close tag name, if exists.
     */
    Token closeTagName;

    /**
     * Next keys (token) links.
     */
    Map<String, Token> tokens = new HashMap<>();

    {
        tokens.put(OPEN_LT, null);
        tokens.put(OPEN_GT, null);
        tokens.put(CLOSE_LT, null);
        tokens.put(CLOSE_GT, null);
    }

    /**
     * <CloseNode/>
     */
    boolean isClosed;

    /**
     * <Node>BLOCK<Node/>
     */
    Block innerBlock;

    /**
     * Outer block of node
     */
    Block outerBlock;

    public void addAttr(String name, String value) {
        if (attributes == null) {
            attributes = new ArrayList<>();
        }
        if (attrs == null) {
            attrs = new HashMap<>();
        }
        if (outerBlock == null || outerBlock.getAllTokens() == null) {
            throw new IllegalStateException("Node must have a reference to outer block with allTokens initialized.");
        }

        // Проверяем есть ли атрибут с таким именем
        Attr existingAttr = attrs.get(name);
        if (existingAttr != null) {
            // Удаляем существующие токены атрибута из allTokens
            removeAttrTokens(existingAttr);
            attributes.remove(existingAttr);
            attrs.remove(name);
        }

        Token openTagEnd = tokens.get(OPEN_GT);
        if (openTagEnd == null) {
            throw new IllegalStateException("Tag end token not found (\"/>\" or \">\")");
        }
        int insertIndex = outerBlock.getAllTokens().indexOf(openTagEnd);

        // Создаем токены нового атрибута
        Token spaceToken = new Token();
        spaceToken.setValue(" ");

        Token nameToken = new Token();
        nameToken.setValue(name);

        Token equalToken = new Token();
        equalToken.setValue("=");

        Token quoteOpen = new Token();
        quoteOpen.setValue("\"");

        Token valueToken = new Token();
        valueToken.setValue(value);

        Token quoteClose = new Token();
        quoteClose.setValue("\"");

        List<Token> tokensToAdd = List.of(spaceToken, nameToken, equalToken, quoteOpen, valueToken, quoteClose);
        //outerBlock.getAllTokens().addAll(insertIndex, tokensToAdd);

        Attr attr = new Attr();
        attr.setName(nameToken);
        attr.setValue(valueToken);
        attributes.add(attr);
        attrs.put(name, attr);
    }

    // Метод удаления токенов существующего атрибута из allTokens
    private void removeAttrTokens(Attr attr) {
        // В зависимости от реализации, надо удалить токены, относящиеся к атрибуту (пробел, имя, =, кавычки, значение)
        // Предполагается, что токены атрибута идут подряд в allTokens
        // Реализовать это можно, например, сохраняя позиции токенов атрибута в Attr
    }

    public Map<String, Attr> getAttrs() {
        return attrs;
    }

    public Attr getAttr(String key) {
        return attrs.get(key);
    }

    public void setAttrs(Map<String, Attr> attrs) {
        this.attrs = attrs;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public List<Attr> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attr> attributes) {
        this.attributes = attributes;
    }

    public String getName() {
        return openTagName.getValue();
    }

    public void setName(String name) {
        openTagName.setValue(name);
        closeTagName.setValue(name);
    }

    public Token getOpenTagName() {
        return openTagName;
    }

    public void setOpenTagName(Token openTagName) {
        this.openTagName = openTagName;
    }

    public Token getCloseTagName() {
        return closeTagName;
    }

    public void setCloseTagName(Token closeTagName) {
        this.closeTagName = closeTagName;
    }

    public Map<String, Token> getTokens() {
        return tokens;
    }

    public void setTokens(Map<String, Token> tokens) {
        this.tokens = tokens;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public Block getInnerBlock() {
        return innerBlock;
    }

    public void setInnerBlock(Block innerBlock) {
        this.innerBlock = innerBlock;
    }

    public Block getOuterBlock() {
        return outerBlock;
    }

    public void setOuterBlock(Block outerBlock) {
        this.outerBlock = outerBlock;
    }
}

class Attr {
    Token name;
    Token value;

    public Token getName() {
        return name;
    }

    public Token getValue() {
        return value;
    }

    public void setName(Token name) {
        this.name = name;
    }

    public void setValue(Token value) {
        this.value = value;
    }
}

class Token {
    String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Token{" + value + '}';
    }
}

class Parser {
    public void parse(Block block) {
        String src = block.getSource();
        List<Token> allTokens = new ArrayList<>();
        block.setAllTokens(allTokens);

        List<Node> allNodes = new ArrayList<>();
        List<Node> children = new ArrayList<>();
        block.setAllNodes(allNodes);
        block.setChildren(children);

        Deque<Node> stack = new ArrayDeque<>();

        int i = 0;
        while (i < src.length()) {
            char ch = src.charAt(i);

            if (ch == '<') {
                int nextClose = src.indexOf('>', i);
                if (nextClose == -1) break; // malformed

                String tagContent = src.substring(i + 1, nextClose).trim();
                boolean selfClosed = tagContent.endsWith("/");

                if (tagContent.startsWith("/")) {
                    // Закрывающий тег
                    Node current = stack.pop();

                    current.getTokens().put(CLOSE_LT, makeToken("<", allTokens)); // <
                    makeToken("/", allTokens);                                    // /
                    Token tagNameToken = makeToken(current.getName(), allTokens); // name
                    current.setCloseTagName(tagNameToken);
                    current.getTokens().put(CLOSE_GT, makeToken(">", allTokens)); // >

                    current.setClosed(true);
                    i = nextClose + 1;
                    continue;
                }

                String tagContentRaw = tagContent.replaceFirst("/$", ""); // удаляем / в конце
                int tagNameEnd = findTagNameEnd(tagContentRaw);
                String tagName = tagContentRaw.substring(0, tagNameEnd).trim();
                String attrSection = tagContentRaw.substring(tagNameEnd).trim();

                Node node = new Node();
                node.setOuterBlock(block);
                node.setTokens(new HashMap<>());
                node.getTokens().put(OPEN_LT, makeToken("<", allTokens));
                node.setOpenTagName(makeToken(tagName, allTokens));

                // Устанавливаем OPEN_GT сразу, до парсинга атрибутов
                node.getTokens().put(OPEN_GT, makeToken(selfClosed ? "/>" : ">", allTokens));

                node.setAttributes(new ArrayList<>());
                node.setAttrs(new HashMap<>());

                parseAttrs(attrSection, node, allTokens);

                // Закрытие тега (повторно не нужно, OPEN_GT уже установлен выше)
                node.setClosed(selfClosed);

                // Добавляем узел в дерево и список узлов
                if (!stack.isEmpty()) {
                    Node parent = stack.peek();
                    if (parent.getChildren() == null) parent.setChildren(new ArrayList<>());
                    parent.getChildren().add(node);
                } else {
                    children.add(node);
                }
                allNodes.add(node);

                if (!selfClosed) {
                    stack.push(node);
                }

                i = nextClose + 1;
            } else {
                // Простой текст
                int nextTag = src.indexOf('<', i);
                if (nextTag == -1) {
                    nextTag = src.length();
                }

                String text = src.substring(i, nextTag);
                List<Token> textTokens = splitTextIntoTokens(text);
                allTokens.addAll(textTokens);
                i = nextTag;
            }
        }
    }

    private int findTagNameEnd(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (Character.isWhitespace(s.charAt(i))) {
                return i;
            }
        }
        return s.length();
    }

    private void parseAttrs(String input, Node node, List<Token> allTokens) {
        int i = 0;
        int len = input.length();
        while (i < len) {
            // Пропускаем пробелы
            while (i < len && Character.isWhitespace(input.charAt(i))) {
                allTokens.add(makeToken(String.valueOf(input.charAt(i)), allTokens));
                i++;
            }

            // Читаем имя
            int start = i;
            while (i < len && !Character.isWhitespace(input.charAt(i)) && input.charAt(i) != '=') {
                i++;
            }
            if (i == start) break; // ничего не нашли
            String name = input.substring(start, i);
            Token nameToken = makeToken(name, allTokens);

            // Пропускаем пробелы
            while (i < len && Character.isWhitespace(input.charAt(i))) {
                allTokens.add(makeToken(String.valueOf(input.charAt(i)), allTokens));
                i++;
            }

            // Ожидаем '='
            if (i >= len || input.charAt(i) != '=') break;
            allTokens.add(makeToken("=", allTokens));
            i++;

            // Пропускаем пробелы
            while (i < len && Character.isWhitespace(input.charAt(i))) {
                allTokens.add(makeToken(String.valueOf(input.charAt(i)), allTokens));
                i++;
            }

            // Ожидаем открывающую кавычку
            if (i >= len || input.charAt(i) != '"') break;
            allTokens.add(makeToken("\"", allTokens));
            i++;

            // Читаем значение до закрывающей кавычки
            start = i;
            while (i < len && input.charAt(i) != '"') {
                i++;
            }
            String value = input.substring(start, i);
            Token valueToken = makeToken(value, allTokens);

            // Закрывающая кавычка
            if (i < len && input.charAt(i) == '"') {
                allTokens.add(makeToken("\"", allTokens));
                i++;
            }

            // Сохраняем атрибут
            // Проверяем, есть ли уже атрибут с таким именем
            if (node.getAttr(name) != null) {
                // обновляем значение существующего атрибута (заменяем value Token)
                Attr attr = node.getAttr(name);
                attr.setName(nameToken);
                attr.setValue(valueToken);
                attr.getValue().setValue(value);
                node.getAttributes().add(attr);
                node.getAttrs().put(name, attr);
            } else {
                // вставляем новый атрибут как у тебя сейчас — пробел, имя, =, "значение", "
                node.addAttr(name, value);
            }
        }
    }

    private List<Token> splitTextIntoTokens(String text) {
        List<Token> tokens = new ArrayList<>();

        int start = 0;
        while (start < text.length()) {
            char ch = text.charAt(start);
            int end = start;

            boolean isWhitespace = Character.isWhitespace(ch);
            while (end < text.length() && Character.isWhitespace(text.charAt(end)) == isWhitespace) {
                end++;
            }

            String part = text.substring(start, end);
            Token token = new Token();
            token.setValue(part);
            tokens.add(token);

            start = end;
        }

        return tokens;
    }

    private Token makeToken(String value, List<Token> allTokens) {
        Token token = new Token();
        token.setValue(value);
        allTokens.add(token);
        return token;
    }
}

class Test {
    public static void main(String[] args) {
        Block block = new Block();
        block.setSource("" +
                "  some text \n\n" +
                "  --- \n\n" +
                "<Book id=\"ID123\" cdate=\"2025-01-01\" \n" +
                "    url=\"http://www.com\"> \n" +
                "    text \n" +
                "    <Ref id=\"ID123123\" /> \n" +
                "    <Ref id=\"ID123dddd\" /> \n" +
                "</Book>\n" +
                "  \n" +
                "<Node id=\"ID22\" cdate=\"2025-02-01\" \n" +
                "    url=\"http://www.qqq\"> \n" +
                "    text \n" +
                "    <Ref id=\"ID125551\" /> \n" +
                "    <Ref id=\"ID123555\" /> \n" +
                "</Note>\n" +
                "text \n");

        Parser parser = new Parser();
        parser.parse(block);
        // tokens = {"  ", "some text", " \n\n  ", "---", " \n\n", "<", "Book", " ", "id", "=", "\"", "ID123", "\"", " " ...}
        System.out.println(Objects.equals(block.getChildren().get(0).getName(), "Book"));
        System.out.println(Objects.equals(block.getChildren().get(1).getAttr("id").getValue().getValue(), "ID22"));
        System.out.println(Objects.equals(block.getChildren().get(1).getTokens().get(OPEN_LT).getValue(), "<"));
        System.out.println(Objects.equals(block.getAllNodes().get(1).getName(), "Ref"));
        System.out.println(Objects.equals(block.join(), block.getSource()));
        System.out.println(block.getSource());
        System.out.println(block.join());

    }
}