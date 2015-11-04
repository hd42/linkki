/*******************************************************************************
 * Copyright (c) 2014 Faktor Zehn AG.
 * 
 * Alle Rechte vorbehalten.
 *******************************************************************************/

package de.faktorzehn.ipm.web.ui.section;

import java.util.Collections;

/**
 * This is the default implementation of {@link PmoBasedSectionFactory}. If you do not need any
 * specialization of {@link PmoBasedSectionFactory} you are perfectly right to use this one.
 * Example:
 * 
 * <pre>
 * &#064;Inject
 * private DefaultPmoBasedSectionFactory sectionFactory;
 * 
 * </pre>
 *
 * @author dirmeier
 */
public class DefaultPmoBasedSectionFactory extends PmoBasedSectionFactory {

    public DefaultPmoBasedSectionFactory() {
        super(() -> Collections.emptyList());
    }

}