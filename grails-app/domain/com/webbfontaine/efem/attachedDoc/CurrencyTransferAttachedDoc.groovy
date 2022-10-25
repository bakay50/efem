package com.webbfontaine.efem.attachedDoc

import com.webbfontaine.efem.currencyTransfer.CurrencyTransfer

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Fady Diarra.
 * Date: 15/07/2020
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
class CurrencyTransferAttachedDoc extends AbstractAttachedDoc {
    static belongsTo = [currencyTransfer: CurrencyTransfer]
    static mapping = {
        table 'ATTACH_CURRENCY_TRANSFER'
        currencyTransfer column: 'CURRENCY_TRANSFER_ID'
    }
}
