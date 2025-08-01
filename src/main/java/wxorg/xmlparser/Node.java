package wxorg.xmlparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {

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
    Map<TokenType, Token> tokens = new HashMap<>();

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
        if (outerBlock == null || openTagName == null) return;

        List<Token> tokens = outerBlock.getAllTokens();
        if (tokens == null) return;

        int insertIndex = -1;

        // Найдём индекс токена OPEN_GT или SELF_CLOSE_GT для вставки перед ним
        for (int i = tokens.indexOf(openTagName) + 1; i < tokens.size(); i++) {
            Token t = tokens.get(i);
            if (t.getType() == TokenType.OPEN_GT || t.getType() == TokenType.SELF_CLOSE_GT) {
                insertIndex = i;
                break;
            }
        }

        if (insertIndex == -1) return;

    // Создаём токены атрибута с учетом последнего пробела перед атрибутом
        Token space = new Token(TokenType.WHITESPACE, " ");
        Token nameToken = new Token(TokenType.STRING, name);
        Token equalsToken = new Token(TokenType.EQUALS, "=");
        Token quoteOpen = new Token(TokenType.QUOTE, "\"");
        Token valueToken = new Token(TokenType.STRING, value);
        Token quoteClose = new Token(TokenType.QUOTE, "\"");

    // Вставляем токены в общий список
    List<Token> newTokens = List.of(space, nameToken, equalsToken, quoteOpen, valueToken, quoteClose);

        tokens.addAll(insertIndex, newTokens);

    // Создаём и сохраняем атрибут
        Attr attr = new Attr();
        attr.setName(nameToken);
        attr.setValue(valueToken);

        if (attributes == null) attributes = new ArrayList<>();
        if (attrs == null) attrs = new HashMap<>();

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

    public Map<TokenType, Token> getTokens() {
        return tokens;
    }

    public void setTokens(Map<TokenType, Token> tokens) {
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
