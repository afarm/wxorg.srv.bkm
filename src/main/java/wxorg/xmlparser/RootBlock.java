package wxorg.xmlparser;

import java.util.*;

/**
 * Результирующая структураю парсинга.
 */
public class RootBlock {
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
    List<XmlNode> allNodes;

    /**
     * Map by id.
     * ??? only listed types - Note, Book, Bookmark, Pass, ...
     */
    Map<String, XmlNode> nodeById = new HashMap<>();

    /**
     * Only children nodes.
     */
    List<XmlNode> children;

    public String join() {
        StringBuilder res = new StringBuilder();
        for (Token token : allTokens) {
            res.append(token.getValue());
        }
        return res.toString();
    }

    public void addNode() { // after/before/first/last

    }

    public void delNode(XmlNode node) {

    }

    // --- getters / setters

    public Map<String, XmlNode> getNodeById() {
        return nodeById;
    }

    public void setNodeById(Map<String, XmlNode> nodeById) {
        this.nodeById = nodeById;
    }

    public List<XmlNode> getChildren() {
        return children;
    }

    public void setChildren(List<XmlNode> children) {
        this.children = children;
    }

    public List<Token> getAllTokens() {
        return allTokens;
    }

    public void setAllTokens(List<Token> allTokens) {
        this.allTokens = allTokens;
    }

    public List<XmlNode> getAllNodes() {
        return allNodes;
    }

    public void setAllNodes(List<XmlNode> allXmlNodes) {
        this.allNodes = allXmlNodes;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

}


