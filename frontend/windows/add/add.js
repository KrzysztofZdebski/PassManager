const serverUrl = 'https://localhost:5001/api/passwords';

$(document).ready(function() {

    chrome.tabs.query({ active: true, lastFocusedWindow: true }, tabs => {
        let openedURL = tabs[0].url;
        console.log(openedURL);
        openedURL = openedURL.replace('https://', '');
        openedURL = openedURL.replace('http://', '');
        openedURL = openedURL.replace('www.', '');
        openedURL = openedURL.slice(0, openedURL.indexOf('/'));
        console.log(openedURL);
        $('#siteName').val(openedURL);
    });

    function getUserFromStorage() {
        return new Promise((resolve, reject) => {
            chrome.storage.local.get('user', (data) => {
                if (chrome.runtime.lastError) {
                    reject(chrome.runtime.lastError);
                } else {
                    resolve(data.user || {});
                }
            });
        });
    }

    $('#addPasswordForm').on('submit', async function(event) {
        event.preventDefault();
        let siteName = $('#siteName').val();
        let password = $('#password').val();
        let key = '';

        try {
            // Use the Promise wrapper to get the user data
            let userN = await getUserFromStorage();

            // Send the user data to the background script (optional)
            chrome.runtime.sendMessage({ type: "Add", userN });

            // Save the password to the server
            await fetch(serverUrl + `/save?passwordName=${encodeURIComponent(password)}&siteName=${encodeURIComponent(siteName)}&user=${encodeURIComponent(userN.username)}`, {
                method: "POST",
            })
            .then(response => response.text())
            .then(data => {
                key = data;
            })
            .catch(error => {
                console.error("Error:", error);
            });

            // Update the local storage with the new password
            chrome.storage.local.get('data', async (result) => {
                let data = result.data || []; // Initialize data as an empty array if it doesn't exist

                // Add the new siteName and password to the data array
                data.push({ siteName: siteName, key: key });

                // Save the updated data back to chrome.storage.local
                chrome.storage.local.set({ 'data': data }, () => {
                    console.log('Data is set to ', data);
                });

                // Redirect to the popup.html
                location.href = '\\windows\\popup\\popup.html';
            });
        } catch (error) {
            console.error("Error retrieving user data:", error);
        }
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
        fetch(`https://localhost:5001/api/passwords/generate?options=someOptions`, {
            method: "GET"
        })
        .then(response => response.text())
        .then(data => {
            $('#password').val(data);
        });    
    });

});