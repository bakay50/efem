<wf:hiddenField name="currentEditableAttDocNumber" disabled="disabled" />
<g:each in="${domainInstance?.attachedDocs}" var="attDoc">
    <g:render template="/attachedDoc/attachedDocRecord" model="[attDoc:attDoc, domainInstance:domainInstance,domainType:domainType]"/>
</g:each>