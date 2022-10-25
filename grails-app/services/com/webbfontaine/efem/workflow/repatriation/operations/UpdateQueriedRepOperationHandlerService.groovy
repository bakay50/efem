package com.webbfontaine.efem.workflow.repatriation.operations

import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.repatriation.RepatriationService
import groovy.util.logging.Slf4j

@Slf4j("LOGGER")
class UpdateQueriedRepOperationHandlerService extends ClientRepOperationHandlerService {
    RepatriationService repatriationService

    @Override
    def afterPersist(Repatriation domainInstance, Repatriation result, Object hasErrors, Object commitOperation) {
        if (!hasErrors) {
            LOGGER.debug("in after persist of ${UpdateQueriedRepOperationHandlerService.class} with repatriation ${result?.id}")
            repatriationService.logConcernedEcDeleted(result, domainInstance?.ecRefToBeDeleted)
            repatriationService.updateEcDeleted(result,domainInstance?.ecChanged)
            repatriationService.updateEcBalanceAndStatus(domainInstance, commitOperation)
            repatriationService.updateEcAmountUpdated(result,domainInstance?.ecChanged)
        }
    }
}
