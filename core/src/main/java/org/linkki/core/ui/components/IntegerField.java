package org.linkki.core.ui.components;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

public class IntegerField extends NumberField {

    private static final long serialVersionUID = 1L;

    private final String pattern;

    private final Locale locale;

    public IntegerField(Locale locale) {
        this("", locale);
    }

    public IntegerField(String pattern, Locale locale) {
        super();
        this.pattern = pattern;
        this.locale = locale;
        init();
    }

    private void init() {
        setConverter(new IntegerFieldConverter(createFormat()));
    }

    private NumberFormat createFormat() {
        if (StringUtils.isEmpty(pattern)) {
            return NumberFormat.getIntegerInstance(locale);
        } else {
            return new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(locale));
        }
    }

}
