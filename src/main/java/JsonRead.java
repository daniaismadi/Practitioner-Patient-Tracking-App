import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class JsonRead {
    static String rootUrl = "https://fhir.monash.edu/hapi-fhir-jpaserver/fhir/";

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
        // JSONObject json = readJsonFromUrl("https://fhir.monash.edu/hapi-fhir-jpaserver/fhir/Observation?_count=13&code=2093-3&patient=3689&_sort=date&_format=json");

        String encountersUrl = retrievePractitionerPatients("500");

        // Declare required variables
        boolean nextPage = true;
        String nextUrl = encountersUrl;
        int pageCount = 0;
        int patientCount = 0;
        ArrayList<String> patientList = new ArrayList<>();

        while (nextPage) {
            // Get all encounters on this page
            JSONObject allEncounters = readJsonFromUrl(nextUrl);

            // Check if there is a next page
            nextPage = false;
            // Get all related links
            JSONArray links = allEncounters.getJSONArray("link");
            for (int i = 0; i < links.length(); i++) {
                // Get current link
                JSONObject link = links.getJSONObject(i);
                // Check if relation is next, this means there is a next page
                String relation = link.getString("relation");
                if (relation.equalsIgnoreCase("next")) {
                    // Update variables accordingly
                    nextPage = true;
                    nextUrl = link.getString("url");
                    pageCount += 1;
                }
            }

            JSONArray encounterData = allEncounters.getJSONArray("entry");
            // Loop through all encounters and retrieve patient ID, patient name, cholesterol level and datetime.
            for (int i = 0; i < encounterData.length(); i++) {
                JSONObject entry = encounterData.getJSONObject(i);
                JSONObject resource = entry.getJSONObject("resource");
                String patientID = resource.getJSONObject("subject").getString("reference");
                String patientName = resource.getJSONObject("subject").getString("display");

                System.out.println(patientID + " " + patientName);
                // Add patient to patientList with "+" separator so it will be easy to extract later.
                patientList.add(patientID + "+" + patientName);
            }

        }
    }

    public static String retrievePatient(String identifier) {
        return rootUrl + "Patient/" + identifier + "?_format=json";
    }

    public static String retrievePractitioner(String identifier) {
        return rootUrl + "Practitioner?identifier=http%3A%2F%2Fhl7.org%2Ffhir%2Fsid%2Fus-npi%7C" + identifier +
                "&_format=json";
    }

    public static String retrievePractitionerPatients(String identifier) {
        String url = rootUrl + "Encounter?_include=Encounter.participant.individual&_include=Encounter." +
                "patient&participant.identifier=http%3A%2F%2Fhl7.org%2Ffhir%2Fsid%2Fus-npi%7C" +
                identifier + "&_format=json";
        return url;
    }
}
