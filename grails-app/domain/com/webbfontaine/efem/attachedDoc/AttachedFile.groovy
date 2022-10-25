package com.webbfontaine.efem.attachedDoc

class AttachedFile {
    byte[] upl_fil
    static belongsTo = [attachedDoc: AttachedDoc,currencyTransferAttachedDoc:CurrencyTransferAttachedDoc,transferAttachedDoc:TransferAttachedDoc,repAttachedDoc:RepAttachedDoc]

    static constraints = {
        upl_fil nullable: true
        attachedDoc nullable: true
    }

    static mapping = {
        version false
        table 'ATTACHED_FILE'
        upl_fil column: 'DATA', sqlType: 'blob'
    }
}
