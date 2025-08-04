package wxorg.xmlparser;

public class Attr {
    Token name;
    Token value;

    public Token getName() {
        return name;
    }

    public Token getValue() {
        return value;
    }

    public void setName(Token name) {
        this.name = name;
    }

    public void setValue(Token value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Attr{" +
                "name=" + name +
                ", value=" + value +
                '}';
    }
}
