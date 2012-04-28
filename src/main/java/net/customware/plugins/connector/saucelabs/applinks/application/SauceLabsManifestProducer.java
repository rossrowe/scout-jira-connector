package net.customware.plugins.connector.saucelabs.applinks.application;

import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.net.RequestFactory;
import net.customware.plugins.connector.core.applinks.AppLinksManifestProducer;
import net.customware.plugins.connector.core.client.manager.ConnectorRequestManager;

/**
 * @since 3.0
 */
public class SauceLabsManifestProducer extends AppLinksManifestProducer {

    public SauceLabsManifestProducer(final RequestFactory requestFactory, final WebResourceManager webResourceManager, ConnectorRequestManager connectorRequestManager) {
        super(requestFactory, webResourceManager, connectorRequestManager);
    }


    @Override
    protected TypeId getApplicationTypeId() {
        return SauceLabsApplicationTypeImpl.TYPE_ID;
    }

    @Override
    protected String getApplicationName() {
        return "Sauce Labs";
    }

}
