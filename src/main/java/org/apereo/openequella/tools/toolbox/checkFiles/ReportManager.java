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

package org.apereo.openequella.tools.toolbox.checkFiles;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apereo.openequella.tools.toolbox.Config;
import org.apereo.openequella.tools.toolbox.utils.Check;
import org.apereo.openequella.tools.toolbox.utils.CheckFilesUtils;
import org.apereo.openequella.tools.toolbox.utils.EmailUtils;
import org.apereo.openequella.tools.toolbox.utils.Stats;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ReportManager {
	private static final Logger logger = LogManager
			.getLogger(ReportManager.class);

	private Stats stats;
	private ResultComparison comparison;
	private List<String> fatalErrors;

	private String stdOut;
	private String errOut;

	private BufferedWriter allStatsWriter;
	private BufferedWriter errorStatsWriter;

	private boolean noFiles = false;

	// For testing
	private List<String> allStatsWriterList;
	private List<String> errorStatsWriterList;

	private static ReportManager mgr = null;

	/**
	 * Assumes Config.setup() was successful.
	 */
	private ReportManager() {
	}

	public static void reset() {
		mgr = null;
	}

	public void setup(boolean noFiles) {
		this.noFiles = noFiles;
		stats = new Stats();
		stats.setReportStartTime();
		fatalErrors = new ArrayList<String>();

		try {
			stdOut = String.format("%s/oEQ_checkFiles_%s_%s_all_stats_%s.csv",
							Config.get(Config.CF_OUTPUT_FOLDER),
							Config.get(Config.CF_MODE),
							Config.get(Config.CF_ADOPTER_NAME), stats
					.getStartTimeStr());
			logger.info("Setting up all-inclusive report:  [{}]", stdOut);

			if(noFiles) {
				allStatsWriterList = new ArrayList<>();
			} else {
				// Just in case...
				new File(Config.get(Config.CF_OUTPUT_FOLDER)).mkdirs();

				File allStatsFile = new File(stdOut);
				allStatsFile.createNewFile();
				allStatsWriter = new BufferedWriter(new FileWriter(allStatsFile));
			}

			errOut = String.format("%s/oEQ_checkFiles_%s_%s_error_stats_%s.csv",
							Config.get(Config.CF_OUTPUT_FOLDER),
							Config.get(Config.CF_MODE),
							Config.get(Config.CF_ADOPTER_NAME), stats
					.getStartTimeStr());
			logger.info("Setting up error report:  [{}]", errOut);

			if(noFiles) {
				errorStatsWriterList = new ArrayList<>();
			} else {
				File errorStatsFile = new File(errOut);
				errorStatsFile.createNewFile();
				errorStatsWriter = new BufferedWriter(
								new FileWriter(errorStatsFile));
			}

		} catch (IOException e) {
			logger.error("Unable to setup file writers: {}", e.getMessage(), e);
		}

	}

	public static ReportManager getInstance() {
		if (mgr == null) {
			mgr = new ReportManager();
		}
		return mgr;
	}

	public boolean isValid() {
		if(noFiles) return true;
		return (errorStatsWriter != null) && (allStatsWriter != null);
	}

	public String getStdOutFilename() {
		return stdOut;
	}

	public String getErrOutFilename() {
		return errOut;
	}

	public boolean finalizeReporting() {
		try {
			stats.setReportEndTime();
			// Send report stats to log4j
			displayStats();

			// Send report stats to file writers
			generateCsvStats();

			// Compare if enabled
      final String compareEnabled = Config.get(Config.CF_COMPARE_MISSING_ATTS_AFTER_RUN);
      if (!Check.isEmpty(compareEnabled) && Boolean.valueOf(compareEnabled)) {
				File prevErrors = CheckFilesUtils.findPreviousErrorStats();

				comparison = CheckFilesUtils.looseCompare(prevErrors,
						new File(errOut));
				if (comparison == null) {
					String msg = "Unable to generate a comparison result.";
					logger.warn(msg);
					addFatalError(msg);
				} else if (!comparison.areReportsEqual()) {
					logger.info("Comparison result:  This report and the last report DO NOT have the same missing attachments!");
					logger.info("Resolved attachments from [{}]",
							comparison.getFirstName());
					for (ResultsRow r : comparison.getOnlyInFirst()) {
						logger.info("- {}", r);
					}
					logger.info("New attachment issues from [{}]",
							comparison.getSecondName());
					for (ResultsRow r : comparison.getOnlyInSecond()) {
						logger.info("- {}", r);
					}
				} else {
					logger.info("Comparison result:  This report and the last report HAVE the same missing attachments!");
				}
			}

			// Email if enabled
			if (Config.getCheckFilesEmailModeConfig() == Config.CheckFilesEmailMode.NORMAL) {
				kickOffEmailHtmlReport("normal");
			} else if ((Config.getCheckFilesEmailModeConfig() == Config.CheckFilesEmailMode.ONLY_NEW_MISSING_ATTACHMENTS_OR_ERRORS)
					&& (comparison != null) && !comparison.areReportsEqual()) {
				kickOffEmailHtmlReport("change in missing attachments");
			} else if ((Config.getCheckFilesEmailModeConfig() == Config.CheckFilesEmailMode.ONLY_NEW_MISSING_ATTACHMENTS_OR_ERRORS)
					&& (comparison != null) && comparison.areReportsEqual()) {
				logger.error("No change in missing attachments, so not sending out the 'only missing attachments' email report.");
			} else if ((Config.getCheckFilesEmailModeConfig() == Config.CheckFilesEmailMode.ONLY_NEW_MISSING_ATTACHMENTS_OR_ERRORS)
					&& (comparison == null)) {
				logger.error("Email ONLY_NEW_MISSING_ATTACHMENTS_OR_ERRORS specified, but comparison was unable to be built - sending an email anyways!.");
				kickOffEmailHtmlReport("change in missing attachments - no compare");
			} else {
				logger.info("No email report specified to be sent.");
			}
			closeWriters();
			return true;
		} catch (IOException e) {
			logger.error("Unable to finalize reporting due to: {}",
					e.getMessage(), e);
			return false;
		}
	}

	private void kickOffEmailHtmlReport(String reportType) {
		int numOfErrors = fatalErrors.size();
		EmailUtils.execute("HTML", Config.get(Config.CF_EMAIL_RECIPIENTS), getReportHeader(), buildHtmlReport());
		if (numOfErrors != fatalErrors.size()) {
			logger.error("Unable to send a '{}' email report due to: {}",
					reportType, getLastFatalError());
		} else {
			logger.error("Email '{}' report sent.", reportType);
		}

	}

	/**
	 * 
	 * @return null if no errors, otherwise the last fatal error string
	 */
	public String getLastFatalError() {
		String msg = null;
		if (hasFatalErrors()) {
			msg = this.fatalErrors.get(this.fatalErrors.size() - 1);
		}
		return msg;
	}

	public boolean failFast() {
		boolean success = true;
		try {
			// Email if enabled
			if (Config.CheckFilesEmailMode.valueOf(Config.get(Config.CF_EMAIL_MODE)) != Config.CheckFilesEmailMode.NONE) {
				int numOfErrors = fatalErrors.size();
				EmailUtils.execute("HTML", Config.get(Config.CF_EMAIL_RECIPIENTS),"CRITICAL FAILURE: "+getReportHeader(), buildFailFastReport());
				if (numOfErrors != fatalErrors.size()) {
					logger.error("Unable to send failfast notification email due to: {}",
							getLastFatalError());
					success = false;
				}
			}
			closeWriters();
			logger.error("Failfast complete? {}", success);
			return success;
		} catch (IOException e) {
			logger.error("Unable to failfast due to: {}", e.getMessage(), e);
			return false;
		}
	}

	public void dualWriteLn(String str, Object... args) throws IOException {
		stdOutWriteln(str, args);
		errOutWriteln(str, args);
	}

	public void stdOutWriteln(String str, Object... args) throws IOException {
		internalWriteln(allStatsWriter, allStatsWriterList, str, args);
	}

	public void errOutWriteln(String str, Object... args) throws IOException {
		internalWriteln(errorStatsWriter, errorStatsWriterList, str, args);
	}

	private void internalWriteln(BufferedWriter bw, List alternativeResults, String str, Object... args) throws IOException {
		String msg = str;

		if ((args != null) && (args.length > 0)) {
			msg = String.format(str, args);
		}

		if(noFiles) {
			alternativeResults.add(msg);
		} else {
			bw.write(msg);
			bw.newLine();
			bw.flush();
		}
	}


	private void closeWriters() throws IOException {
		if(!noFiles) {
			allStatsWriter.close();
			errorStatsWriter.close();
		}
	}

	private void append(StringBuilder sb, String msg, Object... args) {
		sb.append(String.format(msg, args));
	}

	public void generateCsvStats() throws IOException {
		dualWriteLn("Stats");
		dualWriteLn("# Of Items,%d", stats.getNumTotalItems());
		dualWriteLn("# Of Items affected,%d", stats.getNumTotalItemsAffected());
		dualWriteLn("# Of ALL Attachments,%d", stats.getNumTotalAttachments());
		dualWriteLn("# Of MISSING Attachments,%d",
				stats.getNumTotalAttachmentsMissing());
		dualWriteLn("# Of IGNORED Attachments,%d",
				stats.getNumTotalAttachmentsIgnored());
		dualWriteLn("Report Start,%s", stats.getStartTimeStr());
		dualWriteLn("Report End,%s", stats.getEndTimeStr());
		dualWriteLn("Report Duration (seconds),%d", stats.getDuration());
		if (!Config.get(Config.CF_MODE).equalsIgnoreCase("REST")) {
			dualWriteLn("# Of queries ran,%d", stats.getNumOfQueriesRan());
			dualWriteLn("Total duration of queries (ms),%d",
					stats.getTotalDurationOfQueriesRan());
			dualWriteLn("Average duration of queries (ms),%d",
					stats.getAverageDurationOfQueriesRan());
		}
	}

	public void displayStats() throws IOException {
		logger.info("openEQUELLA-Toolbox:CheckFiles report completed.");
		logger.info("All-inclusive report:  [{}]", stdOut);
		logger.info("Error report:  [{}]", errOut);
		logger.info("Stats");
		logger.info("# Of Items: {}", stats.getNumTotalItems());
		logger.info("# Of Items affected:{}", stats.getNumTotalItemsAffected());
		logger.info("# Of ALL Attachments: {}", stats.getNumTotalAttachments());
		logger.info("# Of MISSING Attachments: {}",
				stats.getNumTotalAttachmentsMissing());
		logger.info("# Of IGNORED Attachments: {}",
				stats.getNumTotalAttachmentsIgnored());
		logger.info("Report Start: {}", stats.getStartTimeStr());
		logger.info("Report End: {}", stats.getEndTimeStr());
		logger.info("Report Duration (ms): {}", stats.getDuration());
		if (!Config.get(Config.CF_MODE).equalsIgnoreCase("REST")) {
			logger.info("# Of queries ran: {}", stats.getNumOfQueriesRan());
			logger.info("Total duration of queries (ms): {}",
					stats.getTotalDurationOfQueriesRan());
			logger.info("Average duration of queries (ms): {}",
					stats.getAverageDurationOfQueriesRan());
		}
	}

	public String getReportHeader() {
		return String.format("oEQ-Toolbox:CheckFiles v%s - %s Report", Config.VERSION, stats.getStartTimeStr());
	}

	public String buildHtmlReport() {
		StringBuilder sb = new StringBuilder();
		// Headers
		append(sb, "<html><body>");
		append(sb, "<h2>%s</h2>", getReportHeader());

		// Current run
		append(sb,
				"<div style='color:blue'><b>Details of current run:</b></div><ul>");
		append(sb, "<li><b>Report Duration (seconds)</b> - %d</li>",
				stats.getDuration());
		append(sb, "<li><b>CheckFiles Type</b> - %s</li>", Config.get(Config.CF_MODE));
		append(sb, "<li><b>Adopter Name</b> - %s</li>", Config.get(Config.CF_ADOPTER_NAME));
		append(sb, "<li><b># Of Items Checked</b> - %d</li>",
				stats.getNumTotalItems());
		append(sb, "<li><b># Of Attachments Checked</b> - %d</li>",
				stats.getNumTotalAttachments());
		append(sb, "<li><b># Of Attachments Missing</b> - %d</li>",
				stats.getNumTotalAttachmentsMissing());
		append(sb, "</ul>");

		// List any errors
		append(sb, "<div style='color:blue'><b>oEQ-Toolbox:CheckFiles Errors:</b></div>");
		append(sb, "<ul>");
		if (hasFatalErrors()) {
			for (String fatalErr : fatalErrors) {
				append(sb, "<li>%s</li>", fatalErr);
			}
		} else {
			append(sb, "<div>No fatal errors occurred.</div>");
		}
		append(sb, "</ul>");

		// Comparison
		append(sb,
				"<div style='color:blue'><b>Details of comparison report:</b></div>");
		if (comparison != null) {
			append(sb,
					"<div>Comparison of <i>missing</i> attachments from previous run to current run: ");
			if ((comparison != null) && !comparison.areReportsEqual()) {
				append(sb,
						"<span style='color:red'><b>CHANGED</b></span></div>");
				
				append(sb,
						"<br/><div><i><b>Placement legend: </b> %s</i></div><br/>", ResultsRow.getHeader());
				if (comparison.getOnlyInFirst().size() > 0) {
					append(sb,
							"<div>Attachments that are no longer missing:</div><ul>");
					for (ResultsRow fixed : comparison.getOnlyInFirst()) {
						append(sb, "<li><span style='color:blue'>" + fixed + "</span></li>");
					}
					append(sb, "</ul>");
				}

				if (comparison.getOnlyInSecond().size() > 0) {
					append(sb,
							"<div>Attachments that are now missing:</div><ul>");
					for (ResultsRow broken : comparison.getOnlyInSecond()) {
						append(sb, "<li><span style='color:red'>" + broken + "</span></li>");
					}
					append(sb, "</ul>");
				}
			} else {
				append(sb,
						"<span style='color:blue'><b>NO CHANGE</b></span></div>");
			}
			append(sb,
					"<div><b>Comparison file for previous run:</b> %s</div>",
					comparison.getFirstName());
			append(sb, "<div><b>Comparison file for current run:</b> %s</div>",
					comparison.getSecondName());
		} else {
			append(sb,
					"<div>No comparison specified, or a comparison wasn't able to be made.</div>");
		}
		append(sb,
				"<br/><br/><div><i>Note:  This report is provided as a guide to the results of the oEQ-Toolbox:CheckFiles utility.  " +
				"While efforts have been taken to provide a quality utility to report on the attachment " +
				"status of openEQUELLA items, this utility is provided AS-IS, with no warranty or promise of " +
				"support.</i></div></body></html>");
		return sb.toString();
	}

	public String buildFailFastReport() {
		StringBuilder sb = new StringBuilder();
		append(sb, "<html><body>");
		append(sb, "<h2>%s</h2>", getReportHeader());
		append(sb,
				"<div><span style='color:red'><b>Report run failed due to an unrecoverable error.</b></span></div>");
		if(hasFatalErrors()) {
			append(sb, "<div>Critical errors gathered:</div><div><ul>");
			for(String err : fatalErrors) {
				append(sb, String.format("<li>%s</li>", err));
			}
			append(sb, "</ul></div>");
		}
		append(sb,
				"<div>Please check the Toolbox logs for more details...</div>");
		append(sb,
				"<br/><br/><div><i>Note:  This report is provided as a guide to the results of the CheckFiles utility.  " +
				"While efforts have been taken to provide a quality utility to report on the attachment " +
				"status of openEQUELLA items, this utility is provided AS-IS, with no warranty or promise of " +
				"support.</i></div></body></html>");
		
		return sb.toString();
	}

	public String getReportTimestamp() {
		return stats.getStartTimeStr();
	}

	public Stats getStats() {
		return stats;
	}

	public List<String> getAllStatsWriterList() {
		return this.allStatsWriterList;
	}

	public List<String> getErrorStatsWriterList() {
		return this.errorStatsWriterList;
	}

	public boolean setupReportDetails() {
		try {
			dualWriteLn("CheckFiles type,%s", Config.get(Config.CF_MODE));
			String notSpecified = "[[NONE SPECIFIED]]";
			if (Config.CheckFilesType.valueOf(Config.get(Config.CF_MODE)) == Config.CheckFilesType.REST) {
				dualWriteLn("Institution,%s", Config.get(Config.OEQ_URL));
				if (!Config.get(Config.CF_FILTER_BY_COLLECTION).isEmpty()) {
					dualWriteLn("Filter By Collection UUID,%s", Config.get(Config.CF_FILTER_BY_COLLECTION));
				} else {
					dualWriteLn("Filter By Collection UUID,%s", notSpecified);
				}
			} else {
				dualWriteLn("Database URL,%s", Config.get(Config.CF_DB_URL));
				dualWriteLn("Database Type,%s", Config.get(Config.CF_DB_TYPE));
				dualWriteLn("Filestore attachments directory,%s", Config.get(Config.CF_FILESTORE_DIR));
				if (!Check.isEmpty(Config.get(Config.CF_FILTER_BY_INSTITUTION))) {
					dualWriteLn("Filter By Institution Shortname,%s", Config.get(Config.CF_FILTER_BY_INSTITUTION));
				} else {
					dualWriteLn("Filter By Institution Shortname,%s",
							notSpecified);
				}
				if (!Check.isEmpty(Config.get(Config.CF_FILTER_BY_COLLECTION))) {
					dualWriteLn("Filter By Collection ID,%s", Config.get(Config.CF_FILTER_BY_COLLECTION));
				} else {
					dualWriteLn("Filter By Collection ID,%s", notSpecified);
				}
			}

			dualWriteLn("Report Timestamp,%s", ReportManager.getInstance()
					.getReportTimestamp());

			dualWriteLn(ResultsRow.getHeader());
			return true;
		} catch (IOException e) {
			logger.error("Unable to setup reporting details due to: {}",
					e.getMessage(), e);
			return false;
		}

	}

	public List<String> getFatalErrors() {
		return fatalErrors;
	}

	public void addFatalError(String err) {
		fatalErrors.add(err);
	}

	public boolean hasFatalErrors() {
		return fatalErrors.size() > 0;
	}

	public ResultComparison getComparison() {
		return comparison;
	}

}
