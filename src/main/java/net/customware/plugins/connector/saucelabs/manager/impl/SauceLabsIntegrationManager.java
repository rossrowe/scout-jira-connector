package net.customware.plugins.connector.saucelabs.manager.impl;

import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import net.customware.plugins.connector.core.integration.config.ConnectorConfigurationProvider;
import net.customware.plugins.connector.core.integration.manager.AbstractIntegrationManager;
import net.customware.plugins.connector.core.integration.manager.CatalogueManager;
import net.customware.plugins.connector.core.integration.manager.ConfigurationManager;
import net.customware.plugins.connector.core.remote.manager.RequestManager;
import net.customware.plugins.connector.saucelabs.applinks.application.SauceLabsApplicationType;
import net.customware.plugins.connector.saucelabs.bean.SauceLabsConfigurationProvider;
import net.customware.plugins.connector.saucelabs.manager.SauceLabsManager;
import org.apache.log4j.Logger;

import java.net.URI;
import java.util.Map;

/**
 * <p> <CLASS DETAILS> </p>
 *
 * @author Stéphane
 * @since 1.0
 */
public class SauceLabsIntegrationManager extends AbstractIntegrationManager<SauceLabsManager> {
    private static final Logger LOG = Logger.getLogger(SauceLabsIntegrationManager.class);

    private SauceLabsConfigurationProvider configurationProvider;
    private final I18nResolver i18nResolver;
    private final UserManager userManager;
    private final RequestManager requestManager;

    public SauceLabsIntegrationManager(CatalogueManager catalogueManager, ConfigurationManager configurationManager, I18nResolver i18nResolver, UserManager userManager, AuthenticationConfigurationManager authenticationConfigurationManager, RequestManager requestManager) {
        super(catalogueManager, configurationManager, authenticationConfigurationManager);
        this.i18nResolver = i18nResolver;
        this.userManager = userManager;
        this.requestManager = requestManager;
        configurationProvider = new SauceLabsConfigurationProvider();
    }

    public SauceLabsManager createManagerForConfig(Map<String, String> configs, URI rpcURI, URI displayURI) {
        return new DefaultSauceLabsManager(rpcURI, displayURI, userManager, i18nResolver, configs, requestManager);
    }

    public ConnectorConfigurationProvider getConfigurationProvider() {
        return configurationProvider;
    }

    public Class<? extends ApplicationType> getApplicationType() {
        return SauceLabsApplicationType.class;
    }

}
