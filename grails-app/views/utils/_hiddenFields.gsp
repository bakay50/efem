<%@ page import="com.webbfontaine.efem.BusinessLogicUtils; org.springframework.web.servlet.support.RequestContextUtils; com.webbfontaine.efem.AppConfig;com.webbfontaine.efem.constants.ExchangeRequestType; com.webbfontaine.efem.UserUtils;" %>

%{--Hidden Fields--}%
<g:hiddenField name="locale" value="${RequestContextUtils.getLocale(request)}"/>
<g:hiddenField name="basedOn" value="${exchangeInstance?.basedOn}"/>
<g:hiddenField name="requestType" value="${exchangeInstance?.requestType}"/>
<g:hiddenField name="finexApproval" value="false"/>
<wf:hiddenField id="isGovOfficer" name="isGovOfficer" value="${UserUtils.isGovOfficer()}"/>
<wf:hiddenField id="isBankAgent" name="isBankAgent" value="${UserUtils.isBankAgent()}"/>
<wf:hiddenField id="isTrader" name="isTrader" value="${UserUtils.isTrader()}"/>
<wf:hiddenField id="isDeclarant" name="isDeclarant" value="${UserUtils.isDeclarant()}"/>
<wf:hiddenField id="requestTypeEC" name="requestTypeEC" value="${ExchangeRequestType.EC}"/>
<wf:hiddenField id="operTypWithExportTitle" name="operTypWithExportTitle" value="${ExchangeRequestType.OPERATION_WITH_EXPORT_TITLE}"/>
<wf:hiddenField id="startedOperation" name="startedOperation" value="${exchangeInstance.startedOperation}" />
<g:render template="/utils/notificationProperties" />
<wf:hiddenField id="js_allow_finex_approval" name="js_allow_finex_approval" value="${AppConfig.allowFinexApprovalEA()}" />

%{--Hidden links--}%
<g:hiddenField name="restartSadUrl" value="${createLink(controller: "exchange", action: "create", params: [conversationId: params.conversationId, requestType: ExchangeRequestType.EA, basedOn: ExchangeRequestType.BASE_ON_SAD])}"/>
<g:hiddenField name="restartTvfUrl" value="${createLink(controller: "exchange", action: "create", params: [conversationId: params.conversationId, requestType: ExchangeRequestType.EA, basedOn: ExchangeRequestType.BASE_ON_TVF])}"/>
<g:hiddenField name="loadTvfUrl" value="${createLink(controller: "exchange", action: "loadTvf", params: [conversationId: params.conversationId, op: params.op])}"/>
<g:hiddenField name="loadSadUrl" value="${createLink(controller: "exchange", action: "loadSad", params: [conversationId: params.conversationId, op: params.op])}"/>
<g:hiddenField name="convertDigitUrl" value="${createLink(controller: 'exchange', action: 'convertDigitToLetter')}"/>
<g:hiddenField name="convertGeoAreaNameUrl" value="${createLink(controller: 'exchange', action: 'convertGeoAreaName')}"/>
<g:hiddenField name="retrieveCurrencyRateUrl" value="${createLink(controller: 'exchange', action: 'retrieveCurrencyRateUrl', params: [conversationId:params.conversationId])}"/>
<g:hiddenField name="convertDigitExUrl" value="${createLink(controller: 'execution', action: 'convertDigitToLettre')}"/>
<g:hiddenField name="addExecutionUrl" value="${createLink(controller: 'execution', action: 'addExecution', params: [conversationId:params.conversationId])}"/>
<g:hiddenField name="saveExecutionUrl" value="${createLink(controller: 'execution', action: 'saveExecution', params: [conversationId:params.conversationId])}"/>
<g:hiddenField name="editExecutionUrl" value="${createLink(controller: 'execution', action: 'editExecution', params: [conversationId:params.conversationId])}"/>
<g:hiddenField name="deleteExecutionUrl" value="${createLink(controller: 'execution', action: 'deleteExecution', params: [conversationId:params.conversationId])}"/>
<g:hiddenField name="removeExecutionUrl" value="${createLink(controller: 'execution', action: 'removeExecution', params: [conversationId:params.conversationId])}"/>
<g:hiddenField name="loadExecutionUrl" value="${createLink(controller: 'execution', action: 'loadExecution', params: [conversationId:params.conversationId])}"/>
<g:hiddenField name="cancelExecutionUrl" value="${createLink(controller: 'execution', action: 'cancelExecution', params: [conversationId:params.conversationId])}"/>

<g:hiddenField name="addSupDeclarationUrl" value="${createLink(controller: 'supDeclaration', action: 'addSupDeclaration', params: [conversationId:params.conversationId])}"/>
<g:hiddenField name="editSupDeclarationUrl" value="${createLink(controller: 'supDeclaration', action: 'editSupDeclaration', params: [conversationId:params.conversationId])}"/>
<g:hiddenField name="cancelEditSupDeclarationUrl" value="${createLink(controller: 'supDeclaration', action: 'cancelEditSupDeclaration')}"/>
<g:hiddenField name="deleteSupDeclarationUrl" value="${createLink(controller: 'supDeclaration', action: 'deleteSupDeclaration', params: [conversationId:params.conversationId])}"/>

<g:hiddenField name="canDisplayRemainingBalanceTransfer" value="${AppConfig.handleDisplayFieldsRemainingBalanceTransfer()}" />

<g:hiddenField name="showTvfUrl" value="${grailsApplication.config.rest.tvf.show.url}"/>
<g:hiddenField name="showSadUrl" value="${grailsApplication.config.rest.sad.show.url}"/>
<g:hiddenField name="loadDeclarationUrl" value="${createLink(controller: "exchange", action: "loadDeclaration")}"/>

%{--Hidden Config for js--}%
<g:hiddenField name="js_EaDisabledFields" value="${grailsApplication.config.efemAllowedOfficeCode.code_office}"/>
<g:hiddenField name="js_EnableCheckingFields" value="${grailsApplication.config.efemAllowedCheckingCurrencyCode.enableCheckingCurrencyCode}"/>
<g:hiddenField name="js_EnableCheckingRequestDate" value="${grailsApplication.config.efemAllowedCheckingCurrencyCode.enableByPasscheckingByRequestDate.enableByPassing}"/>
<g:hiddenField name="js_ListCountryOfDest" value="${AppConfig.listCountryOfDestination}"/>
<g:hiddenField name="js_GeoArea1" value="${AppConfig.geoArea1}"/>
<g:hiddenField name="js_GeoArea2" value="${AppConfig.geoArea2}"/>
<g:hiddenField name="js_GeoArea3" value="${AppConfig.geoArea3}"/>
<g:hiddenField name="js_CI_value" value="${ExchangeRequestType.CI}"/>

%{--esad details--}%
<g:hiddenField name="sadInvoiceAmountInForeignCurrency" value="${exchangeInstance?.sadInvoiceAmountInForeignCurrency}"/>
<g:hiddenField name="sadStatus" value="${exchangeInstance?.sadStatus}"/>
<g:hiddenField name="sadTypeOfDeclaration" value="${exchangeInstance?.sadTypeOfDeclaration}"/>

<g:hiddenField name="isDomiciliationOptional" value="${BusinessLogicUtils.isDomiciliationRequired(exchangeInstance?.amountNationalCurrency)}"/>
<g:hiddenField name="op" disabled="" value="${params?.op}"/>
<g:hiddenField name="instanceId" disabled="" value="${exchangeInstance?.id}"/>
<wf:hiddenField name="verifyURL" value="${createLink(controller: 'exchange', action: 'verify')}"/>

<wf:hiddenField name="exportXmlUrl" value="${createLink(controller: 'exchange', action: 'exportXML')}"/>
<wf:hiddenField name="importXmlUrl" value="${createLink(controller: 'exchange', action: 'importXML')}"/>

