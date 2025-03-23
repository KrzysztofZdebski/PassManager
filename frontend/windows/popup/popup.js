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
    refresh();

    $("#find").on("click", () => {
        $(".button").hide();
        let cont = "<div id='searchFormContainer' class='container' style='display: flex; justify-content: flex-start;'>";
        let searchForm = "<input id='searchForm' type='search' placeholder='Search' class='input-form col-sm-10'>";
        let cancelButton = "<button style='width: 65px' id='cancel' class='btn-link col-sm-2'>Cancel</button>";
        $("#nav").prepend(cont + searchForm + cancelButton + "</div>");

        $("#searchForm").on("change", () => {
            let search = $("#searchForm").val().toLowerCase();
            let list = $(".listItem");
            if (search === "") {
                $(".listItem").show();     
            }else{
                list.each((index, item) => {
                    let text = $(item).text().toLowerCase();
                    if (text.includes(search)) {
                        $(item).show();
                    } else {
                        $(item).hide();
                    }
                });
            }
        });
    });

    $(document).on("click", "#cancel", () => {
        $("#searchFormContainer").remove();
        $(".button").show();
        $(".listItem").show();
    });

    $("#delete").on("click", () => {
        if(!confirm("Are you sure you want to delete the selected passwords?")) return;
        let checkboxes = document.querySelectorAll("input[type='checkbox']");
        let checked = [];
        checkboxes.forEach((checkbox, index) => {
            if (checkbox.checked) {
                checked.push(index);
            }
        });
        console.log(checked);
        chrome.storage.local.get("data", data => {
            let passwords = data.data || [];
            let toDelete = [];
            checked.forEach(index => {
                toDelete.push(passwords[index]);
            });
            toDelete.forEach(item => {
                let index = passwords.indexOf(item);
                passwords.splice(index, 1);
            });
            chrome.storage.local.set({ data: passwords }, () => {
                refresh();
            });
        });
    });
});

function refresh(){
    chrome.storage.local.get("data", data => {
        let passwords = data.data || [];
        console.log(passwords);
        $("#list").empty();
        passwords.forEach((password,index) => {
            let site = password.siteName;
            let pass = password.password;
            let row = `
            <li class="list-group-item listItem">
                <input type="checkbox" class="form-check-input" id="item${index}">
                <label class="form-check-label" for="item${index}">${site}: ${pass}</label>
            </li>`;            
            $("#list").append(row);
        });
    });
}