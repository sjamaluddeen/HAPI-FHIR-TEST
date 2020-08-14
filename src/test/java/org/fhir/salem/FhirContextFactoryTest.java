package org.fhir.salem;

import ca.uhn.fhir.context.FhirContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static org.mockito.Mockito.when;


@RunWith(PowerMockRunner.class)
@PrepareForTest(FhirContext.class)
public class FhirContextFactoryTest {

    /**
     * A test method that uses a threads pool of specified size
     * to hammer the FHIR factory with calls to retrieve a FhirContext instance.
     * To pass, the test asserts the call to create a FhirContext is only called exactly one time.
     * @throws InterruptedException
     */
    @Test
    public void createContext_WhenCalledFromMultiThreads_ShouldCreateOnlyOneInstance() throws InterruptedException {
        final int numberOfThreads = 1000;
        final int threadPoolSize = 20;
        PowerMockito.mockStatic(FhirContext.class);
        FhirContext context = Mockito.mock(FhirContext.class);
        when(FhirContext.forR4()).thenReturn(context);
        ExecutorService service = Executors.newFixedThreadPool(threadPoolSize);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            service.execute(() -> {
                FhirContextFactory.forR4();
                latch.countDown();
            });
        }
        latch.await();
        PowerMockito.verifyStatic(FhirContext.class,VerificationModeFactory.times(1));
        FhirContext.forR4();
    }
}
