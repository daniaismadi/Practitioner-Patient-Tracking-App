import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class jsonread {
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
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

    public static void main(String[] args) throws IOException, JSONException {
        JSONObject json = readJsonFromUrl("https://fhir.monash.edu/hapi-fhir-jpaserver/fhir/Observation?_count=13&code=2093-3&patient=3689&_sort=date&_format=json");
//        System.out.println(json.toString());

        JSONArray entry = json.getJSONArray("entry");
//        System.out.println(entry);
        for(int i = 0; i < entry.length(); i++){
            JSONObject entryObjects = entry.getJSONObject(i);
            JSONObject resource = entryObjects.getJSONObject("resource");
            JSONObject valueQuantity = resource.getJSONObject("valueQuantity");
            double choslestrol = valueQuantity.getDouble("value");
            System.out.println(choslestrol);

        }

//        JSONObject cholesterol = json.getJSONObject("entry");
//
//
//        String chol = cholesterol.getString("value");
//
//        System.out.println(chol);
//        System.out.println(json.get("valueQuantity"));
//        System.out.println();
    }
}
