package org.fhir.salem;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.IParam;
import ca.uhn.fhir.util.BundleUtil;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Patient;

import java.io.PrintStream;
import java.util.List;

/**
 * A utility class that works over {@link Patient} resource
 */
public class PatientUtil {

    /** Queries a FHIR compliant server for patients given a set of an {@link ICriterion} and sorted in ascending order by a give {@link IParam}
     ** @param client the client that will send the query
     * @param critirion the critiron used to filter the retrieved Patient entries
     * @param sortBy the parameter used for the ascending order
     * @return {@link Bundle} object to allow caller to process the result
     */

    public static Bundle search(IGenericClient client, ICriterion<?> critirion, IParam sortBy){
        return client.search()
        .forResource("Patient")
        .where(critirion)
        .sort().ascending(sortBy)
        .returnBundle(Bundle.class)
        .execute();
    }

    /**
     * a method that iterates over pages in the provided {@param response} parameter and prints patient information to {@param out}
     * @param response
     * @param context
     * @param client
     * @param out
     */
    public static void print(Bundle response, FhirContext context, IGenericClient client, PrintStream out){
        boolean hasEntries = false;
        List<Patient> entries;
        do {
            entries = BundleUtil.toListOfResourcesOfType(context,response,Patient.class);
            for(Patient p : entries){
                HumanName hn = p.getName().get(0);
                out.println("First: " + hn.getGiven() + ", Last: " + hn.getFamily() + ", DoB: " + (p.hasBirthDate()?p.getBirthDate().toString():"-") );
            }
            hasEntries = response.getLink(Bundle.LINK_NEXT) != null;
            if (hasEntries) {
                response = client.loadPage().next(response).execute();
            }
        }while(hasEntries);

    }
}
