есть файл RJPCCGPC.txt
для этого файла допиши токенайзер и парсер

для записей типа Bookmark: Программирование на языке Lua 2 1JPCCG1C 2025-06-25 15:11
порлучать мап -
"Type" => Bookmark
"Name" => Программирование на языке Lua 2
"Id" => 1JPCCG1C
"Cdate" => 2025-06-25 15:11
предусмотреть конвертацию в отдельные поля Type, Name, Id, Cdate

при добавлении поля в entry соблюдать порядок полей fieldOrder

соблюдать отступы для значений - начинать с одного столбца

> разделитель entries начинается со --- в начале строки, для одной записи в файле не нужен

создать индексы по Id, Tag, filePath

после парсинга join() должен соответсвовать исходной строке файла

напиши тесты


реализация Entry.getValue, getFieldTokens, getList, subMap,

методы setField, subMapAdd, subMapDel

или сериализация в другие форматы (JSON, Markdown)


> Refs, BackRefs через setField(String, Map<String, String>)

удаление через subMapDel

позиционирование с subMapAdd(..., pos)
