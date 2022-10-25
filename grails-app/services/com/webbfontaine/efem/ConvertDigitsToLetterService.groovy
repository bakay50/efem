package com.webbfontaine.efem

import com.ibm.icu.text.NumberFormat
import com.ibm.icu.text.RuleBasedNumberFormat
import com.webbfontaine.efem.constants.ExchangeRequestType
import grails.gorm.transactions.Transactional
import org.apache.commons.lang3.StringUtils

import java.text.DecimalFormat

@Transactional
class ConvertDigitsToLetterService {


    def convertToLetter(digitToConvert,Lang){
        NumberFormat formatter
        if(Lang == 'en' || Lang == 'EN'){
            formatter = new RuleBasedNumberFormat(Locale.ENGLISH, RuleBasedNumberFormat.SPELLOUT);
        }else{
            formatter = new RuleBasedNumberFormat(Locale.FRANCE, RuleBasedNumberFormat.SPELLOUT);
        }
        return formatter.format(digitToConvert);
    }

    def splitNumberToConvert(def digitToConvert){
        def global_number
        def result = []
        if(digitToConvert.toString().contains(".")){
            global_number = digitToConvert.toString().split("\\.")
        }else if(digitToConvert.toString().contains(",")){
            global_number = digitToConvert.toString().split(",")
        }else{
            result.add(digitToConvert)
            global_number = result
        }
        return  global_number
    }

    def checkSeparator(def locale){
        return (locale == ExchangeRequestType.FR || locale == ExchangeRequestType.FR.toLowerCase()) ?ExchangeRequestType.FRENCH_SEPARATOR:ExchangeRequestType.ENGLISH_SEPARATOR
    }

    def retrieveAndConvertNumberToLetter(def amount, def locale,def pattern){
        def decimalNumber
        def splitNumber
        def amountToConvert
        BigDecimal firstNumberIntoBigDecimal
        BigDecimal secondNumberIntoBigDecimal
        def result
        DecimalFormat decimalFormat = new DecimalFormat(pattern)
        splitNumber = splitNumberToConvert(amount)
        if(splitNumber && splitNumber.size() == 1){
            amountToConvert = splitNumber[0]
            firstNumberIntoBigDecimal = (BigDecimal) decimalFormat.parse(StringUtils.deleteWhitespace(amountToConvert.toString().trim()))
        }else if(splitNumber && splitNumber.size() >= 2){
            amountToConvert = splitNumber[0]
            decimalNumber = checkLengthOfValue(splitNumber[1])
            decimalFormat.setParseBigDecimal(true)
            secondNumberIntoBigDecimal = (BigDecimal) decimalFormat.parse(StringUtils.deleteWhitespace(decimalNumber.toString().trim()))
            firstNumberIntoBigDecimal = (BigDecimal) decimalFormat.parse(StringUtils.deleteWhitespace(amountToConvert.toString().trim()))
        }
        if(decimalNumber && decimalNumber != ExchangeRequestType.ZERO_VALUE){
            result = convertToLetter(firstNumberIntoBigDecimal, locale).concat(" ").concat(checkSeparator(locale)).concat(" ").concat(convertToLetter(secondNumberIntoBigDecimal,locale))
        }else{
            result = convertToLetter(firstNumberIntoBigDecimal,locale)
        }
        return result
    }


    def checkLengthOfValue(def val){
        return (val && val?.toString().length() <= 1)?val:val.take(2)
    }
}
