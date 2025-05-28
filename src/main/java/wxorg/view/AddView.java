package wxorg.view;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import wxorg.template.EditTemplate;
import wxorg.util.QueryParser;
import wxorg.util.RandomString;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class AddView {

    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String title = request.getParameter("title");
        String url = request.getParameter("url");

//        if(App.allUrls.contains(url)) {
//            System.out.println("Doublicate url = " + url);
//            return;
//        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm");
        String textarea = "";
        String uid = RandomString.random();

        String types = "";
        types += "<a href='?type=Note'>Note</a>";
        types += "<a href='?type=Bookmark'>Bookmark</a>";

        String tags = "";
        tags += "<a href='?tag=Work'>Note</a>";
        tags += "<a href='?tag=Jira'>Bookmark</a>";

        textarea += String.format("Bookmark: %s %s %s\n", title, uid, simpleDateFormat.format(new Date()));
        textarea += String.format("Url:      %s\n", url);
        textarea += String.format("Tags:     %s\n", "");
        String resStr = EditTemplate.get(textarea);
        resStr = resStr.replace("{types}", types);
        resStr = resStr.replace("{tags}", tags);
        resStr = resStr.replace("{uid}", uid);
        response.getWriter().write(resStr);
    }

}