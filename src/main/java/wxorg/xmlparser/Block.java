package wxorg.xmlparser;

import java.util.ArrayList;
import java.util.List;
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

//    public void addAttr(String name, String value) {
//    }

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
    TokenType type;
    String value;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Token{" + "type=" + type + ", value='" + value + '\'' + '}';
    }
}

enum TokenType {
    OPEN_LT,        // <
    OPEN_GT,        // >
    SELF_CLOSE_GT,  // />
    CLOSE_LT,       // </
    EQUALS,         // =
    QUOTE,          // "
    STRING,         // имя тега, имя атрибута, значение атрибута
    WHITESPACE,     // пробелы, табы, переводы строк
    TEXT            // текст между тегами (не пробелы)
}

class Tokenizer {

    private final String input;
    private final int length;
    private int pos = 0;

    public Tokenizer(String input) {
        this.input = input;
        this.length = input.length();
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (pos < length) {
            char ch = input.charAt(pos);

            if (ch == '<') {
                if (peekNext() == '/') {
                    pos += 2;
                    tokens.add(new Token(TokenType.CLOSE_LT, "</"));
                } else {
                    pos++;
                    tokens.add(new Token(TokenType.OPEN_LT, "<"));
                }
            } else if (ch == '/') {
                if (peekNext() == '>') {
                    pos += 2;
                    tokens.add(new Token(TokenType.SELF_CLOSE_GT, "/>"));
                } else {
                    pos++;
                    tokens.add(new Token(TokenType.STRING, "/"));
                }
            } else if (ch == '>') {
                pos++;
                tokens.add(new Token(TokenType.OPEN_GT, ">"));
            } else if (ch == '=') {
                pos++;
                tokens.add(new Token(TokenType.EQUALS, "="));
            } else if (ch == '"' || ch == '\'') {
                // Добавляем отдельный токен для открывающей кавычки
                tokens.add(new Token(TokenType.QUOTE, String.valueOf(ch)));
                pos++;
                int start = pos;
                while (pos < length && input.charAt(pos) != ch) {
                    pos++;
                }
                // Токен для содержимого между кавычками
                String strValue = input.substring(start, pos);
                tokens.add(new Token(TokenType.STRING, strValue));

                // Добавляем отдельный токен для закрывающей кавычки
                if (pos < length) {
                    tokens.add(new Token(TokenType.QUOTE, String.valueOf(ch)));
                    pos++;
                }
            } else if (Character.isWhitespace(ch)) {
                int start = pos;
                while (pos < length && Character.isWhitespace(input.charAt(pos))) {
                    pos++;
                }
                String spaceStr = input.substring(start, pos);
                tokens.add(new Token(TokenType.WHITESPACE, spaceStr));
            } else {
                // Читаем текст или имя (вне тегов)
                int start = pos;
                while (pos < length && !isSpecialChar(input.charAt(pos))) {
                    pos++;
                }
                String str = input.substring(start, pos);
                tokens.add(new Token(TokenType.STRING, str));
            }
        }

        return tokens;
    }

    private char peekNext() {
        if (pos + 1 >= length) return '\0';
        return input.charAt(pos + 1);
    }

    private boolean isSpecialChar(char ch) {
        return ch == '<' || ch == '>' || ch == '/' || ch == '=' || ch == '"' || ch == '\'' || Character.isWhitespace(ch);
    }
}

class Parser {

    private List<Token> tokens;
    private int pos;
    private int length;

    public void parse(Block block) {
        Tokenizer tokenizer = new Tokenizer(block.getSource());
        tokens = tokenizer.tokenize();
        block.setAllTokens(tokens);
        pos = 0;
        length = tokens.size();

        block.setChildren(new ArrayList<>());
        List<Node> allNodes = new ArrayList<>();
        block.setAllNodes(allNodes);

        while (pos < length) {
            Token token = tokens.get(pos);
            if (token.getType() == TokenType.OPEN_LT) {
                Node node = parseNode(block);
                if (node != null) {
                    block.getChildren().add(node);
                    allNodes.add(node);
                }
            } else if (token.getType() == TokenType.WHITESPACE || token.getType() == TokenType.TEXT || token.getType() == TokenType.STRING) {
                // Можно игнорировать или сохранить как текстовый узел
                pos++;
            } else {
                // Просто сдвигаем позицию, чтобы не зациклиться
                pos++;
            }
        }
    }

    private Node parseNode(Block outerBlock) {
        // Начинается с OPEN_LT
        if (pos >= length || tokens.get(pos).getType() != TokenType.OPEN_LT) return null;

        Node node = new Node();
        node.setOuterBlock(outerBlock);
        pos++; // пропускаем '<'

        // Получаем имя тега
        if (pos >= length || tokens.get(pos).getType() != TokenType.STRING) {
            // Ошибка парсинга
            return null;
        }
        Token tagName = tokens.get(pos);
        node.setOpenTagName(tagName);
        pos++;

        // Читаем атрибуты
        List<Attr> attributes = new ArrayList<>();
        Map<String, Attr> attrsMap = new HashMap<>();
        while (pos < length) {
            Token t = tokens.get(pos);

            if (t.getType() == TokenType.WHITESPACE) {
                pos++;
                continue;
            }
            if (t.getType() == TokenType.OPEN_GT) {
                pos++; // '>'
                break;
            }
            if (t.getType() == TokenType.SELF_CLOSE_GT) {
                // Тег самозакрывающийся
                node.setClosed(true);
                pos++;
                break;
            }
            if (t.getType() == TokenType.CLOSE_LT) {
                // Закрывающий тег: </...>
                // не должно быть здесь, ошибка
                return null;
            }
            // Должен быть атрибут: имя '=' '"' значение '"'
            if (t.getType() == TokenType.STRING) {
                Token attrNameToken = t;
                pos++;

                // ожидаем '='
                if (pos >= length || tokens.get(pos).getType() != TokenType.EQUALS) {
                    return null;
                }
                pos++;

                // ожидаем QUOTE
                if (pos >= length || tokens.get(pos).getType() != TokenType.QUOTE) {
                    return null;
                }
                pos++;

                // ожидаем STRING (значение атрибута)
                if (pos >= length || tokens.get(pos).getType() != TokenType.STRING) {
                    return null;
                }
                Token attrValueToken = tokens.get(pos);
                pos++;

                // ожидаем QUOTE закрывающую
                if (pos >= length || tokens.get(pos).getType() != TokenType.QUOTE) {
                    return null;
                }
                pos++;

                Attr attr = new Attr();
                attr.setName(attrNameToken);
                attr.setValue(attrValueToken);
                attributes.add(attr);
                attrsMap.put(attrNameToken.getValue(), attr);

                continue;
            }
            // Если пришли сюда - неожиданное
            pos++;
        }

        node.setAttributes(attributes);
        node.setAttrs(attrsMap);

        // Если тег закрыт самозакрывающийся, не парсим детей
        if (node.isClosed()) {
            return node;
        }

        // Парсим внутреннее содержимое - дети и текст
        List<Node> children = new ArrayList<>();
        node.setChildren(children);

        while (pos < length) {
            Token t = tokens.get(pos);
            if (t.getType() == TokenType.OPEN_LT) {
                // Может быть открывающий тег или закрывающий
                if (pos + 1 < length && tokens.get(pos + 1).getType() == TokenType.CLOSE_LT) {
                    // Это закрывающий тег
                    break;
                }
                // Вложенный узел
                Node child = parseNode(node.getOuterBlock());
                if (child != null) {
                    children.add(child);
                    outerBlock.getAllNodes().add(child);
                }
            } else if (t.getType() == TokenType.CLOSE_LT) {
                // Закрывающий тег
                break;
            } else {
                // Текстовое содержимое между тегами - можно сделать как Node с текст
                // Либо пропускаем
                pos++;
            }
        }

        // Ждем закрывающий тег: </tagName>
        if (pos < length && tokens.get(pos).getType() == TokenType.CLOSE_LT) {
            pos++; // пропускаем '</'

            // Следующее должен быть STRING с именем тега
            if (pos >= length) return node;

            Token closeName = tokens.get(pos);
            pos++;
            node.setCloseTagName(closeName);

            // Пропускаем '>'
            if (pos < length && tokens.get(pos).getType() == TokenType.OPEN_GT) {
                pos++;
            }
        }

        return node;
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

        System.out.println(block.getSource());

//        Tokenizer tokenizer = new Tokenizer(block.getSource());
//        tokenizer.tokenize().forEach(System.out::println);

        // tokens = {"  ", "some text", " \n\n  ", "---", " \n\n", "<", "Book", " ", "id", "=", "\"", "ID123", "\"", " " ...}
        System.out.println(Objects.equals(block.getChildren().get(0).getName(), "Book"));
        System.out.println(Objects.equals(block.getChildren().get(1).getAttr("id").getValue().getValue(), "ID22"));
        System.out.println(Objects.equals(block.getChildren().get(1).getTokens().get(OPEN_LT).getValue(), "<"));
        System.out.println(Objects.equals(block.getAllNodes().get(1).getName(), "Ref"));
        System.out.println(Objects.equals(block.join(), block.getSource()));
        System.out.println(block.join());

    }
}