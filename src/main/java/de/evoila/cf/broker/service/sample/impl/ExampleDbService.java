/**
 * 
 */
package de.evoila.cf.broker.service.sample.impl;

import de.evoila.cf.cpi.existing.CustomExistingServiceConnection;

/**
 * @author Johannes Hiemer
 */
public class ExampleDbService implements CustomExistingServiceConnection {
	
	public boolean createConnection() {
		return true;
	}
	
	public boolean isConnected() {
		return true;
	}
}