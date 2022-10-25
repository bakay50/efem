<%@ page import=" com.webbfontaine.efem.UserUtils; com.webbfontaine.efem.constants.Statuses; com.webbfontaine.efem.BusinessLogicUtils; com.webbfontaine.efem.security.Roles;" %>
<g:set var="isAuthAvailable" value="${UserUtils.isBankAgent()}"/>
<g:hiddenField name="authAvailable" value="${isAuthAvailable}"/>
<bootstrap:div id="headerTransferContainer">
    <bootstrap:formSection labelCode="transferOrder.registrattionNumber.title.label">
        <bootstrap:genericInput id="requestNo" name="requestNo" maxlength="11" field="requestNo" bean="${transferInstance}"
                                labelCode="transferOrder.requestNo.label"/>
        <bootstrap:genericInput id="requestDate" field="requestDate" bean="${transferInstance}" type="date"
                                regexp="${'^[\\d /]+$'}" onkeydown="return false;"
                                labelCode="transferOrder.requestDate.label"/>
    </bootstrap:formSection>
    <bootstrap:formSection labelCode="transferOrder.exporterDetails.label">
        <bootstrap:genericInput field="importerCode" bean="${transferInstance}"
                                labelCode="transferOrder.importerCode.label" maxlength="17"/>
        <bootstrap:genericInput field="importerNameAddress" bean="${transferInstance}" maxlength="4000" type="area"
                                inputSpan="4" rows="3"
                                labelCode="transferOrder.exporterNameAddress.label"/>
    </bootstrap:formSection>
    <bootstrap:formSection>
        <bootstrap:genericInput field="countryBenefBankCode" bean="${transferInstance}"
                                labelCode="transferOrder.countryBenefBankCode.label"
                                labelField="countryBenefBankName" maxlength="2"/>

        <bootstrap:genericInput field="destinationBank" bean="${transferInstance}"
                                labelCode="transferOrder.destinationBank.label" maxlength="100"/>

        <bootstrap:genericInput field="byCreditOfAccntOfCorsp" id="byCreditOfAccntOfCorsp" bean="${transferInstance}"
                                type="select"
                                noSelection="['': '']" name="byCreditOfAccntOfCorsp"
                                optionKey="key" optionValue="value"
                                from="${referenceService.getByCreditOfAccountSelection()}"
                                labelCode="transferOrder.byCreditOfAccntOfCorsp.label"/>

        <bootstrap:genericInput field="bankAccntNoCredit" bean="${transferInstance}"
                                labelCode="transferOrder.bankAccntNoCredit.label"
                                maxlength="34"/>


        <bootstrap:genericInput field="nameOfAccntHoldCredit" bean="${transferInstance}"
                                labelCode="transferOrder.nameOfAccntHoldCredit.label"
                                maxlength="100"/>

    </bootstrap:formSection>
    <bootstrap:formSection>
        <bootstrap:genericInput field="bankCode" bean="${transferInstance}"
                                labelCode="transferOrder.bankCode.label"
                                labelField="bankName" maxlength="5"/>

        <bootstrap:genericInput field="bankAccntNoDebited" bean="${transferInstance}"
                                labelCode="transferOrder.bankAccntNoDebited.label"
                                maxlength="34"/>

        <bootstrap:genericInput field="charges" id="charges" bean="${transferInstance}" type="select"
                                noSelection="['': '']" name="charges"
                                optionKey="key" optionValue="value" maxlength="65"
                                from="${referenceService.getListOfCharges()}"
                                labelCode="transferOrder.charges.label"/>

    </bootstrap:formSection>
    <bootstrap:formSection>
        <bootstrap:genericInput field="currencyPayCode" bean="${transferInstance}"
                                onchange="retrieveTransferCurrencyRate()"
                                labelCode="transferOrder.currencyCodePay.label" maxlength="3"
                                labelField="currencyPayName"/>
        <bootstrap:genericInput field="ratePayment" bean="${transferInstance}"
                                labelCode="transferOrder.ratePayment.label"/>

        <bootstrap:genericInput field="executionRef" bean="${transferInstance}"
                                labelCode="transferOrder.executionRef.label"
                                maxlength="35" />

        <bootstrap:genericInput field="executionDate" autoComplete="off" bean="${transferInstance}" type="date"
                                regexp="${'^[\\d /]+$'}" onkeydown="return false;" maxDate="0"
                                labelCode="transferOrder.executionDate.label" />
    </bootstrap:formSection>
    <bootstrap:formSection labelCode="transferOrder.listeEA.title.label">
        <bootstrap:formGroup labelCode="transferOrder.transferAmntRequested.label">
            <bootstrap:formInput span="3">
                <wf:genericInput field="transferAmntRequested" bean="${transferInstance}" type="monetary"
                                 class="exchangeMonetary"/>
            </bootstrap:formInput>

            <bootstrap:formInput span="3">
                <wf:genericInput field="transferNatAmntRequest" bean="${transferInstance}" type="monetary"
                                 class="exchangeMonetary"/>
                <label id="xof" class="add-on"><g:message code="exchange.xof.label" default="XOF"/></label>
            </bootstrap:formInput>

        </bootstrap:formGroup>

        <bootstrap:formGroup>
            <bootstrap:formInput span="6">
                <bootstrap:label id="digitoperation">
                    ${transferInstance?.amntRequestedInLetters}
                </bootstrap:label>
                <g:hiddenField name="amountInLetter" value="${transferInstance?.amntRequestedInLetters}"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>

        <bootstrap:formGroup labelCode="transferOrder.transferAmntExecuted.label">
            <bootstrap:formInput span="3">
                <wf:genericInput field="transferAmntExecuted" bean="${transferInstance}" type="monetary"
                                 class="exchangeMonetary"/>
            </bootstrap:formInput>
            <bootstrap:formInput span="3">
                <wf:genericInput field="transferNatAmntExecuted" bean="${transferInstance}" type="monetary"
                                 readonly="readonly" class="exchangeMonetary"/>
                <label id="xof" class="add-on"><g:message code="exchange.xof.label" default="XOF"/></label>
            </bootstrap:formInput>
        </bootstrap:formGroup>

        <bootstrap:formGroup>
            <bootstrap:formInput span="6">
                <bootstrap:label id="finalAmountInWords">
                    ${transferInstance?.amntExecutedInLetters}
                </bootstrap:label>
                <g:hiddenField name="amntExecutedInLetters" value="${transferInstance?.amntExecutedInLetters}"/>
            </bootstrap:formInput>
        </bootstrap:formGroup>
        <bootstrap:div id="listOfOrderClearanceDomsBody">
            <g:render template="/transferOrder/tabs/listOfOrderClearanceDoms"
                      model="[transferInstance: transferInstance]"/>
        </bootstrap:div>
    </bootstrap:formSection>

</bootstrap:div>
