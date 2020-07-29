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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WhereClauseExpression<T> {
  private static final Logger logger = LogManager.getLogger(WhereClauseExpression.class);

  private String expression;
  private T parm;
  private int index;

  public WhereClauseExpression(String expression, T value, int index) {
    this.expression = expression;
    this.parm = value;
    this.index = index;
  }

  public String getExpression() {
    return expression;
  }

  public void setParm(PreparedStatement stmt) throws SQLException {
    if (parm instanceof Integer) {
      stmt.setInt(index, (Integer) parm);
    } else {
      stmt.setString(index, parm.toString());
    }
    logger.info("Setting parameter.  Index=[{}], value=[{}].", index, parm);
  }

  public static String makeWhereClause(List<WhereClauseExpression> exprs) {
    StringBuilder whereClause = new StringBuilder();
    if (exprs.size() > 0) {
      whereClause.append("where");
    }
    for (int i = 0; i < exprs.size(); i++) {
      WhereClauseExpression wce = exprs.get(i);
      whereClause.append(" ").append(wce.getExpression());
      if (i != (exprs.size() - 1)) {
        whereClause.append(" AND");
      } else {
        whereClause.append(" ");
      }
    }
    return whereClause.toString();
  }

  public static void setParms(PreparedStatement stmt, List<WhereClauseExpression> exprs)
      throws SQLException {
    for (WhereClauseExpression wce : exprs) {
      wce.setParm(stmt);
    }
  }
}
