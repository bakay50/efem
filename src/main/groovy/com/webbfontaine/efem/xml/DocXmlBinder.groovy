package com.webbfontaine.efem.xml

import com.webbfontaine.efem.AppConfig
import com.webbfontaine.efem.TypeCastUtils
import com.webbfontaine.efem.constants.UtilConstants
import com.webbfontaine.efem.xml.exchange.BinderExchange
import com.webbfontaine.efem.xml.repatriation.BinderRepatriation

import static com.webbfontaine.efem.constants.UtilConstants.*
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.efem.tvf.Tvf
import com.webbfontaine.efem.tvf.TvfAttachment
import com.webbfontaine.wfutils.AppContextUtils
import groovy.util.logging.Slf4j
import groovy.util.slurpersupport.GPathResult
import org.apache.commons.codec.binary.Base64
import org.joda.time.LocalDate
import com.webbfontaine.grails.plugins.taglibs.BeanDataLoadService

@Slf4j
class DocXmlBinder {

    public static Tvf bindXmlToTvf(GPathResult importXml) throws RuntimeException {
        Tvf tvfInstance = new Tvf()

        if (importXml.size() != 0) {
            BeanDataLoadService beanDataLoadService = AppContextUtils.getBean(BeanDataLoadService)
            tvfInstance.instanceId = importXml.instanceId as String
            tvfInstance.trDate = gregorianToLocalDate(importXml.idfDate as String)
            tvfInstance.cuoCode = importXml.cuoCode as String
            tvfInstance.expTaxPayerAcc = importXml.expTin as String
            tvfInstance.expAddress = importXml.expAdr as String
            tvfInstance.expCtyCode = importXml.expCty as String
            tvfInstance.expCtyName = importXml.expCtyName as String
            tvfInstance.expEmail = importXml.expEmail as String
            tvfInstance.expPhone = importXml.expPhone as String
            tvfInstance.expName = importXml.expNam as String
            tvfInstance.decCode = importXml.appTin as String
            tvfInstance.decName = importXml.appNam as String
            tvfInstance.decPhone = importXml.appTel as String
            tvfInstance.decAddress = importXml.appAdr as String
            tvfInstance.impTaxPayerAcc = importXml.impTin as String
            tvfInstance.impName = importXml.impNam as String
            tvfInstance.impAddress = importXml.impAdr as String
            tvfInstance.impCtyCode = importXml.impCty as String
            tvfInstance.impCtyName = importXml.impCtyName as String
            tvfInstance.impEmail = importXml.impEmail as String
            tvfInstance.impPhone = importXml.impPhone as String
            tvfInstance.status = importXml.status as String
            tvfInstance.flow = importXml.flow as String
            tvfInstance.bankCode = importXml.bankCode as String
            tvfInstance.domiciliationRef = importXml.domiciliationRef as String
            tvfInstance.domiciliationDate = gregorianToLocalDate(importXml.domiciliationDate as String)
            tvfInstance.expirationDate = gregorianToLocalDate(importXml.expirationDate as String)
            tvfInstance.contactName = importXml.contactName as String
            tvfInstance.contactEmail = importXml.contactEmail as String
            tvfInstance.contactTel = importXml.contactTel as String
            def result = beanDataLoadService.loadFullBeanData('HT_BNK', ['code': tvfInstance?.bankCode], 'get')
            tvfInstance.bankName = result?.description
            //from ttInvs node
            tvfInstance.totFreightInFgn = TypeCastUtils.toBigDecimal(importXml.ttInvs.freightD as String)
            tvfInstance.totInsInFgn = TypeCastUtils.toBigDecimal(importXml.ttInvs.insuranceD as String)
            tvfInstance.totOtherInFgn = TypeCastUtils.toBigDecimal(importXml.ttInvs.otherD as String)
            tvfInstance.totCifInFgn = TypeCastUtils.toBigDecimal(importXml.ttInvs.totCIFValD as String)
            tvfInstance.totFobValInFgn = TypeCastUtils.toBigDecimal(importXml.ttInvs.totFOBValD as String)
            tvfInstance.totInvoiceAmount = TypeCastUtils.toBigDecimal(importXml.ttInvs.totInvValD as String)
            tvfInstance.totInvoiceAmountNat = TypeCastUtils.toBigDecimal(importXml.ttInvs.totInvValNat as String)
            tvfInstance.incCode = importXml.ttInvs.incoterm as String
            tvfInstance.invCurCode = importXml.ttInvs.invCur as String
            tvfInstance.invCurRat = new BigDecimal(importXml.ttInvs.invCurRat as String)
            tvfInstance.invCurPayRat = new BigDecimal(importXml.ttInvs.invCurRat as String)
            tvfInstance.invReference = importXml.ttInvs.DInvNum as String
            tvfInstance.invDate = gregorianToLocalDate(importXml.ttInvs.invDat as String)

            if (AppConfig.resolveRetrievingAttachmentAvailable()) {
                def attachments = importXml.ttDocs.collect { attachment ->

                    TvfAttachment tvfAttachment = new TvfAttachment(
                            docCode: attachment.docCode as String,
                            value: attachment.type as String,
                            docRef: attachment.name as String,
                            docDate: gregorianToLocalDate(attachment.docDate as String),
                            fileExtension: attachment.contentType as String,
                            attDoc: Base64.decodeBase64(attachment.bytes as String)

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
        }

        tvfInstance
    }

    private static LocalDate gregorianToLocalDate(String value) {
        if (!value.equals("")) {
            String date = value?.substring(0, value.indexOf("T"))
            if (date) {
                try {
                    return new LocalDate(date)
                } catch (IllegalArgumentException exp) {
                    return null
                }
            }
        }
        return null
    }

    public static Writable bindDomainToXml(domainInstance, domainName) {
        def xml
        if (domainName == REPATRIATION.toUpperCase()) {
            xml = BinderRepatriation.bindRepatriationToXml(domainInstance)
        } else if (domainName == EXCHANGE.toUpperCase()) {
            xml = BinderExchange.bindExchangeToXml(domainInstance)
        }
        return xml
    }

    static def bindXmlToDomain(importXmlFile, String domainName) {
        def domainInstance
        if (domainName.toUpperCase() == UtilConstants.EXCHANGE.toUpperCase()) {
            domainInstance = BinderExchange.bindXmlToExchange(importXmlFile)
        } else if (domainName.toUpperCase() == UtilConstants.REPATRIATION.toUpperCase()) {
            domainInstance = BinderRepatriation.bindXmlToRepatriation(importXmlFile)
        }
        domainInstance
    }

    public static String formatValue(def targetObject, String field) {
        if (targetObject?."${field}" instanceof LocalDate) {
            TypeCastUtils.fromDate(targetObject?."${field}")
        } else if (field == "attachedFile") {
            Base64.encodeBase64String(targetObject?."${field}"?.upl_fil)
        } else if (field in DataBindingHelper.DECIMAL_FIELDS_TO_REFORMAT) {
            def ret = targetObject?."${field}"
            if (ret instanceof BigDecimal) {
                ret = ret.setScale(0, BigDecimal.ROUND_HALF_UP);
            }
            ret
        } else {
            targetObject?."${field}"
        }
    }

    public static void buildAttachments(builder, def domainInstance) {
        if (domainInstance?.attachedDocs?.size() > 0) {
            builder.attachedDocs {
                domainInstance?.attachedDocs?.each { attachment ->
                    builder.attachment {
                        DataBindingHelper.ATTACHMENT_ACCEPTABLE_FIELDS_FOR_EXPORT.each { field ->
                            "${field}"(formatValue(attachment, field))
                        }
                    }
                }
            }
        }
    }

    static def getXmlString(domain, importXml, def field) {
        def fieldValue = importXml."${field}" as String
        def curMaxSize = getMaxSize(domain, field)
        if (fieldValue && curMaxSize) {
            fieldValue = (fieldValue.length() > curMaxSize) ? fieldValue.substring(0, curMaxSize) : fieldValue
        }
        fieldValue
    }

    static def getMaxSize(domain, def field) {
        if (domain instanceof Repatriation) {
            Repatriation?.constrainedProperties?."${field}"?.maxSize
        }
    }

}
