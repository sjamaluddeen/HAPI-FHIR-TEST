package org.fhir.salem;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;

import java.net.URL;

public class SampleClient {

    public static void main(String[] theArgs) {

        FhirContext context;
        IGenericClient client;
        try {
            // create the expensive FHIR context instance
            context = FhirContextFactory.forR4();
            client = ClientFactory.forURLAndContext(context, new URL("http://hapi.fhir.org/baseR4"));
            // Search for Patient resources and ask server to return the result sorted by Patient resource given name
            Bundle response = PatientUtil.search(client,Patient.FAMILY.matches().values("SMITH"),Patient.GIVEN);
			// print the result to the specified output stream
            PatientUtil.print(response,context,client,System.out);
        }catch(Exception ex){
            // log the exception using the console
            System.err.println(ex);
        }
    }
}
