package com.webbfontaine.efem.xml.repatriation

import com.webbfontaine.efem.TypeCastUtils
import com.webbfontaine.efem.attachedDoc.AttachedFile
import com.webbfontaine.efem.attachedDoc.RepAttachedDoc
import com.webbfontaine.efem.repatriation.ClearanceOfDom
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.efem.xml.DataBindingHelper
import com.webbfontaine.efem.xml.DocXmlBinder
import groovy.util.slurpersupport.GPathResult
import groovy.xml.StreamingMarkupBuilder
import org.apache.commons.codec.binary.Base64
import static com.webbfontaine.efem.xml.DocXmlBinder.getXmlString;

class BinderRepatriation {
    public static Repatriation bindXmlToRepatriation(GPathResult importXml) throws RuntimeException {
        Repatriation repatriation = new Repatriation()
        if (importXml.size() != 0) {
            repatriation.natureOfFund = getXmlString(repatriation, importXml, "natureOfFund")
            repatriation.code = getXmlString(repatriation, importXml, "code")
            repatriation.nameAndAddress = getXmlString(repatriation, importXml, "nameAndAddress")
            repatriation.repatriationBankCode = getXmlString(repatriation, importXml, "repatriationBankCode")
            repatriation.repatriationBankName = getXmlString(repatriation, importXml, "repatriationBankName")
            repatriation.currencyCode = getXmlString(repatriation, importXml, "currencyCode")
            repatriation.currencyRate = TypeCastUtils.toBigDecimal(importXml.currencyRate)
            repatriation.currencyName = getXmlString(repatriation, importXml, "currencyName")
            repatriation.receivedAmount = TypeCastUtils.toBigDecimal(importXml.receivedAmount)
            repatriation.receivedAmountNat = TypeCastUtils.toBigDecimal(importXml.receivedAmountNat)
            repatriation.receptionDate = TypeCastUtils.toLocalDate(importXml.receptionDate)
            repatriation.countryOfOriginCode = getXmlString(repatriation, importXml, "countryOfOriginCode")
            repatriation.countryOfOriginName = getXmlString(repatriation, importXml, "countryOfOriginName")
            repatriation.bankOfOriginCode = getXmlString(repatriation, importXml, "bankOfOriginCode")
            repatriation.bankOfOriginName = getXmlString(repatriation, importXml, "bankOfOriginName")
            repatriation.bankNotificationDate = TypeCastUtils.toLocalDate(importXml.bankNotificationDate)
            repatriation.executionRef = getXmlString(repatriation, importXml, "executionRef")
            repatriation.executionDate = TypeCastUtils.toLocalDate(importXml.executionDate)
            repatriation.currencyTransfertDate = TypeCastUtils.toLocalDate(importXml.currencyTransfertDate)
            repatriation.amountTransferred = TypeCastUtils.toBigDecimal(importXml.amountTransferred)
            repatriation.amountRemaining = TypeCastUtils.toBigDecimal(importXml.amountRemaining)
            repatriation.amountTransferredNat = TypeCastUtils.toBigDecimal(importXml.amountTransferredNat)
            repatriation.amountRemainingNat = TypeCastUtils.toBigDecimal(importXml.amountRemainingNat)
            repatriation.status = getXmlString(repatriation, importXml, "status")
            // Clearances ----------------------------------------------
            int clearancesRank = 1
            def clearancesOfDom = importXml.clearances.itemClearances.collect { item ->
                ClearanceOfDom itemClearances = new ClearanceOfDom(
                        rank: TypeCastUtils.toInteger(clearancesRank),
                        ecReference: getXmlString(ClearanceOfDom, item, "ecReference"),
                        ecDate: getXmlString(ClearanceOfDom, item, "ecDate"),
                        domiciliaryBank: getXmlString(ClearanceOfDom, item, "domiciliaryBank"),
                        domiciliationNo: getXmlString(ClearanceOfDom, item, "domiciliationNo"),
                        domiciliationDate: TypeCastUtils.toLocalDate(item.domiciliationDate),
                        dateOfBoarding: TypeCastUtils.toLocalDate(item.dateOfBoarding),
                        domAmtInCurr: TypeCastUtils.toBigDecimal(item.domAmtInCurr),
                        invFinalAmtInCurr: TypeCastUtils.toBigDecimal(item.invFinalAmtInCurr),
                        repatriatedAmtInCurr: TypeCastUtils.toBigDecimal(item.repatriatedAmtInCurr),
                )
                clearancesRank++
                itemClearances
            }
            clearancesOfDom.eachWithIndex { item, index ->
                repatriation?.addToClearances(item);
            }
            // Attached docs ----------------------------------------------
            int attRank = 1;
            def attDocs = importXml.attachedDocs.attachment.collect { attachment ->
                AttachedFile file = new AttachedFile(
                        data: Base64.decodeBase64(attachment.attachedFile.toString())
                )
                RepAttachedDoc attDoc = new RepAttachedDoc(
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
                repatriation.addToAttachedDocs(attachedDoc);
            }
        }
        return repatriation
    }

    public static Writable bindRepatriationToXml(domainInstance) {
        def xml = new StreamingMarkupBuilder().bind { builder ->
            repatriation {
                buildGeneralSegmentRepatriation builder, domainInstance
                buildItemClearances builder, domainInstance
                DocXmlBinder.buildAttachments builder, domainInstance
            }
        }
        xml
    }

    private static void buildGeneralSegmentRepatriation(builder, def domainInstance) {
        DataBindingHelper.REPATRIATION_ACCEPTABLE_FIELDS_FOR_EXPORT.each { field ->
            builder."${field}"(DocXmlBinder.formatValue(domainInstance, field))
        }
    }

    private static void buildItemClearances(builder, def domainInstance) {
        if (domainInstance instanceof Repatriation) {
            if (domainInstance?.clearances?.findAll{it.status == true}?.size() > 0) {
                builder.clearances {
                    domainInstance?.clearances?.each { itemClearances ->
                        builder.itemClearances {
                            DataBindingHelper.ITEM_CLEARANCES_OF_DOM_ACCEPTABLE_FIELDS_FOR_EXPORT.each { field ->
                                "${field}"(DocXmlBinder.formatValue(itemClearances, field))
                            }
                        }
                    }
                }
            }
        }
    }
}
