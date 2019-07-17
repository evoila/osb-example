/**
 * 
 */
package de.evoila.cf.broker.service.custom;

import de.evoila.cf.broker.model.*;
import de.evoila.cf.broker.model.catalog.ServerAddress;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.model.credential.UsernamePasswordCredential;
import de.evoila.cf.broker.repository.*;
import de.evoila.cf.broker.service.AsyncBindingService;
import de.evoila.cf.broker.service.HAProxyService;
import de.evoila.cf.broker.service.impl.BindingServiceImpl;
import de.evoila.cf.broker.util.ServiceInstanceUtils;
import de.evoila.cf.cpi.existing.ExampleExistingServiceFactory;
import de.evoila.cf.security.credentials.CredentialStore;
import de.evoila.cf.security.utils.RandomString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Johannes Hiemer.
 */
@Service
public class ExampleBindingService extends BindingServiceImpl {

	private Logger log = LoggerFactory.getLogger(getClass());

	private CredentialStore credentialStore;

	private ExampleExistingServiceFactory exampleExistingServiceFactory;

	public ExampleBindingService(BindingRepository bindingRepository, ServiceDefinitionRepository serviceDefinitionRepository,
                                 ServiceInstanceRepository serviceInstanceRepository, RouteBindingRepository routeBindingRepository,
                                 HAProxyService haProxyService, JobRepository jobRepository,
                                 AsyncBindingService asyncBindingService, PlatformRepository platformRepository,
                                 CredentialStore credentialStore, ExampleExistingServiceFactory exampleExistingServiceFactory) {
        super(bindingRepository, serviceDefinitionRepository,
                serviceInstanceRepository, routeBindingRepository,
                haProxyService, jobRepository,
                asyncBindingService, platformRepository);
        this.credentialStore = credentialStore;
        this.exampleExistingServiceFactory = exampleExistingServiceFactory;
	}

	@Override
	protected Map<String, Object> createCredentials(String bindingId, ServiceInstanceBindingRequest serviceInstanceBindingRequest,
                                                    ServiceInstance serviceInstance, Plan plan, ServerAddress host) {
        String endpoint = ServiceInstanceUtils.connectionUrl(serviceInstance.getHosts());

        if (host != null)
            endpoint = host.getIp() + ":" + host.getPort();

        credentialStore.createUser(serviceInstance, bindingId);
        UsernamePasswordCredential usernamePasswordCredential = credentialStore.getUser(serviceInstance, bindingId);
		String databaseName = serviceInstance.getId();

		String dbURL = String.format("example://%s:%s@%s/%s", usernamePasswordCredential.getUsername(),
                usernamePasswordCredential.getPassword(), endpoint,
                databaseName);

		Map<String, Object> credentials = new HashMap<>();
		credentials.put("uri", dbURL);

		if (plan.getPlatform() == Platform.EXISTING_SERVICE) {
			ExampleBackendRawService backendRawService = exampleExistingServiceFactory.getConnection(plan);
			if (backendRawService != null) {
				backendRawService.bind(databaseName, usernamePasswordCredential.getUsername());
			}
		}

		return credentials;
	}

	@Override
	protected RouteBinding bindRoute(ServiceInstance serviceInstance, String route) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void unbindService(ServiceInstanceBinding binding, ServiceInstance serviceInstance, Plan plan) {
		log.info("Unbinding the Example Service...");

		UsernamePasswordCredential bindingCredential = credentialStore.getUser(serviceInstance, binding.getId());
		if (plan.getPlatform() == Platform.EXISTING_SERVICE) {
			ExampleBackendRawService backendRawService = exampleExistingServiceFactory.getConnection(plan);
			if (backendRawService != null) {
				backendRawService.unbind(serviceInstance.getId(), bindingCredential.getUsername());
			}
		}

		credentialStore.deleteCredentials(serviceInstance, binding.getId());
	}
}
