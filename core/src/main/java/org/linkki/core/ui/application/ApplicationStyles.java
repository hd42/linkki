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
package org.linkki.core.ui.application;

/**
 * Constants for the CSS styles used throughout the application.
 */
public class ApplicationStyles {

    /** Style for the menu section of the application. */
    public static final String MENU_WRAPPER = "linkki-menu-wrapper";

    public static final String HORIZONTAL_SPACER = "linkki-horizontal-spacer";

    public static final String FIXED_HORIZONTAL_SPACER = "linkki-fixed-horizontal-spacer";

    public static final String SECTION = "linkki-section";

    /** Style for the caption of a section. */
    public static final String SECTION_CAPTION = "linkki-section-caption";

    /** Style for the caption of a section. */
    public static final String SECTION_CAPTION_TEXT = "linkki-section-caption-text";

    /** Style for the line separating the section's header/caption from the body/content. */
    public static final String SECTION_CAPTION_LINE = "linkki-section-caption-line";

    /** Style for the line separating the section's header/caption from the body/content. */
    public static final String LOGIN_PANEL_CAPTION = "linkki-login-panel-caption";

    public static final String LOGIN_PANEL = "linkki-login-panel";

    public static final String BORDERLESS = "borderless";

    public static final String TABLE = "linkki-table";

    public static final String TABLE_CELL = "linkki-table-cell";

    /**
     * @deprecated since February 18th 2019, horizontal-section-spacing will not affect linkki vaadin8
     */
    @Deprecated
    public static final String SPACING_HORIZONTAL_SECTION = "horizontal-section-spacing";

    public static final String DIALOG_BUTTON_BAR = "linkki-dialog-button-bar";

    public static final String MESSAGE_LABEL = "linkki-message-label";

    public static final String MESSAGE_TABLE = "linkki-message-table";

    public static final String MESSAGE_ROW = "linkki-message-row";

    /** A button that is styled as a text, without additional height. **/
    public static final String BUTTON_TEXT = "linkki-button-text";

    private ApplicationStyles() {
        // prevent initialization
    }

}
