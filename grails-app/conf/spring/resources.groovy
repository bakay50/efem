import com.webbfontaine.common.file.upload.ValidatableFileUploadBase
import com.webbfontaine.common.file.validator.FileCorrectnessValidator
import com.webbfontaine.common.file.validator.FileExtensionValidator
import com.webbfontaine.common.file.validator.FileSizeValidator
import com.webbfontaine.efem.rest.client.service.impl.TvfClientService
import com.webbfontaine.efem.sequence.KeyRepository
import com.webbfontaine.efem.sequence.SequenceGenerator
import com.webbfontaine.efem.workflow.BankWorkflow
import com.webbfontaine.efem.workflow.CurrencyTransferWorkflow
import com.webbfontaine.efem.workflow.ExchangeWorkflow
import com.webbfontaine.efem.workflow.RepatriationWorkflow
import com.webbfontaine.efem.workflow.TransferOrderWorkflow
import com.webbfontaine.grails.plugins.utils.fileupload.FileUploadBase
import com.webbfontaine.grails.plugins.utils.fileupload.FileUploadMessageSource
import grails.util.Holders

// Place your Spring DSL code here
beans = {

    exchangeWorkflow(ExchangeWorkflow)
    repatriationWorkflow(RepatriationWorkflow)
    transferOrderWorkflow(TransferOrderWorkflow)
    currencyTransferWorkflow(CurrencyTransferWorkflow)
    bankWorkflow(BankWorkflow)

    sequenceGenerator(SequenceGenerator)
    keyRepository(KeyRepository)
    tvfClientService(TvfClientService)

    fileUploadBase(FileUploadBase) {
    }

    multipartResolver(com.webbfontaine.efem.resolver.CustomMultipartResolver) {
    }

    attachmentFileUploadBase(ValidatableFileUploadBase, ref("fileUploadBase")) {
        def fileUploadMessageSource = new FileUploadMessageSource()
        validators = [
                new FileSizeValidator(fileUploadMessageSource, Holders.config.attachmentMaxSizeBytes),
                new FileCorrectnessValidator(fileUploadMessageSource),
                new FileExtensionValidator(fileUploadMessageSource, Holders.config.attachmentAcceptedFormats)
        ]
    }
}
