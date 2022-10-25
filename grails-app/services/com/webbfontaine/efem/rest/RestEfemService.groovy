package com.webbfontaine.efem.rest

import com.webbfontaine.efem.Exchange
import grails.gorm.transactions.Transactional
import org.hibernate.transform.AliasToBeanResultTransformer
import org.joda.time.format.DateTimeFormat

@Transactional
class RestEfemService {

    def getAllEADocsViaTVF(retrieveExchangeCommand) {
        def result = [:]
        def tvfDate = DateTimeFormat.forPattern("yyyy-MM-dd").parseLocalDate(retrieveExchangeCommand?.tvfDate)
        def resultEADocuments= Exchange.createCriteria().list {
            and {
                eq("tvfNumber", retrieveExchangeCommand.tvfNumber)
                eq("tvfDate",tvfDate)
            }
            resultTransformer(new AliasToBeanResultTransformer(EfemResponse.class))
            projections {
                property('id', 'id')
                property('status', 'status')
                property('requestNo', 'requestNo')
                property('requestDate', 'requestDate')
            }
        }

        result << [exchange: resultEADocuments]
    }
}
