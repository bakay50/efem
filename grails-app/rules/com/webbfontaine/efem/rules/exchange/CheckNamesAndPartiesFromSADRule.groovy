package com.webbfontaine.efem.rules.exchange

import com.webbfontaine.efem.BusinessLogicUtils
import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.UtilConstants
import com.webbfontaine.efem.sad.SadService
import com.webbfontaine.grails.plugins.utils.WebRequestUtils
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.util.Holders
import groovy.util.logging.Slf4j
import static com.webbfontaine.efem.constants.ExchangeRequestType.EA
import static com.webbfontaine.efem.constants.ExchangeRequestType.EC
import static com.webbfontaine.efem.constants.ExchangeRequestType.BASE_ON_SAD
import static com.webbfontaine.efem.workflow.Operation.CREATE
import static com.webbfontaine.efem.workflow.Operation.UPDATE_APPROVED

@Slf4j
class CheckNamesAndPartiesFromSADRule implements Rule {


    @Override
    void apply(RuleContext ruleContext) {
        log.debug("in apply of ${CheckNamesAndPartiesFromSADRule}")
        Exchange exchangeInstance = ruleContext.getTargetAs(Exchange) as Exchange
        if (exchangeInstance?.basedOn == BASE_ON_SAD || exchangeInstance?.requestType == EC) {
            handleRule(exchangeInstance)
        }
    }

    static def handleRule(Exchange exchange) {
        def params = WebRequestUtils.params
        if ((exchange?.requestType == EA && exchange.startedOperation == CREATE) || (exchange?.requestType == EC && exchange?.startedOperation in [UPDATE_APPROVED]) && BusinessLogicUtils.displayDeclaration(exchange)) {
            def sadDocument = retrieveSadDocument(params)
            checkSadDeclarantCode(exchange, sadDocument, params)
            handleError(exchange, sadDocument)
        }
    }

    static def handleError(Exchange exchange, sadDocument) {
        if (sadDocument?.data?.id) {
            checkImportExportCode(exchange, sadDocument)
        } else {
            exchange.errors.reject("exchange.errors.existe.declarationNumber", "The declaration doesn't exist.")
        }
    }

    static def checkImportExportCode(Exchange exchange, sadDocument){
        if(sadDocument?.data?.consigneeCode != UtilConstants.SPECIAL_IMPORTER_CODE) {
            BusinessLogicUtils.handleNameAndPartiesErrors(exchange, exchange?.importerCode, sadDocument?.data?.consigneeCode, "importerCode", "exchange.errors.sad.importerCode.error", "Importer non consistent with that of the declaration")
            BusinessLogicUtils.handleNameAndPartiesErrors(exchange, exchange?.exporterCode, sadDocument?.data?.exporterCode, "exporterCode", "exchange.errors.sad.exporterCode.error", "Exporter non consistent with that of the declaration")
        }
    }

    static void checkSadDeclarantCode(Exchange exchange, sadDocument, params) {
        if (sadDocument?.data?.declarantCode && params.declarantCode && !exchange?.declarantCode?.equals(sadDocument?.data?.declarantCode)) {
            exchange.errors.rejectValue("declarantCode", "exchange.errors.sad.declarantCode.error", "Declarant code non consistent with that of the declaration")
        }
    }

    static def retrieveSadDocument(params) {
        SadService service = Holders.applicationContext.getBean("sadService")
        def sadDocument = service.retrieveSad(params)
        sadDocument
    }
}
