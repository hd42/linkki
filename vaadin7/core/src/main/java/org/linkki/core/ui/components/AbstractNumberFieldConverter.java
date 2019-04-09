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
package org.linkki.core.ui.components;

import static java.util.Objects.requireNonNull;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.data.util.converter.Converter;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractNumberFieldConverter<T extends Number> implements Converter<String, T> {

    private static final long serialVersionUID = -872944068146887949L;

    private final NumberFormat format;

    public AbstractNumberFieldConverter(NumberFormat format) {
        this.format = requireNonNull(format, "format must not be null");
    }

    @Override
    @CheckForNull
    public final T convertToModel(@CheckForNull String value,
            Class<? extends T> targetType,
            @CheckForNull Locale locale)
            throws ConversionException {
        if (StringUtils.isEmpty(value)) {
            return getNullValue();
        }
        try {
            return convertToModel(format.parse(value));
        } catch (ParseException e) {
            throw new ConversionException("Can't parse '" + value + "' to format '" + format + "')");
        }
    }

    @CheckForNull
    protected abstract T getNullValue();

    protected abstract T convertToModel(Number value);

    @NonNull
    @Override
    public String convertToPresentation(@CheckForNull T value,
            Class<? extends String> targetType,
            @CheckForNull Locale locale)
            throws ConversionException {

        if (value == null) {
            return "";
        } else {
            return format.format(value);
        }
    }

    @Override
    public final Class<String> getPresentationType() {
        return String.class;
    }
}