package com.webbfontaine.efem.xml.exchange

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.TypeCastUtils
import com.webbfontaine.efem.attachedDoc.AttachedDoc
import com.webbfontaine.efem.attachedDoc.AttachedFile
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.xml.DataBindingHelper
import com.webbfontaine.efem.xml.DocXmlBinder
import groovy.util.slurpersupport.GPathResult
import groovy.xml.StreamingMarkupBuilder
import org.apache.commons.codec.binary.Base64
import static com.webbfontaine.efem.xml.DocXmlBinder.getXmlString;
import com.webbfontaine.efem.XmlValidationService

class BinderExchange {

    public static Exchange bindXmlToExchange(GPathResult importXml) throws RuntimeException {
        Exchange exchange = new Exchange()
        XmlValidationService xmlValidationService = new XmlValidationService()
        exchange.requestType = getXmlString(exchange, importXml, "requestType")
        exchange.basedOn = getXmlString(exchange, importXml, "basedOn")
        xmlValidationService.checkXmlToImport(exchange, importXml)
        if (exchange.errors.errorCount == 0) {
            if (exchange?.requestType == ExchangeRequestType.EA) {
                if (exchange?.basedOn == ExchangeRequestType.BASE_ON_SAD) {
                    exchange.clearanceOfficeCode = getXmlString(exchange, importXml, "clearanceOfficeCode")
                    exchange.clearanceOfficeName = getXmlString(exchange, importXml, "clearanceOfficeName")
                    exchange.declarationSerial = getXmlString(exchange, importXml, "declarationSerial")
                    exchange.declarationNumber = getXmlString(exchange, importXml, "declarationNumber")
                    exchange.declarationDate = TypeCastUtils.toLocalDate(importXml.declarationDate)
                    exchange.finalAmountInDevise = TypeCastUtils.toBigDecimal(importXml.finalAmountInDevise)
                    exchange.finalAmount = TypeCastUtils.toBigDecimal(importXml.finalAmount)
                } else if (exchange?.basedOn == ExchangeRequestType.BASE_ON_TVF) {
                    exchange.tvfNumber = TypeCastUtils.toInteger(importXml.tvfNumber)
                    exchange.tvfDate = TypeCastUtils.toLocalDate(importXml.tvfDate)
                }
                exchange.domiciliationNumber = getXmlString(exchange, importXml, "domiciliationNumber")
                exchange.domiciliationDate = TypeCastUtils.toLocalDate(importXml.domiciliationDate)
                exchange.domiciliationBankCode = getXmlString(exchange, importXml, "domiciliationBankCode")
                exchange.authorizationDate = TypeCastUtils.toLocalDate(importXml.authorizationDate)
                exchange.authorizedBy = getXmlString(exchange, importXml, "authorizedBy")
                exchange.nationalityCode = getXmlString(exchange, importXml, "nationalityCode")
                exchange.resident = getXmlString(exchange, importXml, "resident")
                exchange.currencyPayCode = getXmlString(exchange, importXml, "currencyPayCode")
                exchange.currencyPayName = getXmlString(exchange, importXml, "currencyPayName")
                exchange.currencyPayRate = TypeCastUtils.toBigDecimal(importXml.currencyPayRate)
            } else if (exchange?.requestType == ExchangeRequestType.EC) {
                exchange.dateOfBoarding = TypeCastUtils.toBigDecimal(importXml.dateOfBoarding)
                exchange.geoArea = getXmlString(exchange, importXml, "geoArea")
                exchange.geoAreaName = getXmlString(exchange, importXml, "geoAreaName")
                exchange.countryOfDestinationCode = getXmlString(exchange, importXml, "countryOfDestinationCode")
                exchange.countryOfDestinationName = getXmlString(exchange, importXml, "countryOfDestinationName")
                exchange.finalAmountInDevise = TypeCastUtils.toBigDecimal(importXml.finalAmountInDevise)
                exchange.finalAmount = TypeCastUtils.toBigDecimal(importXml.finalAmount)
                exchange.isFinalAmount = TypeCastUtils.toBoolean(importXml.isFinalAmount)
                exchange.goodsValuesInXOF = TypeCastUtils.toBigDecimal(importXml.goodsValuesInXOF)
                exchange.exFeesPaidByExpInCIinXOF = TypeCastUtils.toBigDecimal(importXml.exFeesPaidByExpInCIinXOF)
                exchange.exFeesPaidByExpInAbroadinXOF = TypeCastUtils.toBigDecimal(importXml.exFeesPaidByExpInAbroadinXOF)
            }
            exchange.countryOfExportCode = getXmlString(exchange, importXml, "countryOfExportCode")
            exchange.bankCode = getXmlString(exchange, importXml, "bankCode")
            exchange.bankName = getXmlString(exchange, importXml, "bankName")
            exchange.commentHeader = getXmlString(exchange, importXml, "commentHeader")
            exchange.exporterCode = getXmlString(exchange, importXml, "exporterCode")
            exchange.exporterNameAddress = getXmlString(exchange, importXml, "exporterNameAddress")
            exchange.importerCode = getXmlString(exchange, importXml, "importerCode")
            exchange.importerNameAddress = getXmlString(exchange, importXml, "importerNameAddress")
            exchange.declarantNameAddress = getXmlString(exchange, importXml, "declarantNameAddress")
            exchange.declarantCode = getXmlString(exchange, importXml, "declarantCode")
            exchange.beneficiaryName = getXmlString(exchange, importXml, "beneficiaryName")
            exchange.beneficiaryAddress = getXmlString(exchange, importXml, "beneficiaryAddress")
            exchange.operType = getXmlString(exchange, importXml, "operType")
            exchange.operTypeName = getXmlString(exchange, importXml, "operTypeName")
            exchange.currencyCode = getXmlString(exchange, importXml, "currencyCode")
            exchange.currencyName = getXmlString(exchange, importXml, "currencyName")
            exchange.currencyRate = TypeCastUtils.toBigDecimal(importXml.currencyRate)
            exchange.amountMentionedCurrency = TypeCastUtils.toBigDecimal(importXml.amountMentionedCurrency)
            exchange.amountNationalCurrency = TypeCastUtils.toBigDecimal(importXml.amountNationalCurrency)
            exchange.balanceAs = TypeCastUtils.toBigDecimal(importXml.balanceAs)
            exchange.countryProvenanceDestinationCode = getXmlString(exchange, importXml, "countryProvenanceDestinationCode")
            exchange.provenanceDestinationBank = getXmlString(exchange, importXml, "provenanceDestinationBank")
            exchange.bankAccountNocreditedDebited = getXmlString(exchange, importXml, "bankAccountNocreditedDebited")
            exchange.exportationTitleNo = getXmlString(exchange, importXml, "exportationTitleNo")
            exchange.accountNumberBeneficiary = getXmlString(exchange, importXml, "accountNumberBeneficiary")
            // Attached docs ----------------------------------------------
            int attRank = 1;
            def attDocs = importXml.attachedDocs.attachment.collect { attachment ->
                AttachedFile file = new AttachedFile(
                        data: Base64.decodeBase64(attachment.attachedFile.toString())
                )
                AttachedDoc attDoc = new AttachedDoc(
                        rank: attRank,
                        docType: attachment.docType as String,
                        docTypeName: attachment.docTypeName as String,
                        docRef: attachment.docRef as String,
                        docDate: TypeCastUtils.toLocalDate(attachment.docDate),
                        fileExtension: attachment.fileExtension as String,
                        attachedFile: file
                )
                attRank++;
                attDoc
            }
            attDocs?.each { attachedDoc ->
                exchange.addToAttachedDocs(attachedDoc);
            }
        }
        return exchange
    }

    public static Writable bindExchangeToXml(domainInstance) {
        def xml = new StreamingMarkupBuilder().bind { builder ->
            exchange {
                buildGeneralSegmentExchange builder, domainInstance
                DocXmlBinder.buildAttachments builder, domainInstance
            }
        }
        xml
    }

    private static void buildGeneralSegmentExchange(builder, def domainInstance) {
        DataBindingHelper.EXCHANGE_ACCEPTABLE_FIELDS_FOR_EXPORT.each { field ->
            builder."${field}"(DocXmlBinder.formatValue(domainInstance, field))
        }

    }

}
