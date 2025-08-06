package wxorg.xmlparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static wxorg.xmlparser.TokenType.*;

/*

2. Поддержка комментариев (<!-- ... -->) ok
Добавить в токенизатор и парсер (можно сохранить как отдельный узел или игнорировать).

3. Реализация addAttrByOrder() и attrsReorder()
С учётом attrsOrder, переставлять токены атрибутов и перестраивать tokens, attrsList, attrsMap.

4. Методы вставки/удаления узлов (addNode, delNode)
Добавлять или удалять узлы из tokens, children, allNodes.

5. Поддержка CDATA
Сейчас <tag><![CDATA[some <xml>]]></tag> не распознаётся — добавить в Tokenizer как отдельный TokenType.CDATA.

6. Валидация (для отладки)
Можно добавить проверки: совпадают ли openTagName и closeTagName, нет ли незакрытых тегов и т.д.

7. Сериализация дерева с форматированием
Сейчас join() просто собирает tokens, но можно реализовать toXmlString() с отступами, переносами, и т.д.

 */
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

        int startPos = pos; // запомни позицию начала узла

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

            if (token.getType() == COMMENT) {
                // Можно пропустить или сохранить как отдельный узел
                pos++;
                continue;
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

        node.setTokenStartIndex(startPos);
        node.setTokenEndIndex(pos);
        return node;
    }

    public void addNode(XmlNode parent, XmlNode newNode, InsertMode mode) {
        if (parent == null || newNode == null || parent.getChildren() == null) {
            return;
        }
        List<XmlNode> siblings = parent.getChildren();
        int insertIndex = switch (mode) {
            case FIRST -> 0;
            case LAST -> siblings.size();
            default -> -1; // для BEFORE/AFTER нужна доработка
        };

        if (insertIndex == -1) return;
        siblings.add(insertIndex, newNode);
        allNodes.add(newNode);

        // Вставим токены в нужное место внутри parent
        Token open_gt = parent.getOpen_gt();
        Token close_lt = parent.getClose_lt();

        if (open_gt == null || close_lt == null) return;

        int start = tokens.indexOf(open_gt) + 1;
        int end = tokens.indexOf(close_lt);

        int tokenInsertIndex = (mode == InsertMode.FIRST)
                ? tokens.indexOf(open_gt) + 1
                : tokens.indexOf(close_lt);

        List<Token> newTokens = new ArrayList<>();
        newTokens.add(new Token(TokenType.WHITESPACE, "\n"));
        newTokens.addAll(tokens.subList(newNode.getTokenStartIndex(), newNode.getTokenEndIndex()));
        newTokens.add(new Token(TokenType.WHITESPACE, "\n"));

        tokens.addAll(tokenInsertIndex, newTokens);
    }

    public void delNode(XmlNode node) {
        if (node == null) return;

        // Удалить из дерева
        for (XmlNode parent : allNodes) {
            if (parent.getChildren() != null && parent.getChildren().remove(node)) {
                break;
            }
        }

        // Удалить из общего списка
        allNodes.remove(node);

        // Удалить токены
        tokens.subList(node.getTokenStartIndex(), node.getTokenEndIndex()).clear();
    }

    public String join() {
        StringBuilder res = new StringBuilder();
        for (Token token : tokens) {
            res.append(token.getValue());
        }
        return res.toString();
    }

    // --- getters / setters

    public Map<String, XmlNode> getNodeById() {
        return nodeById;
    }

    public void setNodeById(Map<String, XmlNode> nodeById) {
        this.nodeById = nodeById;
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

    public List<Token> getTokens() {
        return tokens;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }
}
