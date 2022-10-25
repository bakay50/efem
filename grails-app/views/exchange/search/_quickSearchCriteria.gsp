<%@ page import="com.webbfontaine.efem.constants.UserProperties; com.webbfontaine.efem.UserUtils;com.webbfontaine.efem.ReferenceService; com.webbfontaine.grails.plugins.utils.TypesCastUtils; com.webbfontaine.efem.constant.SearchTab"%>

<g:set var="isSingleAdb" value="${UserUtils.checkForSingleProp(UserProperties.ADB)}"/>
<g:set var="isSingleTin" value="${UserUtils.checkForSingleProp(UserProperties.TIN)}"/>
<g:set var="isSingleDec" value="${UserUtils.checkForSingleProp(UserProperties.DEC)}"/>

<bootstrap:form>
    <bootstrap:formSection>
        <wf:hiddenField id="searchTab" name="searchTab" value="${SearchTab.QUICK.name()}"/>
        <bootstrap:formGroup name="search.status" groupSpan="12" labelSpan="2" labelCode="exchange.status.label">
            <bootstrap:formInput span="2">
                <wf:selectOperators from="${referenceService?.getExchangeStatus()}"
                                    optionKey="key" selected="" optionValue="value" name="status" value="${searchCommand.status}"
                                    noSelection="['': message(code: 'default.all.label', default: 'ALL')]"/>
            </bootstrap:formInput>
            <bootstrap:label labelCode="exchange.requestType.label" span="2"/>
            <bootstrap:formInput span="2">
                <wf:selectOperators from="${referenceService?.getExchangeRequestType()}"
                                    optionKey="key" selected="" optionValue="value" name="requestType" value="${searchCommand.requestType}"
                                    noSelection="['': message(code: 'default.all.label', default: 'ALL')]"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>
        <bootstrap:formGroup name="search.requestNo" groupSpan="12" labelSpan="2" labelCode="exchange.requestNo.label">
            <bootstrap:formInput span="2">
                <wf:textInput name="requestNo" value="${searchCommand?.requestNo}" class="span2 wfsearchinput"/>
            </bootstrap:formInput>
            <bootstrap:label labelCode="exchange.importerCodeSearch.label" span="2"/>
            <bootstrap:formInput span="2">
                <wf:textInput name="importerCode" id="importerCodeQuick" value="${isSingleTin ? UserUtils.getUserProperty(UserProperties.TIN) : searchCommand?.importerCode}"
                              class="span2 wfsearchinput" readonly="${isSingleTin ? 'readonly' : null}" onfocusout="${isSingleTin ? "" : "limitFieldsBehavior(this.id,'#importerQuickName')"}"/>
                <wf:hiddenField name="importerQuickName"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>
        <bootstrap:formGroup name="search.bankCode" groupSpan="12" labelSpan="2" labelCode="exchange.bankCode.label">
            <bootstrap:formInput span="2">
                <wf:textInput name="bankCode" id="bankCodeQuick" value="${isSingleAdb ? UserUtils.getUserProperty(UserProperties.ADB) : searchCommand?.bankCode}"
                              class="span2 wfsearchinput" readonly="${isSingleAdb ? "readonly" : null}" onfocusout="${isSingleAdb ? "" : "limitFieldsBehavior(this.id,'#bankQuickName')"}"/>
                <wf:hiddenField name="bankQuickName"/>
            </bootstrap:formInput>
            <bootstrap:label labelCode="exchanges.declarantCode.label" span="2"/>
            <bootstrap:formInput span="2">
                <wf:textInput name="declarantCode" id="declarantCodeQuick" value="${isSingleDec ? UserUtils.getUserProperty(UserProperties.DEC) : searchCommand?.declarantCode}"
                              class="span2 wfsearchinput" readonly="${isSingleDec ? 'readonly' : null}" onfocusout="${isSingleDec ? "" : "limitFieldsBehavior(this.id,'#declarantQuickName')"}"/>
                <wf:hiddenField name="declarantQuickName"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>

        <bootstrap:formGroup name="search.treatmentLevel" groupSpan="12" labelSpan="2" labelCode="exchange.search.treatmentLevel.label">
            <bootstrap:formInput span="2">
                <wf:selectOperators from="${referenceService?.getTreatmentLevel()}"
                                    optionKey="key" selected="" optionValue="value" name="treatmentLevel"
                                    value="${searchCommand.treatmentLevel}"
                                    noSelection="['': message(code: 'default.all.label', default: 'ALL')]"/>
            </bootstrap:formInput>
            <bootstrap:label labelCode="exchange.attachedDocs.docRef.label" span="2"/>
            <bootstrap:formInput span="2">
                <wf:textInput name="docRef" value="${searchCommand?.docRef}" class="span2 wfsearchinput"/>
            </bootstrap:formInput>

        </bootstrap:formGroup>

        <bootstrap:formGroup name="search.bankApprovalDate" groupSpan="12" labelSpan="2"
                             labelCode="exchange.bankApprovalDate.label">
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
    </bootstrap:formSection>
    <g:render template="/exchange/search/searchButtons"/>
</bootstrap:form>


