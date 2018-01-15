function select(thumbnail, check) {
    thumbnail.toggleClass('selected');
    check.toggleClass('hidden');
}

$(".thumbnail").on('click', function(e) {
    select($(this), $(this).parent().find('.check'));
});
$(".check").on('click', function(e) {
     select($(this).parent().find('.thumbnail'), $(this));
});
$('#select-all').on('click', function(e) {
    if($(this).text() === 'Select All') {
        $(this).text('Deselect All');
         $('.thumbnail').each(function(k, v) {
             if(!$(this).hasClass('selected'))
                select($(this), $(this).parent().find('.check'));
         });
    } else {
        $(this).text('Select All');
        $('.thumbnail').each(function(k, v) {
             if($(this).hasClass('selected'))
                select($(this), $(this).parent().find('.check'));
         });
    }
});

$('.panel-body').on('click', function(e) {
    $(this).toggleClass('checked');
    $(this).find('.glyphicon').toggleClass('hidden');
});