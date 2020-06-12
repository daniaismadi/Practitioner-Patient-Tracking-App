package database;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface PractitionerDAO {

    /***
     *
     * @param hPracId
     * @return
     */
    String getHPracIdentifier(String hPracId);

    /***
     *
     * @param hPracIdentifier
     * @return
     */
    ArrayList<String> getHPracIds(String hPracIdentifier);

    /***
     *
     * @param hPracId
     * @throws IOException
     * @throws JSONException
     */
    void insertPracById(String hPracId) throws IOException, JSONException;

}
