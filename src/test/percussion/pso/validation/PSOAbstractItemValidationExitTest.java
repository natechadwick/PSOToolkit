/*
 * COPYRIGHT (c) 1999 - 2008 by Percussion Software, Inc., Woburn, MA USA.
 * All rights reserved. This material contains unpublished, copyrighted
 * work including confidential and proprietary information of Percussion.
 *
 * test.percussion.pso.validation PSOAbstractItemValidationExitTest.java
 *
 */
package test.percussion.pso.validation;

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import com.percussion.error.PSException;
import com.percussion.pso.validation.PSOAbstractItemValidationExit;
import com.percussion.pso.workflow.IPSOWorkflowInfoFinder;
import com.percussion.server.IPSRequestContext;
import com.percussion.services.workflow.data.PSState;
import com.percussion.util.PSItemErrorDoc;
import com.percussion.xml.PSXmlDocumentBuilder;

public class PSOAbstractItemValidationExitTest
{
   private static Log log = LogFactory.getLog(PSOAbstractItemValidationExitTest.class); 

   TestableItemValidationExit cut;
   
   Mockery context; 
   @Before
   public void setUp() throws Exception
   {
      context = new Mockery(){{setImposteriser(ClassImposteriser.INSTANCE);}};
      
      cut = new TestableItemValidationExit();
   }
   
   @Test
   public final void testHasErrors()
   {
      Document err = PSXmlDocumentBuilder.createXmlDocument();
      boolean rslt = cut.hasErrors(err);
      assertFalse(rslt);
      PSItemErrorDoc.addError(err, "foo", "bar" , "xyzzy" , new Object[0]);
      rslt = cut.hasErrors(err);
      assertTrue(rslt);
   }
   
   @Test
   public final void testMatchDestinationStateBasics()
   {
      try
      {
         boolean rslt = cut.matchDestinationState("1" , "2", null);
         assertTrue(rslt);
         rslt = cut.matchDestinationState("1" , "2", "");
         assertTrue(rslt);
         rslt = cut.matchDestinationState("1", "2","*"); 
         assertTrue(rslt);
      } catch (PSException ex)
      {  
         log.error("Unexpected Exception " + ex,ex);
         fail("Exception");
      }
   }
   
   @Test
   public final void testMatchDestinationStateComplex()
   {
      final PSState state = context.mock(PSState.class);
      final IPSOWorkflowInfoFinder finder = context.mock(IPSOWorkflowInfoFinder.class);
      cut.setFinder(finder);
      try
      {
         
         context.checking(new Expectations(){{
            one(finder).findDestinationState("1","2");
            will(returnValue(state));
            atLeast(1).of(state).getName();
            will(returnValue("fi"));
         }});
         boolean rslt = cut.matchDestinationState("1" , "2", "fee,fi,fo,fum");
         assertTrue(rslt);
         context.assertIsSatisfied();
         
      } catch (PSException ex)
      {  
         log.error("Unexpected Exception " + ex,ex);
         fail("Exception");
      }
   }
   private class TestableItemValidationExit extends PSOAbstractItemValidationExit
   {

      @Override
      public boolean hasErrors(Document errorDoc)
      {
         return super.hasErrors(errorDoc);
      }

      @Override
      public void validateDocs(Document inputDoc, Document errorDoc,
            IPSRequestContext req, Object[] params)
      {
         
      }

      @Override
      public boolean matchDestinationState(String contentid,
            String transitionid, String allowedStates) throws PSException
      {
         return super.matchDestinationState(contentid, transitionid, allowedStates);
      }

      @Override
      public void setFinder(IPSOWorkflowInfoFinder finder)
      {         
         super.setFinder(finder);
      }
      
   }
}