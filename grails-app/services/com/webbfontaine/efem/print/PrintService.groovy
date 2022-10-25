package com.webbfontaine.efem.print

import com.webbfontaine.efem.AppConfig
import com.webbfontaine.efem.BusinessLogicUtils
import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.TypeCastUtils
import com.webbfontaine.efem.attachedDoc.AttachedDoc
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.constants.UtilConstants
import com.webbfontaine.efem.tvf.Tvf
import com.webbfontaine.grails.plugin.layout.MessageUtils
import com.webbfontaine.sw.rimm.RimmSadAttdocs
import grails.gorm.transactions.Transactional
import grails.util.Holders
import groovy.util.logging.Slf4j
import org.apache.commons.lang.StringUtils
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.servlet.support.RequestContextUtils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

@Slf4j('LOGGER')
@Transactional
class PrintService {

    def tvfService
    def printResultService
    def grailsApplication
    def printParametersService
    def beanDataLoadService

    private static final FRENCH_DATE_FORMAT = "dd/MM/yyyy"
    private boolean IS_DISPLAY = Holders.config.efemciApplicationConfig?.displayBeneficiary
    def decimal_format = Holders.config.numberFormatConfig.decimalNumberFormat

    def getTvfDetails(Exchange exchange) {
        def resultImporter = exchange?.importerNameAddress?.split("\r\n")
        def applicant_name = (resultImporter?.length >= 1) ? resultImporter.getAt(0) : null
        def applicant_adresss = (resultImporter?.length >= 2) ? resultImporter.getAt(1) : null
        def applicant_phone
        def applicant_email
        def recipient_name = exchange?.beneficiaryName
        def recipient_adress = exchange?.beneficiaryAddress
        def recipient_pays
        if (exchange.basedOn == ExchangeRequestType.BASE_ON_TVF && !applicant_name) {
            def tvf = tvfService.getExchangeTvfFromTvfService(exchange)

            applicant_name = tvf?.impName
            applicant_phone = tvf?.impPhone
            applicant_email = tvf?.impEmail
            applicant_adresss = tvf?.impAddress?.replace('\n', ' ')
        }
        [applicant_name : applicant_name, applicant_adresss: applicant_adresss, recipient_name: recipient_name,
         applicant_phone: applicant_phone, applicant_email: applicant_email, recipient_adress: recipient_adress, recipient_pays: recipient_pays]
    }

    def setupTVFParams(Map exchangeParams, Tvf tvf) {
        exchangeParams.put("reference_invoice", tvf?.invReference)
        exchangeParams.put("reference_date", tvf?.invDate)
        exchangeParams.put("devise", tvf?.invCurCode)
        exchangeParams.put("incoterm", tvf?.incCode)
        exchangeParams.put("invoice_amount", tvf?.totInvoiceAmount)
        exchangeParams.put("fob", tvf?.totFobValInFgn)
        exchangeParams.put("fret", tvf?.totFreightInFgn)
        exchangeParams.put("insurance", tvf?.totInsInFgn)
        exchangeParams.put("othercharge", tvf?.totOtherInFgn)
        exchangeParams.put("caf", tvf?.totCifInFgn)
    }

    def decimalFormatter(Exchange exchange) {
        def decimal_format = "#,###.00"
        def formattedValue = new DecimalFormat(decimal_format, new DecimalFormatSymbols(Locale.FRENCH)).format(new BigDecimal(exchange?.amountNationalCurrency?.toString()))
        LOGGER.debug("PrintService, decimal formatter: formattedValue = ${formattedValue}")
        return formattedValue
    }

    def setECParameters(exchange, exchangeParams, nameofprint) {
        nameofprint = isECProvisory(exchange) ? ExchangeRequestType.ENGAGEMENT_DE_CHANGE_PROVISOIRE : ExchangeRequestType.ENGAGEMENT_DE_CHANGE
        exchangeParams.put("TITLE", resourceAsStream(isECProvisory(exchange) ? '/reports/images/logoEC_PROV.png' : '/reports/images/logoEC_EC.jpg'))
        canDisplayStamp(exchangeParams, exchange)
        [exchangeParams, nameofprint]
    }

    def setAEParameters(exchangeParams, nameofprint) {
        nameofprint = ExchangeRequestType.ATTESTATION_EXPORTATION
        exchangeParams.put("TITLE", resourceAsStream('/reports/images/logoEC_AE.jpg'))
        [exchangeParams, nameofprint]
    }

    void canDisplayStamp(exchangeParams, exchange) {
        if (!isECProvisory(exchange) && BusinessLogicUtils.canDisplayStamp(exchange)) {
            exchangeParams.put("STAMP", resourceAsStream('/reports/images/stampFinex.png'))
        }
    }

    def printEcDocument(Exchange exchange, response, printType) {
        boolean isProvisory = false
        def docFormat = "PDF"
        def exchangeParams = [:]
        def nameofprint = 'print-Exchange'
        if (exchange) {
            if (exchange?.requestNo) {
                exchangeParams.put("requestNumber", exchange.requestNo)
            }
            isProvisory = isECProvisory(exchange)
            if (printType == ExchangeRequestType.EC) {
                (exchangeParams, nameofprint) = setECParameters(exchange, exchangeParams, nameofprint)
            } else if (printType == ExchangeRequestType.AE) {
                (exchangeParams, nameofprint) = setAEParameters(exchangeParams, nameofprint)
            }
            setECInvoiceDetails(exchange, exchangeParams)
            printParametersService.setupSadGoodsParams(exchange, isProvisory, exchangeParams)
            printParametersService.setSadInformationsInParams(exchange, exchangeParams)
            if (printType == "EC") {
                if (isProvisory) {
                    printResultService.exportPrintResult(docFormat, [exchange], exchangeParams, "EC_ProvExchangeWithNoGoods.jasper", nameofprint, response)
                } else {
                    printResultService.exportPrintResult(docFormat, [exchange], exchangeParams, "ECExchangeWithGoods.jasper", nameofprint, response)
                }
            } else if (printType == "AE") {
                printResultService.exportPrintResult(docFormat, [exchange], exchangeParams, "EC_AEExchangeWithGoods.jasper", nameofprint, response)
            }
        }
    }

    def printEaDocument(exchange, request, response) {
        LOGGER.debug("in Print EA action")
        def docFormat = "PDF"
        def exchangeParams = [:]
        def nameofprint = 'print-Exchange'
        if (exchange) {
            setupExchangeFiels(exchange, exchangeParams, request)
            setupPrintOutParams(exchange, exchangeParams)
            setupResidentValue(exchange, exchangeParams)
            printResultService.exportPrintResult(docFormat, [exchange], exchangeParams, "ACExchangeTresor.jasper", nameofprint, response)
        }
    }

    def isECProvisory(domain) {
        return ((!domain?.clearanceOfficeCode || !domain?.declarationSerial || !domain?.declarationNumber || !domain?.declarationDate) && domain?.status in [Statuses.ST_APPROVED] && domain.requestType == ExchangeRequestType.EC)
    }

    def resourceAsStream(String resource) {
        return grailsApplication.parentContext.getResource(resource).inputStream
    }


    def setupExchangeFiels(exchange, exchangeParams, request) {
        if (exchange) {
            exchangeParams.put("STATUS", exchange?.status)
            def amountNationalCurrencyComa = decimalFormatter(exchange)
            exchangeParams.put("amountNationalCurrencyComa", amountNationalCurrencyComa)

            if (exchange.basedOn == ExchangeRequestType.BASE_ON_TVF) {
                // get tvf information for update rimmviews and views of TVF
                def tvfDetails = getTvfDetails(exchange)
                exchangeParams.put("applicant_name", tvfDetails?.applicant_name)
                exchangeParams.put("applicant_adresss", tvfDetails?.applicant_adresss)
                exchangeParams.put("applicant_phone", tvfDetails.applicant_phone)
                exchangeParams.put("applicant_email", tvfDetails.applicant_email)
                exchangeParams.put("recipient_name", tvfDetails.recipient_name)
                exchangeParams.put("recipient_adress", tvfDetails.recipient_adress)
                exchangeParams.put("recipient_pays", tvfDetails.recipient_pays)
            } else {
                def importer = exchange?.importerNameAddress?.split("\r\n")
                def applicantName = (importer?.length >= 1) ? importer.getAt(0) : null
                def applicantAdresss = (importer?.length >= 2) ? importer.getAt(1) : null

                exchangeParams.put("applicant_name", applicantName)
                exchangeParams.put("applicant_adresss", applicantAdresss)
                exchangeParams.put("recipient_name", exchange?.beneficiaryName)
                exchangeParams.put("recipient_adress", exchange?.beneficiaryAddress)
            }

            setExecutionsDetails(exchange, exchangeParams)
            exchangeParams.put("locale", FRENCH_DATE_FORMAT)
            setAttachmentDetails(exchange, exchangeParams)
            setAmountMentionedCurrency(exchange, exchangeParams, request)
            setAmountNationalCurrency(exchange, exchangeParams, request)

        }
        exchangeParams
    }


    def setupPrintOutParams(exchange, exchangeParams) {
        String stampPath = AppConfig.StampPath()
        String StartStampDate = AppConfig.StartStampDate()
        String StampCompltPath = stampPath + exchange.bankCode + '_StampsSignature.jpg'
        File file = new File(StampCompltPath);

        if ((exchange?.status in [Statuses.ST_APPROVED, Statuses.ST_EXECUTED])) {
            exchangeParams.put("TITLE", resourceAsStream('/reports/images/logoAcTresor.jpg'))
            if (exchange?.authorizedBy?.equalsIgnoreCase(UtilConstants.THE_FINEX)) {
                exchangeParams.put("STAMP", resourceAsStream('/reports/images/stampFinex.png'))
            } else if (exchange?.authorizedBy != UtilConstants.THE_FINEX && !AppConfig.allowFinexApprovalEA() && exchange.bankApprovalDate.isAfter(TypeCastUtils.toLocalDate(StartStampDate))) {
                exchangeParams.put("ALLOW_BANK_APPROVAL", MessageUtils.getMessage([code: "print.allowBankApproval", locale: LocaleContextHolder.locale]))
                def result = beanDataLoadService.loadFullBeanData('HT_BNK', ['code': exchange?.authorizedBy], 'get')
                exchangeParams.put("BANK_NAME", result?.description)
                if (file.exists())
                {
                    FileInputStream fileByte = new FileInputStream(file);
                    exchangeParams.put("STAMP", fileByte)
                }
                exchangeParams.put("IS_BANK_APPROVAL", !AppConfig.allowFinexApprovalEA())
            }
        }

        if (exchange?.status in [Statuses.ST_REQUESTED]) {
            exchangeParams.put("TITLE", resourceAsStream('/reports/images/logoBrouillonTresor.jpg'))
            exchangeParams.put("draft", resourceAsStream('/reports/images/brouillon.jpg'))
        }

        exchangeParams.put("BENEFICIARY_IS_DISPLAY", IS_DISPLAY)
        exchangeParams.put("LOGO", resourceAsStream('/reports/images/benin-logo.png'))
        exchangeParams.put("StampsSignature", resourceAsStream('/reports/images/govOfficeStampsSignature.png'))
        exchangeParams.put("AMOUNT_FORMATTER", new DecimalFormat("#,##0.00#", new DecimalFormatSymbols(LocaleContextHolder.locale)))

    }

    def setExecutionsDetails(exchange, exchangeParams) {
        def executions
        def executionDate
        def creditCorrespondentAccount
        def accountOwnerCredited
        def bankAccountNumberCreditedDebited
        def currencyExName
        def currencyExCode
        def executingBankCode
        def executingBankName
        def countryProvenanceDestinationExName
        def provenanceDestinationExBank
        def accountExBeneficiary
        if (exchange?.executions) {
            executions = exchange?.executions.sort { a, b -> a?.rank <=> b?.rank }
            executionDate = executions.get(0).executionDate
            creditCorrespondentAccount = executions.get(0).creditCorrespondentAccount
            provenanceDestinationExBank = executions.get(0).provenanceDestinationExBank
            accountOwnerCredited = executions.get(0).accountOwnerCredited
            currencyExName = executions.get(0).currencyExName
            currencyExCode = executions.get(0).currencyExCode
            countryProvenanceDestinationExName = executions.get(0).countryProvenanceDestinationExName
            accountExBeneficiary = executions.get(0).accountExBeneficiary
            executingBankCode = executions.get(0).executingBankCode
            executingBankName = executions.get(0).executingBankName
            bankAccountNumberCreditedDebited = executions.get(0).bankAccountNumberCreditedDebited
        }
        exchangeParams.put("bankAccountNumberCreditedDebited", bankAccountNumberCreditedDebited)
        exchangeParams.put("executingBankCode", executingBankCode)
        exchangeParams.put("executingBankName", executingBankName)
        exchangeParams.put("executionDate", executionDate)
        exchangeParams.put("creditCorrespondentAccount", creditCorrespondentAccount)
        exchangeParams.put("provenanceDestinationExBank", provenanceDestinationExBank)
        exchangeParams.put("accountOwnerCredited", accountOwnerCredited)
        exchangeParams.put("currencyExName", currencyExName)
        exchangeParams.put("currencyExCode", currencyExCode)
        exchangeParams.put("countryProvenanceDestinationExName", countryProvenanceDestinationExName)
        exchangeParams.put("accountExBeneficiary", accountExBeneficiary)
    }

    def setECInvoiceDetails(exchange, exchangeParams) {
        def referenceFacture = ""
        def startSpecificAttDoc = ""
        List requiredFacture = ExchangeRequestType.invoiceCodes

        if (exchange?.attachedDocs) {
            exchange?.attachedDocs?.each { AttachedDoc attDoc ->
                if (attDoc?.docType in requiredFacture) {
                    if (!startSpecificAttDoc.equals(ExchangeRequestType.CODE_FACTURE)) {
                        startSpecificAttDoc = attDoc.docType
                        referenceFacture = attDoc.docRef
                    }
                }
            }
        }
        exchangeParams.put("invoiceNumber", referenceFacture)
    }

    def setAttachmentDetails(exchange, exchangeParams) {
        def specificAttDoc = null
        def startSpecificAttDoc = ""
        List requiredFacture = AppConfig.factureAttachments
        int docSize = exchange?.attachedDocs?.size()
        exchange?.attachedDocs?.each { AttachedDoc attDoc ->
            if ((docSize == 1 || docSize == attDoc?.rank) && attDoc?.docType in requiredFacture) {
                specificAttDoc = "Facture N° ${attDoc.docRef} du ${attDoc.docDate.toString(FRENCH_DATE_FORMAT)}"
            } else if (docSize > 1 && attDoc?.docType in requiredFacture && docSize >= attDoc?.rank) {
                specificAttDoc = "Facture N° ${attDoc.docRef} du ${attDoc.docDate.toString(FRENCH_DATE_FORMAT)}, "
            } else if ((docSize > attDoc?.rank) && !(attDoc?.docType in requiredFacture)) {
                specificAttDoc = "${attDoc.docTypeName}, "
            } else {
                specificAttDoc = "${attDoc.docTypeName} "
            }
            startSpecificAttDoc = startSpecificAttDoc + specificAttDoc
        }
        exchangeParams.put("piecejointe", startSpecificAttDoc)
    }


    def setAmountMentionedCurrency(exchange, exchangeParams, request) {
        def enlettre = null
        def returnnumber
        String local = RequestContextUtils.getLocale(request).language.toString()

        if (exchange.amountMentionedCurrency) {
            if (local == 'fr_FR' || local == 'fr' || local == 'FR') {
                returnnumber = TypeCastUtils.splitNumberToCovert(exchange.amountMentionedCurrency)

                if (returnnumber && returnnumber.size() == 1) {
                    enlettre = TypeCastUtils.convertToLetter(returnnumber[0], local)
                } else if (returnnumber && returnnumber.size() == 2) {
                    String decimalPoint = "virgule"
                    String convertedToWords = TypeCastUtils.convertToLetter(exchange.amountMentionedCurrency, RequestContextUtils.getLocale(request))
                    String wholeNumber = convertedToWords.split(decimalPoint)[0]

                    if (returnnumber[1] && (returnnumber[1] as BigDecimal == BigDecimal.ZERO)) {
                        enlettre = wholeNumber

                    } else {
                        enlettre = TypeCastUtils.convertToLetter(exchange.amountMentionedCurrency, RequestContextUtils.getLocale(request))
                    }
                }
            } else {
                enlettre = TypeCastUtils.convertToLetter(exchange.amountMentionedCurrency, local)
            }
            exchangeParams.put("enlettre", enlettre)
            def currencyName
            currencyName = exchange?.currencyName
            exchangeParams.put("currencyName", currencyName)
        }

    }

    def setAmountNationalCurrency(exchange, exchangeParams, request) {
        if (exchange.amountNationalCurrency) {
            def enlettre_National = null
            def DecimalPartenLettre
            def returnnumber
            def IntegerPartenLettre
            DecimalFormat decimalFormat = new DecimalFormat(decimal_format);
            String local = RequestContextUtils.getLocale(request).language.toString()

            if (local == 'fr_FR' || local == 'fr' || local == 'FR') {
                returnnumber = TypeCastUtils.splitNumberToCovert(exchange.amountNationalCurrency)
                LOGGER.debug("returnnumber:" + returnnumber)
                if (returnnumber && returnnumber.size() == 1) {
                    BigDecimal integer_part = (BigDecimal) decimalFormat.parse(StringUtils.deleteWhitespace(returnnumber[0].toString().trim()));
                    enlettre_National = TypeCastUtils.convertToLetter(integer_part, local)
                } else if (returnnumber && returnnumber.size() == 2) {
                    if (returnnumber[1] && returnnumber[1] != '000') {
                        BigDecimal integer_part = (BigDecimal) decimalFormat.parse(StringUtils.deleteWhitespace(returnnumber[0].toString().trim()));
                        IntegerPartenLettre = TypeCastUtils.convertToLetter(integer_part, local)
                        BigDecimal decimal_part = (BigDecimal) decimalFormat.parse(StringUtils.deleteWhitespace(returnnumber[1].toString().trim()));
                        DecimalPartenLettre = TypeCastUtils.convertToLetter(decimal_part, local)
                        enlettre_National = IntegerPartenLettre.concat(" ").concat("virgule").concat(" ").concat(DecimalPartenLettre)
                    } else {
                        if (returnnumber[0] && returnnumber[0]) {
                            BigDecimal integer_part = (BigDecimal) decimalFormat.parse(StringUtils.deleteWhitespace(returnnumber[0].toString().trim()));
                            enlettre_National = TypeCastUtils.convertToLetter(integer_part, local)
                        }
                    }
                }

            } else {
                enlettre_National = TypeCastUtils.convertToLetter(exchange.amountNationalCurrency, local)
            }
            exchangeParams.put("enlettre_National", enlettre_National)
        }

    }

    def setupVGS_AMT(sad, exchangeParams) {
        BigDecimal IFR_AMT_NMU = BigDecimal.ZERO
        BigDecimal EFR_AMT_NMU = BigDecimal.ZERO
        def VGS_IFR_AMT_NMU = BigDecimal.ZERO
        def VGS_EFR_AMT_NMU = BigDecimal.ZERO
        VGS_IFR_AMT_NMU = sad?.vgs_ifr_amt_nmu
        VGS_EFR_AMT_NMU = sad?.vgs_efr_amt_nmu


        if (VGS_IFR_AMT_NMU) {
            if (VGS_IFR_AMT_NMU.toString().isBigDecimal() == false) {
                IFR_AMT_NMU = new BigDecimal(VGS_IFR_AMT_NMU.toString())
            } else {
                IFR_AMT_NMU = VGS_IFR_AMT_NMU
            }
        } else {
            IFR_AMT_NMU = BigDecimal.ZERO
        }
        if (VGS_EFR_AMT_NMU) {
            if (VGS_EFR_AMT_NMU.toString().isBigDecimal() == false) {
                EFR_AMT_NMU = new BigDecimal(VGS_EFR_AMT_NMU.toString())
            } else {
                EFR_AMT_NMU = VGS_EFR_AMT_NMU
            }
        } else {
            EFR_AMT_NMU = BigDecimal.ZERO
        }
        exchangeParams.put("fret", EFR_AMT_NMU.add(IFR_AMT_NMU))
        exchangeParams.put("insurance", sad?.vgs_ins_amt_nmu)
        exchangeParams.put("othercharge", sad?.vgs_otc_amt_nmu)
        exchangeParams.put("caf", sad?.fin_amt_dty)
    }


    def setUpInvoice(exchange, exchangeParams) {
        def invoice_sad = RimmSadAttdocs.where {
            office_code == exchange.clearanceOfficeCode && declaration_serial == exchange.declarationSerial && declaration_No == exchange.declarationNumber && declaration_date == exchange.declarationDate && attached_Document_code == '0007'
        }.list()
        if (invoice_sad) {
            exchangeParams.put("reference_invoice", invoice_sad?.attached_Document_reference.get(0))
            exchangeParams.put("reference_date", invoice_sad?.attached_Document_date.get(0))
        } else {
            exchangeParams.put("reference_invoice", null)
            exchangeParams.put("reference_date", null)
        }
    }


    def setupResidentValue(Exchange exchange, exchangeParams) {
        String language = LocaleContextHolder.locale.toString()
        if (language == 'fr') {
            exchangeParams.put("resident_local", "oui")
        } else {
            exchangeParams.put("resident_local", exchange.resident)
        }
        exchangeParams
    }
}
