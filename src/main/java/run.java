import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Encounter;
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

    }

}