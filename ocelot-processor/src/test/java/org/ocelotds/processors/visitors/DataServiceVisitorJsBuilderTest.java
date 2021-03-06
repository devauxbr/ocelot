/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ocelotds.processors.visitors;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.ocelotds.KeyMaker;
import org.ocelotds.annotations.JsCacheResult;
import org.ocelotds.frameworks.NoFwk;
import org.ocelotds.frameworks.WriterTest;
import org.ocelotds.processors.JsCacheResultLiteral;
import org.ocelotds.processors.ProcessorConstants;
import org.ocelotds.processors.stringDecorators.NothingDecorator;
import org.ocelotds.processors.stringDecorators.QuoteDecorator;
import org.ocelotds.processors.stringDecorators.StringDecorator;

/**
 *
 * @author hhfrancois
 */
@RunWith(MockitoJUnitRunner.class)
public class DataServiceVisitorJsBuilderTest implements ProcessorConstants{

	@Mock
	private Messager messager;

	@Mock
	private Filer filer;

	@Mock
	private Elements elementUtils;

	@Mock
	private Types typeUtils;

	private DataServiceVisitorJsBuilder instance;

	@Before
	public void setUp() {
		ProcessingEnvironment environment = mock(ProcessingEnvironment.class);
		when(environment.getElementUtils()).thenReturn(elementUtils);
		when(environment.getFiler()).thenReturn(filer);
		when(environment.getMessager()).thenReturn(messager);
		when(environment.getTypeUtils()).thenReturn(typeUtils);
		instance = spy(new DataServiceVisitorJsBuilder(environment, new NoFwk()));
	}

	/**
	 * Test of _visitType method, of class DataServiceVisitorJsBuilder.
	 *
	 * @throws java.io.IOException
	 */
	@Test
	public void test_VisitType() throws IOException {
		System.out.println("_visitType");
		TypeElement typeElement = mock(TypeElement.class);
		Writer writer = WriterTest.getMockWriter();
		doNothing().when(instance).browseAndWriteMethods(anyListOf(ExecutableElement.class), anyString(), eq(writer));
		doReturn(null).when(instance).getOrderedMethods(eq(typeElement), any(Comparator.class));
		doReturn("ClassName").when(instance).getJsClassname(eq(typeElement));

		Name qname = mock(Name.class);
		List methodElements = new ArrayList();

		when(typeElement.getQualifiedName()).thenReturn(qname);
		when(qname.toString()).thenReturn("packageName.ClassName");
		when(typeElement.getEnclosedElements()).thenReturn(methodElements);

		instance._visitType(typeElement, writer);
		verify(writer).append(eq("className")); // instancename
		verify(writer).append(eq("packageName.ClassName")); // _ds = 
	}

	/**
	 * Test of visitMethodElement method, of class DataServiceVisitorJsBuilder. TODO
	 *
	 * @throws java.io.IOException
	 */
	@Test
	public void visitMethodElement() throws IOException {
		System.out.println("visitMethodElement");
		Writer writer = WriterTest.getMockWriter();
		String classname = "packageName.ClassName";
		ExecutableElement methodElement = mock(ExecutableElement.class);
		Name name = mock(Name.class);
		TypeMirror tm = mock(TypeMirror.class);
		List<String> argumentsType = new ArrayList<>();
		List<String> arguments = new ArrayList<>();

		doNothing().when(instance).createMethodBody(eq(classname), any(ExecutableElement.class), anyListOf(String.class), any(Writer.class));
		when(methodElement.getSimpleName()).thenReturn(name);
		when(name.toString()).thenReturn("ClassName");
		doReturn(argumentsType).when(instance).getArgumentsType(eq(methodElement));
		doReturn(arguments).when(instance).getArguments(eq(methodElement));
		doNothing().when(instance).writeArguments(any(Iterator.class), eq(writer));
		doReturn("").when(instance).getMethodComment(eq(methodElement));
		doNothing().when(instance).writeMethodComment(anyString(), any(Iterator.class), any(Iterator.class), eq(tm), eq(writer));
		when(methodElement.getReturnType()).thenReturn(tm);

		instance.visitMethodElement(classname, methodElement, writer);
		WriterTest.testBraces(writer);
	}
	
	/**
	 * Test of writeArguments method, of class.
	 * @throws java.io.IOException
	 */
	@Test
	public void writeArgumentsTest() throws IOException {
		System.out.println("writeArguments");
		Writer writer;
		List<String> arguments;

		arguments = new ArrayList<>();
		writer = new StringWriter();
		instance.writeArguments(arguments.iterator(), writer);
		assertThat(writer.toString()).isEmpty();

		arguments = Arrays.asList("a");
		writer = new StringWriter();
		instance.writeArguments(arguments.iterator(), writer);
		assertThat(writer.toString()).isEqualTo("a");

		arguments = Arrays.asList("a", "b");
		writer = new StringWriter();
		instance.writeArguments(arguments.iterator(), writer);
		assertThat(writer.toString()).isEqualTo("a,"+SPACEOPTIONAL+"b");

		arguments = Arrays.asList("a", "b", "c");
		writer = new StringWriter();
		instance.writeArguments(arguments.iterator(), writer);
		assertThat(writer.toString()).isEqualTo("a,"+SPACEOPTIONAL+"b,"+SPACEOPTIONAL+"c");
	}
	
	/**
	 * Test of writeMethodComment method, of class.
	 * @throws java.io.IOException
	 */
	@Test
	public void writeMethodCommentTest() throws IOException {
		System.out.println("writeMethodComment");
		String methodComment = "";
		Iterator<String> argumentsType = mock(Iterator.class);
		Iterator<String> argumentsName = mock(Iterator.class);
		TypeMirror returnType = mock(TypeMirror.class);
		Writer writer = WriterTest.getMockWriter();

		doNothing().when(instance).writeJavadocComment(anyString(), eq(writer));
		doNothing().when(instance).writeReturnComment(eq(returnType), eq(writer));
		doNothing().when(instance).writeArgumentsComment(any(Iterator.class), any(Iterator.class), eq(writer));

		instance.writeMethodComment(methodComment, argumentsType, argumentsName, returnType, writer);
	}
	
	/**
	 * Test of writeReturnComment method, of class.
	 * @throws java.io.IOException
	 */
	@Test
	public void writeReturnCommentTest() throws IOException {
		System.out.println("writeReturnComment");
		Writer writer = new StringWriter();
		TypeMirror returnType = mock(TypeMirror.class);
		when(returnType.toString()).thenReturn("void").thenReturn("java.lang.String");
		instance.writeReturnComment(returnType, writer);
		assertThat(writer.toString()).isEqualTo("");
		instance.writeReturnComment(returnType, writer);
		assertThat(writer.toString()).isEqualTo(TAB2+" * @return {java.lang.String}"+CR);
	}
	
	/**
	 * Test of writeArgumentsComment method, of class.
	 * @throws java.io.IOException
	 */
	@Test
	public void writeArgumentsCommentTest() throws IOException {
		System.out.println("writeArgumentsComment");
		Writer writer;
		Iterator<String> argumentsType0 = new ArrayList<String>().iterator();
		Iterator<String> argumentsName0 = new ArrayList<String>().iterator();
		Iterator<String> argumentsType1 = Arrays.asList("java.lang.String").iterator();
		Iterator<String> argumentsName1 = Arrays.asList("a").iterator();
		Iterator<String> argumentsType2 = Arrays.asList("java.lang.String", "long").iterator();
		Iterator<String> argumentsName2 = Arrays.asList("a", "b").iterator();
		
		writer = new StringWriter();
		instance.writeArgumentsComment(argumentsType0, argumentsName0, writer);
		assertThat(writer.toString()).isEmpty();

		writer = new StringWriter();
		instance.writeArgumentsComment(argumentsType1, argumentsName1, writer);
		assertThat(writer.toString()).isEqualTo(TAB2+" * @param {java.lang.String}"+SPACE+"a"+CR);

		writer = new StringWriter();
		instance.writeArgumentsComment(argumentsType2, argumentsName2, writer);
		assertThat(writer.toString()).isEqualTo(TAB2+" * @param {java.lang.String}"+SPACE+"a"+CR+TAB2+" * @param {long}"+SPACE+"b"+CR);
	}
	
	/**
	 * Test of writeJavadocComment method, of class.
	 * @throws java.io.IOException
	 */
	@Test
	public void writeJavadocCommentTest() throws IOException {
		System.out.println("writeJavadocComment");
		Writer writer = new StringWriter();
		String methodComment = "";
		instance.writeJavadocComment(methodComment, writer);
		assertThat(writer.toString()).isEqualTo(TAB2+" *"+CR);

		writer = new StringWriter();
		instance.writeJavadocComment(" method do that\n@param a", writer);
		assertThat(writer.toString()).isEqualTo(TAB2+" * method do that"+CR);

		writer = new StringWriter();
		instance.writeJavadocComment(" method do that\n and that\n@param a", writer);
		assertThat(writer.toString()).isEqualTo(TAB2+" * method do that"+CR+TAB2+" * and that"+CR);
	}
	
	/**
	 * Test of getMethodComment method, of class.
	 */
	@Test
	public void getMethodCommentTest() {
		System.out.println("getMethodComment");
		ExecutableElement methodElement = mock(ExecutableElement.class);
		when(elementUtils.getDocComment(eq(methodElement))).thenReturn("DOC");
		String result = instance.getMethodComment(methodElement);
		assertThat(result).isEqualTo("DOC");
	}

	/**
	 * Test of createMethodBody method, of class DataServiceVisitorJsBuilder.
	 *
	 * @throws java.io.IOException
	 */
	@Test
	public void testCreateMethodBody() throws IOException {
		System.out.println("createMethodBody");
		String classname = "CLASSNAME";
		ExecutableElement executableElement = mock(ExecutableElement.class);
		List<String> arguments = mock(List.class);
		Writer writer = mock(Writer.class);
		
		doReturn("METHODNAME").when(instance).getMethodName(eq(executableElement));
		doReturn("ARGS").when(instance).stringJoinAndDecorate(eq(arguments), eq(","), any(NothingDecorator.class));
		doReturn("PARAMNAMES").when(instance).stringJoinAndDecorate(eq(arguments), eq(","), any(QuoteDecorator.class));
		doReturn("KEYS").when(instance).computeKeys(eq(executableElement), eq(arguments));
		doNothing().when(instance).createReturnOcelotPromiseFactory(any(String.class), any(String.class), any(Boolean.class), any(String.class), any(String.class), any(Writer.class));
		
		instance.createMethodBody(classname, executableElement, arguments, writer);
		verify(instance).createReturnOcelotPromiseFactory(any(String.class), any(String.class), any(Boolean.class), any(String.class), any(String.class), any(Writer.class));
	}
	
	/**
	 * Test of getMethodName method, of class DataServiceVisitorJsBuilder.
	 */
	@Test
	public void testGetMethodName() {
		System.out.println("getMethodName");
		ExecutableElement executableElement = mock(ExecutableElement.class);
		Name name = mock(Name.class);
		String expectResult = "METHODNAME";
		when(executableElement.getSimpleName()).thenReturn(name);
		when(name.toString()).thenReturn(expectResult);
		
		String result = instance.getMethodName(executableElement);
		assertThat(result).isEqualTo(expectResult);
	}
	
	/**
	 * Test of computeKeys method, of class DataServiceVisitorJsBuilder.
	 *
	 * @throws java.io.IOException
	 */
	@Test
	public void testComputeKeysFromSpecificArgs() throws IOException {
		System.out.println("computeKeys");
		List<String> arguments = Arrays.asList("a", "b", "c", "d");

		JsCacheResult jcr = mock(JsCacheResult.class);
		when(jcr.keys()).thenReturn(new String[]{"a.c", "b.i", "d"});

		ExecutableElement methodElement = mock(ExecutableElement.class);
		when(methodElement.getAnnotation(eq(JsCacheResult.class))).thenReturn(jcr);
//		doReturn(false).when(instance).considerateAllArgs(any(JsCacheResult.class));

		String result = instance.computeKeys(methodElement, arguments);

		assertThat(result).isEqualTo("(a)?a.c:null,(b)?b.i:null,d");
	}

	/**
	 * Test of computeKeys method, of class DataServiceVisitorJsBuilder.
	 *
	 * @throws java.io.IOException
	 */
	@Test
	public void testComputeKeysFromallArgs() throws IOException {
		System.out.println("computeKeys");
		List<String> arguments = Arrays.asList("a", "b", "c", "d");

		ExecutableElement methodElement = mock(ExecutableElement.class);
		when(methodElement.getAnnotation(eq(JsCacheResult.class))).thenReturn(null);
		doReturn(true).when(instance).considerateAllArgs(any(JsCacheResult.class));

		String result = instance.computeKeys(methodElement, arguments);

		assertThat(result).isEqualTo("a,b,c,d");
	}

	/**
	 * Test of computeKeys method, of class DataServiceVisitorJsBuilder.
	 *
	 * @throws java.io.IOException
	 */
	@Test
	public void testComputeKeysFromNoArg() throws IOException {
		System.out.println("computeKeys");
		List<String> arguments = new ArrayList();
		ExecutableElement methodElement = mock(ExecutableElement.class);

		String result = instance.computeKeys(methodElement, arguments);

		assertThat(result).isEqualTo("");
	}

	/**
	 * Test of computeKeys method, of class DataServiceVisitorJsBuilder.
	 *
	 * @throws java.io.IOException
	 */
	@Test
	public void testComputeKeysFromNullArg() throws IOException {
		System.out.println("computeKeys");
		ExecutableElement methodElement = mock(ExecutableElement.class);
		String result = instance.computeKeys(methodElement, null);
		assertThat(result).isEqualTo("");
	}

	/**
	 * Test of createReturnOcelotPromiseFactory method, of class DataServiceVisitorJsBuilder.
	 * @throws IOException 
	 */
	@Test
	public void testCreateReturnOcelotPromiseFactory() throws IOException {
		System.out.println("createReturnOcelotPromiseFactory");
		String expresult = ProcessorConstants.TAB3+"return promiseFactory.create(_ds,"+ProcessorConstants.SPACEOPTIONAL+"\"c4746bbdace1d5712da7b6fabe58fb9c_\" + JSON.stringify([KEYS]).md5(),"+ProcessorConstants.SPACEOPTIONAL+"\"METHODNAME\","+ProcessorConstants.SPACEOPTIONAL+"true,"+ProcessorConstants.SPACEOPTIONAL+"[ARGS]);"+ProcessorConstants.CR;
		StringWriter writer = new StringWriter();
		instance.createReturnOcelotPromiseFactory("CLSNAME", "METHODNAME", true, "ARGS", "KEYS", writer);
		String result = writer.toString();
		System.out.println(result);
		assertThat(result).isEqualTo(expresult);
	}
	
	/**
	 * Test of createMethodBody method, of class DataServiceVisitorJsBuilder.
	 *
	 * @throws java.io.IOException
	 */
//	@Test
	public void testCreateMethodBodyAllArg() throws IOException {
		System.out.println("createMethodBody");
		String classname = "packageName.ClassName";
		String methodname = "methodName";
		Name name = mock(Name.class);
		when(name.toString()).thenReturn(methodname);
		String classMethodHash = new KeyMaker().getMd5(classname + "." + methodname);

		JsCacheResult jcr = mock(JsCacheResult.class);
		when(jcr.keys()).thenReturn(new String[]{"*"});

		ExecutableElement methodElement = mock(ExecutableElement.class);
		when(methodElement.getSimpleName()).thenReturn(name);
		when(methodElement.getAnnotation(eq(JsCacheResult.class))).thenReturn(jcr);

		List<String> arguments = Arrays.asList("a", "b", "c", "d");

		Writer writer = WriterTest.getMockWriter();

		instance.createMethodBody(classname, methodElement, arguments, writer);

		ArgumentCaptor<String> captureAppend = ArgumentCaptor.forClass(String.class);
		verify(writer, times(22)).append(captureAppend.capture());
		List<String> appends = captureAppend.getAllValues();
		assertThat(appends.get(3)).isEqualTo(classMethodHash);
		assertThat(appends.get(7)).isEqualTo("a,b,c,d");
		assertThat(appends.get(13)).isEqualTo(methodname);
		assertThat(appends.get(16)).isEqualTo("\"a\",\"b\",\"c\",\"d\"");
		assertThat(appends.get(18)).isEqualTo("a,b,c,d");
	}

	@Test
	public void testConsiderateNotAllArgs() {
		boolean result = instance.considerateAllArgs(null);
		assertThat(result).isTrue();

		result = instance.considerateAllArgs(new JsCacheResultLiteral(new String[]{}));
		assertThat(result).isFalse();

		result = instance.considerateAllArgs(new JsCacheResultLiteral());
		assertThat(result).isFalse();

		result = instance.considerateAllArgs(new JsCacheResultLiteral("a"));
		assertThat(result).isFalse();

		result = instance.considerateAllArgs(new JsCacheResultLiteral("a", "b"));
		assertThat(result).isFalse();

		result = instance.considerateAllArgs(new JsCacheResultLiteral("*"));
		assertThat(result).isTrue();

		result = instance.considerateAllArgs(new JsCacheResultLiteral("*", "b"));
		assertThat(result).isTrue();

	}

	@Test
	public void testComputeArgumentsFromListAndDecorateWith() {
		List<String> list = Arrays.asList("a", "b", "c", "d");
		String result = instance.stringJoinAndDecorate(null, ",", null);
		assertThat(result).isEqualTo("");
		
		result = instance.stringJoinAndDecorate(list, ",", null);
		assertThat(result).isEqualTo("a,b,c,d");

		result = instance.stringJoinAndDecorate(list, " ", null);
		assertThat(result).isEqualTo("a b c d");

		result = instance.stringJoinAndDecorate(list, ",", new UnderscoreDecorator());
		assertThat(result).isEqualTo("_a_,_b_,_c_,_d_");
	}
	
	private static class UnderscoreDecorator implements StringDecorator {

		@Override
		public String decorate(String str) {
				return "_"+str+"_";
		}
	}
}
