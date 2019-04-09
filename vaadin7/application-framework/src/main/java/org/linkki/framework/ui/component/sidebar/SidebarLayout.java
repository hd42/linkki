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
package org.linkki.framework.ui.component.sidebar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import org.linkki.framework.ui.LinkkiStyles;

import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Component consisting of a sidebar and a content area.
 * 
 * @implSpec Elements must be added with one of the {@code addSheets} methods or
 *           {@link #addSheet(SidebarSheet)} method. The first added sheet is selected by default, but
 *           another can be chosen via {@link #select(SidebarSheet)}.
 *           <p>
 *           The {@link SidebarLayout} can be styled with the {@link LinkkiStyles#SIDEBAR_LAYOUT},
 *           {@link LinkkiStyles#SIDEBAR} and {@link LinkkiStyles#SIDEBAR_CONTENT} style classes.
 *           Selected buttons in the sidebar are additionally styled with
 *           {@link LinkkiStyles#SIDEBAR_SELECTED}.
 */
public class SidebarLayout extends CssLayout {

    private static final long serialVersionUID = -8283594476427895723L;

    private final CssLayout sidebar;

    private final CssLayout contentArea;

    private final List<SidebarSheet> sidebarSheets = new ArrayList<>();
    @CheckForNull
    private SidebarSheet selected;

    public SidebarLayout() {
        this.sidebar = new CssLayout();
        this.contentArea = new CssLayout();

        // must be set for the splitpanels inside to zoom correctly
        setSizeFull();
        contentArea.setSizeFull();

        setStyleName(LinkkiStyles.SIDEBAR_LAYOUT);
        sidebar.setStyleName(LinkkiStyles.SIDEBAR);
        contentArea.setStyleName(LinkkiStyles.SIDEBAR_CONTENT);

        addComponent(sidebar);
        addComponent(contentArea);
    }

    public void addSheets(Stream<SidebarSheet> sheets) {
        sheets.forEach(this::addSheet);
    }

    public void addSheets(Iterable<SidebarSheet> sheets) {
        sheets.forEach(this::addSheet);
    }

    public void addSheets(@NonNull SidebarSheet... sheets) {
        addSheets(Arrays.stream(sheets));
    }

    public void addSheet(SidebarSheet sheet) {
        sidebarSheets.add(sheet);
        sheet.getButton().addClickListener(e -> select(sheet));
        sidebar.addComponent(sheet.getButton());

        if (selected == null) {
            select(sheet);
        }
    }

    /**
     * Selects the given sheet.
     * 
     * @param sheet A sheet contained in this layout
     */
    public void select(SidebarSheet sheet) {
        if (!sidebarSheets.contains(sheet)) {
            throw new IllegalArgumentException("The sheet " + sheet + " is not part of this SidebarLayout");
        }
        if (selected == sheet) {
            return;
        }
        Component content = sheet.getContent();
        if (content.getParent() == null) {
            contentArea.addComponent(content);
        } else if (content.getParent() != contentArea) {
            throw new IllegalStateException("Content of the selected sidebar sheet already has a parent!");
        }

        if (selected != null) {
            selected.unselect();
        }
        this.selected = sheet;
        sheet.select();
    }

    /**
     * Returns the selected {@link SidebarSheet}. If the {@link SidebarLayout} is instantiated with any
     * sheets there must always be a selected one.
     * 
     * @return The selected {@link SidebarSheet}.
     * 
     * @throws NoSuchElementException if there is no {@link SidebarSheet} at all.
     */
    public SidebarSheet getSelected() {
        if (selected != null) {
            return selected;
        } else {
            throw new NoSuchElementException();
        }
    }

    /**
     * Return all registered {@link SidebarSheet}.
     * 
     * @return The list of available {@link SidebarSheet}s in the order they were added
     */
    public List<SidebarSheet> getSidebarSheets() {
        return Collections.unmodifiableList(sidebarSheets);
    }

    /**
     * For testing purposes only.
     */
    /* private */ CssLayout getContentArea() {
        return contentArea;
    }

    /**
     * For testing purposes only.
     */
    /* private */ CssLayout getSidebar() {
        return sidebar;
    }
}
