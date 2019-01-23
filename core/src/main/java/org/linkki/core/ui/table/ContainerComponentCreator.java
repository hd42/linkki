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

package org.linkki.core.ui.table;

import static java.util.Objects.requireNonNull;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.linkki.core.binding.TableBinding;
import org.linkki.core.binding.aspect.definition.LinkkiAspectDefinition;
import org.linkki.core.binding.descriptor.ElementDescriptor;
import org.linkki.core.binding.descriptor.PropertyElementDescriptors;
import org.linkki.core.binding.descriptor.SimpleBindingDescriptor;
import org.linkki.core.ui.application.ApplicationStyles;
import org.linkki.core.ui.components.LabelComponentWrapper;
import org.linkki.core.ui.table.column.TableColumnWrapper;

import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.TreeTable;


class ContainerComponentCreator<@NonNull T> {
    private final ContainerPmo<T> containerPmo;

    ContainerComponentCreator(ContainerPmo<T> containerPmo) {
        this.containerPmo = containerPmo;
    }


    /**
     * Creates a new table based on the container PMO.
     */
    Table createTableComponent() {
        Table table = containerPmo.isHierarchical() ? new TreeTable() : new Table();
        table.addStyleName(ApplicationStyles.TABLE);
        table.setHeightUndefined();
        table.setWidth("100%");
        table.setSortEnabled(false);
        table.setId(containerPmo.getClass().getSimpleName());
        return table;
    }

    /**
     * Creates a new column for a field of a PMO and applies all
     * {@link LinkkiAspectDefinition#supports(org.linkki.core.ui.components.WrapperType) supported}
     * {@link LinkkiAspectDefinition LinkkiAspectDefinitions}.
     * 
     * @param elementDesc the descriptor for the PMO's field
     */
    void createColumn(TableBinding<T> tableBinding, PropertyElementDescriptors elementDesc) {
        Table table = tableBinding.getBoundComponent();
        ContainerComponentCreator.FieldColumnGenerator<T> columnGen = new ContainerComponentCreator.FieldColumnGenerator<>(
                elementDesc, tableBinding);
        String propertyName = elementDesc.getPmoPropertyName();
        table.addGeneratedColumn(propertyName, columnGen);
        List<LinkkiAspectDefinition> aspectDefs = elementDesc.getAllAspects();
        tableBinding.bind(containerPmo, new SimpleBindingDescriptor(propertyName, aspectDefs),
                          new TableColumnWrapper(table, propertyName));
    }


    /** Column generator that generates a column for a field of a PMO. */
    private static class FieldColumnGenerator<@NonNull T> implements ColumnGenerator {

        private static final long serialVersionUID = 1L;

        private final PropertyElementDescriptors elementDescriptors;
        private final TableBinding<T> tableBinding;

        public FieldColumnGenerator(PropertyElementDescriptors elementDescriptors, TableBinding<T> tableBinding) {
            this.elementDescriptors = requireNonNull(elementDescriptors, "elementDescriptors must not be null");
            this.tableBinding = requireNonNull(tableBinding, "tableBinding must not be null");
        }

        @Override
        public Object generateCell(Table source,
                Object itemId,
                Object columnId) {
            requireNonNull(itemId, "itemId must not be null");
            ElementDescriptor elementDescriptor = elementDescriptors.getDescriptor(itemId);
            Component component = elementDescriptor.newComponent();
            component.addStyleName(ApplicationStyles.BORDERLESS);
            component.addStyleName(ApplicationStyles.TABLE_CELL);

            @SuppressWarnings("unchecked")
            T itemPmo = (T)itemId;
            component.addAttachListener($ -> tableBinding.bind(itemPmo, elementDescriptor,
                                                               new LabelComponentWrapper(component)));
            component.addDetachListener($ -> tableBinding.removeBindingsForComponent(component));

            return component;
        }
    }

}