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
package org.linkki.core.defaults.ui.element;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.linkki.core.uiframework.UiFramework;

import edu.umd.cs.findbugs.annotations.CheckForNull;

/**
 * An {@link ItemCaptionProvider} is used to get the caption for an item in a selection UI component
 * like a combo box.
 * <p>
 * Due to the current VAADIN implementation we cannot be fully type safe.
 * <p>
 * We provide three default implementations:
 * <ul>
 * <li>{@link DefaultCaptionProvider} calls a method {@code getName()} on the value object.</li>
 * <li>{@link ToStringCaptionProvider} simply uses the object's {@link Object#toString()} method.</li>
 * <li>{@link IdAndNameCaptionProvider} calls the methods {@code getName()} and {@code getId()} on the
 * value object and returns a caption in the format "name [id]".</li>
 * </ul>
 */
@FunctionalInterface
public interface ItemCaptionProvider<T> {

    /**
     * Returns the text that should be displayed for the specified value. Depending on the
     * implementation, this value may be localized.
     * 
     * @implNote {@link UiFramework#getLocale()} can be used to determine the current locale, if this
     *           caption provider supports localization.
     * 
     * @param value The value for which we need a caption
     * @return The caption for the specified value
     */
    String getCaption(T value);

    /**
     * Returns the caption for the <code>null</code> value. Default is the empty String.
     * 
     * @return A caption for the <code>null</code> value
     */
    default String getNullCaption() {
        return StringUtils.EMPTY;
    }

    /**
     * This is the unsafe version of {@link #getCaption(Object)}. The framework will only call this
     * method because the type is not necessarily known in the UI selection component. When implementing
     * this interface you could ignore this method because it simply delegates to
     * {@link #getCaption(Object)} which is the type-safe variant for you. However, if anybody does
     * something nasty we would get a ClassCastException right here.
     * 
     * @param value The value for which we need a caption
     * @return The caption for the specified value
     */
    @SuppressWarnings("unchecked")
    default String getUnsafeCaption(Object value) {
        return getCaption((T)value);
    }

    /**
     * A simple caption provider that uses an item's toString() method as its caption.
     * <p>
     * It is in general no good idea to use this, except your list consists of Strings.
     * 
     */
    public class ToStringCaptionProvider implements ItemCaptionProvider<Object> {
        @Override
        public String getCaption(Object t) {
            return Optional.ofNullable(t).map(Object::toString).orElse("");
        }
    }

    /**
     * A caption provider that invokes the first existing method of {@code getName(Locale)},
     * {@code getName()} and {@code toString()} to obtain the caption. The locale is determined by
     * {@link UiFramework#getLocale()}.
     */
    public class DefaultCaptionProvider implements ItemCaptionProvider<Object> {

        @Override
        public String getCaption(Object o) {
            String name = getName(o);
            return name != null ? name : StringUtils.EMPTY;
        }

        @CheckForNull
        private static String getName(Object value) {
            Optional<Method> getLocalizedNameMethod = getMethod(value, "getName", Locale.class);
            if (getLocalizedNameMethod.isPresent()) {
                return invokeStringMethod(getLocalizedNameMethod.get(), value, UiFramework.getLocale());
            }

            Optional<Method> getNameMethod = getMethod(value, "getName");
            if (getNameMethod.isPresent()) {
                return invokeStringMethod(getNameMethod.get(), value);
            }

            Method toStringMethod = getMethod(value, "toString").get();
            return invokeStringMethod(toStringMethod, value);
        }

        private static Optional<Method> getMethod(Object value, String name, Class<?>... parameters) {
            try {
                Method method = value.getClass().getMethod(name, parameters);
                return Optional.of(method);
            } catch (NoSuchMethodException e) {
                return Optional.empty();
            }
        }

        @CheckForNull
        private static String invokeStringMethod(Method method, Object value, Object... parameters) {
            try {
                return (String)method.invoke(value, parameters);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new IllegalStateException(
                        "Can't get value from method " + value.getClass() + "#" + method.getName() + " of " + value);
            }
        }
    }

    /**
     * A caption provider that returns a String in the format "name [id]" and invokes the methods
     * {@code getName} and {@code getId} to obtain these values.
     * 
     * @deprecated since 1.1, use org.linkki.ips.ui.element.IdAndNameCaptionProvider from
     *             org.linkki-framework:linkki-ips-vaadin8 instead. This class has been moved to the
     *             Faktor-IPS-specific linkki module, due to there being no general use-case if linkki
     *             is not used with Faktor-IPS.
     */
    @Deprecated
    public class IdAndNameCaptionProvider implements ItemCaptionProvider<Object> {

        @Override
        public String getCaption(Object o) {
            return getName(o) + " [" + getId(o) + "]";
        }

        @CheckForNull
        private String getId(Object value) {
            return getPropertyValue(value, "getId");
        }

        @CheckForNull
        private String getName(Object value) {
            return getPropertyValue(value, "getName");
        }

        @CheckForNull
        private String getPropertyValue(Object value, String methodName) {
            try {
                Method method = value.getClass().getMethod(methodName);
                return (String)method.invoke(value);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                throw new IllegalStateException(
                        "Can't get value from method " + value.getClass() + "#" + methodName + " of " + value);

            }
        }

    }

}
