<%@ page import="com.webbfontaine.efem.constants.Statuses" %>
<bootstrap:div id="'clearanceOfDomListContainer">
    <bootstrap:div id="clearDomListContainer" style="clear:both">
        <h4 id="clearanceOfDomListTitle" style="float:left">
            <g:message code="clearanceDom.listOfClearanceDom.label" default="Clearance of domiciliations"/>
        </h4>
    </bootstrap:div>
    <div id="alertInfo" class="alert-container"></div>
    <div id="clearanceListError" class="alert-container"></div>
    <g:render template="/repatriation/clearanceOfDom/clearanceDomInfo" />
    <g:render template="/repatriation/clearanceOfDom/clearanceDomError" />
    <table class="table table-bordered table-striped tableNoWrap table-hover table-top" id="attDocTable">
        <thead>
        <tr>
            <th class="actions-th"></th>
            <input type="hidden" disabled="disabled" style="display:none" id="flagdom" name="flagdom"/>
            <th><g:message code="clearanceDom.rank.label" default="#"/></th>
            <th><g:message code="clearanceDom.ecReference.label" default="EC Reference"/></th>
            <th><g:message code="clearanceDom.ecDate.label" default="EC Date"/></th>
            <th><g:message code="clearanceDom.domiciliaryBank.label" default="Domiciliary Bank"/></th>
            <th><g:message code="clearanceDom.domiciliationNo.label" default="Domiciliation No"/></th>
            <th><g:message code="clearanceDom.domiciliationDate.label" default="Domiciliation Date"/></th>
            <th><g:message code="clearanceDom.dateOfBoarding.label" default="Date of Boarding"/></th>
            <th><g:message code="clearanceDom.domiciliatedAmountInCurr.label"  default="Domiciliated Amount In Currency"/></th>
            <th><g:message code="clearanceDom.invoiceFinalAmountInCurrency.label" default="invoice Final Amount In Currency"/></th>
            <th><g:message code="clearanceDom.repatriatedAmountInCurrency.label" default="Repatriated Amount In Currency"/></th>
        </tr>
        <g:if test="${repatriationInstance.isDocumentEditable()}">
            <tr id="addRow">
                <td><g:if test="${repatriationInstance.isDocumentEditable== true}">
                    <a id="addItemCategory" class="btn btn-default" href="javascript:void(0)" title="Add"
                       onclick="addClearenceOfDom()" ><bootstrap:icon name='plus'/></a>
                </g:if>
                </td>
                <td class="js_totalNumberOfClearance">${repatriationInstance?.clearances?.size() == null ? 0 : repatriationInstance?.clearances?.size()}</td>
                <td><wf:textInput name="ecReference" id="ecReference" style="width:102px;" maxlength="12" onblur="callExchangeReference()"/></td>
                <td><wf:textInput name="ecDate" id="ecDate" readonly="readonly" class="span2"  onchange="updateDateDom(this.value,'edecDate')" /></td>
                <td><wf:textInput name="domiciliaryBank" id="domiciliaryBank" class="span2" disabled="true" maxlength="5" regexp="${"[0-9]"}" regexpForRule="${"[0-9]"}"/></td>

                <td><wf:textInput name="domiciliationNo" id="domiciliationNo" class="span2" disabled="true" maxlength="35" regexp="${"[0-9]"}" regexpForRule="${"[0-9]"}"/></td>
                <td><wf:textInput name="domiciliationDate" id="domiciliationDate" readonly="readonly" class="span2" onchange="updateDateDom(this.value,'eddomiciliationDate')"/></td>
                <td><wf:textInput name="dateOfBoarding" id="dateOfBoarding" readonly="readonly" class="span2" onchange="updateDateDom(this.value,'eddateOfBoarding')"/></td>
                <td><wf:textInput name="domAmtInCurr" id="domAmtInCurr" disabled="true" regexp="${"[0-9]"}" regexpForRule="${"[0-9]"}"/> </td>

                <td><wf:textInput name="invFinalAmtInCurr" id="invFinalAmtInCurr" disabled="true" regexp="${"[0-9,.\\s]"}" regexpForRule="${"[0-9,.\\s]"}"/></td>
                <td><wf:textInput name="repatriatedAmtInCurr" id="repatriatedAmtInCurr" onblur="updateFormatAmount('repatriatedAmtInCurr')" regexp="${"[0-9,.\\s]"}" regexpForRule="${"[0-9,.\\s]"}"/></td>
                <input type="hidden" name="currencyCodeEC" id="currencyCodeEC"/>
            </tr>
        </g:if>
        </thead>
        <tbody id="clearanceDomListBody">
        <g:render template="/repatriation/clearanceOfDom/clearanceDomList" model="[repatriationInstance:repatriationInstance]"/>
        </tbody>
    </table>

    <g:if test="${repatriationInstance?.isDocumentEditable() || repatriationInstance?.status == Statuses.ST_CONFIRMED}">
        <table id="editCleaTable" style="display: none">
            <tr id="editCleaTHeader">
                <td class="nav-pills2 actions-th2">
                    <div class="js_actions">
                        <a id="saveClearanceDom" class="okDoc" href="javascript:void(0)" title="Confirm" onclick=""><bootstrap:icon name='ok'/></a>
                        <a id="cancelClearanceDom" class="cancelDoc" href="javascript:void(0)" title="Cancel" onclick=""><bootstrap:icon name='remove'/></a>
                    </div>
                </td>
                <td class="nav-pills2" id="rankTr"></td>
                <td class="nav-pills2" id="ecReferenceTr">
                    <wf:textInput name="edecReference" id="edecReference" onblur="editCallingExchangeReference()" style="float:left !important;width:102px;" maxlength="12"/>
                </td>

                <td class="nav-pills2" id="ecDateTr">
                    <wf:textInput name="edecDate" id="edecDate" class="span2" maxlength="12" disabled="true" onchange="updateDateDom(this.value,'edecDate')"/>
                </td>
                <td class="nav-pills2" id="domiciliaryBankTr">
                    <wf:textInput name="eddomiciliaryBank" id="eddomiciliaryBank" class="span2" disabled="true" regexp="${"[0-9]"}" regexpForRule="${"[0-9]"}"/>
                </td>
                <td class="nav-pills2" id="domiciliationNoTr">
                    <wf:textInput name="eddomiciliationNo" id="eddomiciliationNo" class="span2" disabled="true" regexp="${"[0-9]"}" regexpForRule="${"[0-9]"}"/>
                </td>

                <td class="nav-pills2" id="domiciliationDateTr">
                    <wf:textInput name="eddomiciliationDate" id="eddomiciliationDate" class="span2" maxlength="12" disabled="true" onchange="updateDateDom(this.value,'eddomiciliationDate')"/>
                </td>

                <td class="nav-pills2" id="dateOfBoardingTr">
                    <wf:textInput name="eddateOfBoarding" id="eddateOfBoarding" class="span2" maxlength="12" disabled="true" onchange="updateDateDom(this.value,'eddateOfBoarding')"/>
                </td>

                <td class="nav-pills2" id="domAmtInCurrTr">
                    <wf:textInput name="eddomAmtInCurr" id="eddomAmtInCurr" disabled="true"
                                  regexp="${"[0-9,.\\s]"}" regexpForRule="${"[0-9,.\\s]"}"/>
                </td>

                <td class="nav-pills2" id="invFinalAmtInCurrTr">
                    <wf:textInput name="edinvFinalAmtInCurr" id="edinvFinalAmtInCurr" disabled="true" regexp="${"[0-9,.\\s]"}" regexpForRule="${"[0-9,.\\s]"}"/>
                </td>

                <td class="nav-pills2" id="repatriatedAmtInCurrTr">
                    <wf:textInput name="edrepatriatedAmtInCurr" id="edrepatriatedAmtInCurr" disabled="${!repatriationInstance?.isDocumentEditable}" onblur="updateFormatAmount('edrepatriatedAmtInCurr')" regexp="${"[0-9,.\\s]"}" regexpForRule="${"[0-9,.\\s]"}"/>
                </td>
            </tr>
        </table>
    </g:if>
    <input type="hidden" name="bankCodeDom" id="bankCodeDom"/>
</bootstrap:div>
