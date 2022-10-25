import com.webbfontaine.efem.BankFlag
import com.webbfontaine.efem.rimm.RimmGeoArea
import com.webbfontaine.efem.rimm.RimmOpt

com {
    webbfontaine {
        grails {
            plugins {
                security {
                    active = true
                    auth = true
                    config = "Requestmap"
                    roleName = "EFEMCI"
                    users {
                        admin {
                            roles = ['efemci_admin']
                            properties = [TIN: 'ALL',DEC: 'ALL',ADB: 'ALL',REQ:'ALL']
                        }
                        declarant {
                            roles = ['efem_declarant']
                            properties = [TIN: 'ALL', DEC:'ALL', ADB:'ALL',REQ:'ALL']
                        }
                        trader {
                            roles = ['efemci_trader']
                            properties = [TIN: 'ALL', DEC:'ALL']
                        }
                        bank {
                            roles = ['efemci_bank_agent']
                            properties = [ADB: 'AIB']
                        }
                        gov {
                            roles = ['efemci_govt_officer']
                            properties = [GOV: 'ALL', REQ: 'EC:EA', LVL:1]
                        }
                        gov2 {
                            roles = ['efemci_govt_officer']
                            properties = [GOV: 'ALL', REQ: 'EC:EA', LVL:2]
                        }
                        govsupervisor {
                            roles = ['efemci_govt_supervisor']
                            properties = [TIN: 'ALL',DEC: 'ALL',ADB: 'ALL',REQ:'ALL']
                        }
                        superadministrator {
                            roles = ['efemci_super_administrator']
                            properties = [GOV: 'ALL', REQ: 'EC:EA', LVL:4]
                        }
                    }
                }

                rimm {
                    alias {
                        key = 'code'
                        value = 'description'
                        REF_GEOARE {
                            bean = RimmGeoArea.class.name
                        }
                        REF_OPT {
                            bean = RimmOpt.class.name
                        }
                        REF_BNK_FLG {
                            bean = BankFlag.class.name
                        }
                    }
                }
            }
        }
    }
}

attachmentMaxSizeBytes = 2 * 1024 * 1024
attachmentPageMaxSizeBytes = 2 * 1024 * 1024 // 2MB
attachmentAcceptedFormats = ['pdf']

sessionId="A756FB0EF0AC0868579AC41699FAEB85"
jossoSessionId="5EC3CB0A4DFFB4DFE3AB820E8B24533B"
