package com.webbfontaine.efem.attachedDoc

import com.webbfontaine.efem.transferOrder.TransferOrder

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 11/07/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class TransferAttachedDoc extends AbstractAttachedDoc {
    static belongsTo = [transfert: TransferOrder]
    static mapping = {
        table 'ATTACH_TRANSFER'
        transfert column: 'TRANSFER_ID'
    }
}
