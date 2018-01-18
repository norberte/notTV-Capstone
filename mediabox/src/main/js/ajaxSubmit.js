
    // prepare the form when the DOM is ready
    $(document).ready(function() {
        var options = {
            target:        '#formDiv',   // target element(s) to be updated with server response
            beforeSubmit:  showRequest,  // pre-submit callback
            success:       showResponse  // post-submit callback
        };

        // bind to the form's submit event
        $('#uploadVideo').submit(function() {
            $(this).ajaxSubmit(options);
            // always return false to prevent standard browser submit and page navigation
            return false;
        });
    });

    // pre-submit callback
    function showRequest(formData, jqForm, options) {
        var queryString = $.param(formData);
        alert('About to submit: \n\n' + queryString);
        // returning anything other than false will allow the form submit to continue
        return true;
    }

    // post-submit callback
    function showResponse(responseText, statusText, xhr, $form)  {
        alert('status: ' + statusText + '\n\nresponseText: \n' + responseText +
            '\n\nThe output div should have already been updated with the responseText.');
    }
