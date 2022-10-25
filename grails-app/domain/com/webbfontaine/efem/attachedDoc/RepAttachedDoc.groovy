package com.webbfontaine.efem.attachedDoc
import com.webbfontaine.efem.repatriation.Repatriation
/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA.
 * Date: /05/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class RepAttachedDoc extends AbstractAttachedDoc{
    static belongsTo = [repats:Repatriation]
    static mapping = {
        table 'ATTACH_REP'
        repats column: 'REPATS_ID'
    }
}
