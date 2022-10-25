package com.webbfontaine.efem.constants

enum DocumentTypes {
    EXCHANGE("exchange"), REPATRIATION("repatriation"), TRANSFER("transfer"), CURRENCY_TRANSFER("currencyTransfer")

    private final String label

    DocumentTypes(String label) {
        this.label = label
    }


    String getLabel() {
        return label
    }
}