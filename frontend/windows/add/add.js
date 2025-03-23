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

    $('#showPasswordIcon').on('click', function() {
        const password = $('#password');
        if (password.attr('type') === 'password') {
            password.attr('type', 'text');
            $('#showPasswordIcon').css('opacity', '1');
        } else {
            password.attr('type', 'password');
            $('#showPasswordIcon').css('opacity', '0.3');
        }
    });

    $('#generatePasswordButton').on('click', function() {
        fetch(`http://localhost:5000/api/passwords/generate?options=someOptions`, {
            method: "GET"
        })
        .then(response => response.text())
        .then(data => {
            $('#password').val(data);
        });    
    });
});