<%@ page import="com.webbfontaine.efem.constants.Statuses" %>
<g:set var="i" value="${attDoc?.rank}"/>
<g:set var="attachmentEditable" value="${domainInstance.isAttachmentEditable()}"/>
<g:set var="isNewDoc" value="${!attDoc?.id}"/>
<g:set var="isExchangeAtGoodStatus" value="${domainType.toString().equalsIgnoreCase("exchange") && !(attDoc?.exchange?.status in [Statuses.ST_APPROVED, Statuses.ST_EXECUTED])}"/>
<g:set var="isGoodStatus" value="${domainInstance?.status in [Statuses.ST_STORED, Statuses.ST_QUERIED]}"/>
<tr id="attDocRow_${i}" class="attDocRow">
    <td>
        <g:if test="${ attachmentEditable && (isNewDoc || isExchangeAtGoodStatus || isGoodStatus)}">
            <bootstrap:icon name="pencil" onclick="editAttDoc('${i}')" />
            <bootstrap:icon name="trash" onclick="removeAttDoc('${i}')" />
        </g:if>
    </td>
    <g:hiddenField name="fileExtension_${i}" id="fileExtension_${i}" value="${attDoc?.fileExtension}" />
    <td style="text-align: center;" id="rank_${i}">${i}</td>
    <td id="docType_${i}"> ${attDoc?.docType}</td>
    <td id="docTypeName_${i}"> ${attDoc?.docTypeName}</td>
    <td id="docRef_${i}">${attDoc?.docRef}</td>
    <td id="docDate_${i}">${attDoc?.docDate?.format("dd/MM/yyyy")}</td>
    <td class="centered">
        <g:if test="${attDoc?.fileExtension && attDoc?.attachedFile?.upl_fil}">
            <g:link controller="attachedDoc" name="downloadFile" action="downloadAttDoc" id="${attDoc?.rank}" params="[domainType:domainType,conversationId:params?.conversationId]"
                    style="text-align:center;">${message(code: 'attDoc.download.label', default: 'Download')}</g:link>
        </g:if>
    </td>
</tr>