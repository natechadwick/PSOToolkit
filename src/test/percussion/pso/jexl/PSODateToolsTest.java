/*******************************************************************************
 * Copyright (c) 1999-2011 Percussion Software.
 * 
 * Permission is hereby granted, free of charge, to use, copy and create derivative works of this software and associated documentation files (the "Software") for internal use only and only in connection with products from Percussion Software. 
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL PERCUSSION SOFTWARE BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package test.percussion.pso.jexl;

import java.text.ParseException;
import java.util.Calendar;

import org.junit.Test;

import com.percussion.pso.jexl.PSODateTools;
import junit.framework.TestCase;

/**
 * The class <code>PSODateToolsTest</code> contains tests for the class {@link
 * <code>PSODateTools</code>}
 *
 * @pattern JUnit Test Case
 *
 * @generatedBy CodePro at 10/5/10 3:55 PM
 *
 * @author natechadwick
 *
 * @version $Revision$
 */
public class PSODateToolsTest extends TestCase {

	public PSODateToolsTest(){}
	
	/**
	 * Run the String formatDate(String, Object) method test
	 */
	@Test
	public void testFormatDate() throws ParseException{
		// add test code here
		PSODateTools fixture = new PSODateTools();
		String format = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
		Object date_value = Calendar.getInstance().getTime();
		String result = fixture.formatDate(format, date_value);
		assertTrue(result=="");
	}
}

/*$CPS$ This comment was generated by CodePro. Do not edit it.
 * patternId = com.instantiations.assist.eclipse.pattern.testCasePattern
 * strategyId = com.instantiations.assist.eclipse.pattern.testCasePattern.junitTestCase
 * additionalTestNames = 
 * assertTrue = false
 * callTestMethod = true
 * createMain = false
 * createSetUp = false
 * createTearDown = false
 * createTestFixture = false
 * createTestStubs = false
 * methods = formatDate(QString;!QObject;)
 * package = test.percussion.pso
 * package.sourceFolder = PSOToolkit66/src
 * superclassType = junit.framework.TestCase
 * testCase = PSODateToolsTest
 * testClassType = com.percussion.pso.jexl.PSODateTools
 */