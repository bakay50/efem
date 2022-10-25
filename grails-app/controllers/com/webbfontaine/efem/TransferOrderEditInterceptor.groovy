package com.webbfontaine.efem

import com.webbfontaine.efem.constants.UserProperties
import com.webbfontaine.efem.transferOrder.TransferOrder


class TransferOrderEditInterceptor {

    TransferOrderEditInterceptor() {
        match(controller: 'transferOrder', action: '(edit|show)')
    }
    boolean before() {
        TransferOrder transferInstance = TransferOrder.get(params?.id)
        if (transferInstance){
            if (UserUtils.isBankAgent() && !isConnectedBankOnDocument(transferInstance?.bankCode)){
                redirect(uri: '/access-denied')
                return false
            }

        }
        return true
    }

    boolean isConnectedBankOnDocument(String bankCode){
        return UserUtils.getUserProperty(UserProperties.ADB).contains(bankCode)
    }
}
