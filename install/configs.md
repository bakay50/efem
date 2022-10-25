##version 3.0.6

## version 3.0.6
### [GC-9] [e-Forex] Apply setting of bank stamps for EA.
    com {
        webbfontaine {
                     efem {
                         printea {
                               stamppath = <Absoulte Path>
                               startstampdate = 'Date on dd/mm/YYYY format'
                }
            }
        }
    }


## version 3.0.5.1
### [CIV-5763] [eForex] : Allow multiple recipients to receive email notifications from the WS.
    grails {
        mail {
            queueing=true
            to_admins {
                to = ['edouard.gobou@guce.gouv.ci,'lassine.sidibe@guce.gouv.ci']
                subject = "INFO: SERVEUR DE NOTIFICATION"
                body = "Le serveur de notification est DOWN."
            }
        }
    }

## CIV-5821: eFOREX : Only banks should approve EA
    
    efemciApplicationConfig {
            exchange {
                enableFinexApproval = false
            }
        }
##CIV-5755 [eForex] Allow/using a WS to get/retrieve the TVF data.
    rest {
        tvf {
            basic{
                username  ="wf"
                password = "12345678"
            }
            url = "https://<host>/api/tt"
        }
    }

##version 3.0.2
##CIV-5369 [eRIMM]:Remove Banks management from eFOREX G3 to eRIMM 
    com {
      webfontaine{
        efem {
          rimm {
            bank {
              enabled = false
            }
          }
        }
      }
    }
##CIV-4370 / Add a column in  search results table for repatriation
    com {
      webbfontaine {
        efem {  
          exchange{
              search {
                  repatriation {
                      defaultColumns = [
                          receivedAmount: true
                      ]
                  }
              }
          }
      }
   }
##CIV-4369 / [E-FOREX]Add "Request Date " column  in search results table for Repatriation Sub-module

    com {
      webbfontaine {
        efem {  
          exchange{
              search {
                  repatriation {
                      defaultColumns = [
                             requestDate: true
                      ]
                  }
              }
          }
      }
   }
##CIV-4369 / [E-FOREX]Add a column in the search result table for Transfer Order Sub-module

    com {
      webbfontaine {
        efem {  
          exchange{
              search {
                  transfer {
                      defaultColumns = [
                            transferAmntRequested:true
                      ]
                  }
              }
          }
      }
   }
##CIV-4363 / [E-FOREX] Add a new column in search result table for Currency Transfer

    com {
      webbfontaine {
        efem {  
          exchange{
              search {
                  currencyTransfer {
                      defaultColumns = [
                            amountRepatriated : true
                      ]
                  }
              }
          }
      }

}

## version 3.0.2

## [CIV-4365]

    com {
        webbfontaine {
            efem {
                exchange {
                    fieldsConfig {
                        currencyTransferAuhorization {
                            transferRate = ['P','P','P','P','P','P','P']
                        }
                    }
                }
            }
        }
    }

##CIV-4364 / Improvement of reference management

    com.webbfontaine.plugin.utils.concurrent.stripes = 256

##CIV-4278 / Cannot extract EA and EC data from pdf, excel and csv files when the count is large
    com {
        webbfontaine {
            efem {
                exportCurrentMonth = true
                maxDaysToExport = 30
            }
        }
    }

##CIV-4005 / improve the method getListOfExchange()
    com {
        webbfontaine {
            efem {  
              exchange{
                  search {
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
                    }
              }
          }
       }

##CIV-3998 / Refactor and Create CheckDeclarationSadRule
com {
    webbfontaine {
        efem {

            checkDeclarantValidity = false
            checkCompanyValidity = false
        }
    }
}

##version 3.0.0-RC21
##CIV-3246 / [E-FOREX] Allow to user to select columns to be displayed in the results table :Search Currency Transfer
com {

    webbfontaine {
    
        efem {  
          exchange{
              search {
                  currencyTransfer {
                      defaultColumns = [
                         status : true,
                         requestNo : true,
                         requestDate : true,
                         bankCode : true,
                         currencyTransferDate : true,
                         amountTransferred : true,
                         amountTransferredNat : false,
                         currencyCode : false
                      ]
                  }
                }
          }
      }
   }
   
##CIV-3245 / [E-FOREX] Allow to user to select columns to be displayed in the results table :Search Repatriation
com {

    webbfontaine {
    
        efem {  
          exchange{
              search {
                    repatriation {
                        defaultColumns = [
                           status : true,
                           natureOfFund : true,
                           requestNo : true,
                           currencyCode : true,
                           code : true,
                           repatriationBankCode : false
                        ]
                    }
                }
          }
      }
   }
##CIV-3244 / [E-FOREX] Allow to user to select columns to be displayed in the results table :Search Transfer
com {

    webbfontaine {
    
        efem {  
         exchange{
            search {
                  transfer {
                         defaultColumns = [
                            status : true,
                            requestNo : true,
                            requestDate : true,
                            importerCode : true,
                            bankCode : true,
                            currencyPayCode : false,
                            sumOfClearanceOfDoms : false
                         ]
                     }
                }
          }
      }
   }
##CIV-3243 / [E-FOREX] Allow to user to select columns to be displayed in the results table :Search EA / EC
com {

    webbfontaine {
        efem {  
            exchange{
                    search {
                            defaultColumns = [
                               status : true,
                               requestType : true,
                               basedOn : false,
                               requestNo : true,
                               requestDate : true,
                               tvfNumber : false,
                               declarationNumber : false,
                               bankName : false,
                               bankApprovalDate : false,
                               declarantNameAddress : true,
                               importerNameAddress : true,
                               amountNationalCurrency : false,
                               countryProvenanceDestinationCode : false,
                               balanceAs : false]
                          }
                     }
               }
      }
    }
##version 3.0.0-RC18

### [CIV-2430] - For EC maxApproval must be set to 1 for gov agent profile
    com {
        webbfontaine {
            efem {
                maxApprovalConfigEC = 1
            }
        }
    }

## [CIV-2398] allow the configuration of two different mail accounts for the gov agent profile (finex)
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
## [CIV-2397]
    com {
            webbfontaine {
    
                    efem {
                            exchange {
                                    createTransfer {
                                           enabled = false
                                    }
                                   createCurrencies{
                                          enabled = false
                                   }
                            }
                    }
            }
    }

## [CIV-2254,2255]
    com {
        webbfontaine {
            efem {
                displayFieldsRemainingBalanceTransfer = false
            }
        }
    }

##version 3.0.0-RC17 
## [CIV-2238]
    com {
        webbfontaine {
                     efem {
                          exchange{
                                  fieldsConfig{
                                              repatriationAuhorization{
                                                        executionRef = ['C','M','P','P','C','P','P','P','P','P']
                                                        executionDate= ['C','M','P','P','C','P','P','P','P','P']
                                                                       }
                                              }
                                  }
                          }
                     }
        }
## [CIV-2190]
    efemAllowedCheckingCurrencyCode {
        enableCheckingCurrencyCode = 'Y'
        enableByPasscheckingByRequestDate {
            enableByPassing = 'Y'
            lessReferenceDate = '01/09/2018'
        }
        startingSearchRequestDate = '01/01/2019'
    }

##version 3.0.0-RC17
## [CIV-2198]
    com {
        webbfontaine {
                     efem {
                          displayImportExport = false
                     }
        }
    }
## [CIV-2184]
    com {
        webbfontaine {
                    efem {
                        tvfVariance{
                            rate = 0.1
                            amount = 10000000
                            }
                        }
                    }
        }
    
## [CIV-2193]
    com {
        webbfontaine {
                     efem {
                          displayEAImportExport = false
                     }
        }
    }
## [CIV-1981]
    efemAllowedOfficeCode{
        code_office = ["CIAB6","CIAB4","CIYKP","CIB41","CIRSY"]
    }

##version 3.0.0-RC16

## [CIV-1910]
    rest {
        sad {    
            show {
                url = "https://uatapp4.webbfontaine.ci/sw-viewer/sad/show"
            }
        }
    }
    
## [CIV-1785]
    com {
        webbfontaine {
                    efem {        
                        exchange {        
                            fieldsConfig {        
                                exchangeAuhorization {
                                            importerCode =                      ['P','P','P','P','P','P','P','P','P','P','P']
                                            importerNameAddress =               ['P','P','P','P','P','P','P','P','P','P','P']
                                                    }
                                          }
                                 }
                         }
                    }
        }

## [CIV-1546]
    **New config**
    com {
        webbfontaine {
            efem {
                exchange {
                    fieldsConfig {
                        transferOrderAuhorization {
                        
                            transferAmntExecuted =              ['P','P','P','P','P','P','P','P','P','P']
                            transferNatAmntExecuted =           ['P','P','P','P','P','P','P','P','P','P']
                        
                            //clearance of domiciliation		|0|  |1| |2| |3| |4| |5| |6| |7| |8| |9|
                            
                            eaReference =                       ['O','P','M','P','P','O','M','P','P','P']
                            authorizationDate =                 ['P','P','P','P','P','P','P','P','P','P']
                            registrationNoBank =                ['P','P','P','P','P','P','P','P','P','P']
                            registrationDateBank =              ['P','P','P','P','P','P','P','P','P','P']
                            bankName =                          ['P','P','P','P','P','P','P','P','P','P']
                            amountToBeSettledMentionedCurrency =['P','P','P','P','P','P','P','P','P','P']
                            amountRequestedMentionedCurrency =  ['O','P','M','P','P','O','M','P','P','P']
                            amountSettledMentionedCurrency =    ['C','P','C','P','O','C','P','P','P','P']
                        }
                    }
                }
            }
        }
    }
##version 3.0.0-RC15
## [CIV-1435]
    rest {
        isWebService=false
        }

    dataSources{
        sydamviews {
              dbCreate = 'none'
              url = 'jdbc:oracle:thin:@10.7.150.10:1521:SWWFDB'
              pooled = true
              driverClassName = 'oracle.jdbc.driver.OracleDriver'
              username = 'sydamviews'
              password = 'k62g3hBHs3a'
              dialect = 'org.hibernate.dialect.Oracle10gDialect'
              configClass = HibernateFilterDomainConfiguration
             loggingSql = false
    
           properties {
            maxActive = 30
            minIdle = 5
            maxIdle = 15
            maxWait = 10000
            maxAge = 10 * 60000
            minEvictableIdleTimeMillis = 1800000
            timeBetweenEvictionRunsMillis = 1800000
            numTestsPerEvictionRun = 3
            testOnBorrow = true
            testWhileIdle = true
            testOnReturn = true
            validationQuery = "SELECT 1 FROM DUAL"
         }
        }
        
        efemciviews {
                dbCreate = "none" // one of 'create', 'create-drop', 'update', 'validate', ''
                url = "jdbc:oracle:thin:@10.230.8.58:1521:ASYWDB"
                pooled = true
                driverClassName = "oracle.jdbc.driver.OracleDriver"
                username = "efemci_views"
                password = "efemciviews123"
                dialect = "org.hibernate.dialect.Oracle10gDialect"
                configClass = HibernateFilterDomainConfiguration
                loggingSql = false
                properties {
                maxActive = 30
                minIdle = 5
                maxIdle = 15
                maxWait = 10000
                maxAge = 10 * 60000
                   minEvictableIdleTimeMillis = 1800000
                   timeBetweenEvictionRunsMillis = 1800000
                   numTestsPerEvictionRun = 3
                   testOnBorrow = true
                   testWhileIdle = true
                   testOnReturn = true
                   validationQuery = "SELECT 1 FROM DUAL"
                }
        }
    }

##version 3.0.0-RC15
## [CIV-1254]
**New config**
com {
        webbfontaine {
            efem {
                exchange {
                    fieldsConfig {
                        transferOrderAuhorization {
                            executionRef  = ['C','P','M','P','M','O','M','P','P','P']
                            executionDate = ['C','P','M','P','M','O','M','M','P','P']
                        }
                    }
                }
            }
        }
    }

##version 3.0.0-RC15
## [CIV-1333]
**New config**
repatriationNotification{
    enable = false
    operations{
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

##version 3.0.0-RC15
## [CIV-1259]
**New config**
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
    }
}

##version 3.0.0-RC15
## [CIV-1445]
**New config**

    com {
        webbfontaine {
            efem {
                enabled {
                    rule {
                        CheckUnclearedEaRule = false
                    }
                }
            }
        }
    }

##version 3.0.0-RC10
## [CIV-1360]
**New config**
notification {
    enable = true
    operations {    
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

## [CIV-1337 / CIV-1338]
**New config**
repatriationNotification{
    enable = true
    operations{
        Declare{
            enable = true
            endStatus = [
                    "DECLARED": [[recipient: 'Bank', mailType: "DRB"]],
                    "CONFIRMED": [[recipient: 'Exporter', mailType: "CREXP"],
                                  [recipient: 'DomiciliationBankAgent', mailType : "CRDB"]]
            ]
        }
        Confirm{
            enable = true
            endStatus = [
                    "CONFIRMED": [[recipient: 'Exporter', mailType: "CREXP"],
                                 [recipient: 'DomiciliationBankAgent', mailType : "CRDB"]]
            ]
        }
        Query{
            enable = true
            endStatus = [
                    "QUERIED": [[recipient: 'Exporter', mailType: "QREXP"]]
            ]
        }
        Cancel{
            enable = true
            endStatus = [
                    "CANCELLED": [[recipient: 'Exporter', mailType: "CAREXP"]]
            ]
        }
    }
}

## [CIV-1333 / CIV-1334]
**New config**
repatriationNotification{
    enable = true
    operations{
        Confirm{
            enable = true
            endStatus = [
                    "CONFIRMED": [[recipient: 'Exporter', mailType: "CREXP"]]
            ]
        }
        Query{
            enable = true
            endStatus = [
                    "QUERIED": [[recipient: 'Exporter', mailType: "QREXP"]]
            ]
        }
    }
}

   ### [CIV-1261] - E-mail notification: Query Operation
   **New config**
notification {
    enable = true
    operations {    
     Query {
                enable = true
                endStatus = [
                        "QUERIED": [
                                [recipient: 'applicant', mailType: "QGBAA"],
                                [recipient: 'declarant', mailType: "QGBAD"]]
                ]
            }
     }
}
## [CIV-1259 / CIV-1260 / CIV 1262 / CIV-1281]
**New config**
notification {
    enable = true
    operations {
        Request {
            enable = true
            endStatus = [
                    "REQUESTED": [
                            [recipient: 'applicant', mailType: "RDA"],
                            [recipient: 'bankAgent', mailType: "RDBA"],
                            [recipient: 'DomiciliationBankAgent', mailType: "RDBDA"]]
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
        Cancel {
            enable = true
            endStatus = [
                    "CANCELLED": [
                            [recipient: 'applicant', mailType: "CDBAA"]]
            ]
        }
        Approve {
            enable = true
            endStatus = [
                    "PARTIALLY APPROVED": [
                            [recipient: 'govOfficer', mailType: "PAGOA"]],
                    "APPROVED" : [
                            [recipient: 'applicant', mailType: "AGOA"],
                            [recipient: 'declarant', mailType: "AGOD"]],
                    ]
        }
    }
}

grails{
    mail {
        enabled = true
        serverType = "[GUCE]"
        host = "guce-gouv-ci.mail.protection.outlook.com"
        port = 25
        username = "app.notification@guce.gouv.ci"
        password=  $/ N@t!f@2020\$ /$
        props = ["mail.smtp.auth":"true"]
    }
}
grails.mail.default.from = "no-reply-qa@guce.gouv.ci <no-reply-qa@guce.gouv.ci>"

## [CIV-1095] - Change status approved to partially approved for EA based on TVF
**New config**

    com {
        webbfontaine {
            efem {
                exchange {
                    fieldsConfig {
                        exchangeAuhorization {
                            registrationNumberBank = ['P','P','P','P','C','P','P','P','P','P','M']
                        }
                    }
                    domiciliationFields = ['domiciliationNumber','domiciliationDate','domiciliationBankCode', 'registrationNumberBank']
                }
            }
        }
    }

   ##version 3.0.0-RC6"
    
   ### [CIV-844] - Request from 'Geographical Area' = 002
   **New config**
    
     com { 
         webbfontaine {        
             efem {               
                 geoArea2 = '002'
                 geoArea3 = '003'
             }
         }
     }
     
**CIV-450 Email Notification for Importer after Validate operation and End status = Validated**

    orderNotification {
        enable = true
        operations {
            Request {
                enable = true
                endStatus = [
                        "REQUESTED": [[recipient: 'Bank', mailType: "REQBANK"]],
                        "VALIDATED": [[recipient: 'Importer', mailType: "REQVALIMP"]]
                ]
            }
            Validate {
                enable = true
                endStatus = [
                        "VALIDATED": [[recipient: 'Importer', mailType: "VALIMP"]]
                ]
            }
            Query {
                enable = true
                endStatus = [
                        "QUERIED": [[recipient: 'Importer', mailType: "QUEIMP"]]
                ]
            }
            Cancel {
                enable = true
                endStatus = ["CANCELLED": [
                        [recipient: 'Bank', mailType: "CBANK"],
                        [recipient: 'Importer', mailType: "CIMP"]]
                ]
            }
            Update {
                enable = true
                endStatus = ["VALIDATED": [
                        [recipient: 'Bank', mailType: "UVBANK"],
                        [recipient: 'Importer', mailType: "UVIMP"]]
                ]
            }
        }
    }

**CIV-904 Set XOF in Currency Code Payment field**

    com { 
        webbfontaine {        
            efem {                
                listCountryOfDestination = ["BF", "BJ", "TG", "SN", "NE", "ML", "GW"]
                geoArea1 = '001'
            }
        }
    }

# **Guce eForex Configs**
Latest first.

## version "3.0.7"
### [GUCEBJ-2765] - Alter the rule on AC request amount to check against the declared amount of the AVD if exists
**Datasource**
*EFOREX*

    com {
        webbfontaine {
            efem {
                erc{
                    retrieveAvd {
                        url = "https://<host>/vw/avd"
                    }
                }
            }
        }
    }

## version 3.0.4
### [GUCEBJ-3276] - Value of Bank property should be automatically added in bank list in Header tab
**new config:**

    com {
        webbfontaine {
            efem {
                   josso{
                                jossoAdmin = "<jossoAdmin>"
                            }
            }
         }
     }
=
## version 3.0.3
### [GUCEBJ-3138] - Remove Domiciliation fields in Header tab
**new config:**

    com {
        webbfontaine {
            efem {
                isHeaderDomiciliationVisible = false
            }
         }
     }


## version 3.0.2
### [GUCEBJ-2928] - Email Notification for Government Agent on Approve Operation
**update config:**

    environments {
        development {
            emails {
                govOfficer = "sample@email.com"
            }
        }
        test {
            emails {
                govOfficer = "sample@email.com"
            }
        }
        production {
            emails {
                govOfficer = "sample@email.com"
            }
        }
    }

## version 3.0.2
### [GUCEBJ-2761] - Email Notification for Importer on Request Operation
**update config:**

    grails{
        mail {
            enabled = true
            serverType = "[GUCE]"
            host = "smtp.mailgun.org"
            port = 465
            username = "guce_notifications@mg.benincontrol.com"
            password="FRRek@cZtKDbPd&b"
            props = ["mail.smtp.auth":"true",
                     "mail.smtp.socketFactory.port":"465",
                     "mail.smtp.socketFactory.class":"javax.net.ssl.SSLSocketFactory",
                     "mail.smtp.socketFactory.fallback":"false"]
        }
    }
    
    grails.mail.default.from = "no-reply@guce.gouv.bj <no-reply@guce.gouv.bj>"
    
    notification {
        enable = true
        operations {
            Request {
                enable = true
                endStatus = [
                        "REQUESTED": [
                                [recipient: 'applicant', mailType: "RDA"],
                                [recipient: 'bankAgent', mailType: "RDBA"]]
                ]
            }
            
            Reject {
                enable = true
                endStatus = [
                        "REJECTED": [
                                [recipient: 'applicant', mailType: "RJBAA"],
                                [recipient: 'declarant', mailType: 'RJBAD'],
                                [recipient: 'declarant', mailType: 'RJGOD']]
                ]
            }
            
            Query {
                enable = true
                endStatus = [
                        "QUERIED": [
                                [recipient: 'applicant', mailType: "QGBAA"],
                                [recipient: 'declarant', mailType: "QBAD"],
                                [recipient: 'declarant', mailType: "QGOD"]]
                ]
            }
            
            Cancel {
                enable = true
                endStatus = [
                        "CANCELLED": [
                                [recipient: 'applicant', mailType: "CDBAA"],
                                [recipient: 'declarant', mailType: "CBAD"],
                                [recipient: 'declarant', mailType: "CGOD"]]
                ]
            }
            
            Approve {
                enable = true
                endStatus = [
                        "PARTIALLY APPROVED": [
                                [recipient: 'applicant', mailType: "PABAA"],
                                [recipient: 'govOfficer', mailType: "PAGOA"],
                                [recipient: 'declarant', mailType: "PABAD"]],
                        "APPROVED" : [
                                [recipient: 'applicant', mailType: "AGOA"],
                                [recipient: 'bankAgent', mailType: "AGOBA"],
                                [recipient: 'declarant', mailType: "AGOD"]],
                        ]
            }
            
            Update {
                enable = true
                endStatus = [
                        "EXECUTED": [
                                [recipient: 'applicant', mailType: "UBAA"],
                                [recipient: 'declarant', mailType: "UBAD"]]
                ]
            }
        }
    }

## version 3.0.0-RC2
### [GUCEBJ-2385] - View eSAD button
**update config:**

    rest {
        sad {
            show {
                url = "https://<host>/esad/sad/sad/show"
            }
        }
    }
        
=
## version 3.0.0-RC2
### [GUCEBJ-1924] - EC Grails3 Migration
**update config:**

    com {
            webbfontaine {
                efem {
                    exchange {
                        businessLogicRuleValidation {
        
                            EaRequiredFields =  ["countryOfExportCode":"Country of Export",
                                                   "dateOfBoarding":"Date Of Boarding",
                                                   "declarantCode":"Declarant Code",
                                                   "operType":"Operation Type",
                                                   "amountMentionedCurrency":"Amount",
                                                   "provenanceDestinationBank":"Destination Bank",
                                                   "bankAccountNocreditedDebited":"Bank Account No. to be credited"]
                            
                            EcRequiredFields =  ["operType":"Operation Type",
                                                 "amountMentionedCurrency":"Amount",
                                                 "provenanceDestinationBank":"Destination Bank",
                                                 "bankAccountNocreditedDebited":"Bank Account No. to be credited"]
                        }
                    }
                }
            }
        
=
## version 3.0.0-RC2
### [GUCEBJ-2300] - List of Domiciliation fields with Optional config for Bank Agent
**new config:**

    com.webbfontaine.efem.exchange.domiciliationFields = ['domiciliationNumber','domiciliationDate','domiciliationBankCode']
    
=
## version 3.0.0-RC2
### [GUCEBJ-2209] - Incorrect field values
**update config:**

    tvfToExchangeConfig {
                 tvf {
                              tvfInstanceId = "instanceId"
                              tvfNumber = "trNumber"
                              tvfDate = "trDate"
                              domiciliationNumber = "domiciliationRef"
                              domiciliationDate = "domiciliationDate"
                              bankCode = "bankCode"
                              bankName = "bankName"
                              exporterCode = "expTaxPayerAcc"
                              exporterNameAddress = "expName"
                              importerCode = "impTaxPayerAcc"
                              importerNameAddress = "impName"
                              currencyCode = "invCurCode"
                              currencyName = "invCurName"
                              currencyRate = "invCurRat"
                              currencyPayRate = "invCurPayRat"
                              declarantCode = "decCode"
                              declarantNameAddress = "decName"
                              countryOfExportCode = "expCtyCode"
                              countryOfExportName = "expCtyName"
                              beneficiaryName = "expName"
                              beneficiaryAddress = "expAddress"
                 }
    
    }
    
=
## version 3.0.0-RC2
### [GUCEBJ-2186] - EA fields should only be enabled once user load valid TVF document
**update config:**

    com {
        webbfontaine {
            efem {
               exchange {
                     fieldsConfig {
                                 exchangeAuhorization {
                                                 tvfNumber =  ['M','P','P','P','P','P','P','P','P','P']
                                                 tvfDate =    ['M','P','P','P','P','P','P','P','P','P']                              
                                 }                   
                     }
               }
            }
        }
    }



=
## version 3.0.0-RC2
### [GUCEBJ-2098] - On Print operation, all search results should be included in printout document
**new config:**

    com {
        webbfontaine {
            efem {
                exchange {
                    search {
                        maxLimit = 5000           
                 }
               }
            }
        }
    }
    
=
## version 3.0.0-RC2
### [GUCEBJ-2191] - Query/Reject message box should be enabled on Query/Reject operation only
**update config:**

    com {
        webbfontaine {
            efem {
               exchange {
                     fieldsConfig {
                                 exchangeAuhorization {
                                                 comments = ['O','P','M','M','P','P','P','P','P','P']                              
                                 }                   
                     }
               }
            }
        }
    }



=
## version 3.0.0-RC1
### [GUCEBJ-2169] - Unable to Approve Requested EA - Amount is Mandatory
**new config:**

    tvfToExchangeConfig {
        tvf {
            invCurRat = "currencyRate"
            invCurPayRat = "currencyPayRate"
        }
    }
    
    
    com {
            webbfontaine {
                efem {
                    exchange {
                        businessLogicRuleValidation {
        
                            requiredFields =  ["countryOfExportCode":"Country of Export",
                                               "dateOfBoarding":"Date Of Boarding",
                                               "declarantCode":"Declarant Code",
                                               "operType":"Operation Type",
                                               "amountMentionedCurrency":"Amount",
                                               "provenanceDestinationBank":"Destination Bank",
                                               "bankAccountNocreditedDebited":"Bank Account No. to be credited"]
                        }
                    }
                }
            }
        }
    

    
=
## version 3.0.0-RC1
### [GUCEBJ-2078] - Partially Approved Operation for eForex
**new config:**

    com {
        webbfontaine {
            efem {
                maxApprovalConfig = 1
                maxApprovalConfigEC = 1
                 maxAmountInXofCurrency = '500000.00'
            }
        }
    }


=
## version 3.0.0-RC1
### [GUCEBJ-2066] - eFOREX - Migrate to Grails3
**new config:**

    com {
        webbfontaine {
            grails {
                plugins {
                    taglibs {
                        fieldConfig {
                            treatReadonlyAsDisabled = false
                        }
                    }
                }
            }
        }
    }
    
=
## version 3.0.0
### [SWFEMCI-577] - eFOREX - Migrate to Grails3
**new config:**

    com {
        webbfontaine {
            grails {
                plugins {
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
                        }
                    } 
                }
             }
         }
    }
    
    com {
        webbfontaine {
            efem {
                   
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
                        enabled = true
                    }
                 }   
             }       
         }
     }

    -- TVF and eSAD webservice
    rest {
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
        }
    }
    
    efemAllowedOfficeCode{
        code_office = ["CIAB6","CIAB4","CIYKP","CIB41","CIRSY","CIAB3"]
        incoterm = ["DAT","DAP","DDP"]
    }
    
    efemAllowedCheckingCurrencyCode{
        enableCheckingCurrencyCode = 'Y'
        enableByPasscheckingByRequestDate{
            enableByPassing = 'Y'
            lessReferenceDate = '01/09/2018'
        }
    }
    
    com {
        webbfontaine {
            efem {
                listCountryOfDestination = ["BF", "BJ", "TG", "SN", "NE", "ML", "GW"]
            }
        }
    }
    
    com {
            webbfontaine {
                efem {
                    requiredAttachments = ['325', '380']
                }
            }
        }
