/*
 * Copyright Faktor Zehn GmbH.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */

package org.linkki.samples.appsample.pmo;

import java.time.LocalDate;

import org.linkki.core.message.Message;
import org.linkki.core.message.MessageList;
import org.linkki.core.message.Severity;
import org.linkki.core.ui.section.annotations.BindReadOnly;
import org.linkki.core.ui.section.annotations.BindReadOnly.ReadOnlyType;
import org.linkki.core.ui.section.annotations.ModelObject;
import org.linkki.core.ui.section.annotations.RequiredType;
import org.linkki.core.ui.section.annotations.UIComboBox;
import org.linkki.core.ui.section.annotations.UIDateField;
import org.linkki.core.ui.section.annotations.UITableColumn;
import org.linkki.core.ui.section.annotations.UITableColumn.CollapseMode;
import org.linkki.core.ui.section.annotations.UITextArea;
import org.linkki.samples.appsample.model.Report;
import org.linkki.samples.appsample.model.ReportType;

import edu.umd.cs.findbugs.annotations.NonNull;

public class ReportPmo {

    protected Report report;

    public ReportPmo(Report report) {
        this.report = report;
    }

    @ModelObject
    public Report getReport() {
        return report;
    }

    protected void newReport() {
        report = new Report();
    }

    @UIComboBox(position = 10, label = "Type", modelAttribute = "type", required = RequiredType.REQUIRED)
    @BindReadOnly(ReadOnlyType.DERIVED)
    public void type() {
        /*
         * bind value to the property "type" from report and use enum constants from ReportType as
         * available values
         */
    }

    @UIDateField(position = 20, label = "Occurrence date", modelAttribute = "occurrenceDate")
    @BindReadOnly(ReadOnlyType.DYNAMIC)
    public void occurenceDate() {
        /* bind value to pmo property */
    }

    public boolean isOccurenceDateReadOnly() {
        return report.getType() != ReportType.BUG;
    }

    @UITableColumn(collapsible = CollapseMode.COLLAPSIBLE)
    @UITextArea(position = 30, label = "Description", modelAttribute = "description", required = RequiredType.REQUIRED, rows = 2, width = "50em")
    public void description() {
        /* Use description from report (model object) directly */
    }

    @NonNull
    public MessageList validate() {
        MessageList messages = new MessageList();
        if (ReportType.BUG == report.getType() && report.getOccurrenceDate() == null) {
            messages.add(Message.builder("Date must not be empty", Severity.ERROR).create());
        } else if (report.getOccurrenceDate() != null && report.getOccurrenceDate().isAfter(LocalDate.now())) {
            messages.add(Message.builder("Date must not be in the future", Severity.ERROR).create());
        }

        if (report.getDescription().length() < 100) {
            messages.add(Message.newWarning("warning.emptyDescription",
                                            "A detailed description would help us better understand your report. Your description \""
                                                    + report.getDescription()
                                                    + "\" contains less than 100 charaters."));
        }

        if (ReportType.BUG == report.getType()) {
            messages.add(Message
                    .newInfo("info.screenshot",
                             "A screenshot always helps us better understand the issue you are encountering."));
        }
        return messages;
    }
}