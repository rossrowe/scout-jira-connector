package net.customware.plugins.connector.saucelabs.manager.impl;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.base.Joiner;
import net.customware.plugins.connector.core.exception.ConnectorException;
import net.customware.plugins.connector.core.exception.ConnectorIOException;
import net.customware.plugins.connector.core.integration.bean.DefaultMappingBean;
import net.customware.plugins.connector.core.integration.manager.AbstractRemoteManager;
import net.customware.plugins.connector.core.remote.bean.*;
import net.customware.plugins.connector.core.remote.field.DefaultRemoteField;
import net.customware.plugins.connector.core.remote.field.RemoteField;
import net.customware.plugins.connector.core.remote.field.RemoteFieldType;
import net.customware.plugins.connector.core.remote.manager.RequestManager;
import net.customware.plugins.connector.core.remote.query.ConditionExpression;
import net.customware.plugins.connector.core.remote.query.ConditionNode;
import net.customware.plugins.connector.core.remote.query.RemoteQuery;
import net.customware.plugins.connector.core.util.DateUtils;
import net.customware.plugins.connector.core.util.JSONMarshallingUtil;
import net.customware.plugins.connector.saucelabs.api.SauceLabsTypeDescription;
import net.customware.plugins.connector.saucelabs.api.SauceLabsTypeList;
import net.customware.plugins.connector.saucelabs.bean.SauceLabsBug;
import net.customware.plugins.connector.saucelabs.bean.SauceLabsConfigurationProvider;
import net.customware.plugins.connector.saucelabs.manager.SauceLabsManager;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.Map.Entry;


public class DefaultSauceLabsManager extends AbstractRemoteManager implements SauceLabsManager {

    private static final Logger LOG = Logger.getLogger(DefaultSauceLabsManager.class);

    private final RequestManager requestManager;

    private HttpClient client;
    private Map<String, List<RemoteField>> remoteFieldsMap;
    private Map<String, String> configs;

    private URI rpcURI;
    private URI displayURI;

    private static RemoteField createRemoteField(String id, String name, RemoteFieldType type) {
        DefaultRemoteField remoteField = new DefaultRemoteField();
        remoteField.setId(id);
        remoteField.setName(name);
        remoteField.setFieldType(type);
        return remoteField;
    }

    private static RemoteObjectType createRemoteObjectType(String id, String name, String description) {
        RemoteObjectType remoteField = new RemoteObjectType();
        remoteField.setObjectTypeId(id);
        remoteField.setObjectTypeName(name);

        return remoteField;
    }

    protected String getUrl() {
        if (rpcURI == null) {
            return "";
        }
        return rpcURI.toString() + "/rest/v1/bugs";
    }

    public SauceLabsBug getBugForID(String id) throws ConnectorIOException {
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        String url = getUrl() + "/detail/" + id + "?" + getToken();
        GetMethod method = new GetMethod(url);

        try {
            int status = getClient().executeMethod(method);
            LOG.debug("Sauce Labs Fetch response=" + method.getResponseBodyAsString());
            Map<String, String> attributes = JSONMarshallingUtil.unmarshall(method.getResponseBodyAsString());
            if (status == 200) { // If the request is successful
                return new SauceLabsBug(attributes);
            } else {
                throw new ConnectorIOException("Error fetching bug from remote system, caused by: " + attributes.get("errorMessage"));
            }
        } catch (HttpException e) {
            LOG.error(e);
        } catch (IOException e) {
            LOG.error(e);
        }
        return null;
    }

    public DefaultSauceLabsManager(URI rpcURI, URI displayURI, UserManager userManager, I18nResolver i18nResolver, Map<String, String> config, RequestManager requestManager) {
        this.requestManager = requestManager;
        this.rpcURI = rpcURI;
        this.displayURI = displayURI;
        if (config == null) {
            this.configs = new HashMap<String, String>();
        } else {
            this.configs = config;
        }
        remoteFieldsMap = new HashMap<String, List<RemoteField>>();

        String baseUrl;
        if (rpcURI == null) {
            baseUrl = "";
        } else {
            baseUrl = rpcURI.toASCIIString();
        }

        client = requestManager.createClient(baseUrl);
    }

    public RemoteCreateResponseBean create(RemoteBean... remoteBeans) {
        RemoteCreateResponseBean responseBean = new RemoteCreateResponseBean();
        for (RemoteBean remoteBean : remoteBeans) {
            responseBean.addRecord(new RemoteCreateRecord(remoteBean.getId(), "Create is currently not supported."));
        }
        return responseBean;
    }

    public RemoteDeleteResponseBean delete(RemoteBean... remoteBeans) {
        RemoteDeleteResponseBean responseBean = new RemoteDeleteResponseBean();
        for (RemoteBean remoteBean : remoteBeans) {
            responseBean.addRecord(new RemoteDeleteRecord(remoteBean.getId(), "Delete is currently not supported."));
        }
        return responseBean;
    }

    public RemoteFetchResponseBean fetch(RemoteQuery query) {
        if (query.getConditions().isExpression()) {
            ConditionExpression expression = (ConditionExpression) query.getConditions();
            if (expression.getField().equals("ID")) {
                try {
                    SauceLabsBug bug = getBugForID((String) expression.getValues()[0]);
                    Collection<RemoteBean> results = new ArrayList<RemoteBean>();
                    RemoteBean bean = convertSauceLabsBugToRemoteBean(bug);
                    bean.setId((String) expression.getValues()[0]);
                    bean.setUrl(rpcURI + "/bugs/" + bean.getId());
                    results.add(bean);

                    return new RemoteFetchResponseBean(results);
                } catch (Exception e) {
                    //LOG.warn(e);
                    return new RemoteFetchResponseBean(Collections.<RemoteBean>emptyList(), e.getMessage());
                }
            }
            return new RemoteFetchResponseBean(Collections.<RemoteBean>emptyList(), "Invalid query.");
        } else {
            ConditionNode conditionNode = (ConditionNode) query.getConditions();
            RemoteFetchResponseBean leftResponse = fetch(new RemoteQuery(query.getObjectName(), conditionNode.getLeft()));
            RemoteFetchResponseBean rightResponse = fetch(new RemoteQuery(query.getObjectName(), conditionNode.getRight()));
            Collection<RemoteBean> remoteBeans = new ArrayList<RemoteBean>();
            if (conditionNode.getOperator().equals(ConditionNode.OR_GROUPING)) {
                if (leftResponse != null && leftResponse.getRecords() != null) {
                    remoteBeans.addAll(leftResponse.getRecords());
                }
                if (rightResponse != null && rightResponse.getRecords() != null) {
                    remoteBeans.addAll(rightResponse.getRecords());
                }
            } else {
                if (leftResponse != null && leftResponse.getRecords() != null && rightResponse != null && rightResponse.getRecords() != null) {
                    for (RemoteBean leftBean : leftResponse.getRecords()) {
                        for (RemoteBean rightBean : rightResponse.getRecords()) {
                            if (StringUtils.isNotEmpty(leftBean.getId()) && StringUtils.isNotEmpty(rightBean.getId()) && leftBean.getId().equals(rightBean.getId())) {
                                remoteBeans.add(leftBean);
                                break;
                            }
                        }
                    }
                }
            }

            Collection<String> errors = new ArrayList<String>();
            if (leftResponse != null && leftResponse.hasErrors()) {
                Collections.addAll(errors, leftResponse.getErrors());
            }
            if (rightResponse != null && rightResponse.hasErrors()) {
                Collections.addAll(errors, rightResponse.getErrors());
            }

            return new RemoteFetchResponseBean(remoteBeans, errors.toArray(new String[errors.size()]));
        }
    }


    public List<RemoteField> getFieldsForObjectType(String id) {


        String url = getUrl() + "/types/" + id;
        GetMethod method = new GetMethod(url);

        try {
            int response = getClient().executeMethod(method);
            if (response != 200) {
                throw new ConnectorIOException("An error has occured in SauceLabs, error code " + response + ":" + method.getResponseBodyAsString());
            } else {
                ObjectMapper m = new ObjectMapper();
                List<SauceLabsTypeDescription> bugFields = m.readValue(method.getResponseBodyAsStream(), new TypeReference<List<SauceLabsTypeDescription>>() {
                });
                List<RemoteField> bugRemoteFields = new ArrayList<RemoteField>();
                for (SauceLabsTypeDescription field : bugFields) {
                    bugRemoteFields.add(createRemoteField(field.getId(), field.getName(), RemoteFieldType.STRING));
                    remoteFieldsMap.put(id, bugRemoteFields); // Bug Id
                }
                return bugRemoteFields;
            }

        } catch (Exception e) {
            LOG.error(e);
        }
        return Collections.emptyList();
    }

    public List<RemoteObjectType> getObjectTypes() {
        String url = getUrl() + "/types";
        GetMethod method = new GetMethod(url);
        LOG.debug("SauceLabs Object types =" + url);

        try {
            int response = getClient().executeMethod(method);
            if (response != 200) {
                throw new ConnectorIOException("An error has occured in SauceLabs, error code " + response + ":" + method.getResponseBodyAsString());
            } else {
                ObjectMapper m = new ObjectMapper();
                List<SauceLabsTypeList> bugFields = m.readValue(method.getResponseBodyAsStream(), new TypeReference<List<SauceLabsTypeList>>() {
                });
                List<RemoteObjectType> bugRemoteFields = new ArrayList<RemoteObjectType>();
                for (SauceLabsTypeList field : bugFields) {
                    bugRemoteFields.add(createRemoteObjectType(field.getId(), field.getName(), field.getDescription()));
                }
                return bugRemoteFields;
            }

        } catch (Exception e) {
            LOG.error(e);
        }
        return Collections.emptyList();
    }

    public RemoteQuery getSelectByIDQueryForObject(String objectType, String id) {
        ConditionExpression expression = new ConditionExpression("ID", ConditionExpression.EQUALS, id);
        return new RemoteQuery(objectType, expression);
    }

    public void testConnection() throws ConnectorException {
        // Validate username and access-key for fetching the bug data
        String url = getUrl() + "/validate?" + getToken();
        LOG.debug("SauceLabs Test URL=" + url);

        GetMethod method = new GetMethod(url);
        try {
            int response = getClient().executeMethod(method);
            method.getResponseBodyAsString();

            if (response != 200) {

                throw new ConnectorIOException("Error code " + response + " received while authenticating to SauceLabs," + " response received: " + method.getResponseBodyAsString());
            }
        } catch (ConnectorException e) {
            throw e;
        } catch (Exception e) {
            throw new ConnectorIOException(e);
        }

    }

    protected String[] convertCollectionToStringArray(Collection col) {
        String[] returnValue = new String[col.size()];
        int i = 0;
        for (Object o : col) {
            if (o != null) {
                returnValue[i] = convertObjectToString(o);
            }
            i++;
        }
        return returnValue;
    }

    protected String convertObjectToString(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Date) {
            return DateUtils.format((Date) o, DateUtils.COMMON_DATE_FORMAT);
        }
        if (o instanceof InternetAddress) {
            return ((InternetAddress) o).getAddress();
        }

        // Number is included here since they all have meaningful toString method overrides
        return o.toString();
    }

    public RemoteUpdateResponseBean update(RemoteBean... remoteBeans) {

        RemoteUpdateResponseBean responseBean = new RemoteUpdateResponseBean();
        responseBean.setResponseStatus("SUCCESS");
        for (RemoteBean remoteBean : remoteBeans) {
            String url = getUrl() + "/update/" + remoteBean.getId() + "?" + getToken();
            PostMethod method = new PostMethod(url);

            Map<String, String> tmpAttribs = new HashMap<String, String>();
            for (Entry<String, Object> entry : remoteBean.getAttributes().entrySet()) {
                Object o = entry.getValue();
                if (o instanceof Collection) {
                    String[] values = convertCollectionToStringArray((Collection) o);
                    tmpAttribs.put(entry.getKey(), Joiner.on(",").join(values));
                } else {
                    tmpAttribs.put(entry.getKey(), convertObjectToString(o));
                }
            }
            String jsonUpdate = JSONMarshallingUtil.marshall(tmpAttribs);
            LOG.debug("Sauce Labs update request=" + url + " params:update/" + jsonUpdate);
            method.addParameter("update", jsonUpdate);
            RemoteUpdateRecord result = new RemoteUpdateRecord();
            result.setId(remoteBean.getId());
            try {
                int response = getClient().executeMethod(method);
                LOG.debug("Sauce Labs update response=" + method.getResponseBodyAsString());
                if (response != 200) {
                    throw new ConnectorIOException("An error has occurred while updating the remote bug, caused by: " + method.getResponseBodyAsString());
                }
            } catch (Exception e) {
                String[] errors = new String[1];
                errors[0] = "An error has occurred in SauceLabs during the update. error message:" + e.getMessage();
                result.setErrors(errors);
                responseBean.setResponseStatus("ERROR");
            }
            responseBean.addRecord(result);
        }
        return responseBean;

    }

    protected String getToken() {
        return "username=" + getUsername() + "&access-key=" + getPassword();
    }

    protected String getUsername() {
        return configs.get(SauceLabsConfigurationProvider.FIELD_USERNAME);
    }

    protected String getPassword() {
        return configs.get(SauceLabsConfigurationProvider.FIELD_TOKEN);
    }

    protected HttpClient getClient() {
        return client;
    }

    @SuppressWarnings("unchecked")
    protected RemoteBean convertSauceLabsBugToRemoteBean(SauceLabsBug bug) {
        if (bug == null) {
            return null;
        }
        RemoteBean bean = new RemoteBean();
        bean.setObjectType("1"); // Bug object type
        bean.setId(bug.getAttributes().get("id")); // Saucelabs bug Id
        for (Entry<String, String> currentAttribute : bug.getAttributes().entrySet()) {
            bean.setAttribute(currentAttribute.getKey(), currentAttribute.getValue());
        }

        return bean;
    }

    @Override
    public Collection<DefaultMappingBean> getDefaultMappingsForObject(String objectName) {
        if ("1".equals(objectName)) {
            Collection<DefaultMappingBean> defaultMappings = new ArrayList<DefaultMappingBean>();
            defaultMappings.add(new DefaultMappingBean("DESCRIPTION", "Description", DefaultMappingBean.BI_DIRECTIONAL));
            defaultMappings.add(new DefaultMappingBean("SUMMARY", "Title", DefaultMappingBean.BI_DIRECTIONAL));
            return defaultMappings;
        }
        return super.getDefaultMappingsForObject(objectName);
    }

    @Override
    public String getDefaultLayoutTemplateForObject(String objectName) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("$!fields.get(\"Title\")");
        sb.append("|$!url]");

        return sb.toString();
    }

    @Override
    public String formatObjectForDisplay(RemoteBean remoteBean) {
        Object subject = remoteBean.getAttributes().get("Title");
        if (subject != null) {
            return subject.toString();
        }
        return super.formatObjectForDisplay(remoteBean);
    }
}
