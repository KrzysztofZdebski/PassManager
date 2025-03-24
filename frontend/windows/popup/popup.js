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

    $(document).on("click", ".showBtn", (e) => {
        let idx = e.target.getAttribute("btnIdx");
        if (e.target.textContent === "Hide") {
            chrome.storage.local.get("data", data => {
                let passwords = data.data;
                fetch(`http://localhost:5000/api/passwords/encrypt?password=${passwords[idx].password}&key=${passwords[idx].siteName}`, {
                    method: "GET"
                })
                .then(response => response.text())
                .then(data => {
                    passwords[idx].password = data;
                    chrome.storage.local.set({ data: passwords });
                    e.target.textContent = "Show";
                    hidePasswords();
                }); 
            });
        }else{
            chrome.storage.local.get("data", data => {
                let passwords = data.data;
                fetch(`http://localhost:5000/api/passwords/decrypt?password=${passwords[idx].password}&key=${passwords[idx].siteName}`, {
                    method: "GET"
                })
                .then(response => response.text())
                .then(data => {
                    passwords[idx].password = data;
                    chrome.storage.local.set({ data: passwords });
                    e.target.textContent = "Hide";
                    showPassword(idx);
                }); 
            });
        }
    });

    $(document).on("click", ".editBtn", (e) => {
        refresh();
        let idx = e.target.getAttribute("btnIdx");
        chrome.storage.local.get("data", data => {
            let passwords = data.data || [];
            let password = passwords[idx];
            let site = password.siteName;
            let pass = password.password;
            let cont = `<div id='editFormContainer${idx}' class='container' style='display: flex; justify-content: flex-start;'>`;
            let siteForm = `<input id='siteForm${idx}' style='width: 160px' type='text' value='${site}' class='input-form col-xs-5'>`;
            let passwordForm = `<input id='passwordForm${idx}' style='width: 160px' type='text' value='${pass}' class='input-form col-xs-5'>`;
            let saveButton = `<button style='width: 65px' id='saveEdit${idx}' class='btn-link col-xs-1'>Save</button>`;
            let cancelButton = `<button style='width: 65px' id='cancelEdit${idx}' class='btn-link col-xs-1'>Cancel</button>`;
            $(`#listItem${idx}`).children().children().hide();
            $(`#listItem${idx}`).append(cont + siteForm + passwordForm + saveButton + cancelButton + "</div>");
        });

        $(document).on("click", `#saveEdit${idx}`, () => {
            let site = $(`#siteForm${idx}`).val();
            let password = $(`#passwordForm${idx}`).val();
            chrome.storage.local.get("data", data => {
                let passwords = data.data || [];
                let passwordObj = { siteName: site, password: password };
                passwords[idx] = passwordObj;
                chrome.storage.local.set({ data: passwords }, () => {
                    refresh();
                });
            });
        });

        $(document).on("click", `#cancelEdit${idx}`, () => {
            $(`#editFormContainer${idx}`).remove();
            $(`#listItem${idx}`).children().children().show();
        });
    });
});

function refresh() {
    chrome.storage.local.get("data", data => {
        let passwords = data.data || [];
        $("#list").empty();
        passwords.forEach((password, index) => {
            let site = password.siteName;
            let pass = password.password;
            let row = `
            <li class="list-group-item listItem" id="listItem${index}">
                <div class="d-flex justify-content-between align-items-center">
                    <div class="form-check">
                        <input type="checkbox" class="form-check-input" id="item${index}">
                    </div>
                    <div class="flex-grow-1 mx-2">
                        <label id="siteLabel${index}" class="form-check-label" for="item${index}">${site}:</label>
                        <label id="passLabel${index}" class="form-check-label passLabel" for="item${index}">${pass}</label>
                    </div>
                    <div>
                        <button class="btn btn-link showBtn" id="showButton${index}" btnIdx='${index}' style="margin: 5px">Show</button>
                    </div>
                    <div>
                        <button class="btn btn-link editBtn" id="editButton${index}" btnIdx='${index}' style="margin: 5px">Edit</button>
                    </div>
                </div>
            </li>`;
            $("#list").append(row);
        });
        hidePasswords();
    });
}

function hidePasswords() {  
    let passwords = $('.passLabel');
    passwords.each(function() {
        let text = $(this).text();
        let hidden = "";
        for (let i = 0; i < text.length; i++) {
            hidden += "*";
        }
        $(this).text(hidden);
    });
}

function showPassword(index) {
    let passwordLabel = $(`#passLabel${index}`);
    chrome.storage.local.get("data", data => {
        let passwords = data.data || [];
        let password = passwords[index].password;
        passwordLabel.text(password);
    });
}