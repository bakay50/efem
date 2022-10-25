package com.webbfontaine.efem.tvf


import org.joda.time.LocalDate

class Tvf {

    String instanceId
    Integer trNumber
    LocalDate trDate
    String domiciliationRef
    LocalDate domiciliationDate
    String bankCode
    String bankName //
    String expTaxPayerAcc
    String expName
    String impTaxPayerAcc
    String impName
    String invCurCode
    String invCurName //
    String status
    String decCode
    String decName
    String decPhone
    String decAddress
    String impCtyCode
    String impCtyName
    LocalDate expirationDate
    BigDecimal totInvoiceAmount
    BigDecimal totInvoiceAmountNat
    BigDecimal invCurRat
    BigDecimal invCurPayRat
    String invReference
    LocalDate invDate
    String incCode
    String incName
    BigDecimal totFobValInFgn
    BigDecimal totFreightInFgn
    BigDecimal totInsInFgn
    BigDecimal totOtherInFgn
    BigDecimal totCifInFgn
    String cuoCode
    String cuoName

    String contactName
    String contactEmail
    String contactTel

    // add for email , phone for exporter and importer
    String impAddress
    String impPhone
    String impEmail
    String expAddress
    String expCtyCode
    String expCtyName
    String expEmail
    String expPhone
    String countryOfExportCode //
    String countryOfExportName //
    String flow

    //ttDocs
/*    List<TvfAttachment> attachments

    static hasMany = [attachments: TvfAttachment]
    static fetchMode = [attachments: 'join']

    static mapping = {
        attachments lazy: false
    }*/

}
