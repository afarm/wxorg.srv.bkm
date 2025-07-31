package wxorg;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParserXmlFile {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private List<String> entryTypes;

    XmlMapper xmlMapper;

    public ParserXmlFile(List<String> entryTypes, XmlMapper xmlMapper) {
        this.entryTypes = entryTypes;
        this.xmlMapper = xmlMapper;
    }

    public List<Map<String, String>> parseFile(Path path) throws IOException {
        Map<String, String> xml = xmlMapper.readValue(new File(path.toString()), Map.class);
        xml.put("_file", path.toString());
        List<Map<String, String>> entries = new ArrayList<>();
        entries.add(xml);
        return entries;
    }
}