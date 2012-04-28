package net.customware.plugins.connector.saucelabs.bean;

import java.util.Map;


/**
 * <p> <CLASS DETAILS> </p>
 *
 * @author Stéphane
 * @since 1.0
 */

public class SauceLabsBug {


    private Map<String, Object> attributes;

    public SauceLabsBug() {
        super();
    }

    public SauceLabsBug(Map attributes) {
        super();
        this.attributes = attributes;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

}
