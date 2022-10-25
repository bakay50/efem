// This is a manifest file that'll be compiled into application.js.
//
// Any JavaScript file within this directory can be referenced here using a relative path.
//
// You're free to add application-wide JavaScript to this file, but it's generally better
// to create separate JavaScript files as needed.
//
//= require layout
//= require wf-sys-info
//= require wf-utils/classy
//= require_self
//= require wf-utils/confirm-dialog-box
//= require_tree /business
//= require wf-utils/upload
//= require common
//= require search/searchFormReset
//= require search/searchResult
//= require toastr

if (typeof jQuery !== 'undefined') {
    (function($) {
        $(document).ajaxStart(function() {
            $('#spinner').fadeIn();
        }).ajaxStop(function() {
            $('#spinner').fadeOut();
        });
    })(jQuery);
}
function sortByField(sortField, sortOrder) {
    $.ajax({
        url: $("#searchUrl").val(),
        data: {
            sort: sortField,
            order: sortOrder
        },
        success: function (data, textStatus) {
            $('#searchResults').html(data);
        }
    });
}