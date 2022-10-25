<%@ page import="com.webbfontaine.efem.constants.UserProperties; com.webbfontaine.efem.UserUtils;com.webbfontaine.efem.ReferenceService; com.webbfontaine.grails.plugins.utils.TypesCastUtils;com.webbfontaine.efem.constant.SearchTab" %>

<g:set var="isSingleAdb" value="${UserUtils.checkForSingleProp(UserProperties.ADB)}"/>
<g:set var="isSingleTin" value="${UserUtils.checkForSingleProp(UserProperties.TIN)}"/>
<g:set var="isSingleDec" value="${UserUtils.checkForSingleProp(UserProperties.DEC)}"/>

<bootstrap:form name="advancedSearchForm" method="get"
                url="[action: 'search', controller: 'exchange']">
    <bootstrap:formSection>
        <wf:hiddenField id="searchTab" name="searchTab" value="${SearchTab.ADVANCED.name()}"/>
        <bootstrap:formGroup name="search.requestType" groupSpan="12" labelSpan="2"
                             labelCode="exchange.requestType.label">
            <bootstrap:formInput span="2">
                <wf:selectOperators from="${referenceService?.getExchangeRequestType()}"
                                    optionKey="key" selected="" optionValue="value" name="requestType"
                                    value="${searchCommand.requestType}"
                                    noSelection="['': message(code: 'default.all.label', default: 'ALL')]"/>
            </bootstrap:formInput>
            <bootstrap:label labelCode="exchange.status.label" span="2"/>
            <bootstrap:formInput span="2">
                <wf:selectOperators from="${referenceService?.getExchangeStatus()}"
                                    optionKey="key" selected="" optionValue="value" name="status"
                                    value="${searchCommand.status}"
                                    noSelection="['': message(code: 'default.all.label', default: 'ALL')]"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>

        <bootstrap:formGroup name="search.basedOn" groupSpan="12" labelSpan="2" labelCode="exchange.basedOn.label">
            <bootstrap:formInput span="2">
                <wf:selectOperators from="${referenceService?.getBaseOn()}"
                                    optionKey="key" selected="" optionValue="value" name="basedOn"
                                    value="${searchCommand.basedOn}"
                                    noSelection="['': message(code: 'default.all.label', default: 'ALL')]"/>
            </bootstrap:formInput>
            <bootstrap:label labelCode="exchange.tvfNumber.label" span="2"/>
            <bootstrap:formInput span="2">
                <wf:textInput name="tvfNumber" value="${searchCommand?.tvfNumber}" class="span2 wfsearchinput"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>

        <bootstrap:formGroup name="search.departmentInCharge" groupSpan="12" labelSpan="2"
                             labelCode="exchange.departmentInCharge.label">
            <bootstrap:formInput span="2">
                <wf:selectOperators from="${referenceService?.getDepartmentInCharge()}"
                                    optionKey="key" selected="" optionValue="value" name="departmentInCharge"
                                    value="${searchCommand.departmentInCharge}"
                                    noSelection="['': message(code: 'default.all.label', default: 'ALL')]"/>
            </bootstrap:formInput>
            <bootstrap:label labelCode="exchange.geographicalArea.label" span="2"/>
            <bootstrap:formInput span="2">
                <wf:textInput name="geoArea" value="${searchCommand?.geoArea}" class="span2 wfsearchinput"
                              onfocusout="limitFieldsBehavior(this.id,'#geoAreaName')"/>
                <wf:hiddenField name="geoAreaName"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>

        <bootstrap:formGroup name="search.clearanceOfficeCode" groupSpan="12" labelSpan="2"
                             labelCode="exchange.clearanceOfficeCode.label">
            <bootstrap:formInput span="2">
                <wf:textInput name="clearanceOfficeCode" value="${searchCommand?.clearanceOfficeCode}"
                              class="span2 wfsearchinput"
                              onfocusout="limitFieldsBehavior(this.id,'#clearanceOfficeName')"/>
                <wf:hiddenField name="clearanceOfficeName"/>
            </bootstrap:formInput>
            <bootstrap:label labelCode="exchange.requestNo.label" span="2"/>
            <bootstrap:formInput span="2">
                <wf:textInput name="requestNo" value="${searchCommand?.requestNo}" class="span2 wfsearchinput"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>

        <bootstrap:formGroup name="search.domiciliationBankCode" groupSpan="12" labelSpan="2"
                             labelCode="exchange.domiciliationBankCode.label">
            <bootstrap:formInput span="2">
                <wf:textInput name="domiciliationBankCode" value="${searchCommand?.domiciliationBankCode}"
                              class="span2 wfsearchinput"
                              onfocusout="limitFieldsBehavior(this.id,'#domiciliationBankName')"/>
                <wf:hiddenField name="domiciliationBankName"/>
            </bootstrap:formInput>
            <bootstrap:label labelCode="exchange.srch.amountNationalCurrency.label" span="2"/>
            <bootstrap:formInput span="2">
                <wf:select name="op_amountNationalCurrency" value="${searchCommand?.op_amountNationalCurrency}"
                           optionKey="key" optionValue="value"
                           onchange="toggleRangeFields('op_amountNationalCurrency', 'amountNationalCurrency', 'amountNationalCurrencyTo');"
                           noSelection="['': '']"
                           from="${referenceService?.getOperators(com.webbfontaine.efem.ReferenceService.RANGE_OPERATORS)}"/>
            </bootstrap:formInput>
            <bootstrap:formInput span="2">
                <wf:textInput name="amountNationalCurrency" class="rangeField" value="${searchCommand?.amountNationalCurrency}"/>
            </bootstrap:formInput>
            <bootstrap:formInput span="2">
                <wf:textInput name="amountNationalCurrencyTo" class="rangeField" value="${searchCommand?.amountNationalCurrencyTo}"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>

        <bootstrap:formGroup name="search.declarationNumber" groupSpan="12" labelSpan="2"
                             labelCode="exchange.declarationNumber.label">
            <bootstrap:formInput span="2">
                <wf:textInput name="declarationNumber" value="${searchCommand?.declarationNumber}"
                              class="span2 wfsearchinput"/>
            </bootstrap:formInput>
            <bootstrap:label labelCode="exchange.declarationDate.label" span="2"/>
            <bootstrap:formInput span="2">
                <wf:select name="op_declarationDate" value="${searchCommand?.op_declarationDate}" optionKey="key"
                           optionValue="value"
                           onchange="toggleRangeFields('op_declarationDate', 'declarationDate', 'declarationDateTo');"
                           noSelection="['': '']"
                           from="${referenceService?.getOperators(com.webbfontaine.efem.ReferenceService.RANGE_OPERATORS)}"/>
            </bootstrap:formInput>
            <bootstrap:formInput span="2">
                <wf:datepicker name="declarationDate" class="rangeField"
                               value="${TypesCastUtils.formatDate(searchCommand?.declarationDate)}"
                               regexp="${'^[\\d /]+$'}"/>
            </bootstrap:formInput>
            <bootstrap:formInput span="2">
                <wf:datepicker name="declarationDateTo" class="rangeField"
                               value="${TypesCastUtils.formatDate(searchCommand?.declarationDateTo)}"
                               regexp="${'^[\\d /]+$'}"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>

        <bootstrap:formGroup name="search.registrationNumberBank" groupSpan="12" labelSpan="2"
                             labelCode="exchange.registrationNumberBank.label">
            <bootstrap:formInput span="2">
                <wf:textInput name="registrationNumberBank" value="${searchCommand?.registrationNumberBank}"
                              class="span2 wfsearchinput"/>
            </bootstrap:formInput>
            <bootstrap:label labelCode="exchange.registrationDateBank.label" span="2"/>
            <bootstrap:formInput span="2">
                <wf:select name="op_registrationDateBank" value="${searchCommand?.op_registrationDateBank}"
                           optionKey="key" optionValue="value"
                           onchange="toggleRangeFields('op_registrationDateBank', 'registrationDateBank', 'registrationDateBankTo');"
                           noSelection="['': '']"
                           from="${referenceService?.getOperators(com.webbfontaine.efem.ReferenceService.RANGE_OPERATORS)}"/>
            </bootstrap:formInput>
            <bootstrap:formInput span="2">
                <wf:datepicker name="registrationDateBank" class="rangeField"
                               value="${TypesCastUtils.formatDate(searchCommand?.registrationDateBank)}"
                               regexp="${'^[\\d /]+$'}"/>
            </bootstrap:formInput>
            <bootstrap:formInput span="2">
                <wf:datepicker name="registrationDateBankTo" class="rangeField"
                               value="${TypesCastUtils.formatDate(searchCommand?.registrationDateBankTo)}"
                               regexp="${'^[\\d /]+$'}"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>

        <bootstrap:formGroup name="search.domiciliationNumber" groupSpan="12" labelSpan="2"
                             labelCode="exchange.domiciliationNumber.label">
            <bootstrap:formInput span="2">
                <wf:textInput name="domiciliationNumber" value="${searchCommand?.domiciliationNumber}"
                              class="span2 wfsearchinput"/>
            </bootstrap:formInput>
            <bootstrap:label labelCode="exchange.domiciliationDate.label" span="2"/>
            <bootstrap:formInput span="2">
                <wf:select name="op_domiciliationDate" value="${searchCommand?.op_domiciliationDate}" optionKey="key"
                           optionValue="value"
                           onchange="toggleRangeFields('op_domiciliationDate', 'domiciliationDate', 'domiciliationDateTo');"
                           noSelection="['': '']"
                           from="${referenceService?.getOperators(com.webbfontaine.efem.ReferenceService.RANGE_OPERATORS)}"/>
            </bootstrap:formInput>
            <bootstrap:formInput span="2">
                <wf:datepicker name="domiciliationDate" class="rangeField"
                               value="${TypesCastUtils.formatDate(searchCommand?.domiciliationDate)}"
                               regexp="${'^[\\d /]+$'}"/>
            </bootstrap:formInput>
            <bootstrap:formInput span="2">
                <wf:datepicker name="domiciliationDateTo" class="rangeField"
                               value="${TypesCastUtils.formatDate(searchCommand?.domiciliationDateTo)}"
                               regexp="${'^[\\d /]+$'}"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>

        <bootstrap:formGroup name="search.bankCode" groupSpan="12" labelSpan="2" labelCode="exchange.bankCode.label">
            <bootstrap:formInput span="2">
                <wf:textInput name="bankCode"
                              value="${isSingleAdb ? UserUtils.getUserProperty(UserProperties.ADB) : searchCommand?.bankCode}"
                              class="span2 wfsearchinput" readonly="${isSingleAdb ? "readonly" : null}"
                              onfocusout="${isSingleAdb ? "" : "limitFieldsBehavior(this.id,'#bankName')"}"/>
                <wf:hiddenField name="bankName"/>
            </bootstrap:formInput>
            <bootstrap:label labelCode="exchange.tvfDate.label" span="2"/>
            <bootstrap:formInput span="2">
                <wf:select name="op_tvfDate" value="${searchCommand?.op_tvfDate}" optionKey="key" optionValue="value"
                           onchange="toggleRangeFields('op_tvfDate', 'tvfDate', 'tvfDateTo');" noSelection="['': '']"
                           from="${referenceService?.getOperators(com.webbfontaine.efem.ReferenceService.RANGE_OPERATORS)}"/>
            </bootstrap:formInput>
            <bootstrap:formInput span="2">
                <wf:datepicker name="tvfDate" class="rangeField"
                               value="${TypesCastUtils.formatDate(searchCommand?.tvfDate)}" regexp="${'^[\\d /]+$'}"/>
            </bootstrap:formInput>
            <bootstrap:formInput span="2">
                <wf:datepicker name="tvfDateTo" class="rangeField"
                               value="${TypesCastUtils.formatDate(searchCommand?.tvfDateTo)}" regexp="${'^[\\d /]+$'}"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>

        <bootstrap:formGroup name="search.declarantCode" groupSpan="12" labelSpan="2"
                             labelCode="exchanges.declarantCode.label">
            <bootstrap:formInput span="2">
                <wf:textInput name="declarantCode" id="declarantCodeAdvanced"
                              value="${isSingleDec ? UserUtils.getUserProperty(UserProperties.DEC) : searchCommand?.declarantCode}"
                              class="span2 wfsearchinput" readonly="${isSingleDec ? 'readonly' : null}"
                              onfocusout="${isSingleDec ? "" : "limitFieldsBehavior(this.id,'#declarantAdvancedName')"}"/>
                <wf:hiddenField name="declarantAdvancedName"/>
            </bootstrap:formInput>
            <bootstrap:label labelCode="exchange.authorizationDate.label" span="2"/>
            <bootstrap:formInput span="2">
                <wf:select name="op_authorizationDate" value="${searchCommand?.op_authorizationDate}" optionKey="key"
                           optionValue="value"
                           onchange="toggleRangeFields('op_authorizationDate', 'authorizationDate', 'authorizationDateTo');"
                           noSelection="['': '']"
                           from="${referenceService?.getOperators(com.webbfontaine.efem.ReferenceService.RANGE_OPERATORS)}"/>
            </bootstrap:formInput>
            <bootstrap:formInput span="2">
                <wf:datepicker name="authorizationDate" class="rangeField"
                               value="${TypesCastUtils.formatDate(searchCommand?.authorizationDate)}"
                               regexp="${'^[\\d /]+$'}"/>
            </bootstrap:formInput>
            <bootstrap:formInput span="2">
                <wf:datepicker name="authorizationDateTo" class="rangeField"
                               value="${TypesCastUtils.formatDate(searchCommand?.authorizationDateTo)}"
                               regexp="${'^[\\d /]+$'}"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>

        <bootstrap:formGroup name="search.declarationSerial" groupSpan="12" labelSpan="2"
                             labelCode="exchange.declarationSerial.label">
            <bootstrap:formInput span="2">
                <wf:textInput name="declarationSerial" value="${searchCommand?.declarationSerial}"
                              class="span2 wfsearchinput text-uppercase" regexp="${'[a-zA-Z]'}" maxlength="1"/>
            </bootstrap:formInput>
            <bootstrap:label labelCode="exchange.importerCodeSearch.label" span="2"/>
            <bootstrap:formInput span="2">
                <wf:textInput name="importerCode"
                              value="${isSingleTin ? UserUtils.getUserProperty(UserProperties.TIN) : searchCommand?.importerCode}"
                              class="span2 wfsearchinput" readonly="${isSingleTin ? 'readonly' : null}"
                              onfocusout="${isSingleTin ? "" : "limitFieldsBehavior(this.id,'#importerNameAddress')"}"/>
                <wf:hiddenField name="importerNameAddress"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>

        <bootstrap:formGroup name="search.balanceAs" groupSpan="12" labelSpan="2" labelCode="exchange.balanceAs.label">
            <bootstrap:formInput span="2">
                <wf:selectOperators from="${referenceService?.getBalance()}"
                                    optionKey="key" selected="" optionValue="value" name="balanceAs"
                                    value="${searchCommand.balanceAs}"
                                    noSelection="['': message(code: 'default.all.label', default: 'ALL')]"/>
            </bootstrap:formInput>
            <bootstrap:label labelCode="exchange.srch.countryProvenanceDestinationCode.label" span="2"/>
            <bootstrap:formInput span="2">
                <wf:textInput name="countryProvenanceDestinationCode"
                              value="${searchCommand?.countryProvenanceDestinationCode}"
                              class="span2 wfsearchinput"
                              onfocusout="limitFieldsBehavior(this.id,'#countryProvenanceDestinationName')"/>
                <wf:hiddenField name="countryProvenanceDestinationName"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>

        <bootstrap:formGroup name="search.currencyCode" groupSpan="12" labelSpan="2"
                             labelCode="exchange.currencyCode.label">
            <bootstrap:formInput span="2">
                <wf:textInput name="currencyCode" value="${searchCommand?.currencyCode}" class="span2 wfsearchinput"
                              onfocusout="limitFieldsBehavior(this.id,'#currencyName')"/>
                <wf:hiddenField name="currencyName"/>
            </bootstrap:formInput>
            <bootstrap:label labelCode="exchange.operType.label" span="2"/>
            <bootstrap:formInput span="2">
                <wf:textInput name="operType" value="${searchCommand?.operType}" class="span2 wfsearchinput"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>

        <bootstrap:formGroup name="search.treatmentLevel" groupSpan="12" labelSpan="2"
                             labelCode="exchange.search.treatmentLevel.label">
            <bootstrap:formInput span="2">
                <wf:selectOperators from="${referenceService?.getTreatmentLevel()}"
                                    optionKey="key" selected="" optionValue="value" name="treatmentLevel"
                                    value="${searchCommand.treatmentLevel}"
                                    noSelection="['': message(code: 'default.all.label', default: 'ALL')]"/>
            </bootstrap:formInput>
            <bootstrap:label labelCode="exchange.bankApprovalDate.label" span="2"/>
            <bootstrap:formInput span="2">
                <wf:select name="op_bankApprovalDate" value="${searchCommand?.op_bankApprovalDate}" optionKey="key"
                           optionValue="value"
                           onchange="toggleRangeFields('op_bankApprovalDate', 'bankApprovalDate', 'bankApprovalDateTo');"
                           noSelection="['': '']"
                           from="${referenceService?.getOperators(com.webbfontaine.efem.ReferenceService.RANGE_OPERATORS)}"/>
            </bootstrap:formInput>
            <bootstrap:formInput span="2">
                <wf:datepicker name="bankApprovalDate" class="rangeField"
                               value="${TypesCastUtils.formatDate(searchCommand?.bankApprovalDate)}"
                               regexp="${'^[\\d /]+$'}"/>
            </bootstrap:formInput>
            <bootstrap:formInput span="2">
                <wf:datepicker name="bankApprovalDateTo" class="rangeField"
                               value="${TypesCastUtils.formatDate(searchCommand?.bankApprovalDateTo)}"
                               regexp="${'^[\\d /]+$'}"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>

        <bootstrap:formGroup name="search.executions" groupSpan="12" labelSpan="2"
                             labelCode="exec.executingBankCode.label">
            <bootstrap:formInput span="2">
                <wf:textInput name="executingBankCode" value="${searchCommand?.executingBankCode}"
                              class="span2 wfsearchinput"
                              onfocusout="limitFieldsBehavior(this.id,'#executingBankName')"/>
                <wf:hiddenField name="executingBankName"/>
            </bootstrap:formInput>

            <bootstrap:label labelCode="exchange.requestDate.label" span="2"/>
            <bootstrap:formInput span="2">
                <wf:select name="op_requestDate" value="${searchCommand?.op_requestDate}" optionKey="key"
                           optionValue="value"
                           onchange="toggleRangeFields('op_requestDate', 'requestDate', 'requestDateTo');"
                           noSelection="['': '']"
                           from="${referenceService?.getOperators(com.webbfontaine.efem.ReferenceService.RANGE_OPERATORS)}"/>
            </bootstrap:formInput>
            <bootstrap:formInput span="2">
                <wf:datepicker name="requestDate" class="rangeField"
                               value="${TypesCastUtils.formatDate(searchCommand?.requestDate)}"
                               regexp="${'^[\\d /]+$'}"/>
            </bootstrap:formInput>
            <bootstrap:formInput span="2">
                <wf:datepicker name="requestDateTo" class="rangeField"
                               value="${TypesCastUtils.formatDate(searchCommand?.requestDateTo)}"
                               regexp="${'^[\\d /]+$'}"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>

        <bootstrap:formGroup name="search.docRef" groupSpan="12" labelSpan="2" labelCode="exchange.attachedDocs.docRef.label">
            <bootstrap:formInput span="2">
                <wf:textInput name="docRef" value="${searchCommand?.docRef}" class="span2 wfsearchinput"/>
            </bootstrap:formInput>

        </bootstrap:formGroup>
    </bootstrap:formSection>
    <g:render template="/exchange/search/searchButtons"/>
</bootstrap:form>
