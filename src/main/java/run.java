import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Patient;

public class run {

    /**
     * This is the Java run method, which gets executed
     */
    public static void main(String[] args) {

        // Create a context
        FhirContext ctx = FhirContext.forR4();

        // Create a client
        IGenericClient client = ctx.newRestfulGenericClient("https://fhir.monash.edu/hapi-fhir-jpaserver/fhir/");

        // Read a patient with the given ID
        Patient patient = client.read().resource(Patient.class).withId("4422457").execute();

        // Print the output
        String string = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(patient);
        System.out.println(string);

        /*
         * Some more things to try:
         *
         * Search for Patient resources with the name “Test” and print the results
         *   Bonus: Load the second page
         *
         * Create a new Patient resource and upload it to the server
         *   Bonus: Log the ID that the server assigns to your resource
         *
         * Read a resource from the server
         *   Bonus: Display an error if it has been deleted
         *   Hint for Bonus- See this page: http://hapifhir.io/apidocs/ca/uhn/fhir/rest/server/exceptions/package-summary.html
         */

    }

}