package com.webbfontaine.efem

import com.ibm.icu.text.RuleBasedNumberFormat
import grails.util.Holders
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.joda.time.ReadablePartial
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.nio.charset.StandardCharsets
import java.text.NumberFormat
import java.text.ParsePosition
import java.text.DecimalFormat

class TypeCastUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern(TypeCastUtils.datePattern())
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern('yyyyMMMdd HH:mm:ss')
    private static volatile DateTimeFormatter defaultFormatter

    public static Integer toInteger(Object obj) {
        try {
            return Integer.valueOf(obj?.toString())
        } catch (NumberFormatException ignore) {
            return null
        }
    }

    public static LocalDate toLocalDate(Object obj, DateTimeFormatter formatter = null) {
        String str = getString(obj)

        try {
            if (str) {
                if (formatter) {
                    return formatter.parseLocalDate(str)
                } else {
                    return DATE_FORMATTER.parseLocalDate(str)
                }
            }
        } catch (e) {
            return null
        }
        return null
    }

    public static String fromDate(LocalDate localDate) {
        if (localDate) {
            return DATE_FORMATTER.print(localDate)
        }

        return null
    }

    public static Integer toLong(Object obj) {
        try {
            return Long.valueOf(obj?.toString())
        } catch (NumberFormatException ignore) {
            return null
        }
    }

    public static String datePattern() {
        Holders.config.jodatime.format.org.joda.time.LocalDate
    }

    public static BigDecimal toBigDecimal(Object obj) {
        String attemptText = getString(obj)

        if (attemptText && attemptText.isBigDecimal()) {
            return attemptText.toBigDecimal()
        } else {
            return null
        }
    }

    public static String formatDate(ReadablePartial date) {
        return date ? getDefaultFormatter().print(date) : ''
    }

    private static def getDefaultFormatter() {
        if (!defaultFormatter) {
            defaultFormatter = DateTimeFormat.forPattern(
                    Holders.config.jodatime.format.org.joda.time.LocalDate
            )
        }

        return defaultFormatter
    }

    public static parseLong(def target) {
        try {
            return Long.parseLong(target)
        } catch (NumberFormatException ex) {
            return null
        }
    }

    public static String toUTF8String(byte[] data) {
        return new String(data, StandardCharsets.UTF_8)
    }

    public static Boolean toBoolean(Object obj) {
        String str = getString(obj);
        try {
            if (str) {
                return Boolean.valueOf(str)
            }
        } catch (e) {
            return false
        }
        return false;
    }

    public static String getCurrentDateTime() {
        return DATE_TIME_FORMATTER.print(LocalDateTime.now())
    }

    public static String parseCurrencyApplyPattern(Object obj, def rate = false) {
        String str = getString(obj);
        try {
            if (str) {
                if (str.contains(",")) {
                    NumberFormat format = NumberFormat.getInstance(Locale.FRENCH)
                    return format.parse(str).toString()
                } else {
                    String pattern
                    if (rate) {
                        pattern = Holders.grailsApplication.config.numberFormatConfig.exchangeRateFormat
                    } else {
                        pattern = Holders.grailsApplication.config.numberFormatConfig.decimalNumberFormat
                    }
                    DecimalFormat decimalFormat = new DecimalFormat(pattern);
                    DecimalFormat df = (DecimalFormat) decimalFormat.getInstance(Locale.ENGLISH)
                    df.setParseBigDecimal(true);
                    return (BigDecimal) df.parse(str, new ParsePosition(0));
                }
            }
        } catch (e) {
            return BigDecimal.ZERO
        }
    }

    public static String getString(Object obj) {
        if (obj) {
            String str = obj.toString()

            if (!str.isEmpty()) {
                return str
            }
        }

        return null
    }

    public static BigDecimal parseStringToBigDecimal(item, rate = false) {
        String pattern
        if (rate) {
            pattern = Holders.grailsApplication.config.numberFormatConfig.exchangeRateFormat
        } else {
            pattern = Holders.grailsApplication.config.numberFormatConfig.decimalNumberFormat
        }
        DecimalFormat decimalFormat = new DecimalFormat(pattern)
        DecimalFormat df = (DecimalFormat) decimalFormat.getInstance(Locale.ENGLISH)
        df.setParseBigDecimal(true)
        return item ? (BigDecimal) df.parse(item as String, new ParsePosition(0)) : BigDecimal.ZERO
    }


    public static def convertToLetter(DigitToConvert, Lang) {
        com.ibm.icu.text.NumberFormat formatter
        if (Lang == 'en' || Lang == 'EN') {
            formatter = new RuleBasedNumberFormat(Locale.ENGLISH, RuleBasedNumberFormat.SPELLOUT);
        } else {
            formatter = new RuleBasedNumberFormat(Locale.FRANCE, RuleBasedNumberFormat.SPELLOUT);
        }
        return formatter.format(DigitToConvert);
    }

    public static def splitNumberToCovert(def DigitToConvert) {
        def global_number
        def result = []
        if (DigitToConvert.toString().contains(".")) {
            global_number = DigitToConvert.toString().split("\\.")
        } else if (DigitToConvert.toString().contains(",")) {
            global_number = DigitToConvert.toString().split(",")
        } else {
            result.add(DigitToConvert)
            global_number = result
        }
        return global_number
    }


}
