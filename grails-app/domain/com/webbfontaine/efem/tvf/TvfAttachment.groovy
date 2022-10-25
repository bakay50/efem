package com.webbfontaine.efem.tvf

import org.joda.time.LocalDate

class TvfAttachment {

    String docCode                  //docCode
    String value                  //type
    String docRef                   //name
    LocalDate docDate               //docDate
    byte[] attDoc                   //bytes
    String fileExtension            //contentType
}
