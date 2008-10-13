/*
 * com.percussion.pso.jexl PSOSlotTools.java
 * 
 * COPYRIGHT (c) 1999 - 2008 by Percussion Software, Inc., Woburn, MA USA.
 * All rights reserved. This material contains unpublished, copyrighted
 * work including confidential and proprietary information of Percussion.
 *
 * @author Adam Gent
 *
 */
package com.percussion.pso.jexl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.percussion.extension.IPSJexlExpression;
import com.percussion.extension.IPSJexlMethod;
import com.percussion.extension.IPSJexlParam;
import com.percussion.extension.PSJexlUtilBase;
import com.percussion.i18n.PSI18nUtils;
import com.percussion.services.assembly.IPSAssemblyErrors;
import com.percussion.services.assembly.IPSAssemblyItem;
import com.percussion.services.assembly.IPSAssemblyService;
import com.percussion.services.assembly.IPSSlotContentFinder;
import com.percussion.services.assembly.IPSTemplateSlot;
import com.percussion.services.assembly.PSAssemblyException;
import com.percussion.services.assembly.PSAssemblyServiceLocator;
import com.percussion.services.assembly.IPSAssemblyResult.Status;
import com.percussion.services.assembly.data.PSAssemblyWorkItem;
import com.percussion.utils.exceptions.PSExceptionHelper;

/**
 * This class is a utility class to help work with slots.
 * 
 * 
 * @author agent
 *
 */
public class PSOSlotTools extends PSJexlUtilBase implements IPSJexlExpression
{

   private static final Log ms_log = LogFactory.getLog(PSOListTools.class);
   
   /**
    * Ctor. 
    */
   public PSOSlotTools()
   {
      super();
      // TODO Auto-generated constructor stub
   }
   
   /**
    * Get the contents of a slot as a list of assembly items.
    * 
    * @param item the Assembly Item
    * @param slotName the name of the slot
    * @param params the combined map of parameters to pass to the slot finder.  
    * Never <code>null</code> may be <code>empty</code>
    * @return a list of results
    * @throws Throwable
    * @deprecated use PSAssemblerUtils.getSlotItems() instead. 
    */
   @IPSJexlMethod(description = "Get the contents of a slot as a list of assembly items", params =
   {
         @IPSJexlParam(name = "item", description = "the parent assembly item"),
         @IPSJexlParam(name = "slotName", description = "the slot name"),
         @IPSJexlParam(name = "params", description = "extra parameters to the process")}, returns = "list of assembly items")
   public List<IPSAssemblyItem> getSlotContents(IPSAssemblyItem item,
         String slotName, Map<String, Object> params) throws Throwable
   {
      try
      {
         IPSAssemblyService asm = PSAssemblyServiceLocator.getAssemblyService();
         if(StringUtils.isBlank(slotName))
         {
            String emsg = "Slot name must not be blank";
            ms_log.error(emsg);
            throw new IllegalArgumentException(emsg); 
         }
         IPSTemplateSlot slot = asm.findSlotByName(slotName); 
         if (slot == null)
         {
            throw new IllegalArgumentException(
                  "slot not found, check template's slot reference");
         }
         if (params == null)
            params = new HashMap<String, Object>();
         // Handle old slots
         String findername = slot.getFinderName();
         if (findername == null)
         {
            ms_log.warn("No finder defined for slot " + slot.getName()
                  + " defaulting to sys_RelationshipContentFinder");
            findername = "Java/global/percussion/slotcontentfinder/sys_RelationshipContentFinder";
         }
         IPSSlotContentFinder finder = asm.loadFinder(findername);
         if (finder == null)
            throw new PSAssemblyException(IPSAssemblyErrors.MISSING_FINDER,
                  finder);
         List<IPSAssemblyItem> relitems = finder.find(item, slot, params);
         return relitems;
      }
      catch (PSAssemblyException ae)
      {
         
         /*
          * What should we do if assembly failes.
          */
         PSAssemblyWorkItem work = (PSAssemblyWorkItem) item;
         // Create clone for response
         work = (PSAssemblyWorkItem) work.clone();
         work.setStatus(Status.FAILURE);
         work.setMimeType("text/html");
         //TODO: Change this log message
         ms_log.warn("Assembly failed.");
         List<IPSAssemblyItem> rvalue = new ArrayList<IPSAssemblyItem>();
         // Should we add the failed work item?
         rvalue.add(work);
         return rvalue; 
      }
      catch (Throwable e)
      {
         Throwable orig = PSExceptionHelper.findRootCause(e, true);
         ms_log.error(PSI18nUtils
               .getString("psx_assembly@Problem during assembly"), orig);
         throw e;
      }
   }

}
