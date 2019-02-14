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

package org.linkki.core.ui.table.hierarchy;

import org.linkki.core.ui.section.annotations.ModelObject;

public class NumberRowPmo extends AbstractCodeRow {

    private final Code code;

    public NumberRowPmo(Code code) {
        this.code = code;
    }

    @ModelObject
    public Code getCode() {
        return code;
    }

    @Override
    public String getUpperCaseLetter() {
        return code.getUpperCaseLetter();
    }

    @Override
    public String getLowerCaseLetter() {
        return code.getLowerCaseLetter();
    }

    @Override
    public int getNumber() {
        return code.getNumber();
    }

    @Override
    public String toString() {
        return super.toString() + " \"" + getUpperCaseLetter() + getLowerCaseLetter() + getNumber() + "\"";
    }

}
