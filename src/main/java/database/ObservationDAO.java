package database;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/***
 * Interface for retrieving, inserting and querying observation information from the online FHIR server and the
 * local MongoDB database.
 *
 */
public interface ObservationDAO {

    /***
     * Insert count number of observations, specified by code, of patient with patientId in order of descending
     * date, i.e., the latest observation first.
     *
     * @param patientId     the ID of the patient
     * @param code          the code that specifies the observation type
     * @param count         the number of observations to insert
     */
    void insertPatientObsByCode(String patientId, String code, String count);

    /***
     * Retrieve cholesterol observations of patient from the server and insert into the database.
     *
     * @param patientId     the ID of the patient
     * @param count         the number of observations to insert
     */
    void insertCholesterolObs(String patientId, int count);

    /***
     * Retrieve blood pressure observations (systolic and diastolic) of patient from the server and insert into
     * the database.
     *
     * @param patientId     the ID of the patient
     * @param count         the number of observations to insert
     */
    void insertBPObs(String patientId, int count);

    /***
     * Retrieve cholesterol observations of patient from the local database.
     *
     * @param patientId     the ID of the patient
     * @param count         the number of observations to retrieve
     * @return              a list of an array of objects which is in the format of [Date, Cholesterol Value] sorted in
     *                      descending order of Date. Date is of type Date and Cholesterol Value is of type double.
     * @throws ParseException   Occurs if there is an error in parsing the effectiveDateTime.
     */
    List<Object[]> getCholesterolObs(String patientId, int count) throws ParseException;

    /***
     * Retrieve systolic BP observations of patient from the local database.
     *
     * @param patientId     the ID of the patient
     * @param count         the number of observations to retrieve
     * @return              a list of an array of objects which is in the format of [Date, Systolic BP Value] sorted
     *                      in descending order of Date. Date is of type Date and Systolic BP Value is of type
     *                      double.
     * @throws ParseException   if a parse exception occurs when parsing the effectiveDateTime.
     */
    List<Object[]> getSystolicBPObs(String patientId, int count) throws ParseException;

    /***
     * Retrieve diastolic BP observations of patient from the local database.
     *
     * @param patientId     the ID of the patient
     * @param count         the number of observations to retrieve
     * @return              a list of an array of objects which is in the format of [Date, Diastolic BP Value] sorted
     *                      in descending order of Date. Date is of type Date and Systolic BP Value is of type double.
     * @throws ParseException   if a parse exception occurs when parsing the effectiveDateTime.
     */
    List<Object[]> getDiastolicBPObs(String patientId, int count) throws ParseException;

//    void insertCholesObsByCodes(ArrayList<String> codes, String count, int pageCount);
//    void insertLatestCholesObs(String patientId);
//    void insertPatientLatestObsByCode(String patientId, String code);
//    void insertObs(String obsId);

}
