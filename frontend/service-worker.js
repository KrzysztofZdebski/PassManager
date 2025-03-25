// Listen for installation events
self.addEventListener("install", (event) => {
    console.log("Service Worker installed.");
    // Perform any setup tasks if needed
});

// Listen for activation events
self.addEventListener("activate", (event) => {
    console.log("Service Worker activated.");
    // Perform cleanup of old caches or other tasks
});

// Listen for messages from the extension
self.addEventListener("message", (event) => {
    console.log("Message received in Service Worker:", event.data);
    // Handle messages from the extension
});

// Example: Intercept network requests (optional)
self.addEventListener("fetch", (event) => {
    console.log("Intercepted request:", event.request.url);
    // You can modify or respond to requests here
});

chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
    console.log("Message received in Service Worker:", message);
    if (message.action === "log") {
        console.log(message.message);
        sendResponse({ status: "Message logged in Service Worker" });
    }
});