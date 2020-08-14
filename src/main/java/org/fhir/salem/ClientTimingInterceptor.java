package org.fhir.salem;

import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import ca.uhn.fhir.util.StopWatch;

import java.io.IOException;

/**
 * A {@link IClientInterceptor} that exposes a getter for the {@link IHttpResponse} response {@link StopWatch}
 */
public class ClientTimingInterceptor implements IClientInterceptor {

    /** a reference that will hold the reference to the response stop watch  */
    private StopWatch stopWatch;

    @Override
    public void interceptRequest(IHttpRequest iHttpRequest) {
    }

    @Override
    public void interceptResponse(IHttpResponse iHttpResponse) throws IOException {
        stopWatch = iHttpResponse.getRequestStopWatch();
    }

    /**
     * a method that returns response {@Link StopWatch}
     * @return the response {@Link StopWatch}
     */
    public StopWatch getStopWatch(){
        return stopWatch;
    }
}
