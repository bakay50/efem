package com.webbfontaine.efemci

import grails.gorm.transactions.Transactional

/*
import com.webbfontaine.sw.rimm.RimmCuo
import com.webbfontaine.sw.rimm.RimmLoadSadTvfService
import org.codehaus.groovy.grails.web.binding.DataBindingUtils
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.slf4j.LoggerFactory
import org.codehaus.groovy.grails.commons.GrailsApplication

import org.springframework.web.servlet.support.RequestContextUtils
*/
@Transactional
class SupDeclDocumentService {
    /*
    private static final LOGGER = LoggerFactory.getLogger(SupDeclDocumentService);
    def rimmValueCheckService
    RimmLoadSadTvfService rimmLoadSadTvfService
    def clientWebService
    def grailsApplication
    SupDeclaration editSupDecl(Exchange exchange) {
        LOGGER.debug("in editSupDecl() of ${SupDeclDocumentService}");
        String useWebService= grailsApplication.config.efemciApplicationConfig.webServiceConfig.useWebService 
        def paramsRank = currentRequestParams().rank_dec ?: 0

        String clearanceOfficeName = currentRequestParams().clearanceOfficeName ?: ''
        String clearanceOfficeCode = currentRequestParams().clearanceOfficeCode ?: ''
        String declarationDeclDate = currentRequestParams().declarationDeclDate ?: ''
        String declarationSerial = currentRequestParams().declarationSerial ?: ''
        Integer declarationNumber = currentRequestParams().declarationNumber as Integer
        def sadDate =LocalDate.parse(declarationDeclDate as String,DateTimeFormat.forPattern("dd/MM/yyyy"))

        Exchange supDecInstance=new Exchange(clearanceOfficeCode:clearanceOfficeCode,
                declarationSerial:declarationSerial,
                declarationNumber:declarationNumber,declarationDate:sadDate )

        SupDeclaration supDecl= new SupDeclaration(currentRequestParams())

            if(rimmValueCheckService.checkOrRetriveInstance(RimmCuo,'cuo_cod',clearanceOfficeCode,null,"Boolean") == false){
            LOGGER.warn("clearance Office Code not exist {}", clearanceOfficeCode)
            supDecl.clearanceOfficeCode = null
            supDecl.clearanceOfficeName = null
            supDecl.errors.rejectValue('clearanceOfficeCode', 'SupDeclaration.errors.invalidcode', 'Invalid clearance Office Code ')
        }
        else {
            if (!isDuplicateRecord(exchange.supDeclarations, paramsRank as Integer, 'clearanceOfficeCode', clearanceOfficeCode,'declarationDeclDate',declarationDeclDate,'declarationSerial',declarationSerial,'declarationNumber',declarationNumber)) {
                // check if update value exist with new data
                def newsad               
                if(useWebService=="Y"){
                    newsad = clientWebService.retrieveSad(supDecInstance)

                  }else{
                    newsad = rimmLoadSadTvfService.retrieveOnesad(clearanceOfficeCode,LocalDate.parse(declarationDeclDate as String,
                        DateTimeFormat.forPattern("dd/MM/yyyy")),declarationSerial,declarationNumber)
                  } 
                
                if(newsad){
                    def newsadstatus = newsad?.status
                    def newmodel = newsad?.declaration_type
                   // if(newsadstatus && (newsadstatus == "Exited" || newsadstatus == "EXITED")){
                   if(newsadstatus && (newsadstatus == "Exited" || newsadstatus == "EXITED" || newsadstatus == 'Totally Exited' || newsadstatus == 'Totally exited')){
                        if(newmodel == "EX"){
                            supDecl.errors.rejectValue('clearanceOfficeCode', 'SupDeclaration.errors.model', 'Import exchange document cannot be linked to an export declaration')
                        }else{
                            LOGGER.debug('updating Declaration in list with details : {}', currentRequestParams())
                            DataBindingUtils.bindObjectToInstance(supDecl, currentRequestParams())
                        }
                    }else{
                        supDecl.errors.rejectValue('clearanceOfficeCode', 'SupDeclaration.errors.statusExited', 'The declaration doesn’t have valid status')
                    }
                }else{
                    supDecl.errors.rejectValue('clearanceOfficeCode', 'SupDeclaration.errors.exist', 'The declaration doesn\'t exist')
                }
            } else {
                LOGGER.warn('Declaration already exist in the list with rank = {} clearanceOfficeName = {}', paramsRank, clearanceOfficeName)
                supDecl.errors.rejectValue('clearanceOfficeCode', 'SupDeclaration.errors.duplicate', 'Declaration already exist in the list')
            }
        }
        supDecl
    }

    SupDeclaration deleteSupDecl(Exchange exchange) {
        LOGGER.debug("in deleteSupDecl() of ${SupDeclDocumentService}");

        def paramsRank = currentRequestParams().rank_dec ?: 0
        String paramsclearanceOfficeName = currentRequestParams().clearanceOfficeName
        SupDeclaration supDecl = exchange.supDeclarations.find{
            it.rank == paramsRank as Integer && it.clearanceOfficeName.equals(paramsclearanceOfficeName)
        }
        if (supDecl) {
            exchange.removeFromList(exchange.supDeclarations, supDecl)
            return null
        } else {
            LOGGER.warn('cannot find Declaration in Declaration list with rank = {}, clearanceOfficeName = {}', paramsRank, paramsclearanceOfficeName)
            supDecl = SupDeclaration.newInstance()
            supDecl.errors.rejectValue('rank', 'SupDeclaration.errors.notFound', 'Cannot find Declaration from the list')
        }
        supDecl
    }

    private static boolean isDuplicateRecord(List subDocuments, int subDocumentRank, String keyField, def keyValue , String keyField1, def keyValue1,String keyField2, def keyValue2, String keyField3, def keyValue3) {
        LOGGER.debug("in isDuplicateRecord() of ${ExcSubDocumentService}");
        boolean duplicate = false
        for (obj in subDocuments) {
            if (obj.rank != subDocumentRank) {
                if (obj."$keyField".equals(keyValue) && obj."$keyField1".equals(keyValue1) && obj."$keyField2".equals(keyValue2) && obj."$keyField3".equals(keyValue3)) {
                    duplicate = true
                    break
                }
            }
        }
        duplicate
    }

    private static boolean isDuplicateRecords(List subDocuments, int subDocumentRank , String keyField, def keyValue) {
        LOGGER.debug("in isDuplicateRecord() of ${ExcSubDocumentService}");
        boolean duplicate = false
        for (obj in subDocuments) {
            if (obj.rank != subDocumentRank) {
                if (obj."$keyField".equals(keyValue)) {
                    duplicate = true
                    break
                }
            }
        }
        duplicate
    }

    private static Map currentRequestParams() {
        GrailsWebRequest.lookup().params
    }
    */
}
