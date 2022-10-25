<%@ page import="com.webbfontaine.efem.workflow.Operation" %>
<bootstrap:div id="'orderClearanceOfDomListContainer">
    <div id="alertInfo"></div>
    <div id="orderClearanceListError" style="clear: left; margin-right:0px;margin-bottom:2px;"></div>
    <g:render template="/transferOrder/orderClearanceOfDom/orderClearanceDomError"/>
    <table class="table table-bordered table-striped tableNoWrap" id="attDocTable">
        <thead>
        <tr>
            <th class="actions-th"></th>
            <input type="hidden" disabled="disabled" style="display:none" id="flagdom" name="flagdom"/>
            <th><g:message code="orderClearanceDom.rank.label" default="#"/></th>
            <th><g:message code="orderClearanceDom.eaReference.label" default="EA Reference"/></th>
            <th><g:message code="orderClearanceDom.authorizationDate.label" default="Domiciliation Date"/></th>
            <th><g:message code="orderClearanceDom.bankName.label" default="Bank Name"/></th>
            <th><g:message code="orderClearanceDom.registrationNoBank.label" default="registration No Bank"/></th>
            <th><g:message code="orderClearanceDom.registrationDateBank.label" default="registration Date Bank"/></th>
            <th><g:message code="orderClearanceDom.amountToBeSettledMentionedCurrency.label"
                           default="Amount to be settled in mentionedCurrency"/></th>
            <th><g:message code="orderClearanceDom.amountRequestedMentionedCurrency.label"
                           default="amountRequestedMentionedCurrency"/></th>
            <th><g:message code="orderClearanceDom.amountSettledMentionedCurrency.label"
                           default="amountSettledMentionedCurrency"/></th>
        </tr>
        <g:if test="${transferInstance?.isEaOpearationsEditable() && transferInstance.startedOperation != Operation.VALIDATE}">

            <tr id="addRow">
                <td><g:if test="${transferInstance?.isEaOpearationsEditable()}">
                    <a id="addClearenceOfDom" class="btn btn-default" href="javascript:void(0)" title="Add"
                       onclick="addOrderClearenceOfDom()"><bootstrap:icon name='plus'/></a>
                </g:if>
                </td>
                <td class="js_totalNumberOfClearance">${transferInstance?.orderClearanceOfDoms?.size() == null ? 0 : transferInstance?.orderClearanceOfDoms?.size()}</td>
                <td><wf:textInput name="eaReference" id="eaReference" style="width:102px;" maxlength="35"
                                  onblur="callExchangeEaReference()"/></td>
                <td><wf:textInput name="authorizationDate" id="authorizationDate" readonly="readonly" class="span2"
                                  onchange="updateDateDom(this.value,'edauthorizationDate')"/></td>
                <td><wf:textInput name="bankName" id="bankName" class="span2" disabled="true" maxlength="5"
                                  regexp="${"[0-9]"}" regexpForRule="${"[0-9]"}"/></td>
                <td><wf:textInput name="registrationNoBank" id="registrationNoBank" class="span2" disabled="true"
                                  maxlength="35" regexp="${"[0-9]"}" regexpForRule="${"[0-9]"}"/></td>
                <td><wf:textInput name="registrationDateBank" id="registrationDateBank" readonly="readonly"
                                  class="span2" onchange="updateDateDom(this.value,'edregistrationDateBank')"/></td>
                <td><wf:textInput name="amountToBeSettledMentionedCurrency" id="amountToBeSettledMentionedCurrency"
                                  disabled="true" regexp="${"[0-9]"}" regexpForRule="${"[0-9]"}"/></td>
                <td><wf:textInput name="amountRequestedMentionedCurrency" id="amountRequestedMentionedCurrency"
                                    readonly="${!transferInstance.isFieldEditable('amountRequestedMentionedCurrency')}"
                                  onblur="updateFormatAmount('amountRequestedMentionedCurrency')"
                                  regexp="${"[0-9,.\\s]"}" regexpForRule="${"[0-9,.\\s]"}"/></td>
                <td><wf:textInput name="amountSettledMentionedCurrency" id="amountSettledMentionedCurrency"
                                    readonly="${!transferInstance.isFieldEditable('amountSettledMentionedCurrency')}"
                                  onblur="updateFormatAmount('amountSettledMentionedCurrency')" regexp="${"[0-9,.\\s]"}"
                                  regexpForRule="${"[0-9,.\\s]"}"/></td>
            </tr>
        </g:if>
        </thead>
        <tbody id="orderClearanceDomListBody">
        <g:render template="/transferOrder/orderClearanceOfDom/orderClearanceDomList"
                  model="[transferInstance: transferInstance]"/>
        </tbody>
    </table>

    <g:if test="${transferInstance?.isEaOpearationsEditable()}">
        <table id="editCleaTable" style="display: none">
            <tr id="editCleaTHeader">
                <td class="nav-pills2 actions-th2">
                    <div class="js_actions">
                        <a id="saveOrderClearanceDom" class="okDoc" href="javascript:void(0)" title="Confirm"
                           onclick=""><bootstrap:icon name='ok'/></a>
                        <a id="cancelOrderClearanceDom" class="cancelDoc" href="javascript:void(0)" title="Cancel"
                           onclick=""><bootstrap:icon name='remove'/></a>
                    </div>
                </td>
                <td class="nav-pills2" id="rankTr"></td>
                <td class="nav-pills2" id="eaReferenceTr">
                    <wf:textInput name="edeaReference" id="edeaReference" onblur="editCallingExchangeReference()" disabled="true"
                                  style="float:left !important;width:102px;" maxlength="12"/>
                </td>

                <td class="nav-pills2" id="authorizationDateTr">
                    <wf:textInput name="edauthorizationDate" id="edauthorizationDate" class="span2" maxlength="12"
                                  disabled="true" onchange="updateDateDom(this.value,'edauthorizationDate')"/>
                </td>
                <td class="nav-pills2" id="domiciliaryBankTr">
                    <wf:textInput name="eddomiciliaryBank" id="eddomiciliaryBank" class="span2" disabled="true"
                                  regexp="${"[0-9]"}" regexpForRule="${"[0-9]"}"/>
                </td>
                <td class="nav-pills2" id="registrationNoBankTr">
                    <wf:textInput name="edregistrationNoBank" id="edregistrationNoBank" class="span2" disabled="true"
                                  regexp="${"[0-9]"}" regexpForRule="${"[0-9]"}"/>
                </td>

                <td class="nav-pills2" id="registrationDateBankTr">
                    <wf:textInput name="edregistrationDateBank" id="edregistrationDateBank" class="span2" maxlength="12"
                                  disabled="true" onchange="updateDateDom(this.value,'edregistrationDateBank')"/>
                </td>
                <td class="nav-pills2" id="amountToBeSettledMentionedCurrencyTr">
                    <wf:textInput name="edamountToBeSettledMentionedCurrency" id="edamountToBeSettledMentionedCurrency"
                                  disabled="true"
                                  regexp="${"[0-9,.\\s]"}" regexpForRule="${"[0-9,.\\s]"}"/>
                </td>
                <td class="nav-pills2" id="amountRequestedMentionedCurrencyTr">
                    <wf:textInput name="edamountRequestedMentionedCurrency" id="edamountRequestedMentionedCurrency"
                                  onblur="updateFormatAmount('edamountRequestedMentionedCurrency')"
                                   regexp="${"[0-9,.\\s]"}" regexpForRule="${"[0-9,.\\s]"}"/>
                </td>
                <td class="nav-pills2" id="amountSettledMentionedCurrencyTr">
                    <wf:textInput name="edamountSettledMentionedCurrency" id="edamountSettledMentionedCurrency"
                                  disabled="${!transferInstance?.isFieldEditable('amountSettledMentionedCurrency')}"
                                  onblur="updateFormatAmount('edamountSettledMentionedCurrency')"
                                  regexp="${"[0-9,.\\s]"}" regexpForRule="${"[0-9,.\\s]"}"/>
                </td>
            </tr>
        </table>
    </g:if>
    <input type="hidden" name="bankOrderCodeDom" id="bankOrderCodeDom"/>
</bootstrap:div>
