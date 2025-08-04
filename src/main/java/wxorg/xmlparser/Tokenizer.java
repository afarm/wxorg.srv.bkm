package wxorg.xmlparser;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

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
                tokens.add(new Token(TokenType.GT, ">"));
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
