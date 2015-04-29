/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. 
 */
package fr.hhdev.ocelot.spring;

import fr.hhdev.ocelot.spi.DataServiceException;
import fr.hhdev.ocelot.spi.DataServiceResolverId;
import fr.hhdev.ocelot.spi.DataServiceResolver;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 * Resolver of SPRING
 *
 * @author hhfrancois
 */
@DataServiceResolverId("SPRING")
public class SpringResolver implements DataServiceResolver {

	private static final Logger logger = LoggerFactory.getLogger(SpringResolver.class);

	private ApplicationContext applicationContext;

	@Override
	public Object resolveDataService(String dataService) throws DataServiceException {
		try {
			Class<?> aClass = Class.forName(dataService);
			return resolveDataService(aClass);
		} catch (ClassNotFoundException ex) {
			throw new DataServiceException(dataService, ex);
		}
	}

	@Override
	public <T> T resolveDataService(Class<T> clazz) throws DataServiceException {
		Map<String, ?> beansOfType = applicationContext.getBeansOfType(clazz);
		if (beansOfType == null || beansOfType.isEmpty()) {
			throw new DataServiceException("Unable to find any Spring bean of type : " + clazz.getName());
		}
		if (beansOfType.size() > 1) {
			throw new DataServiceException("Multiple (" + beansOfType.size() + ") Spring beans of type : '" + clazz.getName() + "' founded. Unable to choose one.");
		}
		return clazz.cast(beansOfType.values().iterator().next());
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}