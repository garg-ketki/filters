package filterexample.android.com.filtersexample.util;

import android.util.Log;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import filterexample.android.com.filtersexample.model.FiltersModel;

/**
 * Created by ketki on 25/07/17.
 * Helper class to initialize data for filters
 */

public class FiltersDataInitializer {

    private static final String DATA_URL = "https://api.myjson.com/bins/3b0u2";
    private static final String TAG = FiltersDataInitializer.class.getSimpleName();

    public static FiltersModel getFilterData() {
        InputStream inputStream = null;
        try {
            URL url = new URL(DATA_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept-Encoding", "gzip");
            connection.connect();

            inputStream = connection.getInputStream();
            if ("gzip".equals(connection.getContentEncoding())) {
                inputStream = new GZIPInputStream(inputStream);
            }
            return Utility.parseJson(Utility.readInputStream(inputStream), FiltersModel.class);
        } catch (Exception e) {
            Log.e(TAG, "exception: " + e);
        } finally {
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (Exception e) {
                }
        }
        return null;
    }
}
