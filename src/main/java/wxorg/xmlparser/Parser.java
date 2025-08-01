package wxorg.xmlparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {

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
