package org.fhir.salem;

import ca.uhn.fhir.context.FhirContext;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class {@code FhirContextFactory} a singleton factory to initialize the costly instance of FhirContext
 *
 */

public class FhirContextFactory {

    private static FhirContext fhirContextR4;

    private static Lock  singletonLock = new ReentrantLock();

    /** Fhir Context of R4 factory method.
     * @return a {@link FhirContext} that is compatible to Release 4 of the FHIR standard
     */
    public static FhirContext forR4(){
        // Create the FHIR context
        if(fhirContextR4==null){
            try {
                singletonLock.lock();
                if (fhirContextR4 == null) {
                    fhirContextR4 = FhirContext.forR4();
                    System.out.println(fhirContextR4);
                }
            }catch(Exception ex) {
                throw ex;
            }finally {
                singletonLock.unlock();
            }
        }
        return fhirContextR4;
    }

    //TODO other methods factories can be added here.
}
