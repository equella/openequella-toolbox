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

package org.apereo.openequella.tools.toolbox.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apereo.openequella.tools.toolbox.Config;
import org.apereo.openequella.tools.toolbox.checkFiles.ResultComparison;
import org.apereo.openequella.tools.toolbox.checkFiles.ResultsRow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class CheckFilesUtils {
private static final Logger logger = LogManager.getLogger(CheckFilesUtils.class);

	/**
	 * Walk through both files, ignoring Institution, timestamps, and duration.
	 * Sorts the file rows, and only checks specific values of the csv row read
	 * in.
	 *
	 * @param r1
	 * @param r2
	 * @return
	 * @throws IOException
	 */
	public static ResultComparison looseCompare(File r1, File r2) {
		try {
			if (r1 == null) {
				logger.warn("First comparison file is null, unable to build a comparison report.");
				return null;
			}
			if (r2 == null) {
				logger.warn("Second comparison file is null, unable to build a comparison report.");
				return null;
			}
			logger.info("Beginning to loose compare [{}] and [{}]",
							r1.getName(), r2.getName());
			Map<String, ResultsRow> rows1 = slurpAndNormalizeCsvFile(r1);
			logger.info("Found [{}] result rows in [{}]", rows1.size(),
							r1.getName());
			Map<String, ResultsRow> rows2 = slurpAndNormalizeCsvFile(r2);
			logger.info("Found [{}] result rows in [{}]", rows2.size(),
							r2.getName());
			Set<String> keys1 = rows1.keySet();
			Set<String> keys2 = rows2.keySet();

			List<ResultsRow> onlyInFirst = new ArrayList<ResultsRow>();
			for (String s : findOnlyInFirst(keys1, keys2)) {
				onlyInFirst.add(rows1.get(s));
			}
			List<ResultsRow> onlyInSecond = new ArrayList<ResultsRow>();
			for (String s : findOnlyInFirst(keys2, keys1)) {
				onlyInSecond.add(rows2.get(s));
			}
			return new ResultComparison(r1.getName(), onlyInFirst,
							r2.getName(), onlyInSecond);
		} catch (Exception e) {
			logger.error("Error trying to compare results:  {}",
							e.getMessage(), e);
			return null;
		}
	}

	//Only returns the n-1 file if n is greater than 1.
	// It doesn't return the latest file since that just occured.
	public static File findPreviousErrorStats() {
		File dir = new File(Config.get(Config.CF_OUTPUT_FOLDER));
		String[] errorStats = dir.list(new OnlyErrorStatsFileFilter());

		if(errorStats == null) {
			return null;
		}

		List<String> errorStatsArray = Arrays.asList(errorStats);
		Collections.sort(errorStatsArray);
		File f = null;
		if (errorStatsArray.size() > 1) {
			f = new File(Config.get(Config.CF_OUTPUT_FOLDER),
					errorStatsArray.get(errorStatsArray.size() - 2));
		}
		return f;
	}

	public static Collection<String> findOnlyInFirst(Set<String> s1,
																									 Set<String> s2) {
		Set<String> base = new HashSet<String>();
		base.addAll(s1);
		base.removeAll(s2);
		return base;
	}

	private static Map<String, ResultsRow> slurpAndNormalizeCsvFile(File f)
			throws Exception {
		Map<String, ResultsRow> rows = new HashMap<String, ResultsRow>();
		if (!f.exists()) {
			return rows;
		}
		BufferedReader br = new BufferedReader(new FileReader(f));
		String row = br.readLine();
		while (row != null) {
			if (row.startsWith("Institution") || row.startsWith("CheckFiles type")
					|| row.startsWith("Report Timestamp")
					|| row.startsWith("Report Duration")
					|| row.startsWith("Database URL")
					|| row.startsWith("Database Type")
					|| row.startsWith("Filestore attachments")
					|| row.startsWith("Filter By Institution")
					|| row.startsWith("Filter By Collection")
					|| row.startsWith("# Of") || row.startsWith("Item UUID")
					|| row.startsWith("Stats")
					|| row.startsWith("Report Start")
					|| row.startsWith("Report End")
					|| row.startsWith("# Of queries ran")
					|| row.startsWith("Total duration of queries (ms)")
					|| row.startsWith("Average duration of queries (ms)")) {
				// Ignore - not a 'results row'.
			} else {
				ResultsRow parsedRow = slurpAndNormalizeCsvRow(row);
				rows.put(parsedRow.getPrimaryKey(), parsedRow);
			}
			row = br.readLine();
		}
		br.close();
		return rows;
	}

	/**
	 * Lines parsed into: Item UUID Item Version ItemStatus Collection UUID
	 * Attachment Type Attachment UUID Attachment Status Attachment Response
	 * Code Item Name *** Attachment Filepath ***
	 *
	 * *** Quoted CSV data. not used in comparisons, thus not set.
	 *
	 * @param row
	 * @returnt
	 * @throws Exception
	 */
	private static ResultsRow slurpAndNormalizeCsvRow(String row)
			throws Exception {
		StringTokenizer csv = new StringTokenizer(row, ",");
		if (csv.countTokens() < 10) {
			throw new Exception(String.format(
					"CSV row must have 11 tokens.  row=[%s]", row));
		}
		ResultsRow rr = new ResultsRow();
		rr.setInstitutionShortname(csv.nextToken());
		rr.setCollectionUuid(csv.nextToken());
		rr.setItemUuid(csv.nextToken());
		rr.setItemVersion(csv.nextToken());
		rr.setItemStatus(csv.nextToken().toUpperCase());
		rr.setAttType(normalizeAttType(csv.nextToken()));
		rr.setAttUuid(csv.nextToken());
		rr.setAttStatus(csv.nextToken());
		rr.setAttRespCode(csv.nextToken());
		//Jump one token, and then set the attachment file path
		csv.nextToken();
		rr.setAttFilePath(csv.nextToken());
		return rr;
	}

	private static String normalizeAttType(String nextToken) {
		switch (nextToken.toUpperCase()) {
		case "HTMLPAGE":
		case "HTML":
			return "HTMLPAGE";
		}
		return nextToken.toUpperCase();
	}

	private static boolean comp(String s1, String s2, String match) {
		return s1.startsWith(match) && s2.startsWith(match);
	}

	public static String str(String msg, Object... args) {
		return String.format(msg, args);
	}

	public static String specialCharReplace(String val) {
		switch(val) {
			case "PLUS": { return "\\+";	}
			case "BLANK": { return " "; }
			case "QUESTION_MARK": { return "\\?"; }
			case "SPACE_BACKSLASH": { return " \\\\"; }
			case "BACKSLASH": { return "\\\\"; }
			case "FORWARDSLASH": { return "/"; }
			case "OPEN_PAREN": { return "\\("; }
			case "CLOSE_PAREN": { return "\\)"; }
			case "OPEN_BRACKET": { return "\\["; }
			case "CLOSE_BRACKET": { return "]"; }
			case "OPEN_CURLY": { return "\\{"; }
			case "CLOSE_CURLY": { return "}"; }
			case "LEADING_PERIOD": { return "^\\."; }
			case "PIPE": { return "\\|"; }
			case "ASTERISK": { return "\\*"; }
			case "CARET": { return "\\^"; }
			default: { return val; }
		}
	}
}
