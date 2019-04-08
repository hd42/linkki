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
package org.linkki.core.binding.descriptor;

import static java.util.Objects.requireNonNull;

import java.util.List;

import org.linkki.core.binding.descriptor.aspect.LinkkiAspectDefinition;
import org.linkki.core.binding.descriptor.property.BoundProperty;
import org.linkki.core.pmo.ModelObject;
import org.linkki.util.Sequence;

public abstract class BindingDescriptor {

    private Sequence<LinkkiAspectDefinition> aspectDefinitions;

    public BindingDescriptor(List<? extends LinkkiAspectDefinition> aspectDefinitions) {
        this.aspectDefinitions = Sequence.of(requireNonNull(aspectDefinitions, "aspectDefinitions must not be null"));
    }

    public List<LinkkiAspectDefinition> getAspectDefinitions() {
        return aspectDefinitions.list();
    }

    /**
     * Add the specified list of {@link LinkkiAspectDefinition}. This may be necessary if not all
     * aspects were present when the descriptor was created.
     * 
     * @param additionalAspectDefinition additional aspect definitions that need to be added.
     */
    protected void addAspectDefinitions(List<LinkkiAspectDefinition> additionalAspectDefinition) {
        aspectDefinitions = aspectDefinitions.with(additionalAspectDefinition);
    }

    /**
     * The name of the property that the bound UI element displays. For an UI element that accesses the
     * field of a model/PMO class, this is the name of that field. For an UI element that invokes a
     * method (i.e. a button) this is the name of the method.
     */
    public abstract String getModelPropertyName();

    /**
     * The name of the model object containing the {@link #getModelPropertyName() property} if the bound
     * PMO itself does not contain the property. The PMO has to have a {@link ModelObject @ModelObject}
     * annotation with that name on the method that returns the model object.
     */
    public abstract String getModelObjectName();

    /**
     * The name of the property from the pmo
     */
    public abstract String getPmoPropertyName();

    public BoundProperty getBoundProperty() {
        return BoundProperty.of(getPmoPropertyName()).withModelObject(getModelObjectName())
                .withModelAttribute(getModelPropertyName());
    }

}