/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ocelotds.marshallers;

import javax.enterprise.inject.Instance;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.ocelotds.marshalling.IJsonMarshaller;
import org.ocelotds.objects.FakeCDI;

/**
 *
 * @author hhfrancois
 */
@RunWith(MockitoJUnitRunner.class)
public class JsonMarshallerServicesTest {

	@InjectMocks
	@Spy
	JsonMarshallerServices instance;

	@Spy
	Instance<IJsonMarshaller> iJsonMarshallers = new FakeCDI();

	/**
	 * Test of getIJsonMarshallerInstance method, of class JsonMarshallerServices.
	 * @throws org.ocelotds.marshallers.JsonMarshallerException
	 */
	@Test
	public void testGetIJsonMarshallerInstance() throws JsonMarshallerException {
		System.out.println("getIJsonMarshallerInstance");
		FakeCDI.class.cast(iJsonMarshallers).add(new LocaleMarshaller());
		IJsonMarshaller result = instance.getIJsonMarshallerInstance(LocaleMarshaller.class);
		assertThat(result).isInstanceOf(LocaleMarshaller.class);
	}

	/**
	 * Test of getIJsonMarshallerInstance method, of class JsonMarshallerServices.
	 * @throws org.ocelotds.marshallers.JsonMarshallerException
	 */
	@Test(expected = JsonMarshallerException.class)
	public void testGetIJsonMarshallerInstanceFail() throws JsonMarshallerException {
		System.out.println("getIJsonMarshallerInstance");
		FakeCDI.class.cast(iJsonMarshallers).clear();
		instance.getIJsonMarshallerInstance(LocaleMarshaller.class);
	}
}