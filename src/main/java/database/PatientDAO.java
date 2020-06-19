package database;

import org.bson.Document;

import java.util.ArrayList;

/***
 * Interface for retrieving, inserting and querying patient information from the online FHIR server and the
 * local MongoDB database.
 *
 */
public interface PatientDAO {

    /***
     * Get the full name of this patient.
     *
     * @param patientId     The ID of the patient to retrieve information from.
     * @return              The full name of this patient.
     */
    String getPatientName(String patientId);

    /***
     * Get the last name of this patient.
     *
     * @param patientId     The ID of the patient to retrieve information from.
     * @return              The last name of this patient.
     */
    String getPatientLName(String patientId);

    /***
     * Get the given name of this patient (could be more than one word).
     *
     * @param patientId     The ID of the patient to retrieve information from.
     * @return              The given name of this patient.
     */
    String getPatientFName(String patientId);

    /***
     * Get the gender of this patient.
     *
     * @param patientId     The ID of the patient to retrieve information from.
     * @return              The gender of the patient.
     */
    String getPatientGender(String patientId);

    /***
     * Get the date of birth of this patient.
     *
     * @param patientId     The ID of the patient to retrieve information from.
     * @return              The date of birth of the patient as a string.
     */
    String getPatientBirthdate(String patientId);

    /***
     * Return the city where this patient is from.
     *
     * @param patientId     The ID of the patient to retrieve information from.
     * @return              The city where this patient is from.
     */
    String getPatientAddressCity(String patientId);

    /***
     * Return the state where this patient is from.
     *
     * @param patientId     The ID of the patient to retrieve information from.
     * @return              The state where this patient is from.
     */
    String getPatientAddressState(String patientId);

    /***
     * Return the country where this patient is from.
     *
     * @param patientId     the ID of the patient to retrieve information from
     * @return              the country where this patient is from
     */
    String getPatientAddressCountry(String patientId);

    /***
     * Return list of patient IDs that are sorted by _id (unique identifier given by MongoDB).
     *
     * @param patientIds        the list of patient IDs to sort
     * @return                  the sorted list of patient IDs
     */
    ArrayList<String> getPatientIdsSorted(ArrayList<String> patientIds);

    /***
     * Retrieve information of the patient with this patientId from the FHIR server and insert the document of
     * this patient into the local database.
     *
     * @param patientId     The ID of the patient to retrieve information for.
     */
    void insertPatient(String patientId);

}
