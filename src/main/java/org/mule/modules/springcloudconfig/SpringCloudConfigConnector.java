package org.mule.modules.springcloudconfig;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.mule.api.MuleContext;
import org.mule.api.annotations.Config;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;
import org.mule.api.registry.RegistrationException;
import org.mule.encryption.Encrypter;
import org.mule.encryption.exception.MuleEncryptionException;
import org.mule.modules.springcloudconfig.config.ConnectorConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PreferencesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.mulesoft.modules.configuration.properties.api.EncryptionAlgorithm;
import com.mulesoft.modules.configuration.properties.api.EncryptionMode;

@Connector(name="spring-cloud-config", friendlyName="Spring Cloud Config")
public class SpringCloudConfigConnector extends PreferencesPlaceholderConfigurer {

	private static final String NAME_PROPERTY = "name";

	private static final String PROPERTY_SOURCES_PROPERTY = "propertySources";

	private static final String SOURCE_PROPERTY = "source";

	private static final Logger logger = LoggerFactory.getLogger(SpringCloudConfigConnector.class);
	
	@Inject
	private MuleContext context;
	
    @Config
    ConnectorConfig config;
    
    private Properties props;
    
    private Encrypter encrypter;
    
    public ConnectorConfig getConfig() {
        return config;
    }

    public void setConfig(ConnectorConfig config) {
        this.config = config;
    }
    
    @PostConstruct
    public void setup() throws Exception {
    	
    	props = new Properties();
    	
    	logger.debug("Setting up connector with properties: " + config.toString());
    	
    	Client client = ClientBuilder.newClient();
    	
    	if(config.isEnableBasicAuth()) {
    		logger.debug("Basic auth enabled");
        	client.register(new Authenticator(config.getBasicAuthUsername(), config.getBasicAuthPassword()));
    	}
    	
    	client.register(JacksonJsonProvider.class);
    	WebTarget target = client.target(config.getConfigServerBaseUrl()).path(resolveApplicationName()).path(resolveProfiles()).path(config.getLabel());
    	Map<String, Object> result = target.request().accept(MediaType.APPLICATION_JSON).get(Map.class); 
    	
    	logger.debug("Got settings from cloud config server: " + result.toString());
    	
    	//build the unified set of properties, for this we need to go through the property sources array in inverse order.
    	List<Map> sources = (List<Map>) result.get(PROPERTY_SOURCES_PROPERTY);
    	
    	logger.debug("Property sources are: " + sources);
    	
    	if(config.isEnableEncryptedProps()) {
    		logger.debug("Encrypted properties flag set");
    		encrypter = createEncrypter(config.getEncryptionAlghoritm().toString(), config.getEncryptionMode().toString(), config.getEncryptedPropsPassword());
    	}
    	
    	for(int i = sources.size() - 1 ; i >= 0 ; i--) {
    		
    		Map<String, String> source = (Map<String, String>) sources.get(i).get(SOURCE_PROPERTY);
    		
    		String name = (String) sources.get(i).get(NAME_PROPERTY);
    		
    		if (name != null && logger.isDebugEnabled()) {
    			logger.debug("Reading properties from source: " + name);
    		}
    		
    		for(Entry<String, String> entry : source.entrySet()) {
    			
    			if (logger.isDebugEnabled()) {
    				logger.debug("Read property with key: " + entry.getKey() + " from source.");
    			}
    			
    			if (config.isEnableEncryptedProps() && 
    					entry.getValue() != null && 
    					entry.getValue().startsWith("![") &&
    					entry.getValue().endsWith("]")) {
    				props.put(entry.getKey(), decryptValue(entry.getValue().substring(2, entry.getValue().length()-1)));
        		} else {
        			props.put(entry.getKey(), entry.getValue());
        		}
    		}
    		
    	}
    	
    }

	@Override
    protected String resolvePlaceholder(String placeholder, Properties p) {
    	logger.debug("Call to resolve placeholder: " + placeholder);
    	
    	String value = this.props.getProperty(placeholder, null);
    	
    	if (value != null) {
    		logger.debug("Found key in config server");
    		return value;
    	}
    	
    	logger.debug("Key not found in config server, resolving in the traditional way");
    	return super.resolvePlaceholder(placeholder, p);
    }
    
    /**
     * Return all the configuration read from the Spring Cloud Config Server, 
     * useful for debug or auditing purposes. Please note that sensitive information
     * will not be filtered.
     * @return
     */
    @Processor
    public Map<String, Object> dumpConfiguration() {
    	return new HashMap(props);
    }
    
    private String resolveProfiles() throws RegistrationException {
    	String profiles = config.getProfiles();
    	
    	Environment springEnv = context.getRegistry().lookupObject(Environment.class);
    	
    	if (logger.isDebugEnabled()) logger.debug("Configured profiles: " + profiles);
    	
    	if (StringUtils.isEmpty(profiles) && springEnv != null) {
    		logger.debug("Profiles not defined, trying to resolve them from spring environment...");
    		profiles = StringUtils.join(springEnv.getActiveProfiles(), ",");
    		
    		if (logger.isDebugEnabled()) logger.debug("Found profiles: " + profiles);
    		
    	}
    	
    	if (StringUtils.isEmpty(profiles)) {
    		logger.error("No profiles could be detected");
    		throw new IllegalArgumentException("No profiles could be detected for Spring Cloud Configuration");
    	}
    	
    	if (logger.isDebugEnabled()) logger.debug("Resolved profiles: " + profiles);
    	
    	return profiles;
    }
    
    
    private String resolveApplicationName() {
    	
    	String app = config.getApplicationName();
    	
    	if (logger.isDebugEnabled()) logger.debug("Found app name: " + app);
    	
    	if (StringUtils.isEmpty(app)) {
    		app = context.getConfiguration().getId();
    		
    		if (logger.isDebugEnabled()) logger.debug("Detected app name: " + app);
    	}
    	
    	if (StringUtils.isEmpty(app)) {
    		logger.error("App name could not be detected");
    		throw new IllegalArgumentException("Could not detect app name from context for Spring Cloud Config");
    	}
    	
    	
    	if (logger.isDebugEnabled()) logger.debug("Detected app name: " + app);
    	
    	return app;
    	
    }
    
    private String decryptValue(String encValue) throws MuleEncryptionException {
    	return new String(encrypter.decrypt(Base64.getDecoder().decode(encValue)));
    }
    
    private static Encrypter createEncrypter(final String algorithm, final String mode, final String key) {
        return EncryptionAlgorithm.valueOf(algorithm).getBuilder().forKey(key).using(EncryptionMode.valueOf(mode)).build();
    }

	public void setContext(MuleContext context) {
		this.context = context;
	}
    
}