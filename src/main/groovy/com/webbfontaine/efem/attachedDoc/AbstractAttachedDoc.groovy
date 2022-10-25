package com.webbfontaine.efem.attachedDoc

import com.webbfontaine.efem.rules.attachedDoc.AttachmentBusinessLogicRule
import org.joda.time.LocalDate
/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA.
 * Date: /05/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
abstract class AbstractAttachedDoc implements Serializable {

    Integer rank
    String docType
    String docTypeName
    String docRef
    LocalDate docDate
    String fileExtension
    byte[] upl_fil
    AttachedFile attachedFile
    boolean isImport = false
    boolean isUpdated = false
    boolean isImport() {
        return isImport
    }

    void setIsImport(boolean isImport) {
        this.isImport = isImport
    }
  
    static transients = ['isImport']
    static rules = {[AttachmentBusinessLogicRule]}

    static constraints = {
        docType maxSize:5
        docTypeName maxSize:100
        docRef maxSize:35
    }
    static mapping = {
        upl_fil sqlType: 'blob'
        attachedFile column: 'ATT_FIL', cascade: "all-delete-orphan", lazy: false
        docType column: 'DOC_CODE'
        docTypeName column: 'DOC_TYP_NAME'
    }

}
