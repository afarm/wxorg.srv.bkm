package wxorg;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class EditView {

    private final EntriesService entriesService;
    private final String dir;

    public EditView(EntriesService entriesService, String dir) {
        this.entriesService = entriesService;
        this.dir = dir;
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader("Content-Type", "text/html; charset=utf-8");

        Map<String, String> query = QueryParser.parse(request.getQueryString());

        String uid = URLDecoder.decode(query.get("uid"));
        Entry entry = entriesService.getById(uid);
        request.setCharacterEncoding("UTF-8");
        String data = request.getParameter("data");
        if (data != null) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(dir + "/" + uid + ".txt", StandardCharsets.UTF_8));
            writer.write(data);
            writer.close();
        }

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