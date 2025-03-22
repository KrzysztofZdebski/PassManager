$(document).ready(function() {
    $('#addPasswordForm').on('submit', function(event) {
        event.preventDefault();
        const siteName = $('#siteName').val();
        const password = $('#password').val();

        // Retrieve existing data from chrome.storage.local
        chrome.storage.local.get('data', (result) => {
            let data = result.data || []; // Initialize data as an empty array if it doesn't exist

            // Add the new siteName and password to the data array
            data.push({ siteName: siteName, password: password });

            // Save the updated data back to chrome.storage.local
            chrome.storage.local.set({ 'data': data }, function() {
                console.log('Data is set to ', data);
            });

            // Redirect to the popup.html
            location.href = '\\windows\\popup\\popup.html';
        });
    });

    $('#cancelButton').on('click', function() {
        // Redirect to the popup.html
        location.href = '\\windows\\popup\\popup.html';
    });
});