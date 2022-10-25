package com.webbfontaine.efem.notif

import groovy.transform.TupleConstructor

@TupleConstructor
class ParameterCommand implements Serializable {
    String modName ="efem"
    String numerodemande
    String traderCode
    ArrayList traderCodeArray
    String declarantCode
    String bankCode
    String mailType
    String recipientEmail
    String recipientEmailArray
    String initialStatus
    ArrayList bodyArgs
    ArrayList subjectArgs
}