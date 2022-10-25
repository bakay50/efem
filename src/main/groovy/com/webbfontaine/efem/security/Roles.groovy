package com.webbfontaine.efem.security

import com.webbfontaine.efem.AppConfig

/**
 * Copyrights 2002-2018 Webb Fontaine
 * Developer: A.Bilalang
 * Date: 05/11/2018
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
enum Roles {
    ADMIN,SUPER_ADMINISTRATOR, DECLARANT, TRADER, BANK_AGENT, GOVT_OFFICER, GOVT_SUPERVISOR

    def roleName = AppConfig.resolevRoleName()

    String getAuthority() {
        return "ROLE_${getRole()}"
    }

    String getRole() {
        return "${roleName}_${name()}"
    }
}