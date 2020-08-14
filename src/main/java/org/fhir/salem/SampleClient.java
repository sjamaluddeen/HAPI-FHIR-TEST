package org.fhir.salem;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Patient;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class SampleClient {

    public static void main(String[] theArgs) {

        FhirContext context;
        IGenericClient client;
        try {
            // create the expensive FHIR context instance
            context = FhirContextFactory.forR4();
            client = ClientFactory.forURLAndContext(context, new URL("http://hapi.fhir.org/baseR4"));
            // create and register the timing interceptor
            IClientInterceptor timingInterceptor = new ClientTimingInterceptor();
            client.registerInterceptor(timingInterceptor);
            // read the resources file and populate a list
            List<String> familyNamesList = new ArrayList<>(20);
            InputStream inputStream = SampleClient.class.getResourceAsStream("/familynames.txt");
            Scanner sc = new Scanner(inputStream);
            while (sc.hasNextLine()) {
                familyNamesList.add(sc.nextLine());
            }
            final int NUMBER_OF_LOOPS = 3;
            long[] averageRunTimes = new long[NUMBER_OF_LOOPS];
            for(int i = 0 ; i < NUMBER_OF_LOOPS ; i++) {
                long totalTimefor20Requests = 0;
                averageRunTimes[i] = 0;
                boolean noCache = false;
                if(i==NUMBER_OF_LOOPS-1){
                    noCache=true;
                }
                for(String family: familyNamesList) {
                    PatientUtil.search(client, Patient.FAMILY.matches().values(family), Patient.GIVEN,noCache);
                    totalTimefor20Requests += ((ClientTimingInterceptor) timingInterceptor).getStopWatch().getMillis();
                }
                averageRunTimes[i] = totalTimefor20Requests / familyNamesList.size();
                // sleep to create time gap between loops
                final long SLEEP_TIME = 3000L;
                System.out.println("sleeping for "+ TimeUnit.MILLISECONDS.toSeconds(SLEEP_TIME) +" sec...");
                Thread.sleep(SLEEP_TIME);
            }
            for(int loop = 0 ; loop < averageRunTimes.length; loop++){
                System.out.println("Loop "+(loop+1)+" average time is "+averageRunTimes[loop]+"ms");
            }
        }catch(Exception ex){
            // log the exception using the console
            System.out.println(ex);
        }
    }
}
