const serverUrl = 'https://localhost:5001/api/passwords';
// chrome.storage.local.get("user", data => {
//     let user = data.user || {};
//     if(!authenticate(user.username, user.password)) {
//         chrome.storage.local.remove("user");
//     }
// });

async function authenticate(username, password) {
    let auth = false;
    try {
        const response = await fetch(serverUrl + `/authenticate?username=${username}&password=${password}`, {
            method: "POST",
        })
        .then(response => response.text())
        .then(data => {
            auth = data;
        })
        .catch(error => {
            console.error("Error:", error);
            auth = false;
        });

        if (auth) {
            const data = auth;
            console.log("Login successful:", data);
            chrome.storage.local.set({ user: { username, password } });
            // Redirect to another page or perform further actions
            window.location.href = '\\windows\\popup\\popup.html';
            return true;
        } else {
            const errorData = await response.json();
            errorMessage.textContent = errorData.message || "Invalid credentials";
            errorMessage.style.display = "block";
            return false;
        }
    } catch (error) {
        console.error("Error during login:", error);
        errorMessage.textContent = "An error occurred. Please try again.";
        errorMessage.style.display = "block";
        return false;
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const loginForm = document.getElementById("loginForm");
    const errorMessage = document.getElementById("errorMessage");

    loginForm.addEventListener("submit", async (event) => {
        event.preventDefault();

        const username = document.getElementById("username").value;
        const password = document.getElementById("password").value;

        // Placeholder for authentication logic
        authenticate(username, password);
    });
});