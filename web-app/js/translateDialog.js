function initChangeLanguageConfirmDialog(event) {
    $('.lang_link').click(function (event) {
        var newLink = initChangeLanguageLink($(this).attr("href"));
        if(newLink!==null){
            console.log("index of view page :" + newLink.toString().indexOf("show"));
            if(newLink.toString().indexOf("show") < 0){
                showSubDocConfirmDialog('changeLanguage');
                event.preventDefault();
                $('#changeLanguageConfirmOper').unbind('click').bind('click', function (event) {
                    window.location.href = newLink;
                });
            }
        }
    });
}


function initChangeLanguageLink(link) {
    var newLink;
    if (link.toString().indexOf("list") >= 0) {
        newLink = null;
    }else {
        newLink = link;
    }
    return newLink;
}
