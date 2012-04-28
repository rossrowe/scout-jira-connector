package net.customware.plugins.connector.saucelabs.applinks.application;

import com.atlassian.applinks.spi.application.IdentifiableType;
import com.atlassian.applinks.spi.application.NonAppLinksApplicationType;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceManager;


import org.apache.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;

public class SauceLabsApplicationTypeImpl implements IdentifiableType, SauceLabsApplicationType, NonAppLinksApplicationType {
    static public final TypeId TYPE_ID = new TypeId("saucelabs");

    private final static Logger LOG = Logger.getLogger(SauceLabsApplicationTypeImpl.class);
    protected final WebResourceManager webResourceManager;

    public SauceLabsApplicationTypeImpl(WebResourceManager webResourceManager) {
        this.webResourceManager = webResourceManager;
    }

    public final URI getIconUrl() {

        try {
            return new URI((new StringBuilder()).append(webResourceManager.getStaticPluginResource((new StringBuilder()).append("net.customware.plugins.connector.saucelabs.saucelabs-connector-plugin").append(":applinks-images").toString(), "images", UrlMode.ABSOLUTE)).append("/types/16").append(getId().get()).append(".png").toString());
        } catch (URISyntaxException e) {
            LOG.warn("Unable to find the icon for this application type.", e);
        }
        return null;
    }


    public String getI18nKey() {
        return "applinks.saucelabs";
    }

    public TypeId getId() {
        return TYPE_ID;
    }

    public String getConnectorSystemTypeId() {
        return "saucelabs";
    }
}