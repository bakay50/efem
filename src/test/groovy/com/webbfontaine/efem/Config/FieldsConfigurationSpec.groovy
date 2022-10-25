package com.webbfontaine.efem.Config

import com.webbfontaine.efem.UserUtils
import com.webbfontaine.efem.transferOrder.TransferOrder
import grails.testing.gorm.DataTest
import spock.lang.Specification
import spock.lang.Unroll
import com.webbfontaine.efem.workflow.Operation

/**
 * Copyrights 2002-2016 Webb Fontaine
 * Developer: Bakayoko Abdoulaye
 * Date: 13/09/2017
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */

class FieldsConfigurationSpec extends Specification implements DataTest{

    void setupSpec() {
        mockDomain TransferOrder
    }

    @Unroll("TransferOrder property #fieldName is editable: #editable")
    def 'Test is TransferOrder field is Editable on Create Operation'(){
        when:
        GroovyMock(UserUtils, global: true)
        UserUtils.isBankAgent() >> true
        TransferOrder transferOrder = new TransferOrder()
        transferOrder.startedOperation = Operation.REQUEST
        def result = transferOrder.isFieldEditable(fieldName)

        then:
        editable == result

        where:
        fieldName                           | editable
        'requestNo'                         | false
        'requestDate'                       | false
        'importerCode'                      | true
        'importerNameAddress'               | false
        'countryBenefBankCode'              | true
        'countryBenefBankName'              | false
        'destinationBank'                   | true
        'byCreditOfAccntOfCorsp'            | true
        'bankAccntNoCredit'                 | true
        'nameOfAccntHoldCredit'             | true
        'bankCode'                          | true
        'bankName'                          | false
        'bankAccntNoDebited'                | true
        'charges'                           | true
        'currencyPayCode'                   | true
        'currencyPayName'                   | false
        'ratePayment'                       | false
        'executionRef'                      | true
        'executionDate'                     | true

    }

    @Unroll("TransferOrder property #fieldName is not editable: #editable")
    def 'Test All TransferOrder field is not Editable on DELETE Operation'(){
        when:
        TransferOrder transferOrder = new TransferOrder()
        transferOrder.startedOperation = Operation.DELETE
        def result = transferOrder.isFieldEditable(fieldName)

        then:
        editable == result

        where:
        fieldName                           | editable
        'requestNo'                         | false
        'requestDate'                       | false
        'importerCode'                      | false
        'importerNameAddress'               | false
        'countryBenefBankCode'              | false
        'countryBenefBankName'              | false
        'destinationBank'                   | false
        'byCreditOfAccntOfCorsp'            | false
        'bankAccntNoCredit'                 | false
        'nameOfAccntHoldCredit'             | false
        'bankCode'                          | false
        'bankName'                          | false
        'bankAccntNoDebited'                | false
        'charges'                           | false
        'currencyPayCode'                   | false
        'currencyPayName'                   | false
        'ratePayment'                       | false
        'executionRef'                      | false
        'executionDate'                     | false

    }

    @Unroll("Export TransferOrder property #fieldName is editable: #editable")
    def 'Test is TransferOrder field is Editable on Request Stored Operation'(){
        when:
        GroovyMock(UserUtils, global: true)
        UserUtils.isBankAgent() >> true
        TransferOrder transferOrder = new TransferOrder()
        transferOrder.startedOperation = Operation.REQUEST_STORED
        def result = transferOrder.isFieldEditable(fieldName)

        then:
        editable == result

        where:
        fieldName                           | editable
        'requestNo'                         | false
        'requestDate'                       | false
        'importerCode'                      | true
        'importerNameAddress'               | false
        'countryBenefBankCode'              | true
        'countryBenefBankName'              | false
        'destinationBank'                   | true
        'byCreditOfAccntOfCorsp'            | true
        'bankAccntNoCredit'                 | true
        'nameOfAccntHoldCredit'             | true
        'bankCode'                          | true
        'bankName'                          | false
        'bankAccntNoDebited'                | true
        'charges'                           | true
        'currencyPayCode'                   | true
        'currencyPayName'                   | false
        'ratePayment'                       | false
        'executionRef'                      | true
        'executionDate'                     | true
    }

}
