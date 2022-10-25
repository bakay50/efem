<%@ page import="com.webbfontaine.efem.AppConfig;" %>
<bootstrap:div id="supDeclarationList">
    <g:each in="${supDeclarationlist}" var="supDeclaration">
        <tr class="supDecRow_${supDeclaration.rank} decRow">
            <td>
                <g:if test="${exchangeInstance?.isSupDeclarationEditable()}">
                    <a title="${message(code: 'exec.edit.title', default: 'Edit')}"
                       onclick="editSupDeclaration(${supDeclaration.rank})" class="editItemIcon">
                        <bootstrap:icon name="pencil"/>
                    </a>
                    <a title="${message(code: 'exec.delete.title', default: 'Delete')}"
                       onclick="removeSupDeclaration(${supDeclaration.rank})" class="deleteItemIcon">
                        <bootstrap:icon name="trash"/>
                    </a>
                </g:if>
            </td>
            <td id="rank_${supDeclaration.rank}" title="${supDeclaration.rank}">${supDeclaration.rank}</td>
            <td id="sdClearanceOfficeCode_${supDeclaration.rank}"
                title="${supDeclaration.clearanceOfficeCode}">${supDeclaration.clearanceOfficeCode}</td>
            <td id="sdClearanceOfficeName_${supDeclaration.rank}"
                title="${supDeclaration.clearanceOfficeName}">${supDeclaration.clearanceOfficeName}</td>
            <td id="sdDeclarationSerial_${supDeclaration.rank}"
                title="${supDeclaration.declarationSerial}">${supDeclaration.declarationSerial}</td>
            <td id="sdDeclarationNumber_${supDeclaration.rank}"
                title="${supDeclaration.declarationNumber}">${supDeclaration.declarationNumber}</td>
            <td id="sdDeclarationDate_${supDeclaration.rank}"
                title="${supDeclaration?.declarationDate?.format(AppConfig.dateFormat())}">${supDeclaration?.declarationDate?.format(AppConfig.dateFormat())}</td>
            <td id="declarationCifAmount_${supDeclaration.rank}"
                title="${supDeclaration.declarationCifAmount}">${supDeclaration.declarationCifAmount}</td>
            <td id="declarationRemainingBalance_${supDeclaration.rank}"
                title="${supDeclaration.declarationRemainingBalance}">${supDeclaration.declarationRemainingBalance}</td>
            <td id="declarationAmountWriteOff_${supDeclaration.rank}"
                title="${supDeclaration.declarationAmountWriteOff}">${supDeclaration.declarationAmountWriteOff}</td>
        </tr>
    </g:each>
</bootstrap:div>