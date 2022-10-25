package efemci

import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j

@Slf4j("LOGGER")
@Transactional
class RimmValueCheckService {

    private static Date removeTimeOnDateToday() {
        Calendar calendar = Calendar.instance
        Date today = calendar.time.clearTime()
        return today
    }
    private def loadClass(klass) {
        def classLoader = new GroovyClassLoader(this.class.classLoader)
        return classLoader.loadClass("$klass")
    }

    @Transactional(readOnly = true)
     def checkOrRetriveInstance(klass, codeField, criteriaValue,field,htDate = null,returnType){
         LOGGER.info("in checkOrRetriveInstance() of ${RimmValueCheckService}");
         def result = null
         def criteria = klass.createCriteria()
         LOGGER.info("executing query for $klass.simpleName with criteria: $criteriaValue field :  $field codeField : $codeField htDate:$htDate returnType :$returnType")
         if(field){
             result = applyCriteriaForFields(criteria,field,criteriaValue,codeField)
         }else{
             if (!htDate) {
                 htDate = removeTimeOnDateToday()
             }
             result = applyCriteriaForCheckingOrRetrieveInformation(criteria,htDate,klass,codeField,criteriaValue,returnType)
         }
         return result
     }

      def applyCriteriaForFields(criteria,field,criteriaValue,codeField){
          LOGGER.info("in applyCriteriaForFields() of ${RimmValueCheckService}");
          def result = criteria.get() {
              if (field=="bankCode"){
                  isNotNull("bnk_eml")
              }
              eq("${codeField}", criteriaValue)
          }
          if (field=="bankCode") {
              result= result.collect {
                  if (it.bnk_eml.trim()!="")
                      it
              }
          }
          result.removeAll([null])
          if (result) {
              return true
          }
          LOGGER.debug("no results found")
          return false
      }

      def applyCriteriaForCheckingOrRetrieveInformation(criteria,htDate,klass,codeField,criteriaValue,returnType){
          LOGGER.info("in applyCriteriaForCheckingOrRetrieveInformation() of ${RimmValueCheckService}");
          def beanModel = loadClass("$klass.name")
          LOGGER.info("executing query for $klass.simpleName with criteria: $criteriaValue")
          def result = criteria.get() {

              eq("${codeField}", criteriaValue)

              if (beanModel.getDeclaredFields().find { it.name.equals('dateOfValidity') }) {
                  and {
                      or {
                          and {
                              le('dateOfValidity',htDate)
                              isNull('endOfValidity')
                          }
                          and {
                              le('dateOfValidity',htDate)
                              isNotNull('endOfValidity')
                              gt('endOfValidity',htDate)
                          }
                      }
                  }
              }
          }

          if(returnType == "Boolean"){
                  return result?true:false
          }else if(returnType == "String"){
                  return result?result?."$nameField":null
          }else if(returnType == "Instance") {
                  return result?result:null
          }else {
                  return result
          }

      }
}
