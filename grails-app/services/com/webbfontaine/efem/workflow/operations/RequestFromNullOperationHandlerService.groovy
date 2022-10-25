package com.webbfontaine.efem.workflow.operations


import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.workflow.BpmService
import groovy.util.logging.Slf4j
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import static com.webbfontaine.efem.constant.MailConstants.EX_REQUEST

/**
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Yacouba SYLLA
 * Date: 10/28/2014
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
@Slf4j("LOGGER")
class RequestFromNullOperationHandlerService extends ClientOperationHandlerService {
   
    static transactional = false

    def exchangeWorkflowService
    def exchangeService
    def notificationService
    def grailsApplication
    def sequenceGenerator
    def messagingNotifService

    @Override
    BpmService getBpmService() {
        return exchangeWorkflowService
    }

    @Override
    def beforePersist(Exchange domainInstance, def commitOperation) {
        LOGGER.debug("in beforePersist() of ${RequestFromNullOperationHandlerService}")

        domainInstance?.requestDate = new LocalDate()
        def year = domainInstance?.requestDate?.getYear()
        def requestType = domainInstance?.requestType

        // initialize the value to sequenceGenerator.requestType
		String key = "${year}/${requestType}"
        domainInstance?.requestNumberSequence = sequenceGenerator.nextRequestNumber(key)

        domainInstance?.requestYear = year
        domainInstance?.requestNo = exchangeService.generateRequestNumber(domainInstance)
        domainInstance?.balanceAs = exchangeService.computationOfBalanceAs(domainInstance)
        domainInstance?.requestedBy = exchangeService.getRequestedBy()

        // check Validation Fields for exchange
//        domainInstance = checkValidationExchangeService.checkTypeExchange(domainInstance)
/*        domainInstance = exchangeService.checkReferenceAutorisation(domainInstance)
        domainInstance = exchangeService.checkXOFForEcDependOnArea(domainInstance)
        domainInstance = exchangeService.checkECFields(domainInstance)*/

        /* ---------------  Check user properties restrictions ---------------*/
//        domainInstance =  documentSecurityService.checkAccessAsDecForEA(domainInstance)
//        domainInstance =  documentSecurityService.checkAccessAsDecTinForEC(domainInstance)

        /* ---------------  Check user properties restrictions ---------------*/
        domainInstance?.treatmentLevel=0
        /* if(domainInstance.isAssignableToFinex()) {   
            domainInstance?.treatmentLevel = 1
         }*/   
//        exchangeService.assignToFinexOrBank(domainInstance)
        domainInstance?.lastTransactionDate = LocalDateTime.now()

        LOGGER.debug("Value of Request Sequence Number through Request Operation: ${domainInstance?.requestNumberSequence}")
    }

/*    boolean eMailEnabled(Exchange domainInstance) {
        return "Y".equals(grailsApplication?.config.efemciApplicationConfig.eMailConfig?.EnableEmailNotification)
    }*/

    @Override
    def afterPersist(Exchange domainInstance, Exchange result, Object hasErrors, Object commitOperation) {
        
        if(result)
         domainInstance?.id = result?.id

        if(!hasErrors){
            messagingNotifService.sendAfterCommit(result, EX_REQUEST)
        }

    }

}
