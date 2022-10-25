grails.gorm.default.constraints = {
    '*'(nullable: true)
}

grails.gorm.default.mapping = {
    "user-type" type: org.jadira.usertype.dateandtime.joda.PersistentDateTime, class: org.joda.time.DateTime
    "user-type" type: org.jadira.usertype.dateandtime.joda.PersistentLocalDate, class: org.joda.time.LocalDate
    "user-type" type: org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime, class: org.joda.time.LocalDateTime
}

grails.databinding.dateFormats = ["dd/MM/yyyy", "yyyy-MM-dd"]
dateFormat = 'dd/MM/yyyy'
timeFormat = 'HH:mm:ss'
jodatime.format.html5 = true
jodatime.format.org.joda.time.LocalDate = dateFormat
jodatime.format.org.joda.time.DateTime = "${dateFormat} ${timeFormat}"
jodatime.format.org.joda.time.LocalDateTime = "${dateFormat} ${timeFormat}"
JQueryDateFormat = "dd/mm/yy"
bootstrapDate = 'DD/MM/YYYY'
com.webbfontaine.plugin.utils.concurrent.stripes = 256

com {
    webbfontaine {
        efem {
            erc {
                retrieveAvd {
                    url = "https://<host>/vw/avd"
                }
            }
            josso {
                jossoAdmin = "jjuan"
            }
            exchange {
                createFromTVF {
                    enabled = true
                }

                createFromSAD {
                    enabled = true
                }

                createEC {
                    enabled = true
                }

                createRepat {
                    enabled = false
                }
                createTransfer {
                    enabled = false
                }
                createCurrencies {
                    enabled = false
                }

                search {
                    max = 10
                    maxLimit = 5000
                    exchange {
                        defaultColumns = [
                                status : true,
                                requestType : true,
                                basedOn : true,
                                requestNo : true,
                                requestDate : true,
                                tvfNumber : true,
                                declarationNumber : true,
                                bankName : true,
                                bankApprovalDate : true,
                                declarantNameAddress : true,
                                importerNameAddress : true,
                                exporterNameAddress : true,
                                amountNationalCurrency : true,
                                countryProvenanceDestinationCode : false,
                                balanceAs : true
                        ]
                    }
                    transfer {
                        defaultColumns = [
                                status : true,
                                requestNo : true,
                                requestDate : true,
                                importerCode : true,
                                bankCode : true,
                                transferAmntRequested:true,
                                currencyPayCode : false,
                                sumOfClearanceOfDoms : false
                        ]
                    }

                    repatriation {
                        defaultColumns = [
                                status : true,
                                natureOfFund : true,
                                requestNo : true,
                                requestDate: true,
                                currencyCode : true,
                                receivedAmount: true,
                                code : true,
                                repatriationBankCode : false
                        ]
                    }

                    currencyTransfer {
                        defaultColumns = [
                                status : true,
                                requestNo : true,
                                requestDate : true,
                                bankCode : true,
                                currencyTransferDate : true,
                                amountRepatriated : true,
                                amountTransferred : true,
                                currencyCode : true,
                                amountTransferredNat : true,
                                transferRate: true

                        ]
                    }
                }

                fieldsConfig {

                    /**
                     * Started Operation:
                     *  Index: 0 [NULL, UPDATE_STORED, REQUEST_STORED, REQUEST]
                     *  Index: 1 [REQUEST_QUERIED]
                     *  Index: 2 [QUERY_REQUESTED]
                     *  Index: 3 [REJECT_REQUESTED,REJECT_PARTIALLY_APPROVED]
                     *  Index: 4 [APPROVE_REQUESTED]
                     *  Index: 5 [APPROVE_PARTIALLY_APPROVED]
                     *  Index: 6 [UPDATE_QUERIED]
                     *  Index: 7 [UPDATE_APPROVED]
                     *  Index: 8 [UPDATE_EXECUTED]
                     *  Index: 9 [CANCEL_QUERIED, CANCEL_APPROVED]
                     *  Index: 10 [DOMICILIATE]
                     */
                    exchangeAuhorization {
                        //header							 |0| |1| |2| |3| |4| |5| |6| |7| |8| |9| |10|
                        requestNo =                         ['P','P','P','P','P','P','P','P','P','P','P']
                        requestDate =                       ['P','P','P','P','P','P','P','P','P','P','P']
                        tvfNumber =                         ['M','P','P','P','P','P','P','P','P','P','P']
                        tvfDate =                           ['M','P','P','P','P','P','P','P','P','P','P']
                        clearanceOfficeCode =               ['M','P','P','P','P','P','P','O','O','P','P']
                        declarationSerial =                 ['M','P','P','P','P','P','P','O','O','P','P']
                        declarationNumber =                 ['M','P','P','P','P','P','P','O','O','P','P']
                        declarationDate =                   ['M','P','P','P','P','P','P','O','O','P','P']
                        bankCode =                          ['M','P','P','P','P','P','P','P','P','P','P']
                        bankName =                          ['P','P','P','P','P','P','P','P','P','P','P']
                        registrationNumberBank =            ['P','P','P','P','C','P','P','P','P','P','C']
                        registrationDateBank =              ['P','P','P','P','P','P','P','P','P','P','P']

                        domiciliationNumber =               ['P','P','P','P','C','P','P','C','C','P','P']
                        domiciliationDate =                 ['P','P','P','P','C','P','P','C','C','P','P']
                        domiciliationBankCode =             ['P','P','P','P','C','P','P','C','C','P','P']
                        domiciliationBankName =             ['P','P','P','P','P','P','P','P','P','P','P']

                        authorizationDate =                 ['P','P','P','P','P','P','P','P','P','P','P']
                        expirationDate =                    ['P','P','P','P','P','P','P','P','P','P','P']
                        requestType =                       ['P','P','P','P','P','P','P','P','P','P','P']
                        basedOn =                           ['P','P','P','P','P','P','P','P','P','P','P']
                        year =                              ['P','P','P','P','P','P','P','P','P','P','P']
                        authorizedBy =                      ['P','P','P','P','P','P','P','P','P','P','P']
                        evaluationReport =                  ['P','P','P','P','P','O','P','P','P','P','P']
                        treatmentLevel =                    ['P','P','P','P','P','P','P','P','P','P','P']
                        commentHeader =                     ['O','O','P','P','P','P','O','P','P','P','P']

                        comments =                          ['O','P','M','M','O','O','P','P','P','M','P']
                        countryOfExportCode =               ['P','C','P','P','C','P','C','P','P','P','P']
                        geoArea =                           ['M','P','P','P','P','P','P','P','P','P','P']
                        countryOfDestinationCode =          ['M','P','P','P','P','P','P','P','P','P','P']
                        dateOfBoarding =                    ['P','P','P','P','P','P','P','P','P','P','P']
                        nationalityCode =                   ['M','M','P','P','P','P','M','P','P','P','P']
                        resident =                          ['M','P','P','P','P','P','P','P','P','P','P']
                        departmentInCharge=                 ['P','P','P','P','P','P','P','P','P','P','P']

                        //name and parties					 |0| |1| |2| |3| |4| |5| |6| |7| |8| |9| |10|
                        exporterCode =                      ['C','C','P','P','P','P','P','P','P','P','P']
                        exporterNameAddress =               ['C','C','P','P','P','P','P','P','P','P','P']
                        importerCode =                      ['P','P','P','P','P','P','P','P','P','P','P']
                        importerNameAddress =               ['C','C','P','P','P','P','P','P','P','P','P']
                        declarantNameAddress =              ['C','C','P','P','P','P','P','P','P','P','P']

                        declarantCode =                     ['C','P','P','P','P','P','P','P','P','P','P']
                        beneficiaryName =                   ['O','O','P','P','P','P','P','P','P','P','P']
                        beneficiaryAddress =                ['O','O','P','P','P','P','P','P','P','P','P']

                        //operation and execution			 |0| |1| |2| |3| |4| |5| |6| |7| |8| |9| |10|
                        operType =                          ['M','M','P','P','P','P','M','P','P','P','P']
                        currencyCode =                      ['C','C','P','P','P','P','P','P','P','P','P']
                        currencyRate =                      ['P','P','P','P','P','P','P','P','P','P','P']
                        currencyPayCode =                   ['M','M','P','P','P','P','M','P','P','P','P']
                        currencyPayRate =                   ['P','P','P','P','P','P','P','P','P','P','P']
                        amountMentionedCurrency =           ['M','M','P','P','P','P','M','P','P','P','P']
                        amountNationalCurrency =            ['P','P','P','P','P','P','P','P','P','P','P']
                        goodsValuesInXOF =                  ['O','O','P','P','P','P','O','O','O','P','P']
                        finalAmountInDevise =               ['P','P','P','P','P','P','P','P','P','P','P']
                        finalAmount =                       ['P','P','P','P','P','P','P','P','P','P','P']
                        isFinalAmount =                     ['P','P','P','P','P','P','P','O','O','P','P']
                        balanceAs =                         ['P','P','P','P','P','P','P','P','P','P','P']
                        countryProvenanceDestinationCode =  ['O','O','P','P','P','P','O','P','P','P','P']
                        provenanceDestinationBank =         ['O','O','P','P','P','P','O','P','P','P','P']
                        bankAccountNocreditedDebited =      ['O','O','P','P','P','P','O','P','P','P','P']
                        exportationTitleNo =                ['O','O','P','P','P','P','C','P','P','P','P']
                        executingBankCode =                 ['M','O','P','P','P','P','M','P','P','P','P']

                        exFeesPaidByExpInCIinXOF =          ['O','O','P','P','P','P','O','O','O','P','P']
                        exFeesPaidByExpInAbroadinXOF =      ['O','O','P','P','P','P','O','O','O','P','P']
                        accountNumberBeneficiary =          ['M','M','P','P','P','P','C','P','P','P','P']
                        status =                            ['P','P','P','P','P','P','P','P','P','P','P']
                        remainingBalanceDeclaredAmount =    ['P','P','P','P','P','P','P','P','P','P','P']
                        remainingBalanceDeclaredNatAmount = ['P','P','P','P','P','P','P','P','P','P','P']
                        remainingBalanceTransferDoneAmount =['P','P','P','P','P','P','P','P','P','P','P']
                        remainingBalanceTransferDoneNatAmount=['P','P','P','P','P','P','P','P','P','P','P']

                    }

                    repatriationAuhorization {

                        //header							 |0| |1| |2| |3| |4| |5| |6| |7| |8| |9|

                        requestNo =                         ['P','P','P','P','P','P','P','P','P','P']
                        requestDate =                       ['P','P','P','P','P','P','P','P','P','P']

                        natureOfFund =                      ['P','P','P','P','P','P','P','P','P','P']
                        code =                              ['M','P','P','P','P','P','P','P','P','P']
                        nameAndAddress =                    ['P','P','P','P','P','P','P','P','P','P']
                        repatriationBankCode =              ['M','M','P','P','P','P','M','P','P','P']
                        repatriationBankName =              ['P','P','P','P','P','P','P','P','P','P']
                        currencyCode =                      ['P','P','P','P','P','P','P','P','P','P']
                        currencyRate =                      ['P','P','P','P','P','P','P','P','P','P']
                        receivedAmountNat =                 ['P','P','P','P','P','P','P','P','P','P']
                        currencyName =                      ['P','P','P','P','P','P','P','P','P','P']
                        receivedAmount =                    ['P','P','P','P','P','P','P','P','P','P']
                        receptionDate =                     ['M','M','P','P','P','P','M','P','P','P']
                        countryOfOriginCode =               ['M','P','P','P','P','P','P','P','P','P']
                        countryOfOriginName =               ['P','P','P','P','P','P','P','P','P','P']
                        bankOfOriginCode =                  ['M','M','P','P','P','P','M','P','P','P']
                        bankOfOriginName =                  ['P','P','P','P','P','P','P','P','P','P']
                        bankNotificationDate =              ['P','P','P','P','P','P','P','P','P','P']
                        executionRef =                      ['C','P','P','P','C','P','P','P','P','P']
                        executionDate =                     ['C','P','P','P','C','P','P','P','P','P']
                        currencyTransfertDate =             ['P','P','P','P','P','P','P','P','P','P']

                        amountTransferred =                 ['P','P','P','P','P','P','P','P','P','P']
                        amountRemaining =                   ['P','P','P','P','P','P','P','P','P','P']
                        amountTransferredNat =              ['P','P','P','P','P','P','P','P','P','P']
                        amountRemainingNat =                ['P','P','P','P','P','P','P','P','P','P']
                        status =                            ['P','P','P','P','P','P','P','P','P','P']

                    }


                    /**
                     * Started Operation:
                     *  Index: 0 [NULL]
                     *  Index: 1 [DELETE STORED]
                     *  Index: 2 [REQUEST,REQUEST_STORED,REQUEST_QUERIED]
                     *  Index: 3 [QUERY]
                     *  Index: 4 [VALIDATE]
                     *  Index: 5 [UPDATE_STORED]
                     *  Index: 6 [UPDATE_QUERIED]
                     *  Index: 7 [UPDATE_VALIDATED]
                     *  Index: 8 [CANCEL_QUERIED]
                     *  Index: 9 [CANCEL_VALIDATED]
                     */

                    transferOrderAuhorization {
                        //header							|0|  |1| |2| |3| |4| |5| |6| |7| |8| |9|

                        requestNo =                         ['P','P','P','P','P','P','P','P','P','P']
                        requestDate =                       ['P','P','P','P','P','P','P','P','P','P']

                        importerCode =                      ['M','P','M','P','P','M','M','P','M','P']
                        importerNameAddress =               ['P','P','P','P','P','P','P','P','P','P']
                        countryBenefBankCode =              ['M','P','M','P','P','M','M','P','M','P']
                        countryBenefBankName =              ['P','P','P','P','P','P','P','P','P','P']

                        destinationBank =                   ['M','P','M','P','P','M','M','P','M','P']
                        byCreditOfAccntOfCorsp =            ['O','P','M','P','P','M','M','P','M','P']
                        bankAccntNoCredit =                 ['O','P','M','P','P','O','M','P','M','P']
                        nameOfAccntHoldCredit =             ['O','P','M','P','P','O','M','P','M','P']
                        bankCode =                          ['M','P','M','P','P','M','M','P','M','P']
                        bankName =                          ['P','P','P','P','P','P','P','P','P','P']
                        bankAccntNoDebited =                ['O','P','M','P','P','O','M','P','P','P']
                        charges =                           ['O','P','M','P','P','O','M','P','M','P']
                        currencyPayCode =                   ['O','P','M','P','P','O','M','P','P','P']
                        currencyPayName =                   ['P','P','P','P','P','P','P','P','P','P']
                        ratePayment =                       ['P','P','P','P','P','P','P','P','P','P']
                        executionRef =                      ['C','P','C','P','M','O','P','P','P','P']
                        executionDate =                     ['C','P','C','P','M','O','P','M','P','P']
                        transferAmntRequested =             ['P','P','P','P','P','P','P','P','P','P']
                        transferNatAmntRequest =            ['P','P','P','P','P','P','P','P','P','P']
                        amntRequestedInLetters =            ['P','P','P','P','P','P','P','P','P','P']
                        transferAmntExecuted =              ['P','P','P','P','P','P','P','P','P','P']
                        transferNatAmntExecuted =           ['P','P','P','P','P','P','P','P','P','P']
                        amntExecutedInLetters =             ['P','P','P','P','P','P','P','P','P','P']
                        status =                            ['P','P','P','P','P','P','P','P','P','P']

                        //clearance of domiciliation		|0|  |1| |2| |3| |4| |5| |6| |7| |8| |9|

                        eaReference =                       ['O','P','O','P','P','O','O','P','P','P']
                        authorizationDate =                 ['P','P','P','P','P','P','P','P','P','P']
                        registrationNoBank =                ['P','P','P','P','P','P','P','P','P','P']
                        registrationDateBank =              ['P','P','P','P','P','P','P','P','P','P']
                        bankName =                          ['P','P','P','P','P','P','P','P','P','P']
                        amountToBeSettledMentionedCurrency =['P','P','P','P','P','P','P','P','P','P']
                        amountRequestedMentionedCurrency =  ['O','P','O','P','P','O','O','P','P','P']
                        amountSettledMentionedCurrency =    ['C','P','C','P','O','C','P','P','P','P']
                    }

                    /**
                     * Started Operation:
                     *  Index: 0 [NULL]
                     *  Index: 1 [DELETE_STORED]
                     *  Index: 2 [TRANSFER]
                     *  Index: 3 [TRANSFER_STORED]
                     *  Index: 4 [UPDATE_STORED]
                     *  Index: 5 [UPDATE_TRANSFERRED]
                     *  Index: 6 [CANCEL_TRANSFERRED]
                     */
                    currencyTransferAuhorization {
                        //header							 |0| |1| |2| |3| |4| |5| |6|
                        requestNo =                         ['P','P','P','P','P','P','P']
                        requestDate =                       ['P','P','P','P','P','P','P']
                        bankCode =                          ['M','P','M','M','M','P','P']
                        bankName =                          ['P','P','P','P','P','P','P']
                        currencyCode =                      ['M','P','M','M','M','P','P']
                        currencyName =                      ['P','P','P','P','P','P','P']
                        currencyRate =                      ['P','P','P','P','P','P','P']
                        amountTransferred =                 ['P','P','P','P','P','P','P']
                        amountTransferredNat =              ['P','P','P','P','P','P','P']
                        amountRepatriated =                 ['P','P','P','P','P','P','P']
                        amountRepatriatedNat =              ['P','P','P','P','P','P','P']
                        currencyTransferDate =              ['O','P','M','M','O','P','P']
                        status =                            ['P','P','P','P','P','P','P']
                        repatriationNo =                    ['O','P','P','P','P','P','P']
                        repatriationDate =                  ['O','P','P','P','P','P','P']
                        transferRate =                      ['P','P','P','P','P','P','P']

                        //ClearanceDomiciliation			|0| |1| |2| |3| |4| |5| |6|
                        ecReference =                       ['O','P','O','O','O','M','P']
                        ecDate =                            ['P','P','P','P','P','P','P']
                        domiciliationCodeBank =             ['P','P','P','P','P','P','P']
                        domiciliationNo =                   ['P','P','P','P','P','P','P']
                        domiciliationDate =                 ['P','P','P','P','P','P','P']
                        domiciliatedAmounttInCurr =         ['P','P','P','P','P','P','P']
                        invoiceFinalAmountInCurr =          ['P','P','P','P','P','P','P']
                        repatriatedAmountToBank =           ['P','P','P','P','P','P','P']
                        amountTransferredInCurr =           ['O','P','O','O','O','M','P']
                    }
                    /*********************************************
                     * start Bank
                     * *******************************************/
                    /**
                     * Started Operation:
                     *  Index: 0 [REGISTER]
                     *  Index: 1 [UPDATE]
                     *  Index: 2 [CANCEL]
                     *  Index: 3 [ACTIVATE]
                     */
                    bankAuhorization {
                        //['register', 'update', 'cancel', 'activate']
                        code           = ['M', 'P', 'P', 'P']
                        dateOfValidity = ['M', 'P', 'P', 'P']
                        emailEA        = ['M', 'M', 'P', 'P']
                        emailEC        = ['M', 'M', 'P', 'P']
                        status         = ['C', 'P', 'P', 'P']
                    }
                    /*********************************************
                     * end bank
                     * *******************************************/
                }

                domiciliationFields = ['domiciliationNumber', 'domiciliationDate', 'domiciliationBankCode', 'registrationNumberBank']

                businessLogicRuleValidation {

                    EaRequiredFields =  [
                            "bankCode":"Bank Code",
                            "nationalityCode":"Nationality",
                            "resident":"Resident",
                            "beneficiaryName":"Name", "beneficiaryAddress": "Address",
                            "operType":"Operation Type",
                            "amountMentionedCurrency":"Amount",
                            "countryProvenanceDestinationCode":"Country beneficiary bank",
                            "provenanceDestinationBank":"Destination Bank",
                            "bankAccountNocreditedDebited":"Bank Account No to be credited",
                            "accountNumberBeneficiary":"Bank Account No to be debited"]

                    EcRequiredFields =  ["operType":"Operation Type",
                                         "amountMentionedCurrency":"Amount",
                                         "provenanceDestinationBank":"Destination Bank",
                                         "bankAccountNocreditedDebited":"Bank Account No. to be credited"]

                }
            }
        }
    }
}

com {
    webbfontaine {
        efem {
            isHeaderDomiciliationVisible = false
            requiredAttachments = ['0007']
            factureAttachments = ['325', '380', '035']
            listCountryOfDestination = ["CI","BF", "BJ", "TG", "SN", "NE", "ML", "GW"]
            listGeoArea = ["001", "002","003"]
            geoArea1 = '001'
            geoArea2 = '002'
            geoArea3 = '003'
            maxApprovalConfig = 1
            maxApprovalConfigEC = 1
            maxAmountInXofCurrency = '500000.00'

            isRetrievingAttachmentAvailable = false
            isRetrieveSadHasNoParams = true

            displayEAImportExport = false
            displayImportExport = false
            displayFieldsRemainingBalanceTransfer = false
            displayTransferOrderLinkFromExecution = false
            checkDeclarantValidity = false
            checkCompanyValidity = false
            exportCurrentMonth = false
            maxDaysToExport = 30
            sad {
                operationtypeCode = 'EA001'
            }
            enabled {
                rule {
                    CheckTotalAmountRule = false
                    ChkAttachmentRule = false
                    CheckUnclearedEaRule = false
                }
                rimm {
                    bankFlag = false
                }
            }
            tvfVariance{
                rate = 0.1
                amount = 10000000
            }
        }
    }
}

com {
    webbfontaine {
        grails {
            plugins {
                taglibs {
                    fieldConfig {
                        treatReadonlyAsDisabled = false
                    }
                }
                validation {
                    refConfig {
                        appDomainFieldsDescriptionPostFix = "Name"
                    }
                }
            }
        }
    }
}

rest {
    isWebService = false
    tvf {
        url = "https://<host>/tvf/tt"
        enabled = false

        show {
            url = "http://<host>/tvf/tvfGen/show"
        }
    }
    sad {
        url="https://<host>/esad/sad/declaration"
        enabled = false

        show {
            url = "https://<host>/sw-viewer/sad/show"
        }
    }
}

sadToExchangeConfig {
    sad {
        clearanceOfficeCode = "officeOfDispatchExportCode"
        clearanceOfficeName = "officeOfDispatchExportName"
        declarationSerial = "declarationSerial"
        declarationNumber = "declarationNo"
        declarationDate = "declarationDate"
        sadInstanceId = "id"
        sadStatus = "status"
        sadTypeOfDeclaration = "typeOfDeclaration"
        declarantCode = "declarantCode"
        declarantNameAddress = "declarantName"
        importerCode = "consigneeCode"
        importerNameAddress = "consigneeName"
        countryOfExportCode = "countryOfExportCode"
        countryOfExportName = "countryOfExportName"
        currencyCode = "invoiceCurrencyCode"
        currencyPayCode = "invoiceCurrencyCode"
        currencyRate = "invoiceCurrencyExchangeRate"
        currencyPayRate = "invoiceCurrencyExchangeRate"
        sadInvoiceAmountInForeignCurrency = "invoiceAmountInForeignCurrency"
        exporterCode = "exporterCode"
        exporterNameAddress = "exporterName"
        totalAmountOfCif = "totalAmountOfCostInsuranceFreight"
        sadIncoterm = "deliveryTermsCode"
        domiciliationBankCode = "bankCode"
        domiciliationBankName = "bankName"
        domiciliationNumber = "bankFile"
        cifAmtFcx = "cifAmtFcx"

    }
    exclude {
        sadAttachedDoc = "items"
    }

}

tvfToExchangeConfig {
    tvf {
        tvfInstanceId = "instanceId"
        tvfNumber = "trNumber"
        tvfDate = "trDate"
        domiciliationNumber = "domiciliationRef"
        domiciliationDate = "domiciliationDate"
        domiciliationBankName = "bankName"
        domiciliationBankCode = "bankCode"
        exporterCode = "expTaxPayerAcc"
        exporterNameAddress = "expName"
        importerCode = "impTaxPayerAcc"
        importerNameAddress = "impName"
        currencyCode = "invCurCode"
        currencyPayCode = "invCurCode"
        currencyName = "invCurName"
        currencyRate = "invCurRat"
        currencyPayRate = "invCurPayRat"
        declarantCode = "decCode"
        declarantNameAddress = "decName"
        countryOfExportCode = "countryOfExportCode"
        countryOfExportName = "countryOfExportName"
        beneficiaryName = "expName"
        beneficiaryAddress = "expAddress"
    }

    tvfAttachment {
        docCode = "docType"
        value = "docTypName"
        docRef = "docRef"
        docDate = "docDate"
        fileExtension = "fileExtension"
    }

    tvfAttachedFile {
        attDoc = "upl_fil"
    }
}

efemAllowedOfficeCode{
    code_office = ["CIAB6","CIAB4","CIYKP","CIB41","CIRSY"]
    incoterm = ["DAT","DAP","DDP"]
}
efemAllowedCheckingCurrencyCode {
    enableCheckingCurrencyCode = 'Y'
    enableByPasscheckingByRequestDate {
        enableByPassing = 'Y'
        lessReferenceDate = '01/09/2018'
    }
    startingSearchRequestDate = '01/01/2019'
}

numberFormatConfig {
    exchangeRatePattern = "8,4"
    otherCurrencyValuePattern = "15,0"
    nationalMonetaryValuePattern = "15,0"
    grossMassPattern = "12,0"
    netMassPattern = "12,0"
    emptyWeightPattern = "12,0"
    goodsWeightPattern = "12,0"
    numberOfPackagePattern = "7,0"
    numberOfItemPattern = "7,0"
    numberOfContainerPattern = "7,0"
    supplementaryUnitPattern = "14,3"
    wholeNumberInputFilterPattern = '[0-9\\,]'
    decimalNumberInputFilterPattern = '[0-9\\,\\.]'
    monetaryNumberFormat = "#,###"
    exchangeRateFormat = "#,###.####"
    quantityNumberFormat = "#,###"
    decimalNumberFormat = "#,##0.00"
    decimalNumberFormats = "#,##0.000"
}

efemciApplicationConfig{
    displayBeneficiary = false
    webServiceConfig {
        useWebService = 'N'
    }
}

notification {
    enable = true
    operations {
        Request {
            enable = true
            endStatus = [
                    "REQUESTED": [
                            [recipient: 'applicant', mailType: "RDA"],
                            [recipient: 'bankAgent', mailType: "RDBA"],
                            [recipient: 'DomiciliationBankAgent', mailType: "RDBDA"],
                            [recipient: 'govOfficer', mailType: "RGOA"]]
            ]
        }
        Reject {
            enable = true
            endStatus = [
                    "REJECTED": [
                            [recipient: 'applicant', mailType: "RJBAA"],
                            [recipient: 'declarant', mailType: 'RJBAAD']]
            ]
        }
        Query {
            enable = true
            endStatus = [
                    "QUERIED": [
                            [recipient: 'applicant', mailType: "QGBAA"],
                            [recipient: 'declarant', mailType: "QGBAD"]]
            ]
        }
        Cancel {
            enable = true
            endStatus = [
                    "CANCELLED": [
                            [recipient: 'applicant', mailType: "CAAA"],
                            [recipient: 'bankAgent', mailType: "CAGOVA"],
                            [recipient: 'govOfficer', mailType: "CABAA"]]
            ]
        }
        Approve {
            enable = true
            endStatus = [
                    "PARTIALLY APPROVED": [
                            [recipient: 'govOfficer', mailType: "PAGOA"]],
                    "APPROVED" : [
                            [recipient: 'applicant', mailType: "AGOA"],
                            [recipient: 'declarant', mailType: "AGOD"],
                            [recipient: 'govOfficer', mailType: "ABAGO"],
                            [recipient: 'bankAgent', mailType: "AGOBA"]]
            ]
        }
        Update {
            enable = true
            endStatus = [
                    "EXECUTED": [
                            [recipient: 'bankAgent', mailType: "UAEBANK"]],
                    "APPROVED": [
                            [recipient: 'bankAgent', mailType: "UAEBANK"]]
            ]
        }
    }
}

repatriationNotification {
    enable = false
    operations{
        Declare{
            enable = false
            endStatus = [
                    "DECLARED": [[recipient: 'Bank', mailType: "DRB"]],
                    "CONFIRMED": [[recipient: 'Exporter', mailType: "CREXP"],
                                  [recipient: 'DomiciliationBankAgent', mailType : "CRDB"]]
            ]
        }
        Confirm {
            enable = false
            endStatus = [
                    "CONFIRMED": [[recipient: 'Exporter', mailType: "CREXP"],
                                  [recipient: 'DomiciliationBankAgent', mailType : "CRDB"]]
            ]
        }
        Query {
            enable = false
            endStatus = [
                    "QUERIED": [[recipient: 'Exporter', mailType: "QREXP"]]
            ]
        }
        Cancel {
            enable = false
            endStatus = [
                    "CANCELLED": [[recipient: 'Exporter', mailType: "CAREXP"]]
            ]
        }
        Update {
            enable = false
            endStatus = [
                    "CONFIRMED": [[recipient: 'exporter', mailType: "UPREXP"],
                                  [recipient: 'DomiciliationBankAgent', mailType: "UPRDOBA"],
                                  [recipient: 'bankAgent', mailType: "UPRBA"]]
            ]
        }
    }
}

orderNotification {
    enable = false
    operations {
        Request {
            enable = false
            endStatus = [
                    "REQUESTED": [[recipient: 'Bank', mailType: "REQBANK"]],
                    "VALIDATED": [[recipient: 'Importer', mailType: "REQVALIMP"]]
            ]
        }
        Validate {
            enable = false
            endStatus = [
                    "VALIDATED": [[recipient: 'Importer', mailType: "VALIMP"]]
            ]
        }
        Query {
            enable = false
            endStatus = [
                    "QUERIED": [[recipient: 'Importer', mailType: "QUEIMP"]]
            ]
        }
        Cancel {
            enable = false
            endStatus = ["CANCELLED": [
                    [recipient: 'Bank', mailType: "CBANK"],
                    [recipient: 'Importer', mailType: "CIMP"]]
            ]
        }
        Update {
            enable = false
            endStatus = ["VALIDATED": [
                    [recipient: 'Bank', mailType: "UVBANK"],
                    [recipient: 'Importer', mailType: "UVIMP"]]
            ]
        }
    }
}

environments {
    development {
        emails {
            govOfficerEA = "sampleEA@email.com"
            govOfficerEC = "sampleEC@email.com"
        }
    }
    test {
        emails {
            govOfficerEA = "sampleEA@email.com"
            govOfficerEC = "sampleEC@email.com"
        }
    }
    production {
        emails {
            govOfficerEA = "sampleEA@email.com"
            govOfficerEC = "sampleEC@email.com"
        }
    }
}
