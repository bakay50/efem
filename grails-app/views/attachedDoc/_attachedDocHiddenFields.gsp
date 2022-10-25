<g:hiddenField name="addAttDocUrl" value="${createLink(controller: 'attachedDoc', action: 'addAttDoc')}"/>
<g:hiddenField name="editAttDocUrl" value="${createLink(controller: 'attachedDoc', action: 'editAttDoc', params: [conversationId:params.conversationId])}"/>
<g:hiddenField name="deleteAttDocUrl" value="${createLink(controller: 'attachedDoc', action: 'deleteAttDoc', params: [conversationId:params.conversationId])}"/>
<g:hiddenField name="cancelEditAttDocUrl" value="${createLink(controller: 'attachedDoc', action: 'cancelEditAttDoc', params: [conversationId:params.conversationId])}"/>
<g:hiddenField name="uploadAttDocUrl" value="${createLink(controller: 'attachedDoc', action: 'uploadAttDoc', params: [conversationId:params.conversationId])}"/>