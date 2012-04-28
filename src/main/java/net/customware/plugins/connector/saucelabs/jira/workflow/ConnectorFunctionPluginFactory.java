package net.customware.plugins.connector.saucelabs.jira.workflow;

import java.util.Map;

import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginFunctionFactory;
import com.opensymphony.workflow.loader.AbstractDescriptor;

public class ConnectorFunctionPluginFactory  extends AbstractWorkflowPluginFactory implements WorkflowPluginFunctionFactory {

	@Override
	protected void getVelocityParamsForEdit(Map<String, Object> arg0,
			AbstractDescriptor arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void getVelocityParamsForInput(Map<String, Object> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void getVelocityParamsForView(Map<String, Object> arg0,
			AbstractDescriptor arg1) {
		// TODO Auto-generated method stub
		
	}

	public Map<String, ?> getDescriptorParams(Map<String, Object> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
