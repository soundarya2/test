package com.siriuscom.ipl_scorer.bundle.processes;





import org.osgi.service.component.annotations.Component;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;

@Component(immediate = true,service=WorkflowProcess.class, property = {"process.label=Test Workflow"})
public class WorkflowCustom implements WorkflowProcess {

	
	
	@Override
	public void execute(WorkItem workItem, WorkflowSession session, MetaDataMap map) throws WorkflowException {
		
	}

}
