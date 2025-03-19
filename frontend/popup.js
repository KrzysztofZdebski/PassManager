document.getElementById("save").addEventListener("click", () => {
    const site = document.getElementById("site").value;
    const password = document.getElementById("password").value;

    fetch(`http://localhost:5000/api/passwords/save?site=${site}&password=${password}`, {
        method: "POST"
    }).then(response => response.text()).then(data => {
        document.getElementById("result").textContent = data;
    });
});

document.getElementById("get").addEventListener("click", () => {
    const site = document.getElementById("site").value;

    fetch(`http://localhost:5000/api/passwords/get?site=${site}`)
        .then(response => response.text())
        .then(data => {
            document.getElementById("result").textContent = `Password: ${data}`;
        });
});
