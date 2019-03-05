/**
 * 
 */
package de.evoila.cf.cpi.existing;

import de.evoila.cf.broker.bean.ExistingEndpointBean;
import de.evoila.cf.broker.model.Platform;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.repository.PlatformRepository;
import de.evoila.cf.broker.service.availability.ServicePortAvailabilityVerifier;
import de.evoila.cf.broker.service.custom.ExampleBackendService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Johannes Hiemer
 *
 */
@Service
@EnableConfigurationProperties
@ConditionalOnBean(ExistingEndpointBean.class)
public class ExampleExistingServiceFactory extends ExistingServiceFactory {

	private ExampleBackendService exampleBackendService;

    private ExistingEndpointBean existingEndpointBean;

    public ExampleExistingServiceFactory(PlatformRepository platformRepository, ServicePortAvailabilityVerifier portAvailabilityVerifier,
                                         ExistingEndpointBean existingEndpointBean, ExampleBackendService exampleBackendService) {
        super(platformRepository, portAvailabilityVerifier, existingEndpointBean);
        this.exampleBackendService = exampleBackendService;
        this.existingEndpointBean = existingEndpointBean;
    }

    @Override
    public ServiceInstance getInstance(ServiceInstance serviceInstance, Plan plan) {
        return serviceInstance;
    }

    @Override
    public ServiceInstance createInstance(ServiceInstance serviceInstance, Plan plan, Map<String, Object> customParameters) {
        createDatabase(serviceInstance, plan);

        return serviceInstance;
    }

    @Override
    public void deleteInstance(ServiceInstance serviceInstance, Plan plan) {
        deleteDatabase(serviceInstance, plan);
    }

    private void deleteDatabase(ServiceInstance serviceInstance, Plan plan) {
        log.info("Deleting the Example Service...");
    }

    private void createDatabase(ServiceInstance serviceInstance, Plan plan) {
        log.info("Creating the Example Service...");
    }

    private ExampleBackendService connection(ServiceInstance serviceInstance, Plan plan) {
        ExampleBackendService jdbcService = new ExampleBackendService();

        if (plan.getPlatform() == Platform.EXISTING_SERVICE)
            jdbcService.createConnection(existingEndpointBean.getUsername(), existingEndpointBean.getPassword(),
                    existingEndpointBean.getDatabase(), existingEndpointBean.getHosts());

        return jdbcService;
    }
}
