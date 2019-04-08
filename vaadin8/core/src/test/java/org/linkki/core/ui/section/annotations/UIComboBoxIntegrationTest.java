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

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.Test;
import org.linkki.core.binding.TestEnum;
import org.linkki.core.ui.components.ItemCaptionProvider.ToStringCaptionProvider;
import org.linkki.core.ui.section.annotations.BindTooltip.TooltipType;
import org.linkki.core.ui.section.annotations.UIComboBoxIntegrationTest.ComboBoxTestPmo;

import com.vaadin.ui.ComboBox;

public class UIComboBoxIntegrationTest extends ComponentAnnotationIntegrationTest<ComboBox<TestEnum>, ComboBoxTestPmo> {

    public UIComboBoxIntegrationTest() {
        super(ComboBoxTestModelObject::new, ComboBoxTestPmo::new);
    }

    @Test
    public void testNullSelection() {
        assertThat(getStaticComponent().isEmptySelectionAllowed(), is(false));

        List<@Nullable TestEnum> availableValues = new ArrayList<>(getDefaultPmo().getValueAvailableValues());

        ComboBox<TestEnum> comboBox = getDynamicComponent();
        assertThat(availableValues.contains(null), is(false));
        assertThat(comboBox.isEmptySelectionAllowed(), is(false));

        availableValues.add(null);
        assertThat(availableValues.contains(null), is(true));
        getDefaultPmo().setValueAvailableValues(availableValues);
        modelChanged();
        assertThat(TestUiUtil.getData(comboBox), contains(TestEnum.ONE, TestEnum.TWO, TestEnum.THREE));
        assertThat(comboBox.isEmptySelectionAllowed(), is(true));
    }

    @Test
    public void testStaticAvailableValues() {
        ComboBox<TestEnum> staticComboBox = getStaticComponent();
        assertThat(TestUiUtil.getData(staticComboBox), contains(TestEnum.ONE, TestEnum.TWO, TestEnum.THREE));
    }

    @Test
    public void testDynamicAvailableValues() {
        assertThat(TestUiUtil.getData(getDynamicComponent()), contains(TestEnum.ONE, TestEnum.TWO, TestEnum.THREE));

        List<@Nullable TestEnum> availableValues = new ArrayList<>(getDefaultPmo().getValueAvailableValues());
        availableValues.remove(TestEnum.ONE);
        getDefaultPmo().setValueAvailableValues(availableValues);
        modelChanged();
        assertThat(TestUiUtil.getData(getDynamicComponent()), contains(TestEnum.TWO, TestEnum.THREE));
    }

    @Test
    public void testCaptionProvider() {
        assertThat(getDynamicComponent().getItemCaptionGenerator().apply(TestEnum.ONE), is("Oans"));
        assertThat(getStaticComponent().getItemCaptionGenerator().apply(TestEnum.ONE), is("ONE"));
    }

    @Test
    public void testValue() {
        ComboBox<TestEnum> comboBox = getDynamicComponent();
        assertThat(comboBox.getValue(), is(TestEnum.THREE));

        getDefaultModelObject().setValue(TestEnum.TWO);
        modelChanged();
        assertThat(comboBox.getValue(), is(TestEnum.TWO));

        TestUiUtil.setUserOriginatedValue(comboBox, TestEnum.ONE);
        assertThat(getDefaultModelObject().getValue(), is(TestEnum.ONE));
    }

    @Test
    public void testValueRemainsWhenChangingAvailableValues() {
        getDefaultModelObject().setValue(TestEnum.THREE);
        assertThat(getDynamicComponent().getValue(), is(TestEnum.THREE));

        getDefaultPmo().setValueAvailableValues(Arrays.asList(TestEnum.THREE));
        modelChanged();
        assertThat(getDynamicComponent().getValue(), is(TestEnum.THREE));
    }

    @SuppressWarnings("null")
    @Test
    public void testNullInputIfRequired() {
        ComboBox<TestEnum> comboBox = getDynamicComponent();
        getDefaultPmo().setRequired(true);
        modelChanged();
        assertThat(comboBox.isRequiredIndicatorVisible(), is(true));

        TestUiUtil.setUserOriginatedValue(comboBox, TestEnum.ONE);
        assertThat(getDefaultModelObject().getValue(), is(TestEnum.ONE));

        TestUiUtil.setUserOriginatedValue(comboBox, null);
        assertThat(getDefaultModelObject().getValue(), is(nullValue()));
    }

    @Test
    public void testInitReadOnlyField() {
        ComboBox<TestEnum> comboBox = getDynamicComponent();
        comboBox.setReadOnly(true);
        bind(getDefaultPmo(), "staticValue", comboBox);
        assertThat(comboBox.isReadOnly(), is(true));

        getDefaultPmo().setRequired(true);
        modelChanged();
        assertThat(comboBox.isRequiredIndicatorVisible(), is(true));
        assertThat(comboBox.isReadOnly(), is(true));

        getBindingContext().removeBindingsForComponent(comboBox);
        assertThat(comboBox.isReadOnly(), is(true));
        bind(getDefaultPmo(), "staticValue", comboBox);
        assertThat(comboBox.isReadOnly(), is(true));
    }

    @Override
    protected ComboBoxTestModelObject getDefaultModelObject() {
        return (ComboBoxTestModelObject)super.getDefaultModelObject();
    }

    @UISection
    protected static class ComboBoxTestPmo extends AnnotationTestPmo {

        private List<@Nullable TestEnum> availableValues;

        public ComboBoxTestPmo(Object modelObject) {
            super(modelObject);
            availableValues = new ArrayList<>();
            availableValues.add(TestEnum.ONE);
            availableValues.add(TestEnum.TWO);
            availableValues.add(TestEnum.THREE);
        }

        @Override
        @BindTooltip(tooltipType = TooltipType.DYNAMIC)
        @UIComboBox(position = 1, label = "", enabled = EnabledType.DYNAMIC, required = RequiredType.DYNAMIC, visible = VisibleType.DYNAMIC, content = AvailableValuesType.DYNAMIC, itemCaptionProvider = ToStringCaptionProvider.class)
        public void value() {
            // model binding
        }

        public List<@Nullable TestEnum> getValueAvailableValues() {
            return Collections.unmodifiableList(availableValues);
        }

        public void setValueAvailableValues(List<@Nullable TestEnum> values) {
            this.availableValues = values;
        }

        @Override
        @BindTooltip(TEST_TOOLTIP)
        @UIComboBox(position = 2, label = TEST_LABEL, enabled = EnabledType.DISABLED, required = RequiredType.REQUIRED, visible = VisibleType.INVISIBLE, content = AvailableValuesType.ENUM_VALUES_EXCL_NULL)
        public void staticValue() {
            // model binding
        }
    }

    protected static class ComboBoxTestModelObject {
        @Nullable
        private TestEnum value = TestEnum.THREE;

        @Nullable
        public TestEnum getValue() {
            return value;
        }

        public void setValue(@Nullable TestEnum value) {
            this.value = value;

        }

        @Nullable
        public TestEnum getStaticValue() {
            return getValue();
        }

    }
}
