/**
 * 
 */
package de.evoila.cf.broker.service.sample;

import java.util.List;

import de.evoila.cf.cpi.existing.CustomExistingService;
import de.evoila.cf.cpi.existing.CustomExistingServiceConnection;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.service.sample.raw.ExampleBackendRawService;

/**
 * @author Johannes Hiemer
 */
@Service
public class ExampleBackendService implements CustomExistingService {
	
	private ExampleBackendRawService exampleBackendRawService;
	
	@Override
	public CustomExistingServiceConnection connection(List<String> hosts, int port, String database, String username,
                                                      String password) throws Exception {
		exampleBackendRawService = new ExampleBackendRawService();
		exampleBackendRawService.createConnection();
		
		return exampleBackendRawService;
	}

	public void bindRoleToDatabaseWithPassword(ExampleBackendRawService connection, String database,
			String username, String password) throws Exception {
		exampleBackendRawService.bind();
	}

	@Override
	public void bindRoleToInstanceWithPassword(CustomExistingServiceConnection connection, String database,
			String username, String password) throws Exception {
		if(connection instanceof ExampleBackendRawService)
			this.bindRoleToDatabaseWithPassword((ExampleBackendRawService) connection, database, username, password);		
	}
	
	
}