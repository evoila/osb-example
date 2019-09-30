/**
 * 
 */
package de.evoila.cf.cpi.existing;

import de.evoila.cf.broker.bean.ExistingEndpointBean;
import de.evoila.cf.broker.model.Platform;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.model.credential.UsernamePasswordCredential;
import de.evoila.cf.broker.repository.PlatformRepository;
import de.evoila.cf.broker.service.availability.ServicePortAvailabilityVerifier;
import de.evoila.cf.broker.service.custom.ExampleBackendRawService;
import de.evoila.cf.broker.service.custom.ExampleBackendService;
import de.evoila.cf.security.credentials.CredentialStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Johannes Hiemer.
 */
@Service
@EnableConfigurationProperties
@ConditionalOnBean(ExistingEndpointBean.class)
public class ExampleExistingServiceFactory extends ExistingServiceFactory {

	private ExampleBackendService exampleBackendService;

    private ExistingEndpointBean existingEndpointBean;

    private CredentialStore credentialStore;

    private static String ROOT_USER = "root_user";

    public ExampleExistingServiceFactory(PlatformRepository platformRepository, ServicePortAvailabilityVerifier portAvailabilityVerifier,
                                         ExistingEndpointBean existingEndpointBean, ExampleBackendService exampleBackendService,
                                         CredentialStore credentialStore) {
        super(platformRepository, portAvailabilityVerifier, existingEndpointBean);
        this.exampleBackendService = exampleBackendService;
        this.existingEndpointBean = existingEndpointBean;
        this.credentialStore = credentialStore;
    }

    @Override
    public ServiceInstance getInstance(ServiceInstance serviceInstance, Plan plan) {
        return serviceInstance;
    }

    @Override
    public ServiceInstance createInstance(ServiceInstance serviceInstance, Plan plan, Map<String, Object> customParameters) {
        UsernamePasswordCredential credentials = credentialStore.createUser(serviceInstance, ROOT_USER);
        createDatabase(serviceInstance, plan);

        return serviceInstance;
    }

    @Override
    public ServiceInstance updateInstance(ServiceInstance serviceInstance, Plan plan, Map<String, Object> customParameters) {
        if (serviceInstance.getPlanId().equals(plan.getId())) {
            serviceInstance.setParameters(customParameters);
        } else {
            serviceInstance.setPlanId(plan.getId());
        }

        return serviceInstance;
    }

    @Override
    public void deleteInstance(ServiceInstance serviceInstance, Plan plan) {
        UsernamePasswordCredential credential = credentialStore.getUser(serviceInstance, ROOT_USER);
        deleteDatabase(serviceInstance, plan);
        credentialStore.deleteCredentials(serviceInstance, ROOT_USER);
    }

    private void deleteDatabase(ServiceInstance serviceInstance, Plan plan) {
        log.info("Deleting the Example Service...");
        ExampleBackendRawService backendRawService = getConnection(plan);
        if (backendRawService != null) {
            log.info("Example Service connection status: "+ (backendRawService.isConnected() ? "" : "not ") + "connected");
            backendRawService.deleteDatabase(serviceInstance.getId());
        }
    }

    private void createDatabase(ServiceInstance serviceInstance, Plan plan) {
        log.info("Creating the Example Service...");
        ExampleBackendRawService backendRawService = getConnection(plan);
        if (backendRawService != null) {
            log.info("Example Service connection status: "+ (backendRawService.isConnected() ? "" : "not ") + "connected");
            backendRawService.createDatabase(serviceInstance.getId());
        }
    }

    public ExampleBackendRawService getConnection(Plan plan) {
        ExampleBackendRawService backendRawService = null;
        if (plan.getPlatform() == Platform.EXISTING_SERVICE)
            backendRawService = exampleBackendService.createConnection(existingEndpointBean.getUsername(),
                    existingEndpointBean.getPassword(),null, existingEndpointBean.getHosts());
        return backendRawService;
    }
}
