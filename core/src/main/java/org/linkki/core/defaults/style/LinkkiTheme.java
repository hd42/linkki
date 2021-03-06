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
package org.linkki.core.defaults.style;

/**
 * Constants for the CSS styles used throughout the application.
 */
public class LinkkiTheme {

    /** Style for a horizontal spacer */
    public static final String HORIZONTAL_SPACER = "linkki-horizontal-spacer";

    /** Style for the section layout component */
    public static final String SECTION = "linkki-section";

    /** Style for the caption of a section. */
    public static final String SECTION_CAPTION = "linkki-section-caption";

    /** Style for the caption of a section. */
    public static final String SECTION_CAPTION_TEXT = "linkki-section-caption-text";

    /** Style for the line separating the section's header/caption from the body/content. */
    public static final String SECTION_CAPTION_LINE = "linkki-section-caption-line";

    /**
     * Style for components that should be rendered without a border, for example components in tables
     */
    public static final String BORDERLESS = "borderless";

    /** Style for tables created by linkki */
    public static final String TABLE = "linkki-table";

    /** Style for a single cell within a linkki table */
    public static final String TABLE_CELL = "linkki-table-cell";

    /**
     * @deprecated since February 18th 2019, horizontal-section-spacing will not affect linkki vaadin8
     */
    @Deprecated
    public static final String SPACING_HORIZONTAL_SECTION = "horizontal-section-spacing";

    /** A button that is styled as a text, without additional height. **/
    public static final String BUTTON_TEXT = "linkki-button-text";

    private LinkkiTheme() {
        // prevent initialization
    }

}
