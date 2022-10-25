<g:applyLayout name="page">
    <html>
    <head>
        <g:if env="development"><asset:stylesheet src="errors.css"/></g:if>
    </head>

    <body>
    <content tag="application">
        <div style="text-align: center; padding-top: 10%">
            <h3>
                <strong> Error: Page Not Found (404) </strong>
            </h3>
            <br />

            <h2>
                Path: ${request.forwardURI}
            </h2>
        </div>
    </content>
    </body>
    </html>
</g:applyLayout>