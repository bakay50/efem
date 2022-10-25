package com.webbfontaine.efem.rules

import com.webbfontaine.efem.SupDeclaration
import com.webbfontaine.efem.rules.exchange.SupDeclarationMandatoryRule
import com.webbfontaine.grails.plugins.validation.rules.Rule
import com.webbfontaine.grails.plugins.validation.rules.RuleContext
import grails.testing.gorm.DataTest
import grails.util.Holders
import org.joda.time.LocalDate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.support.StaticMessageSource
import spock.lang.Shared
import spock.lang.Specification


class SupDeclarationMandatoryRuleSpec extends Specification implements DataTest {

    @Shared
    @Autowired
    MessageSource messageSource

    def setupSpec(){
        messageSource = Holders.applicationContext.getBean("messageSource") as StaticMessageSource
        messageSource.useCodeAsDefaultMessage
        initMessageSource(messageSource)
    }

    def "test if rule detects if mandatory fields are not filled in"(){
        given:
        SupDeclaration supDec = new SupDeclaration()
        Rule rule = new SupDeclarationMandatoryRule()

        when:
        rule.apply(new RuleContext(supDec, supDec.errors))

        then:"Should throw an error since no data were put in"
        supDec.hasErrors()
        supDec.errors.getFieldError().defaultMessage == "The fields are mandatory"

        when:
        supDec.clearErrors()
        supDec.clearanceOfficeCode = "Code"
        rule.apply(new RuleContext(supDec, supDec.errors))

        then:"Should still throw an error since data is incomplete"
        supDec.hasErrors()
        supDec.errors.getFieldError().defaultMessage == "The fields are mandatory"

        when:
        supDec.clearErrors()
        supDec.clearanceOfficeCode = "Code"
        supDec.declarationSerial = "decSer"
        supDec.declarationDate = LocalDate.now()
        supDec.declarationNumber = 123
        rule.apply(new RuleContext(supDec, supDec.errors))

        then:"Should not throw an error since data is complete"
        !supDec.hasErrors()
    }

    private static initMessageSource(messageSource) {
        messageSource.addMessage('dec.clearanceOfficeCode.label', Locale.US, "clearanceOfficeCode")
        messageSource.addMessage('dec.declarationSerial.label', Locale.US, "declarationSerial")
        messageSource.addMessage('dec.declarationNumber.label', Locale.US, "declarationNumber")
        messageSource.addMessage('dec.declarationDate.label', Locale.US, "declarationDate")

        messageSource.addMessage('dec.clearanceOfficeCode.label', Locale.FRANCE, "clearanceOfficeCode")
        messageSource.addMessage('dec.declarationSerial.label', Locale.FRANCE, "declarationSerial")
        messageSource.addMessage('dec.declarationNumber.label', Locale.FRANCE, "declarationNumber")
        messageSource.addMessage('dec.declarationDate.label', Locale.FRANCE, "declarationDate")
    }

}