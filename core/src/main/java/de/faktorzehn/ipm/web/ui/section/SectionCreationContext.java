/*******************************************************************************
 * Copyright (c) 2014 Faktor Zehn AG.
 *
 * Alle Rechte vorbehalten.
 *******************************************************************************/

package de.faktorzehn.ipm.web.ui.section;

import static com.google.gwt.thirdparty.guava.common.base.Preconditions.checkNotNull;

import java.util.Optional;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

import de.faktorzehn.ipm.web.ButtonPmo;
import de.faktorzehn.ipm.web.PresentationModelObject;
import de.faktorzehn.ipm.web.binding.BindingContext;
import de.faktorzehn.ipm.web.binding.ButtonPmoBinding;
import de.faktorzehn.ipm.web.binding.dispatcher.PropertyBehaviorProvider;
import de.faktorzehn.ipm.web.binding.dispatcher.PropertyDispatcher;
import de.faktorzehn.ipm.web.binding.dispatcher.PropertyDispatcherFactory;
import de.faktorzehn.ipm.web.ui.section.annotations.ElementDescriptor;
import de.faktorzehn.ipm.web.ui.section.annotations.SectionLayout;
import de.faktorzehn.ipm.web.ui.section.annotations.UIAnnotationReader;
import de.faktorzehn.ipm.web.ui.section.annotations.UISection;

/**
 * Object holding the state about PMO and binding context, as well as the property dispatcher in
 * use, while creating a section for them. Intended to be used only once.
 *
 * @author widmaier
 */
public class SectionCreationContext {

    private static final PropertyDispatcherFactory DISPATCHER_FACTORY = new PropertyDispatcherFactory();

    private final PresentationModelObject pmo;
    private final BindingContext bindingContext;
    private final PropertyBehaviorProvider propertyBehaviorProvider;

    private PropertyDispatcher propertyDispatcher;

    public SectionCreationContext(PresentationModelObject pmo, BindingContext bindingContext,
            PropertyBehaviorProvider propertyBehaviorProvider) {
        this.pmo = pmo;
        this.bindingContext = bindingContext;
        this.propertyBehaviorProvider = propertyBehaviorProvider;
    }

    protected PropertyDispatcher createDefaultDispatcher(Object o) {
        return DISPATCHER_FACTORY.defaultDispatcherChain(o, propertyBehaviorProvider);
    }

    protected PresentationModelObject getPmo() {
        return pmo;
    }

    protected Object getModelObject() {
        return pmo.getModelObject();
    }

    public BaseSection createSection() {
        BaseSection section = createEmptySection();
        createUiElements(section);
        return section;
    }

    private BaseSection createEmptySection() {
        BaseSection section;
        UISection sectionDefinition = pmo.getClass().getAnnotation(UISection.class);
        checkNotNull(sectionDefinition, "PMO " + pmo.getClass() + " must be annotated with @UISection!");
        Optional<Button> editButton = createEditButton(pmo.getEditButtonPmo());
        if (sectionDefinition.layout() == SectionLayout.COLUMN) {
            section = new FormSection(sectionDefinition.caption(), sectionDefinition.closeable(), editButton,
                    sectionDefinition.columns());
        } else {
            section = new HorizontalSection(sectionDefinition.caption(), sectionDefinition.closeable(), editButton);
        }
        return section;
    }

    private Optional<Button> createEditButton(Optional<ButtonPmo> buttonPmo) {
        return buttonPmo.map(b -> ButtonPmoBinding.createBoundButton(bindingContext, b, createDefaultDispatcher(b)));
    }

    private void createUiElements(BaseSection section) {
        UIAnnotationReader annotationReader = new UIAnnotationReader(getPmo().getClass());
        for (ElementDescriptor uiElement : annotationReader.getUiElements()) {
            SectionCreationContext.LabelComponent lf = createLabelAndComponent(section, uiElement);
            bindUiElement(lf.component, uiElement, lf.label);
        }
    }

    private SectionCreationContext.LabelComponent createLabelAndComponent(BaseSection section,
            ElementDescriptor uiElement) {
        Component component = uiElement.newComponent();
        String labelText = uiElement.getLabelText();
        Label label = section.add(labelText, component);
        return new LabelComponent(label, component);
    }

    private void bindUiElement(Component component, ElementDescriptor uiElement, Label label) {
        uiElement.createBinding(bindingContext, pmo, label, component, getPropertyDispatcher());
    }

    protected PropertyDispatcher getPropertyDispatcher() {
        if (propertyDispatcher == null) {
            propertyDispatcher = createDefaultDispatcher(pmo);
        }
        return propertyDispatcher;
    }

    private static class LabelComponent {
        final Label label;
        final Component component;

        LabelComponent(Label label, Component component) {
            this.label = label;
            this.component = component;
        }
    }

}