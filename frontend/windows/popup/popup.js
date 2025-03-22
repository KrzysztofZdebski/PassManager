// $("#save").addEventListener("click", () => {
//     const site = document.getElementById("site").value;
//     const password = document.getElementById("password").value;

//     fetch(`http://localhost:5000/api/passwords/save?site=${site}&password=${password}`, {
//         method: "POST"
//     }).then(response => response.text()).then(data => {
//         document.getElementById("result").textContent = data;
//     });
// });

// $("#get").addEventListener("click", () => {
//     const site = document.getElementById("site").value;

//     fetch(`http://localhost:5000/api/passwords/get?site=${site}`)
//         .then(response => response.text())
//         .then(data => {
//             document.getElementById("result").textContent = `Password: ${data}`;
//         });
// });

$(document).ready(() => {
    // fetch(chrome.runtime.getURL("..\\storage\\uberSecrets.json"))
    //     .then(response => response.json())
    //     .then(data => {
    //         console.log(data);
    //     });
    chrome.storage.local.get("uberSecret", data => {
        console.log(data.toString());
    });

    $("#find").on("click", () => {
        $(".button").hide();
        let cont = "<div id='searchFormContainer' class='container' style='display: flex; justify-content: flex-start;'>";
        let searchForm = "<input id='searchForm' type='text' placeholder='Search' class='input-form col-sm-10'>";
        let cancelButton = "<button style='width: 30px' id='cancel' class='btn-link col-sm-2'>X</button>";
        $("#nav").prepend(cont + searchForm + cancelButton + "</div>");
    });

    $(document).on("click", "#cancel", () => {
        $("#searchFormContainer").remove();
        $(".button").show();
    });

    $("#save").on("click", () => {
        
    });
});