/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.nexial.core.plugins.web;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LocatorHelperTest {
	LocatorHelper subject;

	@Before
	public void init() {
		subject = new LocatorHelper(new WebCommand());
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testResolveFilteringXPath() throws Exception {
		String fixture = "class=nxl2*\n" +
		                 "text()=Save\n" +
		                 "id=*save*";
		String expected = "//*[ends-with(@class,'nxl2') and text()='Save' and contains(@id,'save')]";
		String actual = subject.resolveFilteringXPath(fixture);
		Assert.assertEquals(actual, expected);

		fixture = "@class=nxl2*|text()=Save\n" +
		          "@id=*save*";
		expected = "//*[ends-with(@class,'nxl2') and text()='Save' and contains(@id,'save')]";
		actual = subject.resolveFilteringXPath(fixture);
		Assert.assertEquals(actual, expected);

		fixture = "@class=nxl2*|text()=Save\r\n@id=*save*";
		expected = "//*[ends-with(@class,'nxl2') and text()='Save' and contains(@id,'save')]";
		actual = subject.resolveFilteringXPath(fixture);
		Assert.assertEquals(actual, expected);

		fixture = "@class=*cntri|text()=*Save*\r\n@enabled=true";
		expected = "//*[starts-with(@class,'cntri') and contains(text(),'Save') and @enabled='true']";
		actual = subject.resolveFilteringXPath(fixture);
		Assert.assertEquals(actual, expected);

        fixture = "id=IF1";
        expected = "//*[@id='IF1']";
        actual = subject.resolveFilteringXPath(fixture);
        Assert.assertEquals(actual, expected);

        fixture = "id=IF1\n" +
                  "class=entry-title post-title\n" +
                  "name=TestName";
        expected = "//*[@id='IF1' and @class='entry-title post-title' and @name='TestName']";
        actual = subject.resolveFilteringXPath(fixture);
        Assert.assertEquals(actual, expected);
	}

	@Test
	public void testNormalizeXpathText() throws Exception {
		// basic
		Assert.assertEquals(subject.normalizeXpathText(""), "''");
		Assert.assertEquals(subject.normalizeXpathText(null), "''");
		Assert.assertEquals(subject.normalizeXpathText("Hello"), "'Hello'");
		Assert.assertEquals(subject.normalizeXpathText("Hello Jimmy Johnson"), "'Hello Jimmy Johnson'");

		// quotes
		Assert.assertEquals(subject.normalizeXpathText("Bob's Pizza"), "concat('Bob',\"'\",'s Pizza')");
		Assert.assertEquals(subject.normalizeXpathText("Review \"Cardholder\"'s Enrollments"),
		                    "concat('Review ','\"','Cardholder','\"',\"'\",'s Enrollments')");
		Assert.assertEquals(subject.normalizeXpathText("'Special K'"), "concat(\"'\",'Special K',\"'\")");
		Assert.assertEquals(subject.normalizeXpathText("New's \"flash\""),
		                    "concat('New',\"'\",'s ','\"','flash','\"')");
		Assert.assertEquals(subject.normalizeXpathText(" 'Final' \"Space\" "),
		                    "concat(' ',\"'\",'Final',\"'\",' ','\"','Space','\"',' ')");
	}

	@Test
	public void testFixBadXpath() throws Exception {
		Assert.assertNull(subject.fixBadXpath(null));
		Assert.assertEquals("", subject.fixBadXpath(""));
		Assert.assertEquals("/", subject.fixBadXpath("/"));
		Assert.assertEquals("//", subject.fixBadXpath("//"));
		Assert.assertEquals("//", subject.fixBadXpath(".//"));
		Assert.assertEquals("//", subject.fixBadXpath(" .//"));
		Assert.assertEquals("//", subject.fixBadXpath("    .//"));
		Assert.assertEquals("(//", subject.fixBadXpath("(//"));
		Assert.assertEquals("(//", subject.fixBadXpath("(.//"));
		Assert.assertEquals("(//", subject.fixBadXpath("( .//"));
		Assert.assertEquals("(//", subject.fixBadXpath("( .//"));
	}
}
