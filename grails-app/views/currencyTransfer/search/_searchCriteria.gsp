<%@ page import="com.webbfontaine.efem.constants.UserProperties; com.webbfontaine.efem.UserUtils;com.webbfontaine.efem.ReferenceService; com.webbfontaine.grails.plugins.utils.TypesCastUtils;"%>
<g:set var="isSingleAdb" value="${UserUtils.checkForSingleProp(UserProperties.ADB)}"/>

<bootstrap:grid grid="search-4-8">
    <bootstrap:formSection>
        <bootstrap:formGroup name="search.status" groupSpan="12" labelSpan="2" labelCode="currencyTransfer.status.label">
            <bootstrap:formInput span="2">
                <wf:selectOperators from="${referenceService?.getCurrencyTransferStatus()}"
                                    optionKey="key" selected="" optionValue="value" name="status" value="${searchCommand.status}"
                                    noSelection="['': message(code: 'default.all.label', default: 'ALL')]"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>

        <bootstrap:formGroup name="search.requestNo" groupSpan="12" labelSpan="2" labelCode="currencyTransfer.requestNo.label">
            <bootstrap:formInput span="2">
                <wf:textInput name="requestNo" value="${searchCommand?.requestNo}" class="span2 wfsearchinput"/>
            </bootstrap:formInput>
            <bootstrap:label labelCode="currencyTransfer.requestDate.label" span="2"/>
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

        <bootstrap:formGroup name="search.bankCode" groupSpan="12" labelSpan="2" labelCode="currencyTransfer.bankCode.label">
            <bootstrap:formInput span="2">
                <wf:textInput name="bankCode" value="${isSingleAdb ? UserUtils.getUserProperty(UserProperties.ADB) : searchCommand?.bankCode}"
                              class="span2 wfsearchinput" readonly="${isSingleAdb ? 'readonly' : null}" onfocusout="${isSingleAdb ? "" : "limitFieldsBehavior(this.id,'#bankName')"}"/>
                <wf:hiddenField name="bankName"/>
            </bootstrap:formInput>
            <bootstrap:label labelCode="currencyTransfer.currencyTransferDate.label" span="2"/>
            <bootstrap:formInput span="2">
                <wf:select name="op_currencyTransferDate" value="${searchCommand?.op_currencyTransferDate}" optionKey="key" optionValue="value"
                           onchange="toggleRangeFields('op_currencyTransferDate', 'currencyTransferDate', 'currencyTransferDateTo');" noSelection="['': '']"
                           from="${referenceService?.getOperators(com.webbfontaine.efem.ReferenceService.RANGE_OPERATORS)}"/>
            </bootstrap:formInput>
            <bootstrap:formInput span="2">
                <wf:datepicker name="currencyTransferDate" class="rangeField" value="${TypesCastUtils.formatDate(searchCommand?.currencyTransferDate)}" regexp="${'^[\\d /]+$'}"/>
            </bootstrap:formInput>
            <bootstrap:formInput span="2">
                <wf:datepicker name="currencyTransferDateTo" class="rangeField" value="${TypesCastUtils.formatDate(searchCommand?.currencyTransferDateTo)}" regexp="${'^[\\d /]+$'}"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>

        <bootstrap:formGroup name="search.ecReference" groupSpan="12" labelSpan="2" labelCode="currencyTransfer.ecReference.label">
                    <bootstrap:formInput span="2">
                        <wf:textInput name="ecReference" value="${searchCommand?.ecReference}" class="span2 wfsearchinput"/>
                    </bootstrap:formInput>

            <bootstrap:label labelCode="currencyTransfer.ecDate.label" span="2"/>
            <bootstrap:formInput span="2">
                <wf:select name="op_ecDate" value="${searchCommand?.op_ecDate}" optionKey="key" optionValue="value"
                           onchange="toggleRangeFields('op_ecDate', 'ecDate', 'ecDateTo');" noSelection="['': '']"
                           from="${referenceService?.getOperators(com.webbfontaine.efem.ReferenceService.RANGE_OPERATORS)}"/>
            </bootstrap:formInput>
            <bootstrap:formInput span="2">
                <wf:datepicker name="ecDate" class="rangeField" value="${TypesCastUtils.formatDate(searchCommand?.ecDate)}" regexp="${'^[\\d /]+$'}"/>
            </bootstrap:formInput>
            <bootstrap:formInput span="2">
                <wf:datepicker name="ecDateTo" class="rangeField" value="${TypesCastUtils.formatDate(searchCommand?.ecDateTo)}" regexp="${'^[\\d /]+$'}"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>

        <bootstrap:formGroup name="search.currencyCode" groupSpan="12" labelSpan="2" labelCode="currencyTransfer.currencyCode.label">
            <bootstrap:formInput span="2">
                <wf:textInput name="currencyCode" value="${searchCommand?.currencyCode}"
                              class="span2 wfsearchinput"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>

    </bootstrap:formSection>
    <g:render template="/currencyTransfer/search/searchButtons"/>
</bootstrap:grid>
