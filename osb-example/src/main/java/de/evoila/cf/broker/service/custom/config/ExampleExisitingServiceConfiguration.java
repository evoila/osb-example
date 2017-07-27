/**
 * 
 */
package de.evoila.cf.broker.service.custom.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import de.evoila.cf.broker.service.custom.ExampleExistingServiceFactory;

/**
 * @author Johannes Hiemer.
 */
@Configuration
@EnableConfigurationProperties(value={ExampleExistingServiceFactory.class})
public class ExampleExisitingServiceConfiguration {
}