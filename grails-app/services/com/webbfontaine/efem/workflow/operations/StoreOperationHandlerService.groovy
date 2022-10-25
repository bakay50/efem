package com.webbfontaine.efem.workflow.operations

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.workflow.BpmService
import groovy.util.logging.Slf4j
import org.joda.time.LocalDateTime


@Slf4j("LOGGER")
class StoreOperationHandlerService extends ClientOperationHandlerService {

    def exchangeService

    @Override
    BpmService getBpmService() {
        return exchangeWorkflowService
    }

    @Override
    def beforePersist(Exchange exchange, def commitOperation){
        LOGGER.debug("in beforePersist() of ${StoreOperationHandlerService}")

        exchange?.balanceAs = exchangeService.computationOfBalanceAs(exchange)
        exchange?.lastTransactionDate = LocalDateTime.now()

        LOGGER.debug("Value of Request Sequence Number through Store Operation: ${exchange?.requestNumberSequence}")
    }


}
