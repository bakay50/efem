package com.webbfontaine.efem.Config

enum Config {
    MANDATORY('M'), PROHIBITED('P'), OPTIONAL('O'), CUSTOMIZED('C')

    private final String label

    Config(String label) {
        this.label = label
    }

    boolean isConform(String label) {
        this.label == label
    }
}