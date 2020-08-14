package org.fhir.salem;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.CacheControlDirective;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.*;
import ca.uhn.fhir.util.BundleUtil;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.StringType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.api.mockito.PowerMockito;

import java.io.*;
import java.util.*;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(BundleUtil.class)
public class PatientUtilTest {
    /**
     * this test asserts that the IGenericClient searches for Patient resources using the passed critirion
     * sorted asscendingly by the given IParam
     */
    @Test
    public void search_WhenSearchForPatients_ShouldQueryFormStayIntact(){
        IGenericClient client = PowerMockito.mock(IGenericClient.class);
        IUntypedQuery iUntypedQuery  = mock(IUntypedQuery.class);
        IQuery iQuery = mock(IQuery.class);
        ICriterion iCriterion = mock(ICriterion.class);
        ISort iSort = mock(ISort.class);
        IParam iParam  = mock(IParam.class);
        Boolean noCache = PowerMockito.mock(Boolean.class);
        when(client.search()).thenReturn(iUntypedQuery);
        when(iUntypedQuery.forResource("Patient")).thenReturn(iQuery);
        when(iQuery.where(iCriterion)).thenReturn(iQuery);
        when(iQuery.sort()).thenReturn(iSort);
        when(iSort.ascending(iParam)).thenReturn(iQuery);
        when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);
        when(iQuery.cacheControl(any())).thenReturn(iQuery);
        PatientUtil.search(client,iCriterion,iParam,noCache.booleanValue());
        verify(iUntypedQuery,times(1)).forResource("Patient");
        verify(client,times(1)).search();
        verify(iQuery,times(1)).where(iCriterion);
        verify(iQuery,times(1)).sort();
        verify(iSort,times(1)).ascending(iParam);
        verify(iQuery,times(1)).returnBundle(Bundle.class);
        ArgumentCaptor<CacheControlDirective> argument = ArgumentCaptor.forClass(CacheControlDirective.class);
        verify(iQuery,times(1)).cacheControl(argument.capture());
        Assert.assertEquals("passed noCache value did not match what used in the request",noCache.booleanValue(), argument.getValue().isNoCache());
        verify(iQuery,times(1)).execute();
    }

    /**
     * A test method that iterates over {@code Bundle} and prints patients infromation in the form of: "First: [{givenName}], Family: {familyName}, DoB: {dateOfBirth}"
     * @throws IOException
     */
    @Test
    public void print_WhenBundleOfPatientsIsProvided_PrintPatientsGivenThenFamilyThenDoB() throws IOException {
        List<Patient> patientsFirstPage = new ArrayList<>();
        StringBuilder expectedResult = new StringBuilder(128);
        // prepare first page patients entries
        patientsFirstPage.add(createPatient("John","Smith",createDate(1981,1,1),expectedResult));
        patientsFirstPage.add(createPatient("Jonny","Robert",createDate(1982,2,2),expectedResult));
        patientsFirstPage.add(createPatient("Dina","Jackson",createDate(1983,3,3),expectedResult));
        List<Patient> patientsSecondPage = new ArrayList<>();
        // prepare second page patients entries
        patientsSecondPage.add(createPatient("John2","Smith2",createDate(1984,1,1),expectedResult));
        patientsSecondPage.add(createPatient("Jonny2","Robert2",createDate(1985,2,2),expectedResult));
        patientsSecondPage.add(createPatient("Dina2","Jackson2",createDate(1986,3,3),expectedResult));
        // PowerMockito is used to mock static classes
        PowerMockito.mockStatic(BundleUtil.class);
        // mock non-static classes using Mockito
        FhirContext context = mock(FhirContext.class);
        IGenericClient client = mock(IGenericClient.class);
        Bundle response1 = mock(Bundle.class);
        Bundle response2 = mock(Bundle.class);
        IGetPage igetPage = mock(IGetPage.class);
        IGetPageTyped iGetPagedTyped = mock(IGetPageTyped.class);
        Bundle.BundleLinkComponent blc = mock(Bundle.BundleLinkComponent.class);
        // mock behavior of mocked interfaces
        when(BundleUtil.toListOfResourcesOfType(context,response1,Patient.class)).thenReturn(patientsFirstPage);
        when(response1.getLink(Bundle.LINK_NEXT)).thenReturn(blc);
        when(response2.getLink(Bundle.LINK_NEXT)).thenReturn(null);
        when(client.loadPage()).thenReturn(igetPage);
        when(igetPage.next(response1)).thenReturn(iGetPagedTyped);
        when(iGetPagedTyped.execute()).thenReturn(response2);
        when(BundleUtil.toListOfResourcesOfType(context,response2,Patient.class)).thenReturn(patientsSecondPage);
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent);
        // do actual call
        PatientUtil.print(response1,context,client,out);
        // grab printed contents
        String actualResult = outContent.toString();
        // assert output
        Assert.assertTrue("Print output has been compromised",actualResult.equals(expectedResult.toString()));
    }

    private static Patient createPatient(String given, String family, Date dob,StringBuilder sb){
        Patient p = new Patient();
        HumanName name = new HumanName();
        name.setGiven(Arrays.asList(new StringType(given)));
        name.setFamily(family);
        p.setName(Arrays.asList(name));
        p.setBirthDate(dob);
        sb.append("First: ["+given+"], Last: "+ family+", DoB: "+dob.toString()+System.lineSeparator());
        return p;
    }

    private static Date createDate(int year,int month,int day){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,day,0,0,0);
        return calendar.getTime();
    }
}
