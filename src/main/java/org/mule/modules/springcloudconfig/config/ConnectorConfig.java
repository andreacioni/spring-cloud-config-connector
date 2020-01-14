package org.mule.modules.springcloudconfig.config;

import org.mule.api.annotations.components.Configuration;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;

@Configuration(friendlyName = "Spring Cloud Configuration")
public class ConnectorConfig {
	
	/**
	 * The base URL where the Spring Cloud Config API is hosted.
	 */
	@Configurable
	@Default("http://localhost:8888/")
	private String configServerBaseUrl;
	
	/**
	 * The name of the application whose properties will be read. If not specified, mule app name will be
	 * used.
	 */
	@Configurable
	@Optional
	private String applicationName;
	
	/**
	 * The profiles to take into consideration. This is a comma-separated list. If empty, this module
	 * should try to locate spring profiles.
	 */
	@Configurable
	@Optional
	private String profiles;
	
	/**
	 * The tag for the configuration. Useful for versioning.
	 */
	@Configurable
	@Default("master")
	private String label;
	
	@Configurable
	@Default("false")
	private boolean enableBasicAuth;
	
	@Configurable
	@Optional
	private String basicAuthUsername;		

	@Configurable
	@Optional
	private String basicAuthPassword;

	@Configurable
	@Default("false")
	private boolean enableEncryptedProps;
	
	@Configurable
	@Optional
	private String encryptedPropsPassword;
	
	public String getBasicAuthPassword() {
		return basicAuthPassword;
	}

	public void setBasicAuthPassword(String basicAuthPassword) {
		this.basicAuthPassword = basicAuthPassword;
	}
	
	public boolean isEnableEncryptedProps() {
		return enableEncryptedProps;
	}

	public void setEnableEncryptedProps(boolean enableEncryptedProps) {
		this.enableEncryptedProps = enableEncryptedProps;
	}

	public String getEncryptedPropsPassword() {
		return encryptedPropsPassword;
	}

	public void setEncryptedPropsPassword(String encryptedPropsPassword) {
		this.encryptedPropsPassword = encryptedPropsPassword;
	}

	public boolean isEnableBasicAuth() {
		return enableBasicAuth;
	}

	public void setEnableBasicAuth(boolean enableBasicAuth) {
		this.enableBasicAuth = enableBasicAuth;
	}
	
	public String getBasicAuthUsername() {
		return basicAuthUsername;
	}

	public void setBasicAuthUsername(String basicAuthUsername) {
		this.basicAuthUsername = basicAuthUsername;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getProfiles() {
		return profiles;
	}

	public void setProfiles(String profiles) {
		this.profiles = profiles;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getConfigServerBaseUrl() {
		return configServerBaseUrl;
	}

	public void setConfigServerBaseUrl(String configServerBaseUrl) {
		this.configServerBaseUrl = configServerBaseUrl;
	}

	@Override
	public String toString() {
		return "ConnectorConfig [configServerBaseUrl=" + configServerBaseUrl + ", applicationName=" + applicationName
				+ ", profiles=" + profiles + ", label=" + label + ", enableBasicAuth=" + enableBasicAuth
				+ ", basicAuthUsername=" + basicAuthUsername + ", basicAuthPassword=" + basicAuthPassword + "]";
	}	
	
}