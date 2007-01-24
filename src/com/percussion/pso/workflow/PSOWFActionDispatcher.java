// Decompiled by DJ v3.2.2.67 Copyright 2002 Atanas Neshkov  Date: 4/2/2003 12:57:02 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   WFActionDispatcher.java

package com.percussion.pso.workflow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.percussion.design.objectstore.PSNotFoundException;
import com.percussion.extension.IPSExtension;
import com.percussion.extension.IPSExtensionDef;
import com.percussion.extension.IPSExtensionManager;
import com.percussion.extension.IPSWorkFlowContext;
import com.percussion.extension.IPSWorkflowAction;
import com.percussion.extension.PSDefaultExtension;
import com.percussion.extension.PSExtensionException;
import com.percussion.extension.PSExtensionProcessingException;
import com.percussion.extension.PSExtensionRef;
import com.percussion.server.IPSRequestContext;
import com.percussion.server.PSServer;

public class PSOWFActionDispatcher extends PSDefaultExtension
    implements IPSWorkflowAction 
{
    private static final Log log = LogFactory.getLog(PSOWFActionDispatcher.class);
    public PSOWFActionDispatcher()
    {
        m_extensionDef = null;
        m_codeRoot = null;
    }

    public void init(IPSExtensionDef extensionDef, File codeRoot)
        throws PSExtensionException
    {
        log.debug("Initializing WFActionDispatcher...");
        m_extensionDef = extensionDef;
        m_codeRoot = codeRoot;
    }

    public void performAction(IPSWorkFlowContext wfContext, IPSRequestContext request)
        throws PSExtensionProcessingException
    {
        String sName = "performAction";
        log.debug("WFActionDispatcher::performAction executing...");
        boolean bOK = true;
        int contentId = 0;
        try
        {
            contentId = Integer.parseInt(request.getParameter("sys_contentid"));
        }
        catch(NumberFormatException nfex)
        {
            throw new PSExtensionProcessingException("WFActionDispatcher::performAction", nfex);
        }

        int transitionId = wfContext.getTransitionID();
        int workflowId = wfContext.getWorkflowID();
        log.debug("Content id: " + contentId);
        log.debug("Workflow id: " + workflowId);
        log.debug("Transition Id: " + transitionId);
        //int contentType = 0;
        try
        {
            //contentType = getContentType(contentId, request);
        }
        catch(Exception e)
        {            
            log.error("WFActionDispatcher::performAction", e);
        }
        try
        {
            //Object actions[] = getWorkflowActions(contentType, transitionId);
        	List<String> actions = getWorkflowActions(workflowId, transitionId);
            log.debug("found " + actions.size() + " actions to execute."); 
            for(String action : actions)
            {
                log.debug("Executing " + action + "... ");
                IPSWorkflowAction wfaction = (IPSWorkflowAction)this.getExtension(action, 
                      IPSWorkflowAction.class.getName());
                if(wfaction != null)
                {
                   wfaction.performAction(wfContext, request);
                }
            }

        }
        catch(Exception nfx)
        {
            log.error("WFActionDispatcher::performAction",nfx);
            throw new PSExtensionProcessingException("WFActionDispatcher::performAction", nfx);
        }
        log.debug("WFActionDispatcher::performAction done");
    }

    private List<String> getWorkflowActions(int workflowId, int transitionId)
        throws PSExtensionProcessingException
    {
        String PROP_DELIMITER = ",";
        String VALUE_DELIMITER = "|";
        List<String> actions = new ArrayList<String>();
        Properties props = new Properties();
        try
        {
            props.load(new FileInputStream("rxconfig/Workflow/dispatcher.properties"));
            String sActions = props.getProperty(workflowId + "|" + transitionId);
            if(actions != null)
            {
                for(StringTokenizer st = new StringTokenizer(sActions, ","); st.hasMoreTokens(); actions.add(st.nextToken()));
            } else
            {
                log.error("Could not find property " + workflowId + ":" + transitionId + " in " + "rxconfig/Workflow/dispatcher.properties");
            }
        }
        catch(FileNotFoundException fex)
        {
            log.error("Properties file not found: rxconfig/Workflow/dispatcher.properties", fex);
            //fex.printStackTrace();
        }
        catch(IOException ex)
        {
            log.error("Properties file could not be opened: rxconfig/Workflow/dispatcher.properties",ex);
            //ex.printStackTrace();
        }
        finally
        {
            if(props != null)
                props.clear();
        }
        return actions;
    }


    private IPSExtension getExtension(String workflowActionName, String interfaceName) 
       throws PSExtensionException, PSNotFoundException
    {
       IPSExtension ext = null; 
       IPSExtensionManager extMgr = PSServer.getExtensionManager(null);         
       Iterator itr =  extMgr.getExtensionNames("Java",null,interfaceName,
             workflowActionName);
       while(itr.hasNext())
       {
          PSExtensionRef ref  = (PSExtensionRef) itr.next();
          log.debug("found extension " + ref.getFQN()); 
          ext = extMgr.prepareExtension(ref, null);
          log.debug("prepared extension " + ext.getClass().getCanonicalName()); 
          return ext;
       }  
       log.error("Extension name " + workflowActionName + " was not found "); 
       return ext;
    }

    IPSExtensionDef m_extensionDef;
    File m_codeRoot;
}