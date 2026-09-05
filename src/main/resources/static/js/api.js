const API_BASE = "";

/* =================================
   SESSION STORAGE KEYS
================================= */

const AUTH_KEY = "sdc_auth_header";
const ROLE_KEY = "sdc_role";
const NAME_KEY = "sdc_display_name";
const USERNAME_KEY = "sdc_username";


/* =================================
   SAVE SESSION
================================= */

function saveSession(username, password, role, displayName) {

    sessionStorage.setItem(
        AUTH_KEY,
        "Basic " + btoa(username + ":" + password)
    );

    sessionStorage.setItem(ROLE_KEY, role);
    sessionStorage.setItem(NAME_KEY, displayName || username);
    sessionStorage.setItem(USERNAME_KEY, username);
}


/* =================================
   CLEAR SESSION
================================= */

function clearSession() {

    sessionStorage.removeItem(AUTH_KEY);
    sessionStorage.removeItem(ROLE_KEY);
    sessionStorage.removeItem(NAME_KEY);
    sessionStorage.removeItem(USERNAME_KEY);
}


/* =================================
   CURRENT USER
================================= */

function currentRole() {

    return sessionStorage.getItem(ROLE_KEY);
}


function currentDisplayName() {

    return sessionStorage.getItem(NAME_KEY);
}


function currentUsername() {

    return sessionStorage.getItem(USERNAME_KEY);
}


function getCurrentUser() {

    return {
        username: currentUsername(),
        displayName: currentDisplayName(),
        role: currentRole()
    };
}


/* =================================
   LOGIN CHECK
================================= */

function requireLogin() {

    if (!sessionStorage.getItem(AUTH_KEY)) {

        window.location.href = "login.html";

        return false;
    }

    return true;
}


/* =================================
   ROLE CHECK
================================= */

function requireRole(allowedRoles) {

    const role = currentRole();

    if (!allowedRoles.includes(role)) {

        window.location.href = "dashboard.html";

        return false;
    }

    return true;
}


/* =================================
   NAVIGATION
================================= */

const NAV_LINKS_BY_ROLE = {

    ADMIN: [
        {
            href: "dashboard.html",
            label: "Dashboard",
            icon: "🏠"
        },
        {
            href: "register.html",
            label: "Register Appointment",
            icon: "📅"
        },
        {
            href: "search.html",
            label: "Search & Bill",
            icon: "🧾"
        },
        {
            href: "staff.html",
            label: "Manage Staff",
            icon: "👥"
        },
        {
            href: "help.html",
            label: "Help",
            icon: "❓"
        }
    ],

    RECEPTIONIST: [
        {
            href: "dashboard.html",
            label: "Dashboard",
            icon: "🏠"
        },
        {
            href: "register.html",
            label: "Register Appointment",
            icon: "📅"
        },
        {
            href: "search.html",
            label: "Search & Bill",
            icon: "🧾"
        },
        {
            href: "help.html",
            label: "Help",
            icon: "❓"
        }
    ],

    DENTIST: [
        {
            href: "dashboard.html",
            label: "My Schedule",
            icon: "🏠"
        },
        {
            href: "help.html",
            label: "Help",
            icon: "❓"
        }
    ]
};


/* =================================
   RENDER NAVIGATION
================================= */

function renderNav(activePage) {

    const nav = document.getElementById("nav-links");

    if (!nav) {
        return;
    }

    const user = getCurrentUser();

    if (!user.username) {

        window.location.href = "login.html";

        return;
    }


    /* User information */

    const userName =
        document.getElementById("user-name");

    const userRole =
        document.getElementById("user-role");

    if (userName) {

        userName.textContent =
            user.displayName || user.username;
    }

    if (userRole) {

        userRole.textContent =
            formatRole(user.role);
    }


    /* Navigation links */

    nav.innerHTML = "";

    const links =
        NAV_LINKS_BY_ROLE[user.role] || [];

    links.forEach(function (link) {

        const a =
            document.createElement("a");

        a.href = link.href;

        if (activePage === link.href) {

            a.classList.add("active");
        }

        a.innerHTML = `
            <span class="nav-icon">${link.icon}</span>
            <span>${link.label}</span>
        `;

        nav.appendChild(a);
    });


    /* Logout */

    const logout =
        document.createElement("a");

    logout.href = "#";

    logout.innerHTML = `
        <span class="nav-icon">🚪</span>
        <span>Log out</span>
    `;

    logout.addEventListener(
        "click",
        function (event) {

            event.preventDefault();

            const confirmed =
                confirm(
                    "Are you sure you want to log out?"
                );

            if (!confirmed) {
                return;
            }

            logoutUser();
        }
    );

    nav.appendChild(logout);
}


/* =================================
   LOGOUT
================================= */

function logoutUser() {

    clearSession();

    window.location.href = "login.html";
}


/* =================================
   API FETCH
================================= */

async function apiFetch(path, options) {

    options = options || {};

    const headers = {
        ...(options.headers || {})
    };


    /* Add authentication */

    const authHeader =
        sessionStorage.getItem(AUTH_KEY);

    if (authHeader) {

        headers["Authorization"] =
            authHeader;
    }


    /* Automatically set JSON content type */

    if (
        options.body &&
        !headers["Content-Type"]
    ) {

        headers["Content-Type"] =
            "application/json";
    }


    const response =
        await fetch(
            API_BASE + path,
            {
                ...options,
                headers: headers
            }
        );


    /* Session expired */

    if (response.status === 401) {

        clearSession();

        window.location.href =
            "login.html";

        throw new Error(
            "Session expired"
        );
    }


    return response;
}


/* =================================
   DOWNLOAD FILE
================================= */

async function downloadFile(
    path,
    suggestedFilename
) {

    const response =
        await apiFetch(path);

    if (!response.ok) {

        throw new Error(
            "Download failed with status " +
            response.status
        );
    }

    const blob =
        await response.blob();

    const url =
        window.URL.createObjectURL(blob);

    const a =
        document.createElement("a");

    a.href = url;

    a.download =
        suggestedFilename;

    document.body.appendChild(a);

    a.click();

    a.remove();

    window.URL.revokeObjectURL(url);
}


/* =================================
   FORMAT ROLE
================================= */

function formatRole(role) {

    if (!role) {
        return "";
    }

    return role
        .toLowerCase()
        .replace(
            /\b\w/g,
            char => char.toUpperCase()
        );
}


/* =================================
   TOAST
================================= */

/* =================================
   VALIDATION POPUP
================================= */

function showValidationPopup(message) {

    let container =
        document.querySelector(".validation-popup-container");

    if (!container) {

        container =
            document.createElement("div");

        container.className =
            "validation-popup-container";

        document.body.appendChild(container);
    }

    const popup =
        document.createElement("div");

    popup.className =
        "validation-popup";

    popup.innerHTML = `
        <span class="validation-popup-icon">⚠️</span>
        <span>${message}</span>
    `;

    container.appendChild(popup);

    setTimeout(function () {

        popup.classList.add("hide");

        setTimeout(function () {
            popup.remove();
        }, 300);

    }, 3000);
}

/* =================================
   API ERROR MESSAGE
================================= */

async function getErrorMessage(
    response
) {

    try {

        const data =
            await response.json();

        return (
            data.message ||
            data.error ||
            "Something went wrong."
        );

    } catch (error) {

        return "Something went wrong.";
    }
}