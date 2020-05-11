package database;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public interface PractitionerDAO {

    List<String> getPracPatientNames(String identifier);
    void insertPracPatients(String identifier) throws IOException, JSONException;

}
