package kiosk.util;

import java.text.NumberFormat;

public final class CurrencyHelper {
    private static final NumberFormat PH_FORMAT = NumberFormat.getCurrencyInstance();
    public static String fmt(double value){ return PH_FORMAT.format(value); }
}
