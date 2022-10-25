<%@ page import="com.webbfontaine.efem.security.Roles; com.webbfontaine.efem.AppConfig" %>

<bootstrap:formSection>
    <bootstrap:div id="declarationListContainer">

        <bootstrap:formSection>
            <strong>
                <bootstrap:label labelCode="dec.listOfDec.label" default="Import confirmation"
                                 style="margin: 2rem 0"/>
            </strong>

            <bootstrap:formGroup name="exchange.declaration.amount" groupSpan="12" labelSpan="3"
                                 labelCode="dec.remainingBalanceDeclaredAmount.label">
                <bootstrap:formInput span="3">
                    <wf:genericInput class="exchangeMonetary" field="remainingBalanceDeclaredAmount"
                                     bean="${exchangeInstance}"
                                     type="monetary"
                                     readonly="readonly"/>
                    <wf:textInput class="short-input" name="decXof" value="${exchangeInstance?.currencyPayCode}" readonly="readonly"/>
                </bootstrap:formInput>

                <bootstrap:formInput span="3">
                    <wf:genericInput class="exchangeMonetary" field="remainingBalanceDeclaredNatAmount"
                                     bean="${exchangeInstance}" type="monetary"
                                     readonly="readonly"/>
                    <wf:textInput class="short-input" name="decXof" value="${g.message(code: 'exchange.xof.label')}" readonly="readonly"/>
                </bootstrap:formInput>
            </bootstrap:formGroup>

            <g:if test="${AppConfig.handleDisplayFieldsRemainingBalanceTransfer()}">
                <bootstrap:formGroup name="exchange.declaration.transfer.amount" groupSpan="12" labelSpan="3"
                                     labelCode="dec.remainingBalanceTransferDoneAmount.label">
                    <bootstrap:formInput span="3">
                        <wf:genericInput class="exchangeMonetary" field="remainingBalanceTransferDoneAmount"
                                         bean="${exchangeInstance}"
                                         type="monetary"
                                         readonly="readonly"/>
                        <wf:textInput class="short-input" name="decXof" value="${exchangeInstance?.currencyPayCode}" readonly="readonly"/>
                    </bootstrap:formInput>

                    <bootstrap:formInput span="3">
                        <wf:genericInput class="exchangeMonetary" field="remainingBalanceTransferDoneNatAmount"
                                         bean="${exchangeInstance}"
                                         type="monetary" readonly="readonly"/>
                        <wf:textInput class="short-input" name="decXof" value="${g.message(code: 'exchange.xof.label')}" readonly="readonly"/>
                    </bootstrap:formInput>
                </bootstrap:formGroup>
            </g:if>
        </bootstrap:formSection>
        <bootstrap:div id="supDeclarationListError"></bootstrap:div>
        <table class="table table-bordered table-striped tableNoWrap disable-container" id="itemsTable">
            <thead>
            <tr>
                <th></th>
                <input type="hidden" disabled="disabled" style="display:none" id="flagdom" name="flagdom"/>
                <th class="span1 listRank"><g:message code="dec.rank.label" default="Rank"/></th>
                <th colspan="2"><g:message code="dec.clearanceOfficeCode.label"
                                           default="Clearance Office Code"/></th>
                <th><g:message code="dec.declarationSerial.label" default="Declaration Serial"/></th>
                <th><g:message code="dec.declarationNumber.label" default="Declaration No"/></th>
                <th><g:message code="dec.declarationDate.label" default="Declaration Date"/></th>
                <th><g:message code="dec.declarationCifAmount.label" default="Declaration CIF Amount in mentioned currency"/></th>
                <th><g:message code="dec.declarationRemainingBalance.label" default="Remaining Balance in mentioned currency"/></th>
                <th><g:message code="dec.declarationAmountWriteOff.label" default="Amount to be written-off in mentioned currency"/></th>
            </tr>
            <sec:ifAnyGranted roles="${Roles.TRADER.authority}, ${Roles.DECLARANT.authority}">
                <tr id="supDeclarationAddRow">
                    <td>
                        <g:if test="${exchangeInstance?.isSupDeclarationEditable()}">
                            <bootstrap:linkButton icon="plus" id="addSupDeclaration" class=""
                                                  onclick="addSupDeclaration(event)" href="javascript: void(0)"/>
                        </g:if>
                    </td>
                    <td></td>
                    <td>
                        <wf:textInput id="sdClearanceOfficeCode" name="sdClearanceOfficeCode" value="" class="span1" />

                    </td>
                    <td><label id="sdClearanceOfficeName" name="sdClearanceOfficeName"></label></td>
                    <td><wf:textInputInUppercase id="sdDeclarationSerial" name="sdDeclarationSerial" value=""
                                                 class="span1"/></td>
                    <td><wf:textInput id="sdDeclarationNumber" name="sdDeclarationNumber" value=""/></td>
                    <td><wf:datepicker name="sdDeclarationDate" maxDate="0"
                                       onchange="updateDateSupDec(this.value, 'edSdDeclarationDate')"
                                       dateformat="${grails.util.Holders.config.bootstrapDate}"
                                       regexp="${'^[\\d /]+$'}"
                                       onkeydown="return false;"/></td>
                    <td><wf:textInput id="declarationCifAmount" name="declarationCifAmount" value=""
                                      readonly="readonly" regexp="${"[0-9,.\\s]"}" regexpForRule="${"[0-9,.\\s]"}"/></td>
                    <td><wf:textInput id="declarationRemainingBalance" name="declarationRemainingBalance" value=""
                                      readonly="readonly" regexp="${"[0-9,.\\s]"}" regexpForRule="${"[0-9,.\\s]"}"/></td>
                    <td><wf:textInput id="declarationAmountWriteOff" name="declarationAmountWriteOff" value=""
                                      regexp="${"[0-9,.\\s]"}" regexpForRule="${"[0-9,.\\s]"}"/></td>
                </tr>
            </sec:ifAnyGranted>
            </thead>
            <tbody id="declarationListTableBody">
            <g:render template="/exchange/templates/declarationList"
                      model="[exchangeInstance: exchangeInstance, supDeclarationlist: exchangeInstance.supDeclarations]"/>
            </tbody>
        </table>

        <table id="editItemsTable" class="display-none">
            <tr id="editItemsHeader">
                <td class="nav-pills2 actions-th2">
                    <div class="js_actions">
                        <a id="saveSupDeclaration" class="okDoc" href="javascript:void(0)" title="Confirm"
                           onclick=""><bootstrap:icon name='ok'/></a>
                        <a id="cancelSupDeclaration" class="cancelDoc" href="javascript:void(0)" title="Cancel"
                           onclick=""><bootstrap:icon name='remove'/></a>
                    </div>
                </td>
                <td class="nav-pills2" id="rankTr"></td>
                <td class="nav-pills2" id="clearanceOfficeCodeTr">
                    <wf:textInput id="edSdClearanceOfficeCode" name="edSdClearanceOfficeCode" value="" class="span1" />

                </td>
                <td class="nav-pills2" id="clearanceOfficeNameTr">
                    <label id="edSdClearanceOfficeName" name="edSdClearanceOfficeName"></label>
                </td>
                <td class="nav-pills2" id="declarationSerialTr">
                    <wf:textInputInUppercase id="edSdDeclarationSerial" name="edSdDeclarationSerial" value=""
                                             class="span1"/>
                </td>
                <td class="nav-pills2" id="declarationNumberTr">
                    <wf:textInput id="edSdDeclarationNumber" name="edSdDeclarationNumber" value=""/>
                </td>
                <td class="nav-pills2" id="declarationDateTr">
                    <wf:datepicker name="edSdDeclarationDate" maxDate="0"
                                   dateformat="${grails.util.Holders.config.bootstrapDate}" regexp="${'^[\\d /]+$'}"
                                   onkeydown="return false;"/>
                </td>
                <td class="nav-pills2" id="declarationCifAmountTr">
                    <wf:textInput id="eddeclarationCifAmount" name="eddeclarationCifAmount" value=""
                                  readonly="readonly" regexp="${"[0-9,.\\s]"}" regexpForRule="${"[0-9,.\\s]"}"/>
                </td>
                <td class="nav-pills2" id="declarationRemainingBalanceTr">
                    <wf:textInput id="eddeclarationRemainingBalance" name="eddeclarationRemainingBalance" value=""
                                  readonly="readonly" regexp="${"[0-9,.\\s]"}" regexpForRule="${"[0-9,.\\s]"}"/>
                </td>
                <td class="nav-pills2" id="declarationAmountWriteOffTr">
                    <wf:textInput id="eddeclarationAmountWriteOff" name="eddeclarationAmountWriteOff" value=""
                                  regexp="${"[0-9,.\\s]"}" regexpForRule="${"[0-9,.\\s]"}"/>
                </td>
            </tr>
        </table>
    </bootstrap:div>
</bootstrap:formSection>