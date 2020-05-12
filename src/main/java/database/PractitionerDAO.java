package database;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface PractitionerDAO {

    String getHPracIdentifier(String hPracId);
    ArrayList<String> getHPracIds(String hPracIdentifier);
    void insertPracById(String hPracId) throws IOException;

}
