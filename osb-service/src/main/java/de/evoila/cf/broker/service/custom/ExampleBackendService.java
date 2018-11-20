/**
 * 
 */
package de.evoila.cf.broker.service.custom;

import de.evoila.cf.broker.model.catalog.ServerAddress;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Johannes Hiemer
 */
@Service
public class ExampleBackendService {
	
	private ExampleBackendRawService exampleBackendRawService;
	
	public ExampleBackendRawService createConnection(String username, String password, String database, List<ServerAddress> serverAddresses) {
		exampleBackendRawService = new ExampleBackendRawService();
		exampleBackendRawService.createConnection();
		
		return exampleBackendRawService;
	}

}