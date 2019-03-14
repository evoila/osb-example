/**
 * 
 */
package de.evoila.cf.broker.service.custom;

import de.evoila.cf.broker.model.RouteBinding;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceBinding;
import de.evoila.cf.broker.model.ServiceInstanceBindingRequest;
import de.evoila.cf.broker.model.catalog.ServerAddress;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.model.credential.UsernamePasswordCredential;
import de.evoila.cf.broker.repository.*;
import de.evoila.cf.broker.service.AsyncBindingService;
import de.evoila.cf.broker.service.HAProxyService;
import de.evoila.cf.broker.service.impl.BindingServiceImpl;
import de.evoila.cf.broker.util.ServiceInstanceUtils;
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

	public ExampleBindingService(BindingRepository bindingRepository, ServiceDefinitionRepository serviceDefinitionRepository,
                                 ServiceInstanceRepository serviceInstanceRepository, RouteBindingRepository routeBindingRepository,
                                 HAProxyService haProxyService, JobRepository jobRepository,
                                 AsyncBindingService asyncBindingService, PlatformRepository platformRepository,
                                 CredentialStore credentialStore) {
        super(bindingRepository, serviceDefinitionRepository,
                serviceInstanceRepository, routeBindingRepository,
                haProxyService, jobRepository,
                asyncBindingService, platformRepository);
        this.credentialStore = credentialStore;
	}

	@Override
	protected Map<String, Object> createCredentials(String bindingId, ServiceInstanceBindingRequest serviceInstanceBindingRequest,
                                                    ServiceInstance serviceInstance, Plan plan, ServerAddress host) {
        String endpoint = ServiceInstanceUtils.connectionUrl(serviceInstance.getHosts());

        if (host != null)
            endpoint = host.getIp() + ":" + host.getPort();

        credentialStore.createUser(serviceInstance, bindingId);
        UsernamePasswordCredential usernamePasswordCredential = credentialStore.getUser(serviceInstance, bindingId);

		String dbURL = String.format("example://%s:%s@%s/%s", usernamePasswordCredential.getUsername(),
                usernamePasswordCredential.getPassword(), endpoint,
                new RandomString(10).nextString());

		Map<String, Object> credentials = new HashMap<>();
		credentials.put("uri", dbURL);
		
		return credentials;
	}

	@Override
	protected RouteBinding bindRoute(ServiceInstance serviceInstance, String route) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void unbindService(ServiceInstanceBinding binding, ServiceInstance serviceInstance, Plan plan) {
	    credentialStore.deleteCredentials(serviceInstance, binding.getId());
		log.info("Unbinding the Example Service...");
	}
}
