package com.webbfontaine.efem.signature

import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j
import org.apache.commons.codec.binary.Base64
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

import static org.springframework.http.MediaType.*
import static SignatureUtils.*

@Slf4j("LOGGER")
@Transactional
class GwsSignatureRestService {

    def restTemplate

    def sign(multipartFile, String fileNameWithExt){
        ResponseEntity response = null
        def llx = Integer.parseInt(getXLeft())
        def lly = Integer.parseInt(getYBottom())
        def urx = Integer.parseInt(getXRight())
        def ury = Integer.parseInt(getYTop())
        String requestUrl = gwsRequestUrl
        OutputStream os = null

        try {
            if(multipartFile instanceof byte[]){
                File file = new File(fileNameWithExt)
                os = new FileOutputStream(file)
                os.write(multipartFile)
                multipartFile = file
            }

            String usernameAndPassword = gwsSignAuthentication

            MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>()
            map.add("pdfDoc", new FileSystemResource(multipartFile))
            map.add('document', GUCE_DOCUMENT)
            map.add('llx', llx)
            map.add('lly', lly)
            map.add('urx', urx)
            map.add('ury', ury)
            map.add('documentLocation', documentLocation)
            map.add('showDigitalSignature', showDigitalSignature)

            //set request headers
            HttpHeaders headers = new HttpHeaders()
            headers.setAccept(Arrays.asList(APPLICATION_OCTET_STREAM))
            headers.setContentType(MULTIPART_FORM_DATA)
            String encoded = new String(Base64.encodeBase64(usernameAndPassword.bytes))
            headers.add("Authorization", "Basic $encoded")

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers)
            response = restTemplate.postForEntity(requestUrl, requestEntity, byte[].class)
            LOGGER.debug("Signing PDF reply: ${response}")
        }catch (IllegalArgumentException e) {
            LOGGER.error("Error on Sign PDF: ${e}")
        }finally{
            os?.close()
        }

        response
    }
}
