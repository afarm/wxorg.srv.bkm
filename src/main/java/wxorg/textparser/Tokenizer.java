package wxorg.textparser;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    private final String input;

    private int pos = 0;

    public Tokenizer(String input) {
        this.input = input;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (pos < input.length()) {
            char ch = input.charAt(pos);

            if (ch == '\n') {
                tokens.add(new Token(TokenType.NEWLINE, "\n"));
                pos++;
            } else if (ch == '\r') {
                if (pos + 1 < input.length() && input.charAt(pos + 1) == '\n') {
                    tokens.add(new Token(TokenType.NEWLINE, "\r\n"));
                    pos += 2;
                } else {
                    tokens.add(new Token(TokenType.NEWLINE, "\r"));
                    pos++;
                }
            } else if (ch == ':') {
                tokens.add(new Token(TokenType.COLON, ":"));
                pos++;
            } else if (Character.isWhitespace(ch)) {
                int start = pos;
                while (pos < input.length() && Character.isWhitespace(input.charAt(pos)) && input.charAt(pos) != '\n' && input.charAt(pos) != '\r') {
                    pos++;
                }
                tokens.add(new Token(TokenType.WHITESPACE, input.substring(start, pos)));
            } else {
                int start = pos;
                while (pos < input.length() && !Character.isWhitespace(input.charAt(pos)) && input.charAt(pos) != ':' && input.charAt(pos) != '\n' && input.charAt(pos) != '\r') {
                    pos++;
                }
                tokens.add(new Token(TokenType.STRING, input.substring(start, pos)));
            }
        }

        return tokens;
    }
}
