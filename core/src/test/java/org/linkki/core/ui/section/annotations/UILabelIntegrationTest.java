/*
 * Copyright Faktor Zehn AG.
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
package org.linkki.core.ui.section.annotations;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import org.junit.Test;
import org.linkki.core.ui.section.annotations.UILabelIntegrationTest.LabelTestPmo;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;

public class UILabelIntegrationTest extends ComponentAnnotationIntegrationTest<Label, LabelTestPmo> {

    private static final String STYLES = "blabla";

    @SuppressWarnings("null")
    public UILabelIntegrationTest() {
        super(TestModelObjectWithString::new, LabelTestPmo::new);
    }

    @Test
    public void testTextVieldValue() {
        Label label = getDynamicComponent();

        assertThat(label.getContentMode(), is(ContentMode.HTML));
        assertThat(label.getStyleName(), is(STYLES));
        assertThat(label.getValue(), is(""));

        ((TestModelObjectWithString)getDefaultModelObject()).setValue("fdsa");
        updateUi();
        assertThat(label.getValue(), is("fdsa"));
    }

    @Override
    public void testEnabled() {
        assertThat(getStaticComponent().isEnabled(), is(true));
        assertThat(getDynamicComponent().isEnabled(), is(true));
    }

    @UISection
    protected static class LabelTestPmo extends AnnotationTestPmo {

        public LabelTestPmo(Object modelObject) {
            super(modelObject);
        }

        @Override
        @UILabel(position = 1, visible = VisibleType.DYNAMIC, htmlContent = true, styleNames = STYLES)
        public void value() {
            // model binding
        }

        @Override
        @UILabel(position = 2, label = TEST_LABEL, visible = VisibleType.INVISIBLE)
        public void staticValue() {
            // model binding
        }
    }

    protected static class TestModelObjectWithString extends TestModelObject<String> {

        @Nullable
        private String value = null;

        @SuppressWarnings("null")
        @CheckForNull
        @Override
        public String getValue() {
            return value;
        }

        @Override
        public void setValue(@Nullable String value) {
            this.value = value;
        }
    }
}
