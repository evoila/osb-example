/**
 * 
 */
package de.evoila.cf.cpi.existing;

import de.evoila.cf.broker.bean.ExistingEndpointBean;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.Platform;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.service.custom.ExampleBackendService;
import org.springframework.beans.factory.annotation.Autowired;
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
	
	@Autowired
	private ExampleBackendService exampleDbService;

    @Autowired
    private ExistingEndpointBean existingEndpointBean;

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
