package database;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

/***
 * Interface for retrieving, inserting and querying Encounter information from the online FHIR server and the
 * local MongoDB database.
 *
 */
public interface EncounterDAO {

    /***
     * Get all patients of all the practitioners in hPracIds.
     *
     * @param hPracIds      list of practitioner IDs to retrieve patient IDs from
     * @return              list of patient IDs of these practitioners
     */
    ArrayList<String> getPatientsByHPracId(ArrayList<String> hPracIds);

    /***
     * Insert Encounter information into this database.
     *
     * @param encounterId       The Encounter ID of this encounter.
     */
    void insertEncounter(String encounterId);

    /***
     * Insert all encounters of this practitioner, as specified by identifier. Also insert patient and practitioner
     * documents into the database as encounters are found.
     *
     * @param identifier        The identifier of the practitioner.
     * @param patientDAO        PatientDAO needed to insert patient information into the database.
     * @param practitionerDAO   PractitionerDAO needed to insert practitioner information into the database.
     * @throws IOException      Occurs when there is an error in reading the JSON document from the URL given.
     * @throws JSONException    Occurs when there is an error in reading the JSON document from the URL given.
     */
    void insertEncountersByPrac(String identifier, PatientDAO patientDAO, PractitionerDAO practitionerDAO)
            throws IOException, JSONException;

}
