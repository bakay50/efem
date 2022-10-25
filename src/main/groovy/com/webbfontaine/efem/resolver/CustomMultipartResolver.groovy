package com.webbfontaine.efem.resolver

import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.multipart.MultipartException
import org.springframework.web.multipart.MultipartHttpServletRequest
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest
import org.springframework.web.multipart.support.StandardServletMultipartResolver

import javax.servlet.http.HttpServletRequest

import static com.webbfontaine.efem.constants.TvfConstants.MULTIPART_FILE_UPLOAD_EXCEPTION

public class CustomMultipartResolver extends StandardServletMultipartResolver  {

    public MultipartHttpServletRequest resolveMultipart(HttpServletRequest request) {
        try {
            return super.resolveMultipart(request)
        } catch (MultipartException e) {
            request.setAttribute(MULTIPART_FILE_UPLOAD_EXCEPTION, e.cause)
            return new DefaultMultipartHttpServletRequest(
                    request,
                    new LinkedMultiValueMap(),
                    new HashMap(), new HashMap())
        }
    }
}



