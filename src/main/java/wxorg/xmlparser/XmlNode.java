package wxorg.xmlparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static wxorg.xmlparser.TokenType.GT;
import static wxorg.xmlparser.TokenType.SELF_CLOSE_GT;

public class XmlNode {

    private final List<Token> tokens;

    private int tokenStartIndex;

    private int tokenEndIndex;

    /**
     * Children nodes.
     */
    List<XmlNode> children;

    /**
     * Node attributes list.
     */
    List<Attr> attrsList = new ArrayList<>();

    /**
     * Node (some) attributes map.
     */
    Map<String, Attr> attrsMap = new HashMap<>();

    /**
     * Link for token of open tag name.
     */
    Token openTagName;

    /**
     * Link for token of close tag name, if exists.
     */
    Token closeTagName;

    Token open_lt;

    /**
     * Tag '>' || '/>' for get begin inner tag space
     */
    Token open_gt;

    /**
     * Tag '<' for get end inner tag space
     */
    Token close_lt;

    Token close_gt;

    /**
     * <CloseNode/>
     */
    boolean isClosed;

    List<String> attrsOrder = List.of(new String[]{"id", "cdate", "<<HERE>>", "url", "tags",});

    public XmlNode(List<Token> tokens) {
        this.tokens = tokens;
    }

    // ?? add "\n" after attr
    // ?? add other attr to first line
    // todo
    public void addAttrByOrder(String name, String value) {

    }

    // todo
    public void attrsReorder() {

    }

    public void addAttr(String name, String value) {
        if (openTagName == null) {
            return;
        }
        if (tokens == null) {
            return;
        }

        int insertIndex = -1;

        // Найдём индекс токена OPEN_GT или SELF_CLOSE_GT для вставки перед ним
        for (int i = tokens.indexOf(openTagName) + 1; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            if (token.getType() == GT || token.getType() == SELF_CLOSE_GT) {
                insertIndex = i;
                break;
            }
        }

        if (insertIndex == -1) {
            return;
        }

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

        if (attrsMap == null) {
            attrsMap = new HashMap<>();
        }
        attrsList.add(attr);
        attrsMap.put(name, attr);
    }

    // Метод удаления токенов существующего атрибута из allTokens
    private void removeAttrTokens(Attr attr) {
        // В зависимости от реализации, надо удалить токены, относящиеся к атрибуту (пробел, имя, =, кавычки, значение)
        // Предполагается, что токены атрибута идут подряд в allTokens
        // Реализовать это можно, например, сохраняя позиции токенов атрибута в Attr
    }


    public void setName(String name) {
        openTagName.setValue(name);
        closeTagName.setValue(name);
    }

    // ---

    public List<Token> getTokens() {
        return tokens;
    }

    public Map<String, Attr> getAttrsMap() {
        return attrsMap;
    }

    public Attr getAttr(String key) {
        return attrsMap.get(key);
    }

    public void setAttrsMap(Map<String, Attr> attrsMap) {
        this.attrsMap = attrsMap;
    }

    public List<XmlNode> getChildren() {
        return children;
    }

    public void setChildren(List<XmlNode> children) {
        this.children = children;
    }

    public List<Attr> getAttrsList() {
        return attrsList;
    }

    public void setAttrsList(List<Attr> attrsList) {
        this.attrsList = attrsList;
    }

    public String getName() {
        return openTagName.getValue();
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

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public Token getOpen_gt() {
        return open_gt;
    }

    public void setOpen_gt(Token open_gt) {
        this.open_gt = open_gt;
    }

    public Token getClose_lt() {
        return close_lt;
    }

    public void setClose_lt(Token close_lt) {
        this.close_lt = close_lt;
    }

    public Token getOpen_lt() {
        return open_lt;
    }

    public void setOpen_lt(Token open_lt) {
        this.open_lt = open_lt;
    }

    public Token getClose_gt() {
        return close_gt;
    }

    public void setClose_gt(Token close_gt) {
        this.close_gt = close_gt;
    }

    public int getTokenStartIndex() {
        return tokenStartIndex;
    }

    public int getTokenEndIndex() {
        return tokenEndIndex;
    }

    public void setTokenStartIndex(int index) {
        this.tokenStartIndex = index;
    }

    public void setTokenEndIndex(int index) {
        this.tokenEndIndex = index;
    }

    @Override
    public String toString() {
        return "Node{" +
                "children=" + children +
                ", attributes=" + attrsList +
                ", attrs=" + attrsMap +
                ", openTagName=" + openTagName +
                ", closeTagName=" + closeTagName +
                ", isClosed=" + isClosed +
                '}';
    }
}
