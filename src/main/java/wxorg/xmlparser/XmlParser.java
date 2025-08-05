package wxorg.xmlparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static wxorg.xmlparser.TokenType.*;

public class XmlParser {

    /**
     * Source text.
     */
    String source;

    /**
     * Result parsed all node, include inner.
     */
    List<XmlNode> allNodes = new ArrayList<>();

    /**
     * Map by id.
     * ??? only listed types - Note, Book, Bookmark, Pass, ...
     */
    Map<String, XmlNode> nodeById = new HashMap<>();

    /**
     * Only children nodes.
     */
    List<XmlNode> children = new ArrayList<>();

    private List<Token> tokens;

    private int pos;

    private int length;

    public void parse() {
        Tokenizer tokenizer = new Tokenizer(source);
        tokens = tokenizer.tokenize();
        pos = 0;
        length = tokens.size();

        while (pos < length) {
            Token token = tokens.get(pos);
            if (token.getType() == OPEN_LT) {
                XmlNode node = parseNode();
                if (node != null) {
                    children.add(node);
                    allNodes.add(node);
                }
            } else if (token.getType() == WHITESPACE || token.getType() == TEXT || token.getType() == STRING) {
                // Можно игнорировать или сохранить как текстовый узел
                pos++;
            } else {
                // Просто сдвигаем позицию, чтобы не зациклиться
                pos++;
            }
        }
    }

    private XmlNode parseNode() {
        // Начинается с OPEN_LT
        if (pos >= length || tokens.get(pos).getType() != OPEN_LT) {
            return null;
        }

        XmlNode node = new XmlNode(tokens);
        node.setOpen_lt(tokens.get(pos));
        pos++; // пропускаем '<'

        // Получаем имя тега
        if (pos >= length || tokens.get(pos).getType() != STRING) {
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
            Token token = tokens.get(pos);

            if (token.getType() == WHITESPACE) {
                pos++;
                continue;
            }
            if (token.getType() == GT) {
                node.setOpen_gt(token);
                pos++; // '>'
                break;
            }
            if (token.getType() == SELF_CLOSE_GT) {
                // Тег самозакрывающийся
                node.setClosed(true);
                node.setOpen_gt(token);
                pos++;
                break;
            }
            if (token.getType() == CLOSE_LT) {
                // Закрывающий тег: </...>
                // не должно быть здесь, ошибка
                return null;
            }
            // Должен быть атрибут: имя '=' '"' значение '"'
            if (token.getType() == STRING) {
                Token attrNameToken = token;
                pos++;

                // ожидаем '='
                if (pos >= length || tokens.get(pos).getType() != EQUALS) {
                    return null;
                }
                pos++;

                // ожидаем QUOTE
                if (pos >= length || tokens.get(pos).getType() != QUOTE) {
                    return null;
                }
                pos++;

                // ожидаем STRING (значение атрибута)
                if (pos >= length || tokens.get(pos).getType() != STRING) {
                    return null;
                }
                Token attrValueToken = tokens.get(pos);
                pos++;

                // ожидаем QUOTE закрывающую
                if (pos >= length || tokens.get(pos).getType() != QUOTE) {
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

        node.setAttrsList(attributes);
        node.setAttrsMap(attrsMap);

        // Если тег закрыт самозакрывающийся, не парсим детей
        if (node.isClosed()) {
            return node;
        }

        // Парсим внутреннее содержимое - дети и текст
        List<XmlNode> children = new ArrayList<>();
        node.setChildren(children);

        while (pos < length) {
            Token token = tokens.get(pos);
            if (token.getType() == OPEN_LT) {
                // Может быть открывающий тег или закрывающий
                if (pos + 1 < length && tokens.get(pos + 1).getType() == CLOSE_LT) {
                    node.setClose_lt(tokens.get(pos + 1));
                    // Это закрывающий тег
                    break;
                }
                // Вложенный узел
                XmlNode child = parseNode();
                if (child != null) {
                    children.add(child);
                    allNodes.add(child);
                }
            } else if (token.getType() == CLOSE_LT) {
                // Закрывающий тег
                break;
            } else {
                // Текстовое содержимое между тегами - можно сделать как Node с текст
                // Либо пропускаем
                pos++;
            }
        }

        // Ждем закрывающий тег: </tagName>
        if (pos < length && tokens.get(pos).getType() == CLOSE_LT) {
            node.setClose_lt(tokens.get(pos));
            pos++; // пропускаем '</'

            // Следующее должен быть STRING с именем тега
            if (pos >= length) {
                return node;
            }

            Token closeName = tokens.get(pos);
            pos++;
            node.setCloseTagName(closeName);

            // Пропускаем '>'
            if (pos < length && tokens.get(pos).getType() == GT) {
                node.setClose_gt(tokens.get(pos));
                pos++;
            }
        }

        return node;
    }

    public void addNode(String name) {

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

    public List<XmlNode> getChildren() {
        return children;
    }

    public void setChildren(List<XmlNode> children) {
        this.children = children;
    }

    public String join() {
        StringBuilder res = new StringBuilder();
        for (Token token : tokens) {
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


}
