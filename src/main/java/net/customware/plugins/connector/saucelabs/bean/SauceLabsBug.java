package net.customware.plugins.connector.saucelabs.bean;

import java.util.Map;


/**
 * <p> <CLASS DETAILS> </p>
 *
 * @author Stéphane
 * @since 1.0
 */

public class SauceLabsBug {


    private Map<String, String> attributes;

    public SauceLabsBug() {
        super();
    }

    public SauceLabsBug(Map<String, String> attributes) {
        super();
        this.attributes = attributes;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

}
