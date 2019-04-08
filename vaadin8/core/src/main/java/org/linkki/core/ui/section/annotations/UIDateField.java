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
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.time.LocalDate;

import org.linkki.core.binding.aspect.AspectDefinitionCreator;
import org.linkki.core.binding.aspect.LinkkiAspect;
import org.linkki.core.binding.aspect.definition.LinkkiAspectDefinition;
import org.linkki.core.ui.converters.TwoDigitYearLocalDateConverter;
import org.linkki.core.ui.section.annotations.UIDateField.DateFieldValueAspectCreator;
import org.linkki.core.ui.section.annotations.adapters.DateFieldBindingDefinition;
import org.linkki.core.ui.section.annotations.aspect.FieldAspectDefinitionCreator;
import org.linkki.core.ui.section.annotations.aspect.ValueAspectDefinition;

import com.vaadin.data.Converter;

/**
 * A field for date input, in accordance with {@link com.vaadin.ui.DateField}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@LinkkiBindingDefinition(DateFieldBindingDefinition.class)
@LinkkiAspect(FieldAspectDefinitionCreator.class)
@LinkkiAspect(DateFieldValueAspectCreator.class)
public @interface UIDateField {

    /** Mandatory attribute that defines the order in which UI-Elements are displayed */
    int position();

    /** Provides a description label next to the UI element */
    String label();

    /** Defines if an UI-Component is editable, using values of {@link EnabledType} */
    EnabledType enabled() default ENABLED;

    /** Marks mandatory fields visually */
    RequiredType required() default NOT_REQUIRED;

    /**
     * Specifies if a component is shown, using values of {@link VisibleType}
     */
    VisibleType visible() default VISIBLE;

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
     * Defines the date format, default format of the UI locale is used if no format is specified.
     * linkki uses {@link DateFormat#SHORT} for interpreting.
     */
    String dateFormat() default "";

    class DateFieldValueAspectCreator implements AspectDefinitionCreator<UIDateField> {

        @Override
        public LinkkiAspectDefinition create(UIDateField annotation) {

            TwoDigitYearLocalDateConverter twoDigitYearConverter = new TwoDigitYearLocalDateConverter();

            return new ValueAspectDefinition() {
                @Override
                protected Converter<?, ?> getConverter(Type presentationType, Type modelType) {
                    @SuppressWarnings("unchecked")
                    Converter<LocalDate, ?> superConverter = (Converter<LocalDate, ?>)super.getConverter(presentationType,
                                                                                                         modelType);
                    return twoDigitYearConverter.chain(superConverter);
                }
            };
        }
    }
}
