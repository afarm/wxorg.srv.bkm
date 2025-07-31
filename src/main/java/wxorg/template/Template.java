package wxorg.template;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class Template {

    String templateStr = "";

    public Template(String editTemplatePath) throws IOException {
        templateStr = Files.readString(Path.of(editTemplatePath));
    }

    public String substitute(Map<String, String> map) {
        String tpl = new String(templateStr);
        for (String key : map.keySet()) {
            String val = map.get(key);
            val = val != null ? val : "";
            tpl = tpl.replace("{" + key + "}", val);
        }
        return tpl;
    }
    // --- todo ---
    // ~read from file
    // ~check file modified
    //
    // check/find attribute exists
    //   findAttr(attrName) : AttrPos: start, end, valStart, valEnd, nameEnd, val
    // replace value of attribute
    //   replaceAttrValue(AttrPos attrPos, newVal)
    // add attribute in place
    //   add url tags in new line \n____attrName="attrValue"
    //   addAttr(attrName, attrVal, "first/new line")
}
