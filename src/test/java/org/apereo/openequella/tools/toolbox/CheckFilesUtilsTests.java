/*
 * Licensed to The Apereo Foundation under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * The Apereo Foundation licenses this file to you under the Apache License,
 * Version 2.0, (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apereo.openequella.tools.toolbox;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apereo.openequella.tools.toolbox.checkFiles.ResultComparison;
import org.apereo.openequella.tools.toolbox.utils.CheckFilesUtils;
import org.junit.Test;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CheckFilesUtilsTests {
	private static final Logger LOGGER = LogManager.getLogger(CheckFilesUtils.class);

	@Test
	public void testGeneralReplacement() {
		assertEquals("$", CheckFilesUtils.specialCharReplace("$"));
		assertEquals("\\?", CheckFilesUtils.specialCharReplace("QUESTION_MARK"));
		assertEquals("\\+", CheckFilesUtils.specialCharReplace("PLUS"));
		assertEquals(" ", CheckFilesUtils.specialCharReplace("BLANK"));
		assertEquals(" \\\\", CheckFilesUtils.specialCharReplace("SPACE_BACKSLASH"));
		assertEquals("\\\\", CheckFilesUtils.specialCharReplace("BACKSLASH"));
		assertEquals("/", CheckFilesUtils.specialCharReplace("FORWARDSLASH"));
		assertEquals("\\(", CheckFilesUtils.specialCharReplace("OPEN_PAREN"));
		assertEquals("\\)", CheckFilesUtils.specialCharReplace("CLOSE_PAREN"));
		assertEquals("\\[", CheckFilesUtils.specialCharReplace("OPEN_BRACKET"));
		assertEquals("]", CheckFilesUtils.specialCharReplace("CLOSE_BRACKET"));
		assertEquals("\\{", CheckFilesUtils.specialCharReplace("OPEN_CURLY"));
		assertEquals("}", CheckFilesUtils.specialCharReplace("CLOSE_CURLY"));
		assertEquals("^\\.", CheckFilesUtils.specialCharReplace("LEADING_PERIOD"));
		assertEquals("^\\\\", CheckFilesUtils.specialCharReplace("LEADING_BACKSLASH"));
		assertEquals("", CheckFilesUtils.specialCharReplace(""));
	}

	@Test
	public void testReplaceAllControl() {
		try {
			assertEquals("asdf", "asdf".replaceAll("\\$", "qwerty"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testReplaceAllQuestionMark() {
		try {
			assertEquals("as%XYdf", "as?df".replaceAll(CheckFilesUtils.specialCharReplace("QUESTION_MARK"), "%XY"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testReplaceAllPlus() {
		try {
			assertEquals("as%XYdf", "as+df".replaceAll(CheckFilesUtils.specialCharReplace("PLUS"), "%XY"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testReplaceAllBlank() {
		try {
			assertEquals("as%XYdf", "as df".replaceAll(CheckFilesUtils.specialCharReplace("BLANK"), "%XY"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testReplaceAllSpaceBackslash() {
		try {
			assertEquals("as%XYdf", "as \\df".replaceAll(CheckFilesUtils.specialCharReplace("SPACE_BACKSLASH"), "%XY"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testReplaceAllBackslash() {
		try {
			assertEquals("as %XYdf", "as \\df".replaceAll(CheckFilesUtils.specialCharReplace("BACKSLASH"), "%XY"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testReplaceAllForwardslash() {
		try {
			assertEquals("as %XYdf", "as /df".replaceAll(CheckFilesUtils.specialCharReplace("FORWARDSLASH"), "%XY"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testReplaceAllOpenParen() {
		try {
			assertEquals("as%XYdf", "as(df".replaceAll(CheckFilesUtils.specialCharReplace("OPEN_PAREN"), "%XY"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testReplaceAllCloseParen() {
		try {
			assertEquals("as%XYdf", "as)df".replaceAll(CheckFilesUtils.specialCharReplace("CLOSE_PAREN"), "%XY"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testReplaceAllOpenBracket() {
		try {
			assertEquals("as%XYdf", "as[df".replaceAll(CheckFilesUtils.specialCharReplace("OPEN_BRACKET"), "%XY"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testReplaceAllCloseBracket() {
		try {
			assertEquals("as%XYdf", "as]df".replaceAll(CheckFilesUtils.specialCharReplace("CLOSE_BRACKET"), "%XY"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testReplaceAllOpenCurly() {
		try {
			assertEquals("as%XYdf", "as{df".replaceAll(CheckFilesUtils.specialCharReplace("OPEN_CURLY"), "%XY"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testReplaceAllCloseCurly() {
		try {
			assertEquals("as%XYdf", "as}df".replaceAll(CheckFilesUtils.specialCharReplace("CLOSE_CURLY"), "%XY"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testReplaceAllLeadingPeriod() {
		try {
			assertEquals("%XYasd.f", ".asd.f".replaceAll(CheckFilesUtils.specialCharReplace("LEADING_PERIOD"), "%XY"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}


	@Test
	public void testReplaceAllSlashAndPeriod() {
		try {
			assertEquals("%XYasd.f", ".asd.f".replaceAll(CheckFilesUtils.specialCharReplace("LEADING_PERIOD"), "%XY"));
			assertEquals("as\\d.f", "as\\d.f".replaceAll(CheckFilesUtils.specialCharReplace("LEADING_PERIOD"), "%XY"));
			assertEquals(".asd%XY.f", ".asd\\.f".replaceAll(CheckFilesUtils.specialCharReplace("BACKSLASH"), "%XY"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}


	@Test
	public void testReplaceAllPipes() {
		try {
			assertEquals("as%XYdf", "as|df".replaceAll(CheckFilesUtils.specialCharReplace("PIPE"), "%XY"));
			assertEquals("%XYasdf", "|asdf".replaceAll(CheckFilesUtils.specialCharReplace("PIPE"), "%XY"));
			assertEquals("asdf%XY", "asdf|".replaceAll(CheckFilesUtils.specialCharReplace("PIPE"), "%XY"));
			assertEquals("asdf", "asdf".replaceAll(CheckFilesUtils.specialCharReplace("PIPE"), "%XY"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testReplaceAllAsterisks() {
		try {
			assertEquals("as%XYdf", "as*df".replaceAll(CheckFilesUtils.specialCharReplace("ASTERISK"), "%XY"));
			assertEquals("%XYasdf", "*asdf".replaceAll(CheckFilesUtils.specialCharReplace("ASTERISK"), "%XY"));
			assertEquals("asdf%XY", "asdf*".replaceAll(CheckFilesUtils.specialCharReplace("ASTERISK"), "%XY"));
			assertEquals("asdf", "asdf".replaceAll(CheckFilesUtils.specialCharReplace("ASTERISK"), "%XY"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testReplaceAllCarots() {
		try {
			assertEquals("as%XYdf", "as^df".replaceAll(CheckFilesUtils.specialCharReplace("CARET"), "%XY"));
			assertEquals("%XYasdf", "^asdf".replaceAll(CheckFilesUtils.specialCharReplace("CARET"), "%XY"));
			assertEquals("asdf%XY", "asdf^".replaceAll(CheckFilesUtils.specialCharReplace("CARET"), "%XY"));
			assertEquals("asdf", "asdf".replaceAll(CheckFilesUtils.specialCharReplace("CARET"), "%XY"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testReplaceAllLeadingBackslash() {
		try {
			assertEquals("as\\df", "as\\df".replaceAll(CheckFilesUtils.specialCharReplace("LEADING_BACKSLASH"), ""));
			assertEquals("asdf", "\\asdf".replaceAll(CheckFilesUtils.specialCharReplace("LEADING_BACKSLASH"), ""));
			assertEquals("asdf\\", "asdf\\".replaceAll(CheckFilesUtils.specialCharReplace("LEADING_BACKSLASH"), ""));
			assertEquals("asdf", "asdf".replaceAll(CheckFilesUtils.specialCharReplace("LEADING_BACKSLASH"), ""));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
