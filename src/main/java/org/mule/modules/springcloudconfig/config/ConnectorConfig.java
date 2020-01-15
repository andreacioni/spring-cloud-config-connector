package org.mule.modules.springcloudconfig.config;

import org.mule.api.annotations.components.Configuration;
import org.mule.api.annotations.display.FriendlyName;
import org.mule.api.annotations.display.Password;
import org.mule.api.annotations.display.Placement;
import org.mule.api.annotations.display.Summary;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;
import org.mule.util.StringUtils;

import com.mulesoft.modules.configuration.properties.api.EncryptionAlgorithm;
import com.mulesoft.modules.configuration.properties.api.EncryptionMode;

@Configuration(friendlyName = "Spring Cloud Configuration")
public class ConnectorConfig {
	
	/**
	 * The base URL where the Spring Cloud Config API is hosted.
	 */
	@Configurable
	@Default("http://localhost:8888/")
	@Placement(order=1)
	@FriendlyName("URL")
	private String configServerBaseUrl;
	
	/**
	 * The name of the application whose properties will be read. If not specified, mule app name will be
	 * used.
	 */
	@Configurable
	@Optional
	@Placement(order=2)
	@FriendlyName("Application names")
	@Summary("Comma-separated list of application names used to search properties on configuration server. "
			+ "If a property is defined in one or more application the first that appears in this list is worth")
	private String applicationName;
	
	/**
	 * The profiles to take into consideration. This is a comma-separated list. If empty, this module
	 * should try to locate spring profiles.
	 */
	@Configurable
	@Optional
	@Placement(order=3)
	@Summary("Comma-separated list of profiles used to search properties on configuration server.")
	private String profiles;
	
	/**
	 * The tag for the configuration. Useful for versioning.
	 */
	@Configurable
	@Default("master")
	@Placement(order=3)
	@Summary("Label is the git-branch to checkout in configuration server")
	private String label;
	
	@Configurable
	@Optional
	@FriendlyName("Username")
	@Placement(group = "HTTP Basic Auth", tab = "Security")
	private String basicAuthUsername;		

	@Configurable
	@Optional
	@Password
	@FriendlyName("Password")
	@Placement(group = "HTTP Basic Auth", tab = "Security")
	private String basicAuthPassword;
	
	@Configurable
	@Optional
	@Password
	@FriendlyName("Key")
	@Placement(group = "Encrypted properties", tab = "Security")
	@Summary("The key used to decrypt encrypted properties")
	private String encryptedPropsPassword;
	
	@Configurable
	@Default("AES")
	@FriendlyName("Alghoritm")
	@Placement(group = "Encrypted properties", tab = "Security")
	@Summary("The alghoritm used to decrypt encrypted properties")
	private EncryptionAlgorithm encryptionAlghoritm;
	
	@Configurable
	@Default("CBC")
	@FriendlyName("Mode")
	@Placement(group = "Encrypted properties", tab = "Security")
	@Summary("The alghoritm mode used to decrypt encrypted properties")
	private EncryptionMode encryptionMode;
	
	public boolean isEnableEncryptedProps() {
		return !StringUtils.isEmpty(encryptedPropsPassword);
	}
	
	public boolean isEnableBasicAuth() {
		return !StringUtils.isEmpty(basicAuthUsername) && !StringUtils.isEmpty(basicAuthPassword);
	}

	public String getConfigServerBaseUrl() {
		return configServerBaseUrl;
	}

	public void setConfigServerBaseUrl(String configServerBaseUrl) {
		this.configServerBaseUrl = configServerBaseUrl;
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

	public String getBasicAuthUsername() {
		return basicAuthUsername;
	}

	public void setBasicAuthUsername(String basicAuthUsername) {
		this.basicAuthUsername = basicAuthUsername;
	}

	public String getBasicAuthPassword() {
		return basicAuthPassword;
	}

	public void setBasicAuthPassword(String basicAuthPassword) {
		this.basicAuthPassword = basicAuthPassword;
	}

	public String getEncryptedPropsPassword() {
		return encryptedPropsPassword;
	}

	public void setEncryptedPropsPassword(String encryptedPropsPassword) {
		this.encryptedPropsPassword = encryptedPropsPassword;
	}

	public EncryptionAlgorithm getEncryptionAlghoritm() {
		return encryptionAlghoritm;
	}

	public void setEncryptionAlghoritm(EncryptionAlgorithm encryptionAlghoritm) {
		this.encryptionAlghoritm = encryptionAlghoritm;
	}

	public EncryptionMode getEncryptionMode() {
		return encryptionMode;
	}

	public void setEncryptionMode(EncryptionMode encryptionMode) {
		this.encryptionMode = encryptionMode;
	}
	
}