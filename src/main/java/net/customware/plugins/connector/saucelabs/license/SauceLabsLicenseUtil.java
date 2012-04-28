package net.customware.plugins.connector.saucelabs.license;

import com.atlassian.cache.CacheManager;
import net.customware.plugins.connector.core.integration.license.util.LicenseUtil;
import net.customware.plugins.connector.core.integration.manager.CatalogueManager;

@Deprecated
public class SauceLabsLicenseUtil extends LicenseUtil {

    public SauceLabsLicenseUtil(CatalogueManager catalogueManager, CacheManager cacheFactory) {
        super(catalogueManager, cacheFactory);
    }

}
