package com.webbfontaine.efem.print

import com.webbfontaine.efem.PrintUtils
import com.webbfontaine.efem.TypeCastUtils
import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.command.ExchangeSearchCommand
import com.webbfontaine.efem.command.RepatriationSearchCommand
import com.webbfontaine.efem.command.TransferOrderSearchCommand
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.transferOrder.search.TransferOrderSearchService
import grails.util.Holders
import groovy.util.logging.Slf4j
import net.sf.jasperreports.engine.JasperReport
import net.sf.jasperreports.engine.util.JRLoader
import org.apache.commons.lang.StringUtils
import org.springframework.web.servlet.support.RequestContextUtils
import java.sql.SQLException
import java.sql.SQLRecoverableException
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

@Slf4j('LOGGER')
class PrintController {
    def printResultService
    def clientWebService
    def printService
    def exchangeSearchService
    def repatriationSearchService
    TransferOrderSearchService transferOrderSearchService
    static searchResultData = []

    private static boolean IS_DISPLAY = Holders.config.efemciApplicationConfig?.displayBeneficiary
    private static String IS_WEBSERVICE = Holders.config.efemciApplicationConfig.webServiceConfig.useWebService

    def resourceAsStream(String resource) {
        return grailsApplication.parentContext.getResource(resource).inputStream
    }

    def draftEc() {
        LOGGER.warn("draft Method")
        flash.ExceptionMessage = null
        params._format = "PDF"
        def exchangeParams = [:]
        def nameofprint = 'print-ExchangeDraft'
        LOGGER.debug("Data params?.id = {}", params?.id)
        def exchange = Exchange.read(params?.id as Integer)
        def decimal_format = grailsApplication.config.numberFormatConfig.decimalNumberFormat
        String local = RequestContextUtils.getLocale(request).language.toString()

        try {
            if (exchange) {
                exchangeParams.put("STATUS", exchange.status)
                def applicantRecipientDetails = printService.getTvfDetails(exchange)
                exchangeParams.put("applicant_name", applicantRecipientDetails.applicant_name)
                exchangeParams.put("applicant_adresss", applicantRecipientDetails.applicant_adresss)
                exchangeParams.put("recipient_name", applicantRecipientDetails.recipient_name)
                exchangeParams.put("recipient_adress", applicantRecipientDetails.recipient_adress)

                // check piece Jointe
                def liste_piecesjointe = null
                def enlettre = null
                def enlettre_National = null
                DecimalFormat decimalFormat = new DecimalFormat(decimal_format);
                decimalFormat.setParseBigDecimal(true);
                if (exchange.attachedDocs) {
                    liste_piecesjointe = exchange.attachedDocs.docTypeName.join(' / ')
                }
                exchangeParams.put("piecejointe", liste_piecesjointe)
                if (exchange.amountMentionedCurrency) {
                    def DecimalPartenLettre
                    def returnnumber
                    def IntegerPartenLettre
                    if (local == 'fr_FR' || local == 'fr' || local == 'FR') {
                        def amount = new DecimalFormat(decimal_format, new DecimalFormatSymbols(Locale.FRENCH)).format(new BigDecimal(exchange.amountMentionedCurrency.toString()))
                        returnnumber = TypeCastUtils.splitNumberToCovert(amount)
                        LOGGER.debug("returnnumber:" + returnnumber)
                        if (returnnumber && returnnumber.size() == 1) {
                            BigDecimal integer_part = (BigDecimal) decimalFormat.parse(StringUtils.deleteWhitespace(returnnumber[0].toString().trim()));
                            enlettre = TypeCastUtils.convertToLetter(integer_part, local)
                        } else if (returnnumber && returnnumber.size() == 2) {
                            if (returnnumber[1] && returnnumber[1] != '000') {
                                BigDecimal integer_part = (BigDecimal) decimalFormat.parse(StringUtils.deleteWhitespace(returnnumber[0].toString().trim()));
                                IntegerPartenLettre = TypeCastUtils.convertToLetter(integer_part, local)
                                BigDecimal decimal_part = (BigDecimal) decimalFormat.parse(StringUtils.deleteWhitespace(returnnumber[1].toString().trim()));
                                DecimalPartenLettre = TypeCastUtils.convertToLetter(decimal_part, local)
                                enlettre = IntegerPartenLettre.concat(" ").concat("virgule").concat(" ").concat(DecimalPartenLettre)
                            } else {
                                if (returnnumber[0] && returnnumber[0]) {
                                    BigDecimal integer_part = (BigDecimal) decimalFormat.parse(StringUtils.deleteWhitespace(returnnumber[0].toString().trim()));
                                    enlettre = TypeCastUtils.convertToLetter(integer_part, local)
                                }
                            }
                        }

                    } else {
                        enlettre = TypeCastUtils.convertToLetter(exchange.amountMentionedCurrency, local)
                    }
                    exchangeParams.put("enlettre", enlettre)
                }
                if (exchange.amountNationalCurrency) {
                    def DecimalPartenLettre
                    def returnnumber
                    def IntegerPartenLettre
                    if (local == 'fr_FR' || local == 'fr' || local == 'FR') {
                        def amount = new DecimalFormat(decimal_format, new DecimalFormatSymbols(Locale.FRENCH)).format(new BigDecimal(exchange.amountNationalCurrency.toString()))
                        returnnumber = TypeCastUtils.splitNumberToCovert(amount)
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
                exchangeParams.put("BENEFICIARY_IS_DISPLAY", IS_DISPLAY)
                if (RequestContextUtils.getLocale(request).language.toString().toUpperCase().equals('FR')) {
                    nameofprint = "BrouillonAutorisationDeChange"
                    exchangeParams.put("TITLE_DRAFT", resourceAsStream('/reports/images/logoBrouillonTresor.jpg'))
                    exchangeParams.put("draft", resourceAsStream('/reports/images/brouillon.jpg'))
                } else if (RequestContextUtils.getLocale(request).language.toString().toUpperCase().equals('EN')) {
                    nameofprint = "ExchangeDraft"
                    exchangeParams.put("TITLE_DRAFT", resourceAsStream('/reports/images/logoBrouillonTresor.jpg'))
                    exchangeParams.put("draft", resourceAsStream('/reports/images/brouillon.jpg'))
                } else {
                    nameofprint = "ExchangeDraft"
                    exchangeParams.put("TITLE_DRAFT", resourceAsStream('/reports/images/logoBrouillon.jpg'))
                    exchangeParams.put("draft", resourceAsStream('/reports/images/logoBrouillonTresor.jpg'))
                }
                printResultService.exportPrintResult(params._format, [exchange], exchangeParams, "ACDraft.jasper", nameofprint, response)
            } else {
                LOGGER.debug("Cannot retreive exchange with id : ${params?.id}")
                flash.ExceptionMessage = message(code: 'exeption.msg')
                redirect(action: 'show', id: params?.id as Integer, controller: 'exchange')
            }
        } catch (Throwable t) {
            flash.ExceptionMessage = message(code: 'exeption.msg')
            exchangeParams.put("exception", "true")
            LOGGER.debug("Exception in printController t : ${t.getMessage()}")
            redirect(action: 'show', id: params?.id as Integer, controller: 'exchange')
        } catch (SQLRecoverableException sqlRecExcep) {
            flash.ExceptionMessage = message(code: 'exeption.msg')
            LOGGER.debug("Exception in printController sqlRecExcep : ${sqlRecExcep.getMessage()}")
            redirect(action: 'show', id: params?.id as Integer, controller: 'exchange')
        }
        catch (SQLException sqlExcep) {
            flash.ExceptionMessage = message(code: 'exeption.msg')
            LOGGER.debug("Exception in printController sqlExcep : ${sqlExcep.getMessage()}")
            redirect(action: 'show', id: params?.id as Integer, controller: 'exchange')
        }
        catch (IllegalArgumentException ex) {
            flash.ExceptionMessage = message(code: 'exeption.msg')
            exchangeParams.put("exception", "true")
            LOGGER.warn("Exception in printController ex : ${ex.getMessage()}")
            redirect(action: 'show', id: params?.id as Integer, controller: 'exchange')
        }

    }

    String getBeneficiaryNameAddress(Exchange exchange){
        if(exchange.executions.size() > 0){
            return exchange.executions.first().accountExBeneficiary
        }else{
            return exchange.beneficiaryNameAddress
        }
    }

    def printEa() {
        LOGGER.debug("in Print EA action")
        flash.ExceptionMessage = null
        params._format = "PDF"
        def exchange = Exchange.get(params?.id as Integer)
        try {
            if (exchange) {
                exchange.beneficiaryNameAddress = getBeneficiaryNameAddress(exchange)
                printService.printEaDocument(exchange, request, response)
            } else {
                redirect action: 'index'
            }
        }
        catch (Throwable t) {
            flash.ExceptionMessage = message(code: 'exeption.msg')
            LOGGER.debug("Exception in printController t : ${t.getMessage()}")
            redirect(action: 'show', id: params?.id as Integer, controller: 'exchange')
        } catch (SQLRecoverableException sqlRecExcep) {
            flash.ExceptionMessage = message(code: 'exeption.msg')
            LOGGER.debug("Exception in printController sqlRecExcep : ${sqlRecExcep.getMessage()}")
            redirect(action: 'show', id: params?.id as Integer, controller: 'exchange')
        } catch (SQLException sqlExcep) {
            flash.ExceptionMessage = message(code: 'exeption.msg')
            LOGGER.debug("Exception in printController sqlExcep : ${sqlExcep.getMessage()}")
            redirect(action: 'show', id: params?.id as Integer, controller: 'exchange')
        }
        catch (IllegalArgumentException ex) {
            flash.ExceptionMessage = message(code: 'exeption.msg')
            LOGGER.debug("Exception in printController ex : ${ex.getMessage()}")
            redirect(action: 'show', id: params?.id as Integer, controller: 'exchange')
        }
    }

    def printEc() {
        def printType = params?.printType
        LOGGER.info("printECDoc Method")
        flash.ExceptionMessage = null
        def exchange
        try {
            exchange = Exchange.get(params?.id as Integer)
            if (exchange) {
                printService.printEcDocument(exchange, response, printType)
            } else {
                redirect action: 'index'
            }
        } catch (Throwable t) {
            flash.ExceptionMessage = message(code: 'exeption.msg')
            LOGGER.info("Exception in printController sqlExcep : ${t.getMessage()}")
            redirect(action: 'show', id: params?.id as Integer, controller: 'exchange')
        } catch (SQLRecoverableException sqlRecExcep) {
            flash.ExceptionMessage = message(code: 'exeption.msg')
            LOGGER.info("Exception in printController sqlRecExcep : ${sqlRecExcep.getMessage()}")
            redirect(action: 'show', id: params?.id as Integer, controller: 'exchange')
        } catch (SQLException sqlExcep) {
            flash.ExceptionMessage = message(code: 'exeption.msg')
            LOGGER.info("Exception in printController sqlExcep : ${sqlExcep.getMessage()}")
            redirect(action: 'show', id: params?.id as Integer, controller: 'exchange')
        }

    }

    def draft() {
        LOGGER.warn("draft Method")
        flash.ExceptionMessage = null
        params._format = "PDF"
        def sad
        def exchangeParams = [:]
        def nameofprint = 'print-ExchangeDraft'
        LOGGER.warn("Data params?.id = {}", params?.id)
        def exchange = Exchange.get(params?.id as Integer)
        def tvf = exchange?.tvf
        String IS_WEBSERVICE = Holders.config.efemciApplicationConfig.webServiceConfig.useWebService

        try {
            if (exchange) {
                // set common TVF OR SAD information
                if (exchange.basedOn == ExchangeRequestType.BASE_ON_TVF) {
                    if (tvf) {
                        exchangeParams.put("reference_invoice", tvf?.invoice_reference)
                        exchangeParams.put("reference_date", tvf?.invoice_reference)
                        exchangeParams.put("devise", tvf?.invoice_cur_code)
                        exchangeParams.put("incoterm", tvf?.incoterm_code)
                        exchangeParams.put("invoice_amount", tvf?.tot_ivn_amt)
                        exchangeParams.put("fob", tvf?.tot_fob_fgn_cur)
                        exchangeParams.put("fret", tvf?.tot_frg_fgn_cur)
                        exchangeParams.put("insurance", tvf?.tot_ins_fgn_cur)
                        exchangeParams.put("othercharge", tvf?.tot_otc_fgn_cur)
                        exchangeParams.put("caf", tvf?.tot_cif_fgn_cur)
                    }

                } else {
                    // get sad information
                    if (IS_WEBSERVICE == "Y") {
                        sad = clientWebService.retrieveSad(exchange)
                    } else {
                        sad = rimmLoadSadTvfService.retrieveOnesad(exchange.clearanceOfficeCode, exchange.declarationDate, exchange.declarationSerial, exchange?.declarationNumber)
                    }
                    if (sad) {
                        exchangeParams.put("devise", sad?.invoice_currency_code)
                        exchangeParams.put("incoterm", sad?.tpt_tod_cod)
                        exchangeParams.put("invoice_amount", sad?.vgs_inv_amt_fcx)
                        exchangeParams.put("fob", sad?.vgs_cif)
                        printService.setupVGS_AMT(sad, exchangeParams)
                    }
                    printService.setUpInvoice(exchange, exchangeParams)
                }


                LOGGER.debug("language:${RequestContextUtils.getLocale(request).language.toString().toUpperCase()}")
                if (RequestContextUtils.getLocale(request).language.toString().toUpperCase().equals('FR')) {
                    nameofprint = "BrouillonAutorisationDeChange"
                    exchangeParams.put("TITLE_DRAFT", resourceAsStream('/reports/images/logoBrouillon.jpg'))
                    exchangeParams.put("draft", resourceAsStream('/reports/images/brouillon.jpg'))
                } else if (RequestContextUtils.getLocale(request).language.toString().toUpperCase().equals('EN')) {
                    nameofprint = "ExchangeDraft"
                    exchangeParams.put("TITLE_DRAFT", resourceAsStream('/reports/images/logoBrouillon.jpg'))
                    exchangeParams.put("draft", resourceAsStream('/reports/images/brouillon.jpg'))
                } else {
                    nameofprint = "ExchangeDraft"
                    exchangeParams.put("TITLE_DRAFT", resourceAsStream('/reports/images/logoBrouillon.jpg'))
                    exchangeParams.put("draft", resourceAsStream('/reports/images/brouillon.jpg'))
                }
                printResultService.exportPrintResult(params._format, [exchange], exchangeParams, "exchangedraft.jasper", nameofprint, response)
            } else {
                redirect action: 'index'
            }
        }
        catch (Throwable t) {
            flash.ExceptionMessage = message(code: 'exeption.msg')
            LOGGER.error("Exception in printController t : ${t.getMessage()}")
            redirect(action: 'show', id: params?.id as Integer, controller: 'exchange')
        } catch (SQLRecoverableException sqlRecExcep) {
            flash.ExceptionMessage = message(code: 'exeption.msg')
            LOGGER.warn("Exception in printController sqlRecExcep : ${sqlRecExcep.getMessage()}")
            exchangeParams.put("exception", "true")
            redirect(action: 'show', id: params?.id as Integer, controller: 'exchange')
        }
        catch (SQLException sqlExcep) {
            flash.ExceptionMessage = message(code: 'exeption.msg')
            LOGGER.error("Exception in printController sqlExcep : ${sqlExcep.getMessage()}")
            redirect(action: 'show', id: params?.id as Integer, controller: 'exchange')
        }
        catch (IllegalArgumentException ex) {
            flash.ExceptionMessage = message(code: 'exeption.msg')
            LOGGER.error("Exception in printController ex : ${ex.getMessage()}")
            redirect(action: params.actionName, id: params?.id as Integer, controller: 'exchange')
        }

    }

    def printed() {
        LOGGER.warn("printed Method")
        flash.ExceptionMessage = null
        params._format = "PDF"
        def exchangeParams = [:]
        def nameofprint = 'print-Exchange'
        LOGGER.debug("Data params?.id = {}", params?.id)
        def exchange = Exchange.read(params?.id as Integer)
        def tvf = exchange?.tvf
        try {

            if (exchange) {
                // set common TVF OR SAD information
                if (exchange.basedOn == ExchangeRequestType.BASE_ON_TVF) {
                    if (tvf) {
                        printService.setupTVFParams(exchangeParams, tvf)
                    }
                }
                exchangeParams.put("EXECUTIONS_SUBREPORT", (JasperReport) JRLoader.loadObject(resourceAsStream('/reports/exchangeExecution.jasper')))
                LOGGER.debug("Data info execution = {}", exchange?.executions)
                exchangeParams.put("executions", exchange?.executions.sort { a, b -> a?.rank <=> b?.rank })
                LOGGER.debug("language:${RequestContextUtils.getLocale(request).language.toString().toUpperCase()}")
                if (RequestContextUtils.getLocale(request).language.toString().toUpperCase().equals('FR')) {
                    nameofprint = "AutorisationDeChange"
                    exchangeParams.put("TITLE", resourceAsStream('/reports/images/logoAC.jpg'))
                } else if (RequestContextUtils.getLocale(request).language.toString().toUpperCase().equals('EN')) {
                    nameofprint = "Exchange"
                    exchangeParams.put("TITLE", resourceAsStream('/reports/images/logoAC.jpg'))
                } else {
                    nameofprint = "Exchange"
                    exchangeParams.put("TITLE", resourceAsStream('/reports/images/logoAC.jpg'))
                }
                printResultService.exportPrintResult(params._format, [exchange], exchangeParams, "exchange.jasper", nameofprint, response)
            } else {
                redirect action: 'index'
            }
        }
        catch (Throwable t) {
            flash.ExceptionMessage = message(code: 'exeption.msg')
            LOGGER.error("Exception in printController t : ${t.getMessage()}")
            redirect(action: 'show', id: params?.id as Integer, controller: 'exchange')
        } catch (SQLRecoverableException sqlRecExcep) {
            flash.ExceptionMessage = message(code: 'exeption.msg')
            LOGGER.error("Exception in printController sqlRecExcep : ${sqlRecExcep.getMessage()}")
            redirect(action: 'show', id: params?.id as Integer, controller: 'exchange')
        }
        catch (SQLException sqlExcep) {
            flash.ExceptionMessage = message(code: 'exeption.msg')
            LOGGER.error("Exception in printController sqlExcep : ${sqlExcep.getMessage()}")
            redirect(action: 'show', id: params?.id as Integer, controller: 'exchange')
        }
        catch (IllegalArgumentException ex) {
            flash.ExceptionMessage = message(code: 'exeption.msg')
            LOGGER.error("Exception in printController ex : ${ex.getMessage()}")
            redirect(action: 'show', id: params?.id as Integer, controller: 'exchange')
        }

    }

    //---------------------------------    PRINT EC DOCUMENT --------------------------------

    def removeSpace(things) {
        LOGGER.debug("in removeSpace --  before transform :${things}")
        def thing = things?.toString().trim().replaceAll("\\s+", " ").replaceAll("\\u00A0", "").replaceAll("\\s+", "")?.toString()
        LOGGER.debug("in removeSpace -- first transform :${thing}")
        StringBuffer buf = new StringBuffer()
        char[] charArray = thing?.toString()?.toCharArray();
        for (char ouput in charArray) {
            if (ouput && !Character.isSpaceChar(ouput)) {
                buf.append(ouput)
            }
        }
        LOGGER.debug("in removeSpace -- final transform :${buf.toString()}")
        LOGGER.debug("in removeSpace -- final transform toUpperCase  :${buf.toString().toUpperCase()}")
        return buf.toString().toUpperCase()
    }


    def gwsPrintAeDoc() {
        params?.printType = ExchangeRequestType.AE
        redirect action: 'printEc', params: params
    }

    def gwsPrintECDoc() {
        params?.printType = ExchangeRequestType.EC
        redirect action: 'printEc', params: params
    }

    def gwsPrintDec() {
        redirect action: 'printEa', params: params
    }

    def gwsPrintDraftTec() {
        redirect action: 'draftEc', params: params
    }

    def exportResult(ExchangeSearchCommand searchCommand) {
        PrintUtils.setExportLimitDate(searchCommand)
        searchResultData = exchangeSearchService.getSearchResults(searchCommand, params)
        printResultService.exportPrintResult(params.exportFormat, searchResultData.resultList, params, params.name_jasper, params.print_name, response)
    }

    def exportRepatriationResult(RepatriationSearchCommand searchCommand) {
        PrintUtils.setExportLimitDate(searchCommand)
        searchResultData = repatriationSearchService.getSearchResults(searchCommand, params)
        printResultService.exportPrintResult(params.exportFormat, searchResultData.resultList, params, params.name_jasper, params.print_name, response)
    }

    def exportTransferOrderResult(TransferOrderSearchCommand searchCommand) {
        PrintUtils.setExportLimitDate(searchCommand)
        searchResultData = transferOrderSearchService.getSearchResults(searchCommand, params)
        printResultService.exportPrintResult(params.exportFormat, searchResultData.resultList, params, params.name_jasper, params.print_name, response)
    }

}
