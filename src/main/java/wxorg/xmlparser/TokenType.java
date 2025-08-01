package wxorg.xmlparser;

public enum TokenType {
    OPEN_LT,        // <
    OPEN_GT,        // >
    SELF_CLOSE_GT,  // />
    CLOSE_LT,       // </
    EQUALS,         // =
    QUOTE,          // "
    STRING,         // имя тега, имя атрибута, значение атрибута
    WHITESPACE,     // пробелы, табы, переводы строк
    TEXT            // текст между тегами (не пробелы)
}
