<%@ page import="com.webbfontaine.efem.constants.UserProperties; com.webbfontaine.efem.UserUtils;com.webbfontaine.efem.ReferenceService; com.webbfontaine.grails.plugins.utils.TypesCastUtils;"%>
<g:set var="isSingleTin" value="${UserUtils.checkForSingleProp(UserProperties.TIN)}"/>
<g:set var="isSingleAdb" value="${UserUtils.checkForSingleProp(UserProperties.ADB)}"/>

<bootstrap:grid grid="search-4-8">
    <bootstrap:formSection>
        <bootstrap:formGroup name="search.status" groupSpan="12" labelSpan="2" labelCode="transferOrder.status.label">
            <bootstrap:formInput span="2">
                <wf:selectOperators from="${referenceService?.getTransferOrderStatus()}"
                                    optionKey="key" selected="" optionValue="value" name="status" value="${searchCommand.status}"
                                    noSelection="['': message(code: 'default.all.label', default: 'ALL')]"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>

        <bootstrap:formGroup name="search.requestNo" groupSpan="12" labelSpan="2" labelCode="transferOrder.requestNo.label">
            <bootstrap:formInput span="2">
                <wf:textInput name="requestNo" value="${searchCommand?.requestNo}" class="span2 wfsearchinput"/>
            </bootstrap:formInput>
            <bootstrap:label labelCode="transferOrder.requestDate.label" span="2"/>
            <bootstrap:formInput span="2">
                <wf:select name="op_requestDate" value="${searchCommand?.op_requestDate}" optionKey="key" optionValue="value"
                           onchange="toggleRangeFields('op_requestDate', 'requestDate', 'requestDateTo');" noSelection="['': '']"
                           from="${referenceService?.getOperators(com.webbfontaine.efem.ReferenceService.RANGE_OPERATORS)}"/>
            </bootstrap:formInput>
            <bootstrap:formInput span="2">
                <wf:datepicker name="requestDate" class="rangeField" value="${TypesCastUtils.formatDate(searchCommand?.requestDate)}" regexp="${'^[\\d /]+$'}"/>
            </bootstrap:formInput>
            <bootstrap:formInput span="2">
                <wf:datepicker name="requestDateTo" class="rangeField" value="${TypesCastUtils.formatDate(searchCommand?.requestDateTo)}" regexp="${'^[\\d /]+$'}"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>

        <bootstrap:formGroup name="search.executionRef" groupSpan="12" labelSpan="2" labelCode="transferOrder.executionRef.label">
            <bootstrap:formInput span="2">
                <wf:textInput name="executionRef" value="${searchCommand?.executionRef}" class="span2 wfsearchinput"/>
            </bootstrap:formInput>
            <bootstrap:label labelCode="transferOrder.executionDate.label" span="2"/>
            <bootstrap:formInput span="2">
                <wf:select name="op_executionDate" value="${searchCommand?.op_executionDate}" optionKey="key" optionValue="value"
                           onchange="toggleRangeFields('op_executionDate', 'executionDate', 'executionDateTo');" noSelection="['': '']"
                           from="${referenceService?.getOperators(com.webbfontaine.efem.ReferenceService.RANGE_OPERATORS)}"/>
            </bootstrap:formInput>
            <bootstrap:formInput span="2">
                <wf:datepicker name="executionDate" class="rangeField" value="${TypesCastUtils.formatDate(searchCommand?.executionDate)}" regexp="${'^[\\d /]+$'}"/>
            </bootstrap:formInput>
            <bootstrap:formInput span="2">
                <wf:datepicker name="executionDateTo" class="rangeField" value="${TypesCastUtils.formatDate(searchCommand?.executionDateTo)}" regexp="${'^[\\d /]+$'}"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>

        <bootstrap:formGroup name="search.companyCode" groupSpan="12" labelSpan="2" labelCode="transferOrder.importerCode.label">
            <bootstrap:formInput span="2">
                <wf:textInput name="importerCode" value="${isSingleTin ? UserUtils.getUserProperty(UserProperties.TIN) : searchCommand?.importerCode}"
                              class="span2 wfsearchinput" readonly="${isSingleTin ? 'readonly' : null}" onfocusout="${isSingleTin ? "" : "limitFieldsBehavior(this.id,'#importerNameAddress')"}"/>
                <wf:hiddenField name="importerNameAddress"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>
        <bootstrap:formGroup name="search.eaReference" groupSpan="12" labelSpan="2" labelCode="transferOrder.eaReference.searchLabel">
            <bootstrap:formInput span="2">
                <wf:textInput name="eaReference" value="${searchCommand?.eaReference}" class="span2 wfsearchinput"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>
        <bootstrap:formGroup name="search.bankCode" groupSpan="12" labelSpan="2" labelCode="transferOrder.bankCode.label">
            <bootstrap:formInput span="2">
                <wf:textInput name="bankCode" value="${isSingleAdb ? UserUtils.getUserProperty(UserProperties.ADB) : searchCommand?.bankCode}"
                              class="span2 wfsearchinput" readonly="${isSingleAdb ? 'readonly' : null}" onfocusout="${isSingleAdb ? "" : "limitFieldsBehavior(this.id,'#bankName')"}"/>
                <wf:hiddenField name="bankName"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>
    </bootstrap:formSection>
    <g:render template="/exchange/search/searchButtons"/>
</bootstrap:grid>
