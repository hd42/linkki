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

package org.linkki.core.ui.section.annotations.aspect;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.commons.lang3.ClassUtils;
import org.linkki.core.binding.descriptor.aspect.Aspect;
import org.linkki.core.binding.descriptor.aspect.LinkkiAspectDefinition;
import org.linkki.core.binding.dispatcher.PropertyDispatcher;
import org.linkki.core.binding.wrapper.ComponentWrapper;
import org.linkki.util.handler.Handler;

import com.vaadin.data.util.AbstractProperty;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.DateField;

import edu.umd.cs.findbugs.annotations.CheckForNull;

/**
 * Aspect definition for value change. Defines that the data source will get/set values through
 * {@link Aspect Aspects} by providing a handler in
 * {@link #initModelUpdate(PropertyDispatcher, ComponentWrapper, Handler)}.
 */
public class FieldValueAspectDefinition implements LinkkiAspectDefinition {

    public static final String NAME = LinkkiAspectDefinition.VALUE_ASPECT_NAME;

    @Override
    public void initModelUpdate(PropertyDispatcher propertyDispatcher,
            ComponentWrapper componentWrapper,
            Handler modelUpdated) {
        AbstractField<?> field = (AbstractField<?>)componentWrapper.getComponent();
        FieldBindingDataSource<?> dataSource = new FieldBindingDataSource<>(
                propertyDispatcher.getValueClass(),
                () -> propertyDispatcher.pull(Aspect.of(NAME)),
                v -> propertyDispatcher.push(Aspect.of(NAME, v)),
                modelUpdated);

        prepareFieldToHandleNullForRequiredFields(field);
        field.setPropertyDataSource(dataSource);

        // Make bound components "immediate", i.e. let them update their PMO as soon as a field is
        // left, a checkbox is checked etc.
        field.setImmediate(true);
    }

    /**
     * LIN-90, LIN-95: if a field is required and the user enters blank into the field, Vaadin does not
     * transfer {@code null} into the data source. This leads to the effect that if the user enters a
     * valid value, the value is transfered to the model. If the user then enters blank, he sees an
     * empty field but the value in the model is still set to the old value.
     * <p>
     * How do we avoid this? If the field has no converter, we set invalidCommitted to {@code true}.
     * {@code null} is regarded as invalid value, but it is transferable to the model. This does not
     * work for fields with a converter. {@code null} handling is OK for those fields, but if the user
     * enters a value that cannot be converted, Vaadin tries to commit the value to the data source
     * doing so tries to convert it. This leads to an exception (as the value cannot be converted).
     * <p>
     * Example: Enter an invalid number like '123a' into a number field. We can't commit the value as it
     * is invalid and cannot be converted. To get this to work, those fields have to override
     * {@link AbstractField#validate()} to get rid of the unwanted check that leads to a validation
     * exception for {@code null} values in required fields.
     * 
     * @param field
     * 
     * @see AbstractField#validate()
     */
    private void prepareFieldToHandleNullForRequiredFields(AbstractField<?> field) {
        // note: we prepare the field if it is required or not, as the required state
        // can be changed dynamically.
        boolean commitInvalid = true;
        if (field.getConverter() != null && !compatibleTypeConverter(field)) {
            ensureThatFieldsWithAConverterOverrideValidate(field);
            commitInvalid = false;
        }
        field.setInvalidCommitted(commitInvalid);
    }

    private void ensureThatFieldsWithAConverterOverrideValidate(AbstractField<?> field) {
        Method validateMethod;
        try {
            validateMethod = field.getClass().getMethod("validate");
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
        if (validateMethod.getDeclaringClass().getName().startsWith("com.vaadin")) {
            throw new IllegalStateException(
                    String.format("A field that has a converter must override validate() to disable Vaadin's required field handling! See %s#%s for further explanation",
                                  getClass().getSimpleName(),
                                  "prepareFieldToHandleNullForRequiredFields(AbstractField)"));
        }
    }

    /**
     * Some fields could have converters because they will never throw a conversion exception.
     * <ul>
     * <li>DateField only converts from Date to LocalDate (compatible data type)</li>
     * </ul>
     * 
     * @return
     */
    private boolean compatibleTypeConverter(AbstractField<?> field) {
        return field instanceof DateField;
    }

    @Override
    public Handler createUiUpdater(PropertyDispatcher propertyDispatcher, ComponentWrapper componentWrapper) {
        AbstractField<?> field = (AbstractField<?>)componentWrapper.getComponent();
        FieldBindingDataSource<?> dataSource = (FieldBindingDataSource<?>)field.getPropertyDataSource();
        return () -> {
            dataSource.fireReadOnlyStatusChange();
            dataSource.fireValueChange();
        };
    }

    /**
     * Overrides behavior of {@link AbstractProperty} so it uses the given handler to set/get values in
     * a property.
     */
    private static final class FieldBindingDataSource<T> extends AbstractProperty<T> {

        private static final long serialVersionUID = 1L;

        private final Class<?> valueClass;

        private final Supplier<T> aspectValueGetter;

        private final Consumer<T> aspectValueSetter;

        private Handler uiUpdater;

        public FieldBindingDataSource(Class<?> valueClass, Supplier<T> valueGetter,
                Consumer<T> valueSetter,
                Handler uiUpdater) {
            this.valueClass = requireNonNull(valueClass, "valueClass must not be null");
            this.aspectValueGetter = requireNonNull(valueGetter, "valueSupplier must not be null");
            this.aspectValueSetter = requireNonNull(valueSetter, "valueConsumer must not be null");
            this.uiUpdater = requireNonNull(uiUpdater, "uiUpdater must not be null");
        }

        @Override
        @CheckForNull
        public T getValue() {
            return aspectValueGetter.get();
        }

        @Override
        public void setValue(@CheckForNull T newValue) throws com.vaadin.data.Property.ReadOnlyException {
            aspectValueSetter.accept(newValue);
            uiUpdater.apply();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Class<? extends T> getType() {
            return (Class<? extends T>)ClassUtils.primitiveToWrapper(valueClass);
        }

        /*
         * Override for visibility in Binding
         */
        @Override
        protected void fireValueChange() {
            super.fireValueChange();
        }

        /*
         * Override for visibility in Binding
         */
        @Override
        protected void fireReadOnlyStatusChange() {
            super.fireReadOnlyStatusChange();
        }

        @Override
        public boolean isReadOnly() {
            // read-only is controlled by an independent aspect
            return false;
        }

        @Override
        public void setReadOnly(boolean newStatus) {
            throw new UnsupportedOperationException("Cannot specify read-only for this data source");
        }
    }

}
