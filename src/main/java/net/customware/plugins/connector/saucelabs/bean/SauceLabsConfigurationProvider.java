package net.customware.plugins.connector.saucelabs.bean;

import net.customware.plugins.connector.core.integration.config.ConnectorConfigurationProvider;
import net.customware.plugins.connector.core.integration.config.field.ConfigurationField;
import net.customware.plugins.connector.core.integration.config.field.SingleTextValueConfigurationField;

import java.util.Map;

public class SauceLabsConfigurationProvider implements ConnectorConfigurationProvider {

    protected static final String SAUCELABS = "saucelabs";

    public static final String FIELD_USERNAME = SAUCELABS + ".username";
    public static final String FIELD_TOKEN = SAUCELABS + ".token";


    public ConfigurationField[] getAvailableAttributes() {
        return new ConfigurationField[]{
                new SingleTextValueConfigurationField(FIELD_USERNAME, true),
                new SingleTextValueConfigurationField(FIELD_TOKEN, true),
        };
    }

    public String getSystemId() {
        return SAUCELABS;
    }

    public String getDisplay(Map<String, String> configs) {
        if (configs == null) {
            return null;
        }
        return configs.get(FIELD_USERNAME);
    }
}
