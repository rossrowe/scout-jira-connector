package net.customware.plugins.connector.saucelabs.applinks.application;

import com.atlassian.applinks.spi.Manifest;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.applinks.spi.manifest.ManifestNotFoundException;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.net.RequestFactory;
import net.customware.plugins.connector.core.applinks.AppLinksManifestProducer;
import net.customware.plugins.connector.core.client.manager.ConnectorRequestManager;

import java.net.URI;
import java.net.URISyntaxException;

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

    /**
     * Overriden so that we can supply a unique URI when invoking the superclass method.  We do this so that
     * we can supply multiple Sauce connections (so that different user accounts can be used to export Sauce snapshots
     * to Jira) - without this an error message is generated when a user attempts
     * to add multiple Sauce connections.
     *
     * @param url the URI that references the RPC url - should always be https://saucelabs.com
     * @return a new {@link Manifest} instance
     * @throws ManifestNotFoundException
     */
    @Override
    public Manifest getManifest(URI url) throws ManifestNotFoundException {
        URI uniqueUri;
        try {
            uniqueUri = new URI(url.toString() + System.currentTimeMillis());
        } catch (URISyntaxException e) {
            //shouldn't happen, but if it does, just invoke super
            return super.getManifest(url);
        }
        return super.getManifest(uniqueUri);
    }
}
