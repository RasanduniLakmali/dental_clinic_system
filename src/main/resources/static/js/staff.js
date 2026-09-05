requireLogin();
requireRole(["ADMIN"]);
renderNav("staff.html");

const successAlert = document.getElementById("success-alert");
const errorAlert = document.getElementById("error-alert");

function roleLabel(role) {
  return role.charAt(0) + role.slice(1).toLowerCase();
}

async function loadStaff() {
  const body = document.getElementById("staff-body");
  body.innerHTML = "<tr><td colspan='6'>Loading...</td></tr>";
  try {
    const response = await apiFetch("/api/admin/staff");
    const staff = await response.json();
    body.innerHTML = "";
    if (staff.length === 0) {
      body.innerHTML = "<tr><td colspan='6'>No staff accounts yet.</td></tr>";
      return;
    }
    staff.forEach(function (s) {
      const row = document.createElement("tr");
      const statusBadge = s.active
        ? "<span style=\"color:var(--success)\">Active</span>"
        : "<span style=\"color:var(--danger)\">Disabled</span>";
      row.innerHTML = "<td>" + s.displayName + "</td><td>" + s.username + "</td><td>" + s.email +
        "</td><td>" + roleLabel(s.role) + "</td><td>" + statusBadge + "</td><td></td>";
      const actionCell = row.lastElementChild;
      const toggleBtn = document.createElement("button");
      toggleBtn.type = "button";
      toggleBtn.textContent = s.active ? "Disable" : "Enable";
      toggleBtn.addEventListener("click", function () { toggleStatus(s.id, !s.active); });
      actionCell.appendChild(toggleBtn);
      body.appendChild(row);
    });
  } catch (err) {
    body.innerHTML = "<tr><td colspan='6'>Could not load staff.</td></tr>";
  }
}

async function toggleStatus(id, active) {
  try {
    await apiFetch("/api/admin/staff/" + id + "/status", {
      method: "PATCH",
      body: JSON.stringify({ active })
    });
    loadStaff();
  } catch (err) {
    errorAlert.textContent = "Could not update that account.";
    errorAlert.classList.add("visible");
  }
}

loadStaff();

document.getElementById("staff-form").addEventListener("submit", async function (event) {
  event.preventDefault();
  successAlert.classList.remove("visible");
  errorAlert.classList.remove("visible");

  const submitBtn = document.getElementById("staff-submit-btn");
  const submitLabel = document.getElementById("staff-submit-label");
  submitBtn.disabled = true;
  submitLabel.textContent = "Creating...";

const displayName =
    document.getElementById("displayName").value.trim();

const email =
    document.getElementById("email").value.trim();

const role =
    document.getElementById("role").value;


if (!displayName) {

    showValidationPopup(
        "Full name is required."
    );

    return;
}


if (!email) {

    showValidationPopup(
        "Email address is required."
    );

    return;
}


const emailPattern =
    /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

if (!emailPattern.test(email)) {

    showValidationPopup(
        "Please enter a valid email address."
    );

    return;
}

  const payload = {
    displayName: document.getElementById("displayName").value.trim(),
    email: document.getElementById("email").value.trim(),
    role: document.getElementById("role").value
  };

  try {
    const response = await apiFetch("/api/admin/staff", {
      method: "POST",
      body: JSON.stringify(payload)
    });
    const data = await response.json();
    if (!response.ok) {
      const firstError = typeof data === "object" ? Object.values(data)[0] : "Could not create the account.";
      errorAlert.textContent = firstError;
      errorAlert.classList.add("visible");
      return;
    }

    if (data.emailSent) {
      successAlert.textContent = "Account created for " + data.username + ". Login details were emailed to " + data.email + ".";
    } else {
      successAlert.textContent = "Account created for " + data.username + ". Email could not be sent - " +
        "temporary password: " + data.temporaryPassword + " (share this with them directly, once).";
    }
    successAlert.classList.add("visible");
    document.getElementById("staff-form").reset();
    loadStaff();
  } catch (err) {
    errorAlert.textContent = "Could not reach the server.";
    errorAlert.classList.add("visible");
  } finally {
    submitBtn.disabled = false;
    submitLabel.textContent = "Create account";
  }
});
