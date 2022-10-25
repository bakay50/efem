package com.webbfontaine.efem

import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.efem.constants.Statuses
import com.webbfontaine.efem.repatriation.ClearanceOfDom
import com.webbfontaine.efem.repatriation.Repatriation
import com.webbfontaine.repatriation.RepatriationService
import grails.gorm.transactions.Transactional
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import org.joda.time.LocalDate
import spock.lang.Specification

@Integration
@Rollback
@Transactional
class RepatriationServiceIntegrationSpec extends Specification {

    RepatriationService repatriationService

    def "test handleSetCancelRepatriationClearanceOfDom()"() {
        given:
        Exchange exchange = new Exchange(requestNo: "EC2020010039", requestType: ExchangeRequestType.EC, exporterCode: "0020136Z", status: Statuses.ST_EXECUTED, currencyCode: "EUR"
                , geoArea: "003", amountNationalCurrency: new BigDecimal("5000.00"), amountMentionedCurrency: new BigDecimal("5000.00"), balanceAs: new BigDecimal("5000.00")
                , declarationNumber: "123", declarationSerial: "C", clearanceOfficeCode: "CIAB").save()
        Repatriation repatriation = new Repatriation("id": 1, "requestNo": "R01", "status": "Executed")

        repatriation.clearances = [
                new ClearanceOfDom(ecReference: "EC2020010039", ecDate: new LocalDate(), domiciliaryBank: "ECO1", domiciliationNo: "NUM96", domiciliationDate: new LocalDate(), domAmtInCurr: new BigDecimal("5000"), invFinalAmtInCurr: new BigDecimal("5000"), repatriatedAmtInCurr: new BigDecimal("500"))
        ]

        when:
        repatriationService.handleSetCancelRepatriationClearanceOfDom(repatriation)

        then:
        exchange.balanceAs == new BigDecimal("5500")
        exchange.status == Statuses.ST_APPROVED
    }
}
