/**
 * Gets the thumbnail with the associated id, and 
 * calls the callback with the result.
 */
export function getThumbnail(id, callback) {
    $.ajax({
        type: "GET",
        url: "/process/get-thumbnail/"+id,
        dataType: "binary",
        processData: false,
        success: (file) => {
            if(file !== null) {
                const reader = new FileReader();
                reader.onloadend = () => callback(reader.result);
                reader.readAsDataURL(file);
            }
        }
    });
}
