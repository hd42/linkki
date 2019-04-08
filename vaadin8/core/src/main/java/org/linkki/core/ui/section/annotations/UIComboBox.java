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
package org.linkki.core.ui.section.annotations;

import static org.linkki.core.ui.section.annotations.EnabledType.ENABLED;
import static org.linkki.core.ui.section.annotations.RequiredType.NOT_REQUIRED;
import static org.linkki.core.ui.section.annotations.VisibleType.VISIBLE;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.linkki.core.binding.aspect.AspectDefinitionCreator;
import org.linkki.core.binding.aspect.LinkkiAspect;
import org.linkki.core.binding.aspect.definition.LinkkiAspectDefinition;
import org.linkki.core.ui.components.ComponentWrapper;
import org.linkki.core.ui.components.ItemCaptionProvider;
import org.linkki.core.ui.components.ItemCaptionProvider.DefaultCaptionProvider;
import org.linkki.core.ui.section.annotations.UIComboBox.ComboBoxAvailableValuesAspectDefinitionCreator;
import org.linkki.core.ui.section.annotations.adapters.ComboboxBindingDefinition;
import org.linkki.core.ui.section.annotations.aspect.AvailableValuesAspectDefinition;
import org.linkki.core.ui.section.annotations.aspect.FieldAspectDefinitionCreator;
import org.linkki.core.ui.section.annotations.aspect.ValueAspectDefinitionCreator;

import com.vaadin.ui.ComboBox;

/**
 * Creates a ComboBox with the specified parameters.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@LinkkiBindingDefinition(ComboboxBindingDefinition.class)
@LinkkiAspect(ComboBoxAvailableValuesAspectDefinitionCreator.class)
@LinkkiAspect(FieldAspectDefinitionCreator.class)
@LinkkiAspect(ValueAspectDefinitionCreator.class)
public @interface UIComboBox {

    /** Mandatory attribute that defines the order in which UI-Elements are displayed */
    int position();

    /** Provides a description label next to the UI element */
    String label();

    /**
     * Specifies the source of the available values, the content of the combo box.
     * 
     * @see AvailableValuesType
     */
    AvailableValuesType content() default AvailableValuesType.ENUM_VALUES_INCL_NULL;

    /** Defines if an UI-Component is editable, using values of {@link EnabledType} */
    EnabledType enabled() default ENABLED;

    /** Marks mandatory fields visually */
    RequiredType required() default NOT_REQUIRED;

    /**
     * Specifies if a component is shown, using values of {@link VisibleType}
     */
    VisibleType visible() default VISIBLE;

    /**
     * Specifies the width of the field. Use CSS units like em, px or %.
     * <p>
     * For example: "25em" or "100%".
     */
    String width() default "-1px";

    /**
     * Name of the model object that is to be bound if multiple model objects are included for model
     * binding
     */
    String modelObject() default ModelObject.DEFAULT_NAME;

    /**
     * The name of a property in the class of the bound {@link ModelObject} to use model binding
     */
    String modelAttribute() default "";

    /**
     * Specifies which {@link ItemCaptionProvider} should be used to convert {@link #content()} into
     * String captions.
     * <p>
     * Default value assumes that the value class has a method "getName" and uses this method for the
     * String representation.
     */
    Class<? extends ItemCaptionProvider<?>> itemCaptionProvider() default DefaultCaptionProvider.class;

    class ComboBoxAvailableValuesAspectDefinitionCreator implements AspectDefinitionCreator<UIComboBox> {

        @Override
        public LinkkiAspectDefinition create(UIComboBox annotation) {
            return new AvailableValuesAspectDefinition<ComboBox<Object>>(annotation.content(),
                    ComboBox<Object>::setDataProvider) {

                @Override
                @SuppressWarnings("unchecked")
                protected void handleNullItems(ComponentWrapper componentWrapper, List<@Nullable Object> items) {
                    boolean hasNullItem = items.removeIf(i -> i == null);
                    ((ComboBox<Object>)componentWrapper.getComponent()).setEmptySelectionAllowed(hasNullItem);
                }

            };
        }

    }

}
