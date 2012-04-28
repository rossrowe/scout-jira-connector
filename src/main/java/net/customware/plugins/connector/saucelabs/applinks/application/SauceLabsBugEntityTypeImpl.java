package net.customware.plugins.connector.saucelabs.applinks.application;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.spi.application.IdentifiableType;
import com.atlassian.applinks.spi.application.NonAppLinksEntityType;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.plugin.util.Assertions;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceManager;

import java.net.URI;
import java.net.URISyntaxException;



public class SauceLabsBugEntityTypeImpl implements IdentifiableType, SauceLabsBugEntityType, NonAppLinksEntityType {
	   private static final TypeId TYPE_ID = new TypeId("saucelabs.bug");
	    private WebResourceManager webResourceManager;

	    public SauceLabsBugEntityTypeImpl(WebResourceManager webResourceManager) {
	        this.webResourceManager = webResourceManager;
	    }

	    public TypeId getId() {
	        return TYPE_ID;
	    }

	    public Class<? extends ApplicationType> getApplicationType() {
	        return SauceLabsApplicationType.class;
	    }

	    public String getI18nKey() {
	        return "applinks.saucelabs.bug";
	    }

	    public String getPluralizedI18nKey() {
	        return "applinks.saucelabs.bugs";
	    }

	    public final URI getIconUrl() {
	        try {
	            return new URI((new StringBuilder()).append(webResourceManager.getStaticPluginResource((new StringBuilder()).append("net.customware.plugins.connector.saucelabs.saucelabs-connector-plugin").append(":applinks-images").toString(), "images", UrlMode.ABSOLUTE)).append("/types/16").append(getId().get()).append(".png").toString());
	        } catch (URISyntaxException e) {
	            //LOG.warn("Unable to find the icon for this application type.", e);
	        }
	        return null;
	    }

	    public URI getDisplayUrl(final ApplicationLink link, final String sobject) {
	        Assertions.isTrue(String.format("Application link %s is not of type %s",
	                link.getId(), getApplicationType().getName()),
	                link.getType() instanceof SauceLabsApplicationType);
	        //TODO
	        return null;
	    }
	}
