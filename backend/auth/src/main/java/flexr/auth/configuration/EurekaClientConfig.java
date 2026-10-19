package flexr.auth.configuration;

import com.netflix.discovery.shared.transport.jersey.TransportClientFactories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.netflix.eureka.http.EurekaClientHttpRequestFactorySupplier;
import org.springframework.cloud.netflix.eureka.http.RestTemplateDiscoveryClientOptionalArgs;
import org.springframework.cloud.netflix.eureka.http.RestTemplateTransportClientFactories;

@Configuration
public class EurekaClientConfig {

    @Bean
    public RestTemplateDiscoveryClientOptionalArgs discoveryClientOptionalArgs(
            EurekaClientHttpRequestFactorySupplier supplier
    ) {
        return new RestTemplateDiscoveryClientOptionalArgs(supplier);
    }

    @Bean
    public TransportClientFactories transportClientFactories(
            RestTemplateDiscoveryClientOptionalArgs optionalArgs
    ) {
        return new RestTemplateTransportClientFactories(optionalArgs);
    }
}
