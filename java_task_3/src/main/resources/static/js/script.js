$(function(){
    // Links marked with the open-dialog class will open a dialog when clicked
    $('.open-dialog').on('click', function(event) {
        // Prevent the default action of the link
        event.preventDefault();

        // Extract the dialog id from the link id. Removes the initial 'open-' part
        var dialogId = event.currentTarget.id.slice(5);
        // Extract the dialog action from the dialog id. The action is the part before the first '-'
        var dialogAction = dialogId.substring(0, dialogId.indexOf('-'));

        // Reuse the add dialog for both add and edit actions
        if (dialogAction === 'edit') {
            dialogId = dialogId.replace('edit', 'add')
        }

        // Get the form inside the dialog. The form id is the dialog id with '-form' appended
        var form = $('#'.concat(dialogId).concat('-form'));
        // Reset the form to clear any previous data
        form[0].reset();


        var actionUrl = event.currentTarget.href;
        var actionMethod = 'delete';

        if (dialogAction === 'add') {
            actionMethod = 'post';
        } else if (dialogAction === 'edit') {
            actionMethod = 'put';

            // Change the form button text to 'Save' for edit actions
            form.find('button[type="submit"]').text('Save');

            // Populate the form with the data from the row that was clicked
            var row = event.currentTarget.parentElement.parentElement;

            // For single line text fields, the data is stored in <td> tags
            form.find('input').each(function(index, input) {
                input.value = $(row).find('td:not([class])')[index].innerText;
            });

            // For multiline text fields, the data is stored in <p> tags inside the <td> tags with the 'text-content' class
            form.find('textarea').each(function(index, area) {
                area.value = $(row).find('td.text-content > p')[index].innerText;
            });

            // For select fields, the data is stored in <td> tags with the 'reference' class
            form.find('select').each(function(index, select) {
                selectedData = $(row).find('td.reference')[index].innerText;

                $(select).find('option').each(function(index, option) {
                    if (option.text === selectedData) {
                        option.selected = true;
                    }
                });
            });
        }

        // Send the form data as JSON when the form is submitted. URL and REST verb are set based on the dialog action
        form.on("submit", function(event) {
            var formData = new FormData(event.target);
            var formValues = Object.fromEntries(formData.entries());

            // To prevent CORS
            var httpHeaders = {
                'X-XSRF-TOKEN': $("meta[name='_csrf']").attr("content")
            };

            // If present, add the OAuth2 token to the headers
            var oAuth2Token = localStorage.getItem("OAuth2Token");

            if (oAuth2Token) {
                httpHeaders.Authorization = 'Bearer '.concat(oAuth2Token);
            }

            $.ajax({
                url: actionUrl,
                method: actionMethod,
                contentType : 'application/json; charset=utf-8',
                headers: httpHeaders,
                data: JSON.stringify(formValues),
                success: function(data) {
                    location.reload(true);
                },
                error: function(jqXHR) {
                    console.log(jqXHR.responseText);
                    alert("Request failed!");
                }
            });

            // Remove the event listener after the form is submitted to prevent multiple submissions
            form.off("submit", false);
        });

        // Show the dialog
        var dialog = $('#'.concat(dialogId))[0];
        dialog.showModal();
    });

    // Buttons marked with the 'close' class will dismiss the dialog when clicked.
    // The id of the dialog is the one after slicing the the button of the 'close-' prefix.
    $(':button.close').on('click', function(event) {
        $('#'.concat(event.target.id.slice(6)))[0].close();
    });
});