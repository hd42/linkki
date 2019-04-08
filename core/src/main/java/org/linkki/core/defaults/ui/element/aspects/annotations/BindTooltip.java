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
package org.linkki.core.defaults.ui.element.aspects.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.commons.lang3.StringUtils;
import org.linkki.core.binding.descriptor.aspect.LinkkiAspectDefinition;
import org.linkki.core.binding.descriptor.aspect.annotation.AspectDefinitionCreator;
import org.linkki.core.binding.descriptor.aspect.annotation.LinkkiAspect;
import org.linkki.core.defaults.ui.element.aspects.TooltipAspectDefinition;
import org.linkki.core.defaults.ui.element.aspects.annotations.BindTooltip.BindTooltipAspectDefinitionCreator;
import org.linkki.core.defaults.ui.element.aspects.types.TooltipType;

/**
 * Shows a tooltip next to a UI-Element. The annotation can be added to the method the UI-Element is
 * bound.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD, ElementType.METHOD })
@LinkkiAspect(BindTooltipAspectDefinitionCreator.class)
public @interface BindTooltip {

    /** The displayed text for {@link TooltipType#STATIC} */
    String value()

    default StringUtils.EMPTY;

    /** Defines how the tooltip text should be retrieved */
    TooltipType tooltipType() default TooltipType.STATIC;


    class BindTooltipAspectDefinitionCreator implements AspectDefinitionCreator<BindTooltip> {

        @Override
        public LinkkiAspectDefinition create(BindTooltip annotation) {
            BindTooltip bindTooltip = annotation;
            return new TooltipAspectDefinition(bindTooltip.tooltipType(), bindTooltip.value());
        }

    }

}
