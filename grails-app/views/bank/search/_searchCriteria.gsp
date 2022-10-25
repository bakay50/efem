<%@ page import="com.webbfontaine.efem.BusinessLogicUtils; com.webbfontaine.grails.plugins.utils.TypesCastUtils; grails.util.Holders" %>
<bootstrap:grid grid="search-4-8">
    <bootstrap:formSection>
        <bootstrap:formGroup groupSpan="8" labelSpan="3" default="Code" labelCode="bank.code.label">
            <bootstrap:formInput span="4">
                <wf:textInput   onPaste="return false;"  id="codeBankSearch" name="code"  maxlength="35" value="${searchCommand?.code}" />
            </bootstrap:formInput>
        </bootstrap:formGroup>

        <bootstrap:formGroup groupSpan="8" labelSpan="3" labelCode="bank.dateOfValidity.label">
            <bootstrap:formInput span="3">
                <wf:select name="op_dov" value="${searchCommand?.op_dov}" optionKey="key"
                           optionValue="value"
                           onchange="toggleRangeFields('op_dov', 'dateOfValidity', 'dovTo');"
                           noSelection="['': '']"
                           from="${referenceService?.getOperators(com.webbfontaine.efem.ReferenceService.RANGE_OPERATORS)}"/>
            </bootstrap:formInput>
            <bootstrap:formInput span="3">
                <wf:datepicker  id="dateOfValidity" name="dateOfValidity" class="rangeField" value="${TypesCastUtils.formatDateTime(searchCommand?.dateOfValidity)}"
                    dateformat="${Holders.config.jodatime.format.org.joda.time.LocalDateTime}"
                                regexp="${'^[\\d /]+$'}"/>
            </bootstrap:formInput>
            <bootstrap:formInput span="3">
                <wf:datepicker  id="dovTo" name="dovTo" class="rangeField"
                               value="${TypesCastUtils.formatDateTime(searchCommand?.dovTo)}" dateformat="${Holders.config.jodatime.format.org.joda.time.LocalDateTime}"
                               regexp="${'^[\\d /]+$'}"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>
        <bootstrap:formGroup groupSpan="8" labelSpan="3" labelCode="bank.status.label">
            <bootstrap:formInput span="4">
                <wf:selectOperators from="${referenceService?.getReferenceStatus()}"
                                    optionKey="key" selected="" optionValue="value" name="status"
                                    value="${searchCommand.status}"
                                    noSelection="['': message(code: 'default.all.label', default: 'ALL')]"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>
    </bootstrap:formSection>
    <wf:searchButtons clear="resetCriteriaFields()" />

</bootstrap:grid>
