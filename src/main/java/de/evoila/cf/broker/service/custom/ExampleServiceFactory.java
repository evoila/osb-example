/**
 * 
 */
package de.evoila.cf.broker.service.custom;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.service.sample.impl.ExampleDbService;
import de.evoila.cf.cpi.existing.CustomExistingService;
import de.evoila.cf.cpi.existing.CustomExistingServiceConnection;
import de.evoila.cf.cpi.existing.ExistingServiceFactory;

/**
 * @author Johannes Hiemer
 *
 */
@Service
@ConditionalOnProperty(prefix="existing.endpoint", 
	name = {"hosts", "port",
			"database",
			"username", "password"
		}, havingValue="")
public class ExampleServiceFactory extends ExistingServiceFactory {
	
	@Override
	protected void createInstance(CustomExistingServiceConnection connection, String database) throws PlatformException {
		if(connection instanceof ExampleDbService)
			createInstance((ExampleDbService) connection, database);
	}
		
	@Override
	protected void deleteInstance(CustomExistingServiceConnection connection, String database) throws PlatformException {
		if(connection instanceof ExampleDbService)
			deleteInstance((ExampleDbService) connection, database);
	}

	protected void deleteInstance(ExampleDbService connection, String database)
			throws PlatformException {
		// Do nothing we just want to mock that stuff		
	}

	@Override
	protected CustomExistingService getCustomExistingService() {
		return null;
	}

	protected void createInstance(ExampleDbService connection, String database)
			throws PlatformException {
		// Do nothing we just want to mock that stuff
	}

}
