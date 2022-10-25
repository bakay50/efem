<%@ page import="com.webbfontaine.efem.constants.UserProperties; com.webbfontaine.efem.UserUtils;com.webbfontaine.efem.ReferenceService; com.webbfontaine.grails.plugins.utils.TypesCastUtils;"%>
<g:set var="isSingleTin" value="${UserUtils.checkForSingleProp(UserProperties.TIN)}"/>
<g:set var="isSingleAdb" value="${UserUtils.checkForSingleProp(UserProperties.ADB)}"/>

<bootstrap:grid grid="search-4-8">
    <bootstrap:formSection>
        <bootstrap:formGroup name="search.status" groupSpan="12" labelSpan="2" labelCode="repatriation.status.label">
            <bootstrap:formInput span="2">
                <wf:selectOperators from="${referenceService?.getRepatriationStatus()}"
                                    optionKey="key" selected="" optionValue="value" name="status" value="${searchCommand.status}"
                                    noSelection="['': message(code: 'default.all.label', default: 'ALL')]"/>
            </bootstrap:formInput>
            <bootstrap:label labelCode="repatriation.natureOfFund.label" span="2"/>
            <bootstrap:formInput span="2">
                <wf:selectOperators from="${referenceService?.getNatureOfFund()}"
                                    optionKey="key" selected="" optionValue="value" name="natureOfFund" value="${searchCommand.natureOfFund}"
                                    noSelection="['': message(code: 'default.all.label', default: 'ALL')]"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>

        <bootstrap:formGroup name="search.requestNo" groupSpan="12" labelSpan="2" labelCode="repatriation.requestNo.label">
            <bootstrap:formInput span="2">
                <wf:textInput name="requestNo" value="${searchCommand?.requestNo}" class="span2 wfsearchinput"/>
            </bootstrap:formInput>
            <bootstrap:label labelCode="repatriation.requestDate.label" span="2"/>
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

        <bootstrap:formGroup name="search.ecReference" groupSpan="12" labelSpan="2" labelCode="repatriation.ecReference.label">
            <bootstrap:formInput span="2">
                <wf:textInput name="ecReference" value="${searchCommand?.ecReference}" class="span2 wfsearchinput"/>
            </bootstrap:formInput>
            <bootstrap:label labelCode="repatriation.receptionDate.label" span="2"/>
            <bootstrap:formInput span="2">
                <wf:select name="op_receptionDate" value="${searchCommand?.op_receptionDate}" optionKey="key" optionValue="value"
                           onchange="toggleRangeFields('op_receptionDate', 'receptionDate', 'receptionDateTo');" noSelection="['': '']"
                           from="${referenceService?.getOperators(com.webbfontaine.efem.ReferenceService.RANGE_OPERATORS)}"/>
            </bootstrap:formInput>
            <bootstrap:formInput span="2">
                <wf:datepicker name="receptionDate" class="rangeField" value="${TypesCastUtils.formatDate(searchCommand?.receptionDate)}" regexp="${'^[\\d /]+$'}"/>
            </bootstrap:formInput>
            <bootstrap:formInput span="2">
                <wf:datepicker name="receptionDateTo" class="rangeField" value="${TypesCastUtils.formatDate(searchCommand?.receptionDateTo)}" regexp="${'^[\\d /]+$'}"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>
        <bootstrap:formGroup name="search.companyCode" groupSpan="12" labelSpan="2" labelCode="repatriation.companyCode.label">
            <bootstrap:formInput span="2">
                <wf:textInput name="code" value="${isSingleTin ? UserUtils.getUserProperty(UserProperties.TIN) : searchCommand?.code}"
                              class="span2 wfsearchinput" readonly="${isSingleTin ? 'readonly' : null}" onfocusout="${isSingleTin ? "" : "limitFieldsBehavior(this.id,'#importerNameAddress')"}"/>
                <wf:hiddenField name="importerNameAddress"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>

        <bootstrap:formGroup name="search.repatriationBankCodeSearch" groupSpan="12" labelSpan="2" labelCode="repatriation.repatriationBankCodeSearch.label">
            <bootstrap:formInput span="2">
                <wf:textInput name="repatriationBankCode" value="${isSingleAdb ? UserUtils.getUserProperty(UserProperties.ADB) : searchCommand?.repatriationBankCode}"
                              class="span2 wfsearchinput" readonly="${isSingleAdb ? 'readonly' : null}" onfocusout="${isSingleAdb ? "" : "limitFieldsBehavior(this.id,'#repatriationBankName')"}"/>
                <wf:hiddenField name="repatriationBankName"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>
    </bootstrap:formSection>
    <g:render template="/exchange/search/searchButtons"/>
</bootstrap:grid>
