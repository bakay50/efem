package com.webbfontaine.efem.print

import com.webbfontaine.efem.Exchange
import com.webbfontaine.efem.constants.ExchangeRequestType
import com.webbfontaine.sw.rimm.RimmSadDetails
import com.webbfontaine.sw.rimm.RimmSadItems
import grails.gorm.transactions.Transactional
import grails.util.Holders
import groovy.util.logging.Slf4j
import net.sf.jasperreports.engine.JasperReport
import net.sf.jasperreports.engine.util.JRLoader
import org.joda.time.LocalDate

@Slf4j('LOGGER')
@Transactional
class PrintParametersService {

    def rimmLoadSadTvfService
    def clientWebService
    private static final BANK_CODE = 18

    def setupSadGoodsParams(Exchange exchange, boolean isProvisory, def exchangeParams) {
        ArrayList<RimmSadItems> listGoods = new ArrayList<RimmSadItems>()
        if (exchange?.departmentInCharge?.toString()?.trim() != ExchangeRequestType.departmentInCharg) {
            if (exchange?.registrationNumberBank?.toString()?.trim()) {
                def temporary_list
                if (exchange?.clearanceOfficeCode && exchange?.declarationSerial && exchange?.declarationNumber && exchange?.declarationDate) {
                    temporary_list =  retrieveExchangeSadArticlesWithAttachedDocs(exchange)
                }
                def registrationNumberBank = removeSpace(exchange?.registrationNumberBank?.trim())
                if (temporary_list) {
                    temporary_list = goodsListByExchangeRegistrationNumberBank(temporary_list, registrationNumberBank)
                    temporary_list.removeAll([null])
                    temporary_list = temporary_list?.unique()
                    if (temporary_list) {
                        addGoodsToListGoods(temporary_list, listGoods)
                        temporary_list.clear()
                    }
                }
            }
        } else {
            if (!isProvisory) {
                listGoods = rimmLoadSadTvfService.list_items(exchange?.clearanceOfficeCode, exchange?.declarationDate, exchange?.declarationSerial, exchange?.declarationNumber)
            }
        }
        if (!isProvisory) {
            LOGGER.info("List of sad goods = ${listGoods}")
            exchangeParams.put("GOODS_SUBREPORT", (JasperReport) JRLoader.loadObject(resourceAsStream('/reports/exchangeGoods.jasper')))
            LocalDate writeOfDate = exchange?.registrationDateBank?.plusDays(150)
            exchangeParams.put("writeOfDate", writeOfDate)
            exchangeParams.put("sadGoods", listGoods)
        }
        exchangeParams
    }

    def  addGoodsToListGoods(List<Object> temporary_list, listGoods) {
        temporary_list.each { it ->
            if (it != null) {
                listGoods.add(new RimmSadItems(instanceid: it[0], key_item_nbr: it[1], office_code: it[2], office_name: it[3], declaration_date: it[4], declaration_year: it[5], declaration_serial: it[6], declaration_No: it[7], declaration_type: it[8], ide_ast_ser: it[9], ide_ast_nbr: it[10], ide_ast_dat: it[11], sh_code: it[12], vit_stv: it[13], vit_wgt_net: it[14], gds_dsc: it[15], gds_ds3: it[16], vit_cif: it[17]))
            }
        }
    }

    def goodsListByExchangeRegistrationNumberBank(temporary_list, registrationNumberBank) {
        temporary_list.collect { it ->
            if (it[BANK_CODE] && (removeSpace(it[BANK_CODE]) == registrationNumberBank)) {
                return it
            }
        }
    }

    def removeSpace(things) {
        def thing = things?.toString().trim().replaceAll("\\s+", " ").replaceAll("\\u00A0", "").replaceAll("\\s+", "")?.toString()
        StringBuffer buf = new StringBuffer()
        char[] charArray = thing?.toString()?.toCharArray();
        for (char ouput in charArray) {
            if (ouput && !Character.isSpaceChar(ouput)) {
                buf.append(ouput)
            }
        }
        return buf.toString().toUpperCase()
    }

    def setSadInformationsInParams(Exchange exchange, exchangeParams) {
        RimmSadDetails sad
        def invoiceNumber
        def executionDate

        String IS_WEBSERVICE = Holders.config.efemciApplicationConfig.webServiceConfig.useWebService
        if (exchange?.clearanceOfficeCode && exchange?.declarationSerial && exchange?.declarationNumber && exchange?.declarationDate) {
            if (IS_WEBSERVICE == "Y") {
                sad = clientWebService.retrieveSad(exchange)
            } else {
                sad = rimmLoadSadTvfService.retrieveOnesad(exchange.clearanceOfficeCode, exchange.declarationDate, exchange.declarationSerial, exchange?.declarationNumber)
            }
            if (sad) {
                exchangeParams.put("devise", sad?.invoice_currency_code)
                exchangeParams.put("sadIncoterm", sad?.tpt_tod_cod)
                exchangeParams.put("invoice_amount", sad?.vgs_inv_amt_fcx)
                exchangeParams.put("fob", sad?.vgs_cif)
            }
            if (exchange?.requestNo){
                exchangeParams.put("requestNumber", exchange?.requestNo)
            }
            if (!exchange?.attachedDocs.isEmpty()){
                exchange?.attachedDocs?.each() {
                    if (it?.docType in ExchangeRequestType.invoiceCodes) {
                        invoiceNumber = it?.docRef
                    }
                }
                exchangeParams.put("invoiceNumber", invoiceNumber)
            }
            if (!exchange?.executions.isEmpty()) {
                def executions = exchange?.executions.sort { a, b -> a?.rank <=> b?.rank }
                executionDate = executions.get(0)?.executionDate
                exchangeParams.put("executionDate", executionDate)
            }
        }
        exchangeParams
    }

    def retrieveExchangeSadArticlesWithAttachedDocs(exchange){
        RimmSadDetails.withNewSession {
            def query_all = "select items.instanceid as instanceid ,items.key_item_nbr as key_item_nbr,items.office_code as office_code,items.office_name as office_name,items.declaration_date as declaration_date," +
                    "items.declaration_type as declaration_type,items.declaration_year as declaration_year,items.declaration_serial as declaration_serial,items.declaration_No as declaration_No," +
                    "items.ide_ast_ser as ide_ast_ser, items.ide_ast_nbr as ide_ast_nbr, items.ide_ast_dat as ide_ast_dat,items.sh_code as SH_CODE, items.vit_stv as vit_stv, items.vit_wgt_net as vit_wgt_net, " +
                    "items.gds_dsc as gds_dsc, items.gds_ds3 as gds_ds3 , items.vit_cif as vit_cif,att.attached_Document_reference as reference from RimmSadAttdocs att,RimmSadItems items where att.id = items.instanceid " +
                    "and att.key_itm_nbr = items.key_item_nbr and items.office_code=:office_code and items.declaration_date=:declaration_date and items.declaration_serial =:declaration_serial and " +
                    "items.declaration_No =:declaration_No"
            return RimmSadDetails.executeQuery(query_all, [office_code   : exchange?.clearanceOfficeCode, declaration_serial: exchange?.declarationSerial,
                                                           declaration_No: exchange?.declarationNumber, declaration_date: exchange?.declarationDate])
        }
    }
    def resourceAsStream(String resource) {
        return Holders.getGrailsApplication().parentContext.getResource(resource).inputStream
    }
}
