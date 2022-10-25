<%@ page import="com.webbfontaine.efem.constants.Statuses" %>
<bootstrap:div id="clearanceOfDomListContainer">
    <bootstrap:div id="clearDomListContainer">
        <h4 id="clearanceOfDomListTitle" class="float-left">
            <g:message code="clearanceDom.listOfClearanceDom.label" default="Clearance of domiciliations"/>
        </h4>
    </bootstrap:div>
    <div id="alertInfo"></div>
    <div id="clearanceListError" class="clearance-error"></div>
    <g:render template="/currencyTransfer/clearanceDoms/clearanceDomError"/>
    <table class="table table-bordered table-striped tableNoWrap">
        <thead>
            <tr>
                <th class="actions-th"></th>
                <input type="hidden" disabled="disabled" class="display-none" id="flagdom" name="flagdom"/>
                <th><g:message code="clearanceDom.rank.label" default="#"/></th>
                <th><g:message code="clearanceDom.ecReference.label" default="EC Reference"/></th>
                <th><g:message code="clearanceDom.ecDate.label" default="EC Date"/></th>
                <th><g:message code="clearanceDom.ecExporterName.label" default="Exporter Name"/></th>
                <th><g:message code="clearanceDom.domiciliaryBank.label" default="Domiciliary Bank"/></th>
                <th><g:message code="clearanceDom.domiciliationNo.label" default="Domiciliation No"/></th>
                <th><g:message code="clearanceDom.domiciliationDate.label" default="Domiciliation Date"/></th>
                <th><g:message code="clearanceDom.domiciliatedAmountInCurr.label"  default="Domiciliated Amount In Currency"/></th>
                <th><g:message code="clearanceDom.invoiceFinalAmountInCurrency.label" default="invoice Final Amount In Currency"/></th>
                <th><g:message code="clearanceDom.repatriatedAmountInCurrency.label" default="Repatriated Amount In Currency"/></th>
                <th><g:message code="clearanceDom.AmountTransferInCurrency.label" default="Amount Transferred in Currency"/></th>
            </tr>
        </thead>
        <tbody id="clearanceDomListBody">
            <g:render template="/currencyTransfer/clearanceDoms/clearanceDomList" model="[currencyTransferInstance:currencyTransferInstance, clearenceOfDomList:clearenceOfDomList]"/>
        </tbody>
    </table>

    <g:if test="${currencyTransferInstance?.isDocumentEditable()}">
        <table id="editCleaTable" class="display-none">
            <tr id="editCleaTHeader">
                <td class="nav-pills2 actions-th2">
                    <div class="js_actions">
                        <a id="saveClearanceDom" class="okDoc" href="javascript:void(0)" title="Confirm" onclick=""><bootstrap:icon name='ok'/></a>
                        <a id="cancelClearanceDom" class="cancelDoc" href="javascript:void(0)" title="Cancel" onclick=""><bootstrap:icon name='remove'/></a>
                    </div>
                </td>
                <td class="nav-pills2" id="rankTr"></td>
                <td class="nav-pills2" id="ecReferenceTr">
                    <wf:textInput name="edecReference" id="edecReference" onblur="editCallingExchangeReference()" readonly="readonly" class="float-left w-102" maxlength="12"/>
                </td>

                <td class="nav-pills2" id="ecDateTr">
                    <wf:textInput name="edecDate" id="edecDate" class="span2" maxlength="12" disabled="true" onchange="updateDateDom(this.value,'edecDate')"/>
                </td>

                <td class="nav-pills2" id="ecExporterNameTr">
                    <wf:textInput name="edecExporterName" id="edecExporterName" class="span2" disabled="true" regexp="${"[0-9]"}" regexpForRule="${"[0-9]"}" />
                </td>

                <td class="nav-pills2" id="domiciliationCodeBankTr">
                    <wf:textInput name="eddomiciliationCodeBank" id="eddomiciliationCodeBank" class="span2" disabled="true" regexp="${"[0-9]"}" regexpForRule="${"[0-9]"}"/>
                </td>

                <td class="nav-pills2" id="domiciliationNoTr">
                    <wf:textInput name="eddomiciliationNo" id="eddomiciliationNo" class="span2" disabled="true" regexp="${"[0-9]"}" regexpForRule="${"[0-9]"}"/>
                </td>

                <td class="nav-pills2" id="domiciliationDateTr">
                    <wf:textInput name="eddomiciliationDate" id="eddomiciliationDate" class="span2" maxlength="12" disabled="true" onchange="updateDateDom(this.value,'eddomiciliationDate')"/>
                </td>

                <td class="nav-pills2" id="domiciliatedAmounttInCurrTr">
                    <wf:textInput name="eddomiciliatedAmounttInCurr" id="eddomiciliatedAmounttInCurr" disabled="true"
                                  regexp="${"[0-9,.\\s]"}" regexpForRule="${"[0-9,.\\s]"}"/>
                </td>

                <td class="nav-pills2" id="invoiceFinalAmountInCurrTr">
                    <wf:textInput name="edinvoiceFinalAmountInCurr" id="edinvoiceFinalAmountInCurr" disabled="true" regexp="${"[0-9,.\\s]"}" regexpForRule="${"[0-9,.\\s]"}"/>
                </td>
                <td class="nav-pills2" id="repatriatedAmountToBankTr">
                    <wf:textInput name="edrepatriatedAmountToBank" id="edrepatriatedAmountToBank" disabled="true" regexp="${"[0-9,.\\s]"}" regexpForRule="${"[0-9,.\\s]"}"/>
                </td>

                <td class="nav-pills2" id="amountTransferredInCurrTr">
                    <wf:textInput name="edamountTransferredInCurr" id="edamountTransferredInCurr" readonly="${!currencyTransferInstance?.isFieldEditable('amountTransferredInCurr')}" onblur="updateFormatAmount('edamountTransferredInCurr')" regexp="${"[0-9,.\\s]"}" regexpForRule="${"[0-9,.\\s]"}"/>
                </td>
            </tr>
        </table>
    </g:if>
</bootstrap:div>
