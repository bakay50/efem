$(document).ready(function () {
    currencyTransferToastNotification()
})

function currencyTransferToastNotification() {
    const displayToast = ($('#displayNotification').val() === 'true')
    if (displayToast) {
        const endpoint = $('#currencyNotificationUrl').val()
        $.ajax({
            type: 'GET',
            cache: false,
            global: false,
            url: endpoint,
            success: function (data) {
                if (data.prefinancingCount) {
                    toastr.options = {
                        "closeButton": false,
                        "debug": false,
                        "newestOnTop": false,
                        "progressBar": false,
                        "positionClass": "toast-bottom-right",
                        "preventDuplicates": true,
                        "onclick": null,
                        "showDuration": "0",
                        "hideDuration": "0",
                        "timeOut": 0,
                        "extendedTimeOut": 0,
                        "showEasing": "swing",
                        "hideEasing": "linear",
                        "showMethod": "fadeIn",
                        "hideMethod": "fadeOut",
                        "tapToDismiss": false,
                        'escapeHtml':true
                    }
                    toastr.info(msg('currencyTransfer.message.notification', data.prefinancingCount))
                }
            }
        })
    }
}