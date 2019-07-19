/**
 * 
 */
package de.evoila.cf.broker.service.custom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Johannes Hiemer
 * This class contains a specific implementation for the access to the 
 * underlying service. For a database for example it contains the access
 * to the connection, the update and create commands etc.
 * 
 * This class should not have any dependencies to Spring or other large
 * Frameworks but instead work with the Drivers directly against the native
 * API.
 */
public class ExampleBackendRawService {

	protected Logger log = LoggerFactory.getLogger(getClass());

	private boolean initialized;
	
	public void createConnection() {
		this.initialized = true;
	}
	
	public boolean isConnected() {
		return this.initialized;
	}

	public void createDatabase(String databaseName) {
		log.info("Created an example service \""+databaseName+"\".");
	}

	public void deleteDatabase(String databaseName) {
		log.info("Deleted an example service \""+databaseName+"\".");
	}

	public void bind(String databaseName, String username) {
		log.info("Bound \""+username+"\" to example service \""+databaseName+"\".");
	}

	public void unbind(String databaseName, String username) {
		log.info("Unbound \""+username+"\" from example service \""+databaseName+"\".");
	}

}