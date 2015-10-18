/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. 
 */
package org.ocelotds;

/**
 * Constants Class
 *
 * @author hhfrancois
 */
public interface Constants {

	String ALGORITHM = "MD5";
	String UTF_8 = "UTF-8";
	String JS = ".js";
	String HTML = ".htm";
	String SLASH = "/";
	String BACKSLASH_N = "\n";
	String LOCALE = "LOCALE";
	String SECURITY_CONTEXT = "SECURITYCONTEXT";
	String SESSION_BEANS = "SESSIONBEANS";
	String PRINCIPAL = "PRINCIPAL";
	String ANONYMOUS = "ANONYMOUS";

	String OCELOT = "ocelot";
	String OCELOT_CORE = OCELOT + "-core";
	String OCELOT_HTML = OCELOT + "-html";
	String OCELOT_MIN = OCELOT + "-min";
	String SLASH_OCELOT_JS = SLASH + OCELOT + JS;
	String SLASH_OCELOT_HTML = SLASH + OCELOT + HTML;

	String MINIFY_PARAMETER = "minify";
	String JSTYPE = "text/javascript;charset=UTF-8";
	String HTMLTYPE = "text/html;charset=UTF-8";
	String FALSE = "false";
	String TRUE = "true";

	String WSS = "wss";
	String WS = "ws";

	/**
	 * This string will be replaced by the contextPath in ocelot-core.js
	 */
	String CTXPATH = "%CTXPATH%";
	String PROTOCOL = "%WSS%";

	int DEFAULT_BUFFER_SIZE = 1024 * 4;

	interface Options {
		String SECURE = "ocelot.websocket.secure";
		String STACKTRACE_LENGTH = "ocelot.stacktrace.length";
	}

	interface Topic {

		String SUBSCRIBERS = "subscribers";
		String COLON = ":";
		String ALL = "ALL";
	}

	interface Message {

		String ID = "id";
		String TYPE = "type";
		String DATASERVICE = "ds";
		String OPERATION = "op";
		String ARGUMENTS = "args";
		String ARGUMENTNAMES = "argNames";
		String DEADLINE = "deadline";
		String RESPONSE = "response";
		String LANGUAGE = "language";
		String COUNTRY = "country";

		interface Fault {

			String MESSAGE = "message";
			String CLASSNAME = "classname";
			String STACKTRACE = "stacktrace";

		}
	}

	interface Resolver {

		String POJO = "pojo";
		String CDI = "cdi";
		String EJB = "ejb";
		String SPRING = "spring";
	}

	interface Cache {

		String CLEANCACHE_TOPIC = "ocelot-cleancache";
		String ALL = "ALL";
		String USE_ALL_ARGUMENTS = "*";
		String ARGS_NOT_CONSIDERATED = "-";
	}
	
	interface Provider {
		String JAVASCRIPT = "JS";
		String HTML = "HTML";
	}
	
	interface Container {
		String GLASSFISH = "GLASSFISH";
		String WILDFLY = "WILDFLY";
		String TOMCAT = "TOMCAT";
		String TOMEE = "TOMEE";
	}
	
	interface BeanManager {
		String BEANMANAGER_JEE = "java:comp/BeanManager";
		String BEANMANAGER_ALT = "java:comp/env/BeanManager";
	}
}
