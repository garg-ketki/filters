package filterexample.android.com.filtersexample.util;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by ketki on 25/07/17.
 * Provides utility methods to support Filter Application
 */

public class Utility {

    public static String readInputStream(InputStream is) throws Exception {
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        int n;
        while ((n = reader.read(buffer)) != -1) {
            writer.write(buffer, 0, n);
        }
        return writer.toString();
    }

    public static <T> T parseJson(String json, Class<T> tClass) {
        return new Gson().fromJson(json, tClass);
    }
}
