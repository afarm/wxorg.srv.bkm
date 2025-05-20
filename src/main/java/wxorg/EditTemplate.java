package wxorg;

public class EditTemplate extends ServletWrapper {

    public static String get(String textarea) {
        return String.format(
            """
                <html>
                  <meta charset="UTF-8"/>
                  <style>
                    html * {
                      font-size: 11px !important;
                      font-family: monospace !important;
                    }
                    textarea {
                      white-space: pre;
                      overflow-wrap: normal;
                      width: 90%%;
                      height: 250px;
                    }
                  </style>
                  <body>
                    <pre>
                    <form method='post'>
                      <textarea>%s</textarea>
                      <button type='submit'>Save</button> <button 
                        type='submit'>Save & Close</button> <button
                        type='submit'>Delete</button>
                    </form>
                  </body>
                </html>    
                """,
            textarea);
    }
}