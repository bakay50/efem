package com.webbfontaine.efem.tvf

import com.webbfontaine.efem.AppConfig
import com.webbfontaine.efem.TypeCastUtils
import com.webbfontaine.efem.attachedDoc.AttachedDoc
import com.webbfontaine.efem.attachedDoc.AttachedFile
import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.TvfConstants
import com.webbfontaine.efem.http.HttpClientFactory
import com.webbfontaine.efem.http.HttpClientUtil
import com.webbfontaine.efem.rules.exchange.TvfRule
import com.webbfontaine.efem.xml.DocXmlBinder
import com.webbfontaine.grails.plugins.taglibs.FormattingUtils
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import com.webbfontaine.sw.rimm.RimmLoadSadTvfService
import grails.gorm.transactions.Transactional
import grails.util.Holders
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import groovy.util.slurpersupport.GPathResult
import org.apache.commons.codec.binary.Base64
import org.apache.http.HttpStatus
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import static com.webbfontaine.efem.constants.TvfConstants.IM_FLOW

@Transactional
@Slf4j('log')
class TvfService {

    def grailsApplication
    RimmLoadSadTvfService rimmLoadSadTvfService
    def exchangeService

    def loadTvf(String tvfNumber, String tvfDate, boolean validationEnabled = true) {
        boolean isWebService = Holders.config.rest?.isWebService
        if (isWebService) {
            return loadExchangeFromTvfByUrl(tvfNumber, tvfDate, validationEnabled)
        } else {
            return loadExchangeFromTvfByView(tvfNumber, tvfDate)
        }
    }

    Tvf retrieveTvf(def tvfNumber, def tvfDate, Character loadAttachDocs = 'N'){
        def slurper = new JsonSlurper()
        def tvfDetails = rimmLoadSadTvfService.RetrieveInfoTvf(tvfNumber, LocalDate.parse(tvfDate as String, DateTimeFormat.forPattern("dd/MM/yyyy")), loadAttachDocs)
        String tvfString = tvfDetails?.toString()
        def response = slurper.parseText(tvfString)
        if (response?.message != null) {
            return setTvfError(response?.message)
        } else {
            return bindViewResultToTvf(response?.data_segment_general)
        }
    }

    def loadExchangeFromTvfByView(def tvfNumber, def tvfDate) {
        Exchange exchangeInstance = new Exchange()
        try {
            Tvf tvfInstance = retrieveTvf(tvfNumber, tvfDate)
            if(tvfInstance?.hasErrors()){
                exchangeInstance.errors.rejectValue('tvfNumber', tvfInstance?.errors?.allErrors?.first()?.code, tvfInstance?.errors?.allErrors?.first()?.defaultMessage)
            }else{
                setExchangeWithTvfInstance(exchangeInstance, tvfInstance, tvfNumber, tvfDate)
            }
        } catch (IllegalArgumentException e) {
            log.error("Error encountered during load TVF from view.", e)
        }
        return exchangeInstance
    }

    def setTvfError(String error) {
        Tvf tvfInstance = new Tvf()
        switch (error) {
            case "Erreur_tvf_status":
                tvfInstance.errors.reject('exchange.errors.tvf_status', 'The TVF doesn\'t have a valid status')
                break
            case "Erreur_tvf_existe":
                tvfInstance.errors.reject('exchange.errors.tvf_existe', 'The TVF doesn\'t exist')
                break
            case "Erreur_tvf_date_expiration_null":
                tvfInstance.errors.reject('exchange.errors.tvf.expiration_date_null', 'Expiration Date is null')
                break
            case "Erreur_tvf_expiration":
                tvfInstance.errors.reject( 'exchange.errors.tvf.expire_date', 'The TVF has expired')
                break
            case "Erreur_Serveur":
                tvfInstance.errors.reject( 'exchange.errors.serveur', 'Internal server error. Please ask support team')
                break
            case "Erreur_tvf_flows":
                tvfInstance.errors.reject( 'exchange.errors.tvf_flow', 'Export exchange document cannot be linked to an import TVF.')
                break
            default:
                tvfInstance.errors.reject('exchange.errors.serveur', 'Internal server error. Please ask support team')
                break
        }
        return tvfInstance
    }

    def loadExchangeFromTvfByUrl(String tvfNumber, String tvfDate, boolean validationEnabled = true) {
        Exchange exchangeInstance = new Exchange()

        try {
            String tvf = getTvfResponse(exchangeInstance, tvfNumber, tvfDate, validationEnabled);
            def errorMap = TvfUtils.checkTvfResponse(tvf, exchangeInstance)

            if (!errorMap.isValidTvf) {
                return exchangeInstance
            }

            GPathResult tvfData = TvfUtils.parseXml(tvf)
            Tvf tvfInstance = DocXmlBinder.bindXmlToTvf(tvfData)

            if (Objects.equals(tvfInstance.status, TvfConstants.ST_VALIDATED) || !validationEnabled) {
                setExchangeWithTvfInstance(exchangeInstance, tvfInstance, tvfNumber, tvfDate)
            } else {
                log.warn("Loaded TVF is not yet validated for Number: ${tvfNumber} and Date ${tvfDate}")
                exchangeInstance.errors.reject("exchange.loadTvf.status.invalid.error")
            }
        } catch (IllegalArgumentException e) {
            exchangeInstance.isDocumentValid = false
            log.error("Error encountered during load TVF.", e)
        }

        exchangeInstance
    }

    def setExchangeWithTvfInstance(Exchange exchangeInstance, Tvf tvfInstance,  tvfNumber,  tvfDate){
        exchangeInstance.tvf = tvfInstance
        exchangeInstance.tvfNumber = Integer.parseInt(tvfNumber)
        exchangeInstance.tvfDate = TvfUtils.gPathToLocalDate(tvfDate)
        if (isTvfDataValid(exchangeInstance)) {
            tvfInstance.trNumber == Integer.parseInt(tvfNumber)
            TvfUtils.setValueToTargetObject(tvfInstance, getExchangeFieldsMapping(), exchangeInstance)
        }
    }

    String getTvfResponse(exchangeInstance, tvfNumber, tvfDate, boolean validationEnabled = true) {
        String url = Holders.config.rest.tvf.url
        String noValidationFlag = "${TvfConstants.NO_TVF_WS_VALIDATION}=${!validationEnabled}"
        HttpGet request
        try {
            CloseableHttpClient client = HttpClientFactory.createClientWithBasicAuth()
            if (AppConfig.resolveRetrieveSadHasNoParamsInPayload()) {
                request = new HttpGet("${url}" +
                        "/${TvfUtils.formatDate(tvfDate)}/${tvfNumber}")
            } else {
                request = new HttpGet("${url}" +
                        "/${TvfUtils.formatDate(tvfDate)}/${tvfNumber}?${noValidationFlag}")
            }
            CloseableHttpResponse response = client.execute(request);

            def result = HttpClientUtil.getData(response, "xml")

            if (checkTVFAvailability(result.statusCode)) {
                exchangeInstance.isDocumentValid = false
                exchangeInstance.errors.reject("load.tvf.error", "The TVF doesn't exist")
            } else {
                return result.data
            }

        } catch (IllegalArgumentException e) {
            log.error("Error Encountered during load TVF for TVF Number ${tvfNumber} and TVF Date ${tvfDate}", e)
            exchangeInstance.errors.reject("load.tvf.error", "The TVF doesn't exist")
        }
    }

    private boolean checkTVFAvailability(statusCode) {
        if (statusCode in [HttpStatus.SC_INTERNAL_SERVER_ERROR, HttpStatus.SC_NOT_FOUND]) {
            return true
        }
        return false
    }

    private boolean isTvfDataValid(Exchange exchangeInstance) {
        exchangeInstance?.clearErrors()
        grailsApplication.mainContext.getBean(TvfRule)?.apply(new RuleContext(exchangeInstance, exchangeInstance.errors))
        return !exchangeInstance?.hasErrors()
    }

    def mapTvfAttachmentToLicense(Tvf tvfInstance, Exchange exchangeInstance) {
        tvfInstance?.attachments?.each {
            AttachedDoc attachedDoc = new AttachedDoc()
            AttachedFile attachedFile = new AttachedFile(attachedDoc: attachedDoc)
            TvfUtils.setValueToTargetObject(it, getAttachmentFieldsMapping(), attachedDoc)
            TvfUtils.setValueToTargetObject(it, getAttachedFileFieldsMapping(), attachedFile)
            attachedDoc.setAttachedFile(attachedFile)
            exchangeInstance.addAttDoc(attachedDoc)
        }
    }

    private def getExchangeFieldsMapping() {
        Holders.config.tvfToExchangeConfig.tvf
    }

    private def getAttachmentFieldsMapping() {
        Holders.config.tvfToExchangeConfig.tvfAttachment
    }

    private static def getAttachedFileFieldsMapping() {
        Holders.config.tvfToExchangeConfig.tvfAttachedFile
    }

    def getExchangeTvfFromTvfService(Exchange exchangeInstance) {
        Exchange exchange = loadTvf(exchangeInstance?.tvfNumber?.toString(), FormattingUtils.fromDate(exchangeInstance?.tvfDate), false)
        exchange?.tvf
    }

    Tvf bindViewResultToTvf(def tvfDetails) throws RuntimeException {
        Tvf tvfInstance = new Tvf()
        tvfInstance.instanceId = tvfDetails?.id
        tvfInstance.trDate = LocalDate.parse(tvfDetails?.transaction_date as String, DateTimeFormat.forPattern("yyyy-MM-dd"))
        tvfInstance.cuoCode = tvfDetails?.cuo_code
        tvfInstance.expTaxPayerAcc = tvfDetails?.exp_taxpayer_account
        tvfInstance.expAddress = tvfDetails?.exporter_address
        tvfInstance.expCtyCode = tvfDetails?.exporter_country_code
        tvfInstance.expCtyName = tvfDetails?.exporter_country_name
        tvfInstance.expEmail = tvfDetails?.exporter_email
        tvfInstance.expPhone = tvfDetails?.exporter_phone
        tvfInstance.expName = tvfDetails?.exporter_name?.replace("\n" + tvfDetails?.exporter_address, "")
        tvfInstance.decCode = tvfDetails?.declarant_code
        tvfInstance.impTaxPayerAcc = tvfDetails?.imp_taxpayer_account
        tvfInstance.impName = tvfDetails?.importer_name?.replace("\n" + tvfDetails?.importer_address, "")
        tvfInstance.impAddress = tvfDetails?.importer_address
        tvfInstance.impEmail = tvfDetails?.importer_email
        tvfInstance.impPhone = tvfDetails?.importer_phone
        tvfInstance.status = tvfDetails?.transaction_status
        tvfInstance.flow = tvfDetails?.flow?:IM_FLOW
        tvfInstance.bankCode = tvfDetails?.bank_code
        tvfInstance.bankName = tvfDetails?.bank_name
        tvfInstance.domiciliationRef = tvfDetails?.domiciliation_ref
        tvfInstance.domiciliationDate = tvfDetails.domiciliation_date ? LocalDate.parse(tvfDetails?.domiciliation_date as String, DateTimeFormat.forPattern("yyyy-MM-dd")) : null
        tvfInstance.totFreightInFgn = TypeCastUtils.toBigDecimal(tvfDetails?.tot_frg_fgn_cur)
        tvfInstance.totInsInFgn = TypeCastUtils.toBigDecimal(tvfDetails?.tot_ins_fgn_cur)
        tvfInstance.totOtherInFgn = TypeCastUtils.toBigDecimal(tvfDetails?.tot_otc_fgn_cur)
        tvfInstance.totCifInFgn = TypeCastUtils.toBigDecimal(tvfDetails?.tot_cif_fgn_cur)
        tvfInstance.totFobValInFgn = TypeCastUtils.toBigDecimal(tvfDetails?.tot_fob_fgn_cur)
        tvfInstance.totInvoiceAmount = TypeCastUtils.toBigDecimal(tvfDetails?.tot_ivn_amt)
        tvfInstance.totInvoiceAmountNat = TypeCastUtils.toBigDecimal(tvfDetails?.tot_ivn_nat_amt)
        tvfInstance.incCode = tvfDetails?.incoterm_code
        tvfInstance.invCurCode = tvfDetails?.invoice_cur_code
        tvfInstance.invCurRat = exchangeService.getCurrencyRate(tvfDetails?.invoice_cur_code)
        tvfInstance.invCurPayRat = exchangeService.getCurrencyRate(tvfDetails?.invoice_cur_code)
        tvfInstance.invReference = tvfDetails?.invoice_reference
        tvfInstance.invDate = tvfDetails.invoice_date ? LocalDate.parse(tvfDetails?.invoice_date as String, DateTimeFormat.forPattern("yyyy-MM-dd")) : null
        tvfInstance.countryOfExportCode = tvfDetails?.countryOfExportCode
        tvfInstance.countryOfExportName = tvfDetails?.countryOfExportName
        if (AppConfig.resolveRetrievingAttachmentAvailable()) {
            def attachments = tvfDetails?.data_attachdocs?.collect { attachment ->
                TvfAttachment tvfAttachment = new TvfAttachment(
                        docCode: attachment.docCode,
                        value: attachment.type,
                        docRef: attachment.name,
                        docDate: LocalDate.parse(attachment.docDate as String, DateTimeFormat.forPattern("dd/MM/yyyy")),
                        fileExtension: attachment.contentType,
                        attDoc: Base64.decodeBase64(attachment.bytes)
                )
                tvfAttachment
            }
            if (attachments.size() > 0) {
                tvfInstance.attachments = new ArrayList<TvfAttachment>()
                attachments?.each { attachment ->
                    tvfInstance.attachments.add(attachment)
                }
            }
        }
        tvfInstance
    }

}
