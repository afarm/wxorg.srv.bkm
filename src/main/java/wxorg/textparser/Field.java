package wxorg.textparser;

import java.util.ArrayList;
import java.util.List;

public class Field {

    Token key;

    List<Token> value = new ArrayList<>();

    public Field(Token token) {
        key = token;
    }

    public Token getKey() {
        return key;
    }

    public void setKey(Token key) {
        this.key = key;
    }

    public List<Token> getValue() {
        return value;
    }

    public void setValue(List<Token> value) {
        this.value = value;
    }

    String getStrKey() {
        return key.getValue();
    }
}
