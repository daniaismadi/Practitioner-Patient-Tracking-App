package database;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/***
 * Interface for retrieving, inserting and querying practitioner information from the online FHIR server and the
 * local MongoDB database.
 *
 */
public interface PractitionerDAO {

    /***
     * Get the identifier of the practitioner with this ID.
     *
     * @param hPracId       The ID of the practitioner.
     * @return              The identifier of this practitioner.
     */
    String getHPracIdentifier(String hPracId);

    /***
     * Get the list of practitioner IDs that have this identifier.
     *
     * @param hPracIdentifier   The identifier to search for
     * @return                  List of practitioner IDs that have this identifier
     */
    ArrayList<String> getHPracIds(String hPracIdentifier);

    /***
     * Retrieve information of the practitioner with this ID from the FHIR server and insert the document (JSON) of
     * information into this local database.
     *
     * @param hPracId           The ID of the practitioner to retrieve information for.
     */
    void insertPracById(String hPracId);

}
