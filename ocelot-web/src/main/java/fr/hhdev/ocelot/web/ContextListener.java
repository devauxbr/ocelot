/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package fr.hhdev.ocelot.web;

import fr.hhdev.ocelot.Constants;
import fr.hhdev.ocelot.IServicesProvider;
import fr.hhdev.ocelot.OcelotConfiguration;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Web application lifecycle listener.
 *
 * @author hhfrancois
 */
@WebListener
public class ContextListener implements ServletContextListener {

	private final static Logger logger = LoggerFactory.getLogger(ContextListener.class);

	@Inject
	@Any
	Instance<IServicesProvider> servicesProviders;
	
	@Inject
	OcelotConfiguration configuration;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		logger.trace("Context initialisation...");
		ServletContext sc = sce.getServletContext();
		boolean minify = Boolean.parseBoolean(sc.getInitParameter("ocelot.minifyjs"));
		logger.trace("Read minifyjs option in web.xml '{}' = {}.", Constants.Options.MINIFYJS, minify);
		String stacktrace = sc.getInitParameter(Constants.Options.STACKTRACE);
		if(stacktrace==null) {
			stacktrace = "0";
		}
		int stacktracedeep = Integer.parseInt(stacktrace);
		logger.trace("Read stacktracedeep option in web.xml '{}' = {}.", Constants.Options.STACKTRACE, stacktracedeep);
		configuration.setStacktracedeep(stacktracedeep);
		try {
			String filename = createOcelotServicesJsFile(minify);
			logger.trace("Generate temp file '{}' minified {}.", filename, minify);
			sc.setInitParameter(Constants.OCELOT_SERVICES_JS, filename);
		} catch (IOException ex) {
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		String filename = sce.getServletContext().getInitParameter(Constants.OCELOT_SERVICES_JS);
		File file = new File(filename);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * Create ocelot-services.js from the contentation of all services available from all modules
	 * @param minify
	 * @return
	 * @throws IOException 
	 */
	private String createOcelotServicesJsFile(boolean minify) throws IOException {
		File file = File.createTempFile(Constants.OCELOT_SERVICES, Constants.JS);
		try (FileOutputStream out = new FileOutputStream(file)) {
			createLicenceComment(out);
			for (IServicesProvider servicesProvider : servicesProviders) {
				logger.trace("Find javascript services provider : '{}'", servicesProvider.getClass().getName());
				servicesProvider.streamJavascriptServices(out, minify);
			}
		}
		return file.getAbsolutePath();
	}

	/**
	 * Add MPL 2.0 License
	 *
	 * @param out
	 */
	private void createLicenceComment(OutputStream out) {
		try {
			out.write("/* This Source Code Form is subject to the terms of the Mozilla Public\n".getBytes());
			out.write(" * License, v. 2.0. If a copy of the MPL was not distributed with this\n".getBytes());
			out.write(" * file, You can obtain one at http://mozilla.org/MPL/2.0/.\n".getBytes());
			out.write(" * Classes generated by Ocelot Framework.\n".getBytes());
			out.write(" */\n".getBytes());
		} catch (IOException ioe) {
		}
	}

}
