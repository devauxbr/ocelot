/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. 
 */
package fr.hhdev.ocelot.processors;

import fr.hhdev.ocelot.annotations.JsCacheResult;
import fr.hhdev.ocelot.annotations.TransientDataService;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;

/**
 * Visitor of class annoted fr.hhdev.ocelot.annotations.DataService<br>
 * Generate javascript classes
 *
 * @author hhfrancois
 */
public class DataServiceVisitor1 implements ElementVisitor<String, Writer> {

	protected ProcessingEnvironment environment;

	public DataServiceVisitor1(ProcessingEnvironment environment) {
		this.environment = environment;
	}

	@Override
	public String visitType(TypeElement typeElement, Writer writer) {
		try {
			createClassComment(typeElement, writer);
			writer.append("		\n");
			writer.append("		writer.append(\"function \").append(\""+typeElement.getSimpleName()+"\").append(\"() {\\n\");\n");
			writer.append("		writer.append(\"\\tthis.ds = \\\"\").append(\""+typeElement.getQualifiedName().toString()+"\").append(\"\\\";\\n\");\n");
			List<ExecutableElement> methodElements = ElementFilter.methodsIn(typeElement.getEnclosedElements());
			for (ExecutableElement methodElement : methodElements) {
				if (isConsiderateMethod(methodElement)) {
					String methodName = methodElement.getSimpleName().toString();
					List<String> argumentsType = getArgumentsType(methodElement);
					List<String> arguments = getArguments(methodElement);
					TypeMirror returnType = methodElement.getReturnType();
					writer.append("		writer.append(\"\\n\");\n");
					createMethodComment(methodElement, arguments, returnType, writer);

					writer.append("		writer.append(\"\\tthis.\").append(\""+methodName+"\").append(\" = function (\");\n");
					if (arguments.size() != argumentsType.size()) {
						environment.getMessager().printMessage(Diagnostic.Kind.ERROR, (new StringBuilder()).append("Cannot Create service : ").append(typeElement.getSimpleName()).append(" cause method ").append(methodElement.getSimpleName()).append(" arguments inconsistent - argNames : ").append(arguments.size()).append(" / args : ").append(argumentsType.size()).toString(), typeElement);
						return null;
					}
					int i = 0;
					while (i < argumentsType.size()) {
						writer.append("		writer.append(\""+(String) arguments.get(i)+"\");\n");
						if ((++i) < arguments.size()) {
							writer.append("		writer.append(\", \");\n");
						}
					}
					writer.append("		writer.append(\") {\\n\");\n");

					createMethodBody(methodElement, arguments.iterator(), writer);

					writer.append("		writer.append(\"\\t};\\n\");\n");
				}
			}
			writer.append("		writer.append(\"}\\n\");\n");

		} catch (IOException ex) {
		}
		return null;
	}

	/**
	 * Crée un commentaire pour la classe
	 *
	 * @param typeElement
	 * @param writer
	 */
	protected void createClassComment(TypeElement typeElement, Writer writer) {
		try {
			String comment = environment.getElementUtils().getDocComment(typeElement);
			writer.append("		writer.write(\"\\t/**\\n\");\n");
			if (comment == null) {
				List<? extends TypeMirror> interfaces = typeElement.getInterfaces();
				for (TypeMirror typeMirror : interfaces) {
					TypeElement element = (TypeElement) environment.getTypeUtils().asElement(typeMirror);
					comment = environment.getElementUtils().getDocComment(element);
				}
			}
			if (comment != null) {
				String[] commentLines = comment.split("\n");
				for (String commentLine : commentLines) {
					writer.append("		writer.append(\"\\t *\").append(\""+commentLine+"\").append(\"\\n\");\n");
				}
			}
			writer.append("		writer.append(\"\\t */\\n\");\n");
		} catch (IOException ioe) {
		}
	}

	/**
	 * Retourne true si la methode doit etre traitee
	 *
	 * @param methodElement
	 * @return
	 */
	public boolean isConsiderateMethod(ExecutableElement methodElement) {
		// Si la méthode est annotée transient
		List<? extends AnnotationMirror> annotationMirrors = methodElement.getAnnotationMirrors();
		for (AnnotationMirror annotationMirror : annotationMirrors) {
			if (annotationMirror.getAnnotationType().toString().equals(TransientDataService.class.getName())) {
				return false;
			}
		}

		// Si la méthode est statique ou non publique
		if (!methodElement.getModifiers().contains(Modifier.PUBLIC) || methodElement.getModifiers().contains(Modifier.STATIC)) {
			return false;
		}
		// Si ce n'est pas une méthode de Object
		TypeElement objectElement = environment.getElementUtils().getTypeElement(Object.class.getName());
		return !objectElement.getEnclosedElements().contains(methodElement);
	}

	/**
	 * Retourne la liste ordonnee du type des arguments de la methode
	 *
	 * @param methodElement
	 * @return
	 */
	public List<String> getArgumentsType(ExecutableElement methodElement) {
		ExecutableType methodType = (ExecutableType) methodElement.asType();
		List<String> argumentsType = new ArrayList<>();
		for (TypeMirror argumentType : methodType.getParameterTypes()) {
			argumentsType.add(argumentType.toString());
		}
		return argumentsType;
	}

	/**
	 * Retourne la liste ordonnee du nom des arguments de la methode
	 *
	 * @param methodElement
	 * @return
	 */
	public List<String> getArguments(ExecutableElement methodElement) {
		List<String> arguments = new ArrayList<>();
		for (VariableElement variableElement : methodElement.getParameters()) {
			arguments.add(variableElement.toString());
		}
		return arguments;
	}

	/**
	 * Creer la javadoc de la methode en javascript
	 *
	 * @param methodElement
	 * @param argumentsName
	 * @param returnType
	 * @param writer
	 */
	protected void createMethodComment(ExecutableElement methodElement, List<String> argumentsName, TypeMirror returnType, Writer writer) {
		try {
			String methodComment = environment.getElementUtils().getDocComment(methodElement);
			writer.append("		writer.write(\"\\t/**\\n\");\n");
			// Le commentaire de la javadoc
			if (methodComment != null) {
				methodComment = methodComment.split("@")[0];
				int lastIndexOf = methodComment.lastIndexOf("\n");
				if (lastIndexOf >= 0) {
					methodComment = methodComment.substring(0, lastIndexOf);
				}
				String[] commentLines = methodComment.split("\n");
				for (String commentLine : commentLines) {
					writer.append("		writer.append(\"\\t *\").append("+commentLine+").append(\"\\n\");\n");
				}
			}
			// La liste des arguments de la javadoc
			for (String argumentName : argumentsName) {
				writer.append("		writer.append(\"\\t * @param \").append(\""+argumentName+"\").append(\"\\n\");\n");
			}
			// Si la methode retourne ou non quelque chose
			if (!returnType.toString().equals("void")) {
				writer.append("		writer.append(\"\\t * @return \").append(\""+returnType.toString()+"\").append(\"\\n\");\n");
			}
			writer.append("		writer.append(\"\\t */\\n\");\n");
		} catch (IOException ex) {
		}
	}

	/**
	 * Cree le corps de la methode
	 *
	 * @param methodElement
	 * @param arguments
	 * @param writer
	 */
	protected void createMethodBody(ExecutableElement methodElement, Iterator<String> arguments, Writer writer) {
		try {
			writer.append("		writer.append(\"\\t\\tvar op = \\\"\").append(\""+methodElement.getSimpleName()+"\").append(\"\\\";\\n\");\n");
			StringBuilder args = new StringBuilder("");
			StringBuilder keys = new StringBuilder("[");
			if (arguments != null && arguments.hasNext()) {
				JsCacheResult jcr = methodElement.getAnnotation(JsCacheResult.class);
				KeySelector ks = new KeySelector("**");
				if (jcr != null) {
					ks = new KeySelector(jcr.keys());
				}
				while (arguments.hasNext()) {
					String arg = arguments.next();
					keys.append(ks.next(arg));
					args.append(arg);
					if (arguments.hasNext()) {
						args.append(", ");
						keys.append(", ");
					}
				}
			}
			keys.append("]");
			writer.append("		writer.append(\"\\t\\tvar id = (this.ds + \\\".\\\" + op + \\\"(\\\" + JSON.stringify(\").append(\""+keys.toString()+"\").append(\") + \\\")\\\").hash32Code();\\n\");\n");
			writer.append("		writer.append(\"\\t\\treturn getOcelotToken.call(this, id, op, [\").append(\""+args.toString()+"\").append(\"]);\\n\");\n");
		} catch (IOException ex) {
		}
	}

	/**
	 * Classe permettant de retoruner le l'argument et son selecteur
	 */
	private class KeySelector {

		private final Iterator<String> keys;
		private String lastKey = "**";

		public KeySelector(String keys) {
			if (keys == null) {
				keys = "**";
			}
			this.keys = Arrays.asList(keys.split(",")).iterator();
			if (!this.keys.hasNext()) {
				lastKey = "";
			}
		}

		public String next(String arg) {
			String current;
			if (keys.hasNext()) {
				current = keys.next().trim();
				lastKey = current;
			} else {
				if (!lastKey.equals("**")) {
					return "null";
				} else {
					return arg;
				}
			}
			switch (current) {
				case "**":
				case "*":
					return arg;
				case "-":
					return "null";
				default:
					return "(" + arg + ")?" + arg + "." + current + ":null";
			}
		}
	}

	@Override
	public String visit(Element e, Writer p) {
		return null;
	}

	@Override
	public String visit(Element e) {
		return null;
	}

	@Override
	public String visitPackage(PackageElement e, Writer p) {
		return null;
	}

	@Override
	public String visitVariable(VariableElement e, Writer p) {
		return null;
	}

	@Override
	public String visitExecutable(ExecutableElement e, Writer p) {
		return null;
	}

	@Override
	public String visitTypeParameter(TypeParameterElement e, Writer p) {
		return null;
	}

	@Override
	public String visitUnknown(Element e, Writer p) {
		return null;
	}
}