package com.erp.util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class InputFieldDecorator {
    private static final Locale LOCALE_PT_BR = new Locale("pt", "BR");
    private static final DecimalFormatSymbols SYMBOLS = DecimalFormatSymbols.getInstance(LOCALE_PT_BR);
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00", SYMBOLS);
    private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("0.##", SYMBOLS);
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("'R$' #,##0.00", SYMBOLS);

    private InputFieldDecorator() {
    }

    public static JComponent withPrefix(JTextField field, String prefix) {
        return decorate(field, prefix, null);
    }

    public static JComponent withSuffix(JTextField field, String suffix) {
        return decorate(field, null, suffix);
    }

    public static void configureDecimalField(JTextField field) {
        configureDecimalField(field, 2);
    }

    public static void configureDecimalField(JTextField field, int decimalPlaces) {
        if (field.getDocument() instanceof AbstractDocument) {
            ((AbstractDocument) field.getDocument()).setDocumentFilter(new DecimalDocumentFilter(decimalPlaces));
        }
        field.setHorizontalAlignment(SwingConstants.RIGHT);
    }

    public static String formatDecimal(double value) {
        return DECIMAL_FORMAT.format(value);
    }

    public static String formatDecimal(Double value) {
        return value == null ? "" : DECIMAL_FORMAT.format(value);
    }

    public static String formatDecimal(BigDecimal value) {
        return value == null ? "" : DECIMAL_FORMAT.format(value);
    }

    public static String formatPercent(Double value) {
        return value == null ? "" : PERCENT_FORMAT.format(value);
    }

    public static String formatCurrency(BigDecimal value) {
        return value == null ? CURRENCY_FORMAT.format(BigDecimal.ZERO) : CURRENCY_FORMAT.format(value);
    }

    public static BigDecimal parseBigDecimal(String value) {
        String normalized = normalizeNumericText(value);
        if (normalized.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(normalized);
    }

    public static double parseDouble(String value) {
        String normalized = normalizeNumericText(value);
        if (normalized.isEmpty()) {
            return 0d;
        }
        return Double.parseDouble(normalized);
    }

    public static void applyCurrencyRenderer(JTable table, int... columns) {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                if (value instanceof BigDecimal) {
                    setText(formatCurrency((BigDecimal) value));
                } else if (value instanceof Number) {
                    setText(formatCurrency(BigDecimal.valueOf(((Number) value).doubleValue())));
                } else {
                    setText(value == null ? "" : value.toString());
                }
            }
        };
        renderer.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int column : columns) {
            table.getColumnModel().getColumn(column).setCellRenderer(renderer);
        }
    }

    private static JComponent decorate(JTextField field, String prefix, String suffix) {
        JPanel container = new JPanel(new BorderLayout(6, 0));
        container.setOpaque(true);
        container.setBackground(field.getBackground());

        Border border = UIManager.getBorder("TextField.border");
        if (border != null) {
            container.setBorder(border);
        }

        field.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        field.setOpaque(false);

        if (prefix != null && !prefix.isEmpty()) {
            container.add(createAffixLabel(prefix), BorderLayout.WEST);
        }
        if (suffix != null && !suffix.isEmpty()) {
            container.add(createAffixLabel(suffix), BorderLayout.EAST);
        }

        container.add(field, BorderLayout.CENTER);
        return container;
    }

    private static JLabel createAffixLabel(String value) {
        JLabel label = new JLabel(value);
        label.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        label.setForeground(UIManager.getColor("Label.disabledForeground") != null
                ? UIManager.getColor("Label.disabledForeground")
                : Color.GRAY);
        return label;
    }

    private static String normalizeNumericText(String value) {
        return value == null ? "" : value.trim().replace(".", "").replace(',', '.');
    }

    private static final class DecimalDocumentFilter extends DocumentFilter {
        private final int decimalPlaces;

        private DecimalDocumentFilter(int decimalPlaces) {
            this.decimalPlaces = decimalPlaces;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            replace(fb, offset, 0, string, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            String current = fb.getDocument().getText(0, fb.getDocument().getLength());
            String replacement = text == null ? "" : text;
            String candidate = current.substring(0, offset) + replacement + current.substring(offset + length);
            String normalized = candidate.replace('.', ',');
            if (isValidDecimal(normalized)) {
                fb.replace(0, fb.getDocument().getLength(), normalized, attrs);
            }
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            replace(fb, offset, length, "", null);
        }

        private boolean isValidDecimal(String value) {
            if (value.isEmpty()) {
                return true;
            }
            if (!value.matches("\\d*(,\\d*)?")) {
                return false;
            }
            int separatorIndex = value.indexOf(',');
            if (separatorIndex < 0) {
                return true;
            }
            return value.length() - separatorIndex - 1 <= decimalPlaces;
        }
    }
}
