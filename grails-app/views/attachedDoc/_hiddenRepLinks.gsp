<wf:hiddenField name="addAttDocUrl" value="${createLink(controller: 'attachedDoc', action: 'addAttDoc',params="[domainType:'repatriation']")}" />
<wf:hiddenField name="updateAttDocURL" value="${createLink(controller: 'attachedDoc', action: 'updateAttDoc',params="[domainType:'repatriation']")}"/>
<wf:hiddenField name="deleteAttDocUrl" value="${createLink(controller: 'attachedDoc', action: 'deleteAttDoc', params: [conversationId:params.conversationId,domainType:'repatriation'])}"/>
<wf:hiddenField name="cancelEditAttDocUrl" value="${createLink(controller: 'attachedDoc', action: 'cancelEditAttDoc', params: [conversationId:params.conversationId,domainType:'repatriation'])}"/>
<wf:hiddenField name="uploadAttDocUrl" value="${createLink(controller: 'attachedDoc', action: 'uploadAttDoc', params: [conversationId:params.conversationId,domainType:'repatriation'])}"/>
<wf:hiddenField name="checkMaxXofUrl" value="${createLink(controller: 'exchange', action: 'checkMaxAmountInXofCurrency')}"/>
<wf:hiddenField name="attachmentAcceptedFormats" value="${grailsApplication.config.attachmentAcceptedFormats.join(',')}"/>
