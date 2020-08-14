package org.fhir.salem;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import ca.uhn.fhir.context.FhirContext;

import java.net.URL;

/**
 * An {@link IGenericClient} factory
 */
public class ClientFactory {

    /** A method that creates an {@link IGenericClient} client
     * @param context the context the will create the client
     * @param url the URL to which client will be sending requests
     * @return the created client
     */
    public static IGenericClient forURLAndContext(FhirContext context, URL url){
        IGenericClient client = context.newRestfulGenericClient(url.toString());
        client.registerInterceptor(new LoggingInterceptor(false));
        return client;
    }
}
