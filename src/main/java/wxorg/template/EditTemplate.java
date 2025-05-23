package wxorg.template;

public class EditTemplate  {

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
                    <form method='post' action="/?act=edit&uid={uid}">
                    Types: <select name='types'>
                      <option value="" selected></option>
                      <option value="Note">Note</option>
                      <option value="Bookmark">Bookmark</option>
                    </select> Tags: <select name='tags'>
                      <option value="" selected></option>
                      <option value="Work">Work</option>
                      <option value="Jira">Jira</option>
                    </select>

                      <textarea name='data'>%s</textarea>
                      <button type='submit'>Save</button> <button 
                        type='submit'>Save & Close</button> <button
                        type='submit'>Delete</button>
                    </form>
                    {backRefs}
                  </body>
                </html>    
                """,
            textarea);
    }
}