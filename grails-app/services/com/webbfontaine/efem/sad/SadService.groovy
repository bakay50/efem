package com.webbfontaine.efem.sad

import com.webbfontaine.efem.AppConfig
import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.constants.UtilConstants
import com.webbfontaine.efem.http.HttpClientFactory
import com.webbfontaine.efem.http.HttpClientUtil
import com.webbfontaine.efem.rimm.RimmOpt
import com.webbfontaine.sw.rimm.RimmLoadSadTvfService
import com.webbfontaine.sw.rimm.RimmSadAttdocs
import com.webbfontaine.sw.rimm.RimmSadDetails
import com.webbfontaine.sw.rimm.RimmSadGeneralSegement
import grails.gorm.transactions.Transactional
import grails.util.Holders
import groovy.util.logging.Slf4j
import org.apache.commons.lang.StringUtils
import org.apache.http.HttpStatus
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

import java.sql.SQLException
import java.sql.SQLRecoverableException

import static com.webbfontaine.efem.UserUtils.getUserProperty
import static com.webbfontaine.efem.UserUtils.userPropertyValueAsList
import static com.webbfontaine.efem.constants.TvfConstants.EX_FLOW
import static com.webbfontaine.efem.constants.UserProperties.*
import static com.webbfontaine.efem.constants.UtilConstants.SPECIAL_IMPORTER_CODE
import static com.webbfontaine.efem.constants.UtilConstants.XOF

@Transactional
@Slf4j(value = "LOGGER")
class SadService {

    RimmLoadSadTvfService rimmLoadSadTvfService

    def retrieveSad(Map params) {
        boolean isWebService = Holders.config.rest?.isWebService
        if (isWebService) {
            return retrieveFromUrl(params)
        } else {
            return retrieveFromRimmview(params)
        }
    }

    def retrieveExchangeFromSad(Map params) {
        Exchange exchangeInstance
        def sad = retrieveSad(params)
        exchangeInstance = getExchangeFromSad(sad, params?.requestType)
        return exchangeInstance
    }

    def retrieveFromRimmview(params) {
        RimmSadDetails sadDetails = rimmLoadSadTvfService.retrieveOnesad(params?.clearanceOfficeCode, LocalDate.parse(params?.declarationDate as String, DateTimeFormat.forPattern("dd/MM/yyyy")), StringUtils.upperCase(params?.declarationSerial as String), params?.declarationNumber)
        def result = [:]
        result.statusCode = sadDetails == null ? HttpStatus.SC_NOT_FOUND : HttpStatus.SC_OK
        result.data = sadDetails
        return formatRimmSadDetails(result)
    }

    def retrieveFromUrl(params) {
        String url = AppConfig.sadUrl

        def sessionId = Holders.config.sessionId
        def jossoSessionId = Holders.config.jossoSessionId
        def result
        HttpGet request

        try {

            CloseableHttpClient client = HttpClientFactory.createClientWithDefaultSSL()

            if (AppConfig.resolveRetrieveSadHasNoParamsInPayload()) {
                def convertedDate = params.declarationDate.replace("/", "-")
                request = new HttpGet("${url}" +
                        "/${params.clearanceOfficeCode}" +
                        "/${params.declarationSerial}" +
                        "/${params.declarationNumber}" +
                        "/${convertedDate}")

                request.addHeader("src", UtilConstants.GUCE)
            } else {
                request = new HttpGet("${url}" +
                        "/?officeOfDispatchExportCode=${params.clearanceOfficeCode}" +
                        "&customsRegistrationSerial=${params.declarationSerial}" +
                        "&customsRegistrationNumber=${params.declarationNumber}" +
                        "&customsRegistrationDate=${params.declarationDate}" +
                        "&eforex=true")
            }

            HttpClientUtil.setRequestHeaders(request, sessionId, jossoSessionId)
            CloseableHttpResponse response = client.execute(request)
            result = HttpClientUtil.getData(response, "json")
        } catch (IllegalArgumentException e) {
            LOGGER.error("Error Encountered during load SAD for ${params} ", e)
        }
        def sad = result?.data
        sad.statusCode = result?.statusCode
        LOGGER.debug("Web Service Response status: ${result?.statusCode}, response value: ${result?.data}")
        return result
    }

    Exchange getExchangeFromSad(def sad, String requestType) {

        Exchange exchangeInstance = new Exchange()
        try {
            def errorMap = SadUtils.checkSadResponse(sad, exchangeInstance)
            if (errorMap.isValidSad && checkSadOwner(exchangeInstance, sad?.data) && checkSadForEA(exchangeInstance, sad?.data, requestType)) {
                mappedSadToExchange(exchangeInstance, sad?.data?.declaration ?: sad?.data)
            } else {
                return exchangeInstance
            }
        } catch (IllegalArgumentException e) {
            LOGGER.error("Error Encountered during load SAD for ${sad} ", e)
            exchangeInstance.isDocumentValid = false
            exchangeInstance.errors.reject("load.sad.error", "The SAD doesn't exist.")
        }

        LOGGER.debug("Web Service Response status: ${sad?.statusCode}, response value: ${sad}")
        return exchangeInstance
    }

    def mappedSadToExchange(Exchange exchange, def sadData) {
        exchange.operType = AppConfig.sadOperationtypeCode
        exchange.operTypeName = getOperationTypeName(exchange.operType)
        exchange.basedOn = ExchangeRequestType.BASE_ON_SAD
        SadUtils.setValueToTargetObject(sadData, getSadFieldsMapping(), exchange)
        if (exchange.exporterNameAddress) {
            def namAdd = exchange.exporterNameAddress.split("\\n", 2)
            exchange.beneficiaryName = namAdd[0]
            exchange.beneficiaryAddress = namAdd.size() > 1 ? namAdd[1] : null
        }
    }

    def getOperationTypeName(operTypCod) {
        return RimmOpt.withNewSession {
            RimmOpt.findByCode(operTypCod)?.description ?: ""
        }
    }

    boolean checkSadOwner(Exchange exchange, def sadData) {
        def dec = userPropertyValueAsList(DEC) ?: getUserProperty(DEC)
        def tin = userPropertyValueAsList(TIN) ?: getUserProperty(TIN)

        boolean result = (sadData?.declarantCode in dec || dec == ALL) && (sadData?.consigneeCode in tin || tin == ALL)
        exchange.isSadOwner = true
        exchange.isDocumentValid = true
        exchange.defineSpecialImporter(sadData?.consigneeCode)
        if (!result) {
            if((sadData?.consigneeCode != SPECIAL_IMPORTER_CODE) || (sadData?.consigneeCode == SPECIAL_IMPORTER_CODE && sadData?.invoiceCurrencyCode == XOF)) {
                exchange.isSadOwner = false
                exchange.isDocumentValid = false
            }
            if(sadData?.consigneeCode != SPECIAL_IMPORTER_CODE){
                exchange.errors.reject("load.sad.ownership.error", "The declaration cannot be used by the connected user")
            } else if(sadData?.consigneeCode == SPECIAL_IMPORTER_CODE && sadData?.invoiceCurrencyCode == XOF) {
                exchange.errors.reject("load.sad.specialImporter.error", "Declarations and invoices denominated in XOF do not require Exchange authorizations (EA).")
            }else{
                result = true
            }
        }
        return result
    }

    boolean checkSadForEA(Exchange exchange, def sadData, String requestType) {
        String sadTypeOfDeclaration = sadData?.declaration ? sadData?.declaration?.typeOfDeclaration : sadData?.typeOfDeclaration
        boolean result = sadTypeOfDeclaration == EX_FLOW && requestType == ExchangeRequestType.EA
        if (result) {
            exchange.isDocumentValid = false
            exchange.errors.reject("load.sad.declaration.error", "Import exchange document cannot be linked to an export declaration")
        }
        return !result
    }

    private def getSadFieldsMapping() {
        Holders.config.sadToExchangeConfig.sad
    }

    def formatRimmSadDetails(def infos) {
        def result = [:]
        result.statusCode = infos.statusCode
        result.data = [:]
        result.data.id = infos.data?.id
        result.data.invoiceAmountInNationalCurrency = infos.data?.vgs_inv_amt_nmu
        result.data.countryOfExportName = infos.data?.countryOfExportName
        result.data.insuranceAmountInNationalCurrency = infos.data?.vgs_ins_amt_nmu
        result.data.totalAssessedAmount = infos.data?.fin_amt_dty
        result.data.declarantName = infos.data?.declarant_name
        result.data.bankName = infos.data?.domiciliation_Bank_Name
        result.data.invoiceCurrencyName = infos.data?.invoice_currency_name
        result.data.declarantReferenceYear = infos.data?.declaration_year
        result.data.totalAmountOfCostInsuranceFreight = infos.data?.vgs_cif
        result.data.consigneeName = infos.data?.importer_name
        result.data.officeOfDispatchExportName = infos.data?.office_name
        result.data.exporterCode = infos.data?.exporter_code
        result.data.externalFreightAmountInNationalCurrency = infos.data?.vgs_efr_amt_nmu
        result.data.declarantCode = infos.data?.declarant_code
        result.data.bankCode = infos.data?.domiciliation_Bank_Code
        result.data.officeOfDispatchExportCode = infos.data?.office_code
        result.data.typeOfDeclaration = infos.data?.declaration_type
        result.data.invoiceCurrencyCode = infos.data?.invoice_currency_code
        result.data.countryOfExportCode = infos.data?.countryOfExportCode
        result.data.invoiceAmountInForeignCurrency = infos.data?.vgs_inv_amt_fcx
        result.data.consigneeCode = infos.data?.declaration_type == EX_FLOW ? infos.data?.exporter_code : infos.data?.importer_code
        result.data.exporterName = infos.data?.exporter_name
        result.data.invoiceCurrencyExchangeRate = infos.data?.invoice_currency_rate
        result.data.status = infos.data?.status
        result.data.deliveryTermsCode = infos.data?.tpt_tod_cod
        result.data.declarationNo = infos.data?.declaration_No
        result.data.declarationSerial = infos.data?.declaration_serial
        result.data.declarationDate = infos.data?.declaration_date
        result.data.cifAmtFcx = infos?.data?.cif_amt_fcx
        return result
    }

    static Boolean checkPrintAE(Exchange exchangeInstance) {
        boolean result = false
        boolean infosSad = exchangeInstance?.clearanceOfficeCode && exchangeInstance?.declarationSerial && exchangeInstance?.declarationNumber && exchangeInstance?.declarationDate && exchangeInstance.sadInstanceId
        if (exchangeInstance?.status in [Statuses.ST_APPROVED, Statuses.ST_EXECUTED] && infosSad && exchangeInstance.requestType == ExchangeRequestType.EC) {
            try {
                if (exchangeInstance.isReleaseOrder) {
                    result = true
                } else {
                    ArrayList<HashMap> listSad = RetrieveSADInformation(exchangeInstance)

                    boolean resultOperations = verifyReleaseOrder(listSad)

                    boolean resultSadNotCancelled = verifyIfSADIsNotCancelled(listSad)

                    if (resultSadNotCancelled && resultOperations) {
                        exchangeInstance.isReleaseOrder = true
                        exchangeInstance.save(flush: true, validate: false, deepValidate: false)
                        result = true
                    }
                }

            } catch (Throwable throwable) {
                LOGGER.warn("Exception in checkSADFlow t : ${throwable.getMessage()}")
            } catch (SQLRecoverableException sqlRecExcep) {
                LOGGER.warn("Exception in checkSADFlow sqlRecExcep : ${sqlRecExcep.getMessage()}")
            } catch (SQLException sqlExcep) {
                LOGGER.warn("Exception in checkSADFlow sqlExcep : ${sqlExcep.getMessage()}")
            }
            catch (IllegalArgumentException ex) {
                LOGGER.warn("Exception in checkSADFlow ex : ${ex.getMessage()}")
            }
        }
        return result
    }

    static ArrayList RetrieveSADInformation(exchangeInstance) {
        RimmSadGeneralSegement.withNewSession {
            def query = "select new map(bol.instanceid as id ,track.op_name as op_name,track.status as status)" +
                    " from RimmAsyBrkIed ied,RimmSadGeneralSegement bol ,RimmAsyBrkTrack track where ied.ied_id = track.ied_id and ied.instance_id = bol.instanceid and " +
                    "track.c_binder_id = 'un.asybrk B_asysad' and bol.del_dat = null and track.status != 'Stored' and bol.office_code =:cleranceOfficeCode  " +
                    "and bol.declaration_serial =:declarationSerial and bol.declaration_No =:declarationNumber and bol.declaration_date =:declaration_date"
            return RimmSadGeneralSegement.executeQuery(query, [cleranceOfficeCode: exchangeInstance.clearanceOfficeCode, declarationSerial: StringUtils.upperCase(exchangeInstance.declarationSerial), declarationNumber: exchangeInstance.declarationNumber, declaration_date: exchangeInstance.declarationDate])
        }
    }

    static boolean verifyReleaseOrder(listSad) {
        ArrayList<String> listOperName = new ArrayList<String>()
        checkOutOperation(listSad, listOperName)
        return listOperName.intersect([UtilConstants.PRINT_RELEASE_ORDER, UtilConstants.PRINT_RELEASE_ORDER_SEL])
    }

    private static Object checkOutOperation(listSad, listOperName) {
        listSad?.each() {
            listOperName.add(it?.op_name)
        }
    }

    static def verifyIfSADIsNotCancelled(listSad) {
        def sad = listSad.get(listSad.size() - 1)
        return sad.status != Statuses.ST_CANCELLED
    }

    def sadAttachedDocument(Exchange exchange) {
        return RimmSadAttdocs.withNewSession {
            RimmSadAttdocs.createCriteria().list {
                projections {
                    property("attached_Document_reference")
                }
                and {
                    eq("office_code", exchange?.clearanceOfficeCode)
                    eq("declaration_date", exchange?.declarationDate)
                    eq("declaration_serial", StringUtils.upperCase(exchange?.declarationSerial))
                    eq("declaration_No", exchange?.declarationNumber)
                    eq("attached_Document_code", "2052")
                }
            }
        }
    }
}
