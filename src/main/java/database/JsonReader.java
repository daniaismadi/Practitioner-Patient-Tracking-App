package database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/***
 * A class to facilitate reading JSON resource documents.
 */
public class JsonReader {

    /***
     * Reads the JSON document.
     *
     * @param rd            The reader.
     * @return              The JSON document as a string.
     * @throws IOException  Occurs if there is an error in reading the JSON document.
     */
    static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    /***
     * Read the JSON resource document from URL.
     *
     * @param url               The URL of this JSON resource document.
     * @return                  The JSON resource document online as a JSON object.
     * @throws IOException      Occurs if there is an error in reading the JSON document.
     * @throws JSONException    Occurs if there is an error in reading the JSON document.
     */
    static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }
}
