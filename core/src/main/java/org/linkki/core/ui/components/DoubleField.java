package org.linkki.core.ui.components;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class DoubleField extends NumberField {

    public static final String DEFAULT_DOUBLE_FORMAT = "#,##0.00##";

    private static final long serialVersionUID = 7642987884125435087L;

    private final Locale locale;

    private final String pattern;

    public DoubleField(Locale locale) {
        this(DEFAULT_DOUBLE_FORMAT, locale);
    }

    public DoubleField(String pattern, Locale locale) {
        super();
        this.pattern = pattern;
        this.locale = locale;
        setConverter(createConverter());
    }

    private DoubleFieldConverter createConverter() {
        return new DoubleFieldConverter(createFormat());
    }

    protected final DecimalFormat createFormat() {
        return new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(locale));
    }

}
