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

import java.util.List;

public class ResultComparison {
	private List<ResultsRow> onlyInFirst;
	private List<ResultsRow> onlyInSecond;
	private String firstName;
	private String secondName;

	public ResultComparison(String firstName, List<ResultsRow> onlyInFirst,
			String secondName, List<ResultsRow> onlyInSecond) {
		this.onlyInFirst = onlyInFirst;
		this.firstName = firstName;
		this.onlyInSecond = onlyInSecond;
		this.secondName = secondName;
	}

	public List<ResultsRow> getOnlyInFirst() {
		return onlyInFirst;
	}

	public List<ResultsRow> getOnlyInSecond() {
		return onlyInSecond;
	}

	public boolean areReportsEqual() {
		return onlyInFirst.isEmpty() && onlyInSecond.isEmpty();
	}

	public String getFirstName() {
		return firstName;
	}

	public String getSecondName() {
		return secondName;
	}
}
