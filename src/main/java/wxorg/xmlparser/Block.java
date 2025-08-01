package wxorg.xmlparser;

import java.util.*;

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


