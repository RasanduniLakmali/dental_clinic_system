let selectedRole = "ADMIN";

document.querySelectorAll(".role-tab").forEach(function (tab) {
  tab.addEventListener("click", function () {
    document.querySelectorAll(".role-tab").forEach(function (t) { t.classList.remove("active"); });
    tab.classList.add("active");
    selectedRole = tab.getAttribute("data-role");
    document.getElementById("login-error").classList.remove("visible");
  });
});

document.getElementById("login-form").addEventListener("submit", async function (event) {
  event.preventDefault();
  const username = document.getElementById("username").value.trim();
  const password = document.getElementById("password").value;
  
    if (!username) {

    showValidationPopup(
        "Username is required."
    );

    return;
}

if (!password) {

    showValidationPopup(
        "Password is required."
    );

    return;
}

  const errorEl = document.getElementById("login-error");
  const loginBtn = document.getElementById("login-btn");
  const loginBtnLabel = document.getElementById("login-btn-label");
  errorEl.classList.remove("visible");
  
  loginBtn.disabled = true;
  loginBtnLabel.textContent = "Signing in...";

  try {
    const response = await fetch("/api/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, password })
    });
    const data = await response.json();

    if (!response.ok || !data.role) {
      showValidationPopup(
    data.message || "Incorrect username or password."
);
      errorEl.classList.add("visible");
      return;
    }

    if (data.role !== selectedRole) {
      errorEl.textContent = "This account is not a " + selectedRole.charAt(0) + selectedRole.slice(1).toLowerCase() +
        " account. Select the " + data.role.charAt(0) + data.role.slice(1).toLowerCase() + " tab instead.";
      errorEl.classList.add("visible");
      return;
    }

    saveSession(username, password, data.role, data.displayName);
    window.location.href = "dashboard.html";
  } catch (err) {
    errorEl.textContent = "Could not reach the server. Is the backend running?";
    errorEl.classList.add("visible");
  } finally {
    loginBtn.disabled = false;
    loginBtnLabel.textContent = "Sign in";
  }
});
