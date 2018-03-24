/**
 * 
 */
package de.evoila.cf.broker.service.custom;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import de.evoila.cf.broker.model.*;
import de.evoila.cf.broker.util.RandomString;
import de.evoila.cf.broker.util.ServiceInstanceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.service.impl.BindingServiceImpl;

/**
 * @author Johannes Hiemer.
 *
 */
@Service
public class ExampleBindingService extends BindingServiceImpl {

	private Logger log = LoggerFactory.getLogger(getClass());

    RandomString randomString = new RandomString(10);

	@Override
	protected Map<String, Object> createCredentials(String bindingId, ServiceInstance serviceInstance,
			Plan plan, ServerAddress host) throws ServiceBrokerException {
        String endpoint = ServiceInstanceUtils.connectionUrl(serviceInstance.getHosts());

        if (host != null)
            endpoint = host.getIp() + ":" + host.getPort();

		String dbURL = String.format("example://%s:%s@%s/%s", randomString.nextString(),
                randomString.nextString(), endpoint,
                randomString.nextString());

		Map<String, Object> credentials = new HashMap<String, Object>();
		credentials.put("uri", dbURL);
		
		return credentials;
	}

	@Override
	protected void deleteBinding(ServiceInstanceBinding binding, ServiceInstance serviceInstance, Plan plan) throws ServiceBrokerException {
		log.info("Unbinding the Example Service...");
	}

	@Override
	public ServiceInstanceBinding getServiceInstanceBinding(String id) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected RouteBinding bindRoute(ServiceInstance serviceInstance, String route) {
		throw new UnsupportedOperationException();
	}

}
