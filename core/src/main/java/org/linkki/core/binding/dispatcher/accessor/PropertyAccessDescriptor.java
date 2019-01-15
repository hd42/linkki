/*
 * Copyright Faktor Zehn GmbH.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.linkki.core.binding.dispatcher.accessor;

import static java.util.Objects.requireNonNull;
import static org.linkki.util.LazyCachingSupplier.lazyCaching;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.eclipse.jdt.annotation.NonNull;

/**
 * This class wraps a {@link PropertyDescriptor} for a bound class and a specified property
 * identified by its name. It could create a {@link WriteMethod} or {@link ReadMethod} object for
 * the bound class and property using the methods {@link #createWriteMethod()} and
 * {@link #createReadMethod()}.
 *
 */
public class PropertyAccessDescriptor {

    private static final String GET_PREFIX = "get";
    private static final String SET_PREFIX = "set";
    private static final String IS_PREFIX = "is";

    private final Class<?> boundClass;
    private final String propertyName;
    private final Supplier<Optional<Method>> getter = lazyCaching(this::findGetter);
    private final Supplier<Optional<Method>> setter = lazyCaching(this::findSetter);

    private String capitalizedPropertyName;

    public PropertyAccessDescriptor(Class<?> boundClass, String propertyName) {
        this.boundClass = requireNonNull(boundClass, "clazz must not be null");
        this.propertyName = requireNonNull(propertyName, "propertyName must not be null");
        capitalizedPropertyName = StringUtils.capitalize(propertyName);
    }

    private final Optional<Method> findGetter() {
        Class<?> @NonNull [] params = {};
        Method foundMethod = MethodUtils.getMatchingAccessibleMethod(boundClass, GET_PREFIX + capitalizedPropertyName,
                                                                     params);
        if (foundMethod == null) {
            Class<?> @NonNull [] params1 = {};
            foundMethod = MethodUtils.getMatchingAccessibleMethod(boundClass, IS_PREFIX + capitalizedPropertyName,
                                                                  params1);
        }
        return Optional.ofNullable(foundMethod);
    }


    private final Optional<Method> findSetter() {
        return getter.get()//
                .map(Method::getReturnType)
                .map(returnTyp -> new Class<?>[] { returnTyp })
                .map(params -> MethodUtils.getMatchingAccessibleMethod(boundClass, SET_PREFIX + capitalizedPropertyName,
                                                                       params))
                .flatMap(Optional::ofNullable);
    }

    public WriteMethod createWriteMethod() {
        return new WriteMethod(this);
    }

    public ReadMethod createReadMethod() {
        return new ReadMethod(this);
    }

    Supplier<Optional<Method>> getReflectionWriteMethod() {
        return setter;
    }

    Supplier<Optional<Method>> getReflectionReadMethod() {
        return getter;
    }

    public Class<?> getBoundClass() {
        return boundClass;
    }

    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public String toString() {
        return PropertyAccessDescriptor.class.getSimpleName() + '[' + boundClass + '#' + propertyName + ']';
    }

}
