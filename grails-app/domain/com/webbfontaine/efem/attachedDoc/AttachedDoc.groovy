package com.webbfontaine.efem.attachedDoc
import com.webbfontaine.efem.Exchange
/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA.
 * Date: /05/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class AttachedDoc extends AbstractAttachedDoc{

    static belongsTo = [exchange:Exchange]
    static mapping = {
        table 'ATTACHED_DOC'

    }

}
