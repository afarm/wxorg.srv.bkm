package wxorg.xmlparser;

public enum TokenType {
    OPEN_LT,        // <
    GT,             // >  -  begin tag close || end tag close
    SELF_CLOSE_GT,  // />
    CLOSE_LT,       // </
    EQUALS,         // =
    QUOTE,          // "
    STRING,         // имя тега, имя атрибута, значение атрибута
    WHITESPACE,     // пробелы, табы, переводы строк
    COMMENT,        // <!-- комментарий -->
    TEXT            // текст между тегами (не пробелы)

}
