package com.webbfontaine.efem

import com.webbfontaine.efem.currencyTransfer.CurrencyTransfer

class CurrencyTansferBusinessLogicService {

    def initDocumentForCreate(CurrencyTransfer currencyTransfer) {
        currencyTransfer.isDocumentEditable = true
        currencyTransfer?.isAttachmentEditable = true
        currencyTransfer
    }
}
