package wxorg.view;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import wxorg.template.EditTemplate;
import wxorg.DataSourceService;
import wxorg.Entry;
import wxorg.util.QueryParser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class EditView {

    private final DataSourceService entriesService;

    private final String dir;

    public EditView(DataSourceService dataSourceService, String dir) {
        this.entriesService = dataSourceService;
        this.dir = dir;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uid = request.getParameter("uid");
        Entry entry = entriesService.getById(uid);

        String fileStr = Files.readString(Paths.get(dir + "/" + uid + ".txt"));
        String textarea = "";
        String resStr = "--";
        if (entry != null) {
            textarea += fileStr;
            resStr = EditTemplate.get(textarea);
        }
        String backRefs = "Backrefs:\n";

        backRefs += String.format("      XXX header\n");
        backRefs += String.format("      XXX header\n");
        backRefs += String.format("      XXX header\n");
        backRefs += String.format("      XXX header\n");
        resStr = resStr.replace("{backRefs}", backRefs);
        resStr = resStr.replace("{uid}", uid);

//        Process process = new ProcessBuilder("idea", "file" + entry.uid + ".txt").start();

        response.getWriter().write(resStr);
    }
}