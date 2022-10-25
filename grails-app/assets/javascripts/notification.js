$(document).ready(function() {
   pushNotification();
});

function pushNotification() {
    const displayNotification = ($("#displayNotification").val() === 'true')
    if (displayNotification) {
        const urlNotification = $("#notificationUrl").val()
        $.ajax({
            type: 'GET',
            cache: false,
            global: false,
            url: urlNotification,
            success: function(data) {
                if (data.numberNotif) {
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
                    toastr.info(msg('message.notification', data.numberNotif))
                }
            }
        })
    }
}

