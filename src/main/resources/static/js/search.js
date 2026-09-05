requireLogin();
requireRole(["ADMIN", "RECEPTIONIST"]);
renderNav("search.html");

let currentAppointmentNumber = null;

document.getElementById("search-btn").addEventListener("click", async function () {
  const number = document.getElementById("search-number").value.trim();
  const errorEl = document.getElementById("search-error");
  const detailsCard = document.getElementById("details-card");
  const billBox = document.getElementById("bill-box");
  errorEl.classList.remove("visible");
  detailsCard.style.display = "none";
  billBox.style.display = "none";

  if (!number) {

    showValidationPopup(
        "Please enter an appointment number."
    );

    return;
}

  try {
    const response = await apiFetch("/api/appointments/" + encodeURIComponent(number));
    if (!response.ok) {
      const data = await response.json();
      showValidationPopup(
    "No appointment found with that number."
);
      errorEl.classList.add("visible");
      return;
    }
    const a = await response.json();
    currentAppointmentNumber = a.appointmentNumber;
    document.getElementById("d-patient").textContent = a.patientName;
    document.getElementById("d-contact").textContent = a.contactNumber;
    document.getElementById("d-address").textContent = a.address;
    document.getElementById("d-dentist").textContent = a.dentistName;
    document.getElementById("d-treatment").textContent = a.treatmentType;
    document.getElementById("d-date").textContent = a.appointmentDate;
    document.getElementById("d-time").textContent = a.appointmentTime;
    document.getElementById("d-status").textContent = a.status;
    detailsCard.style.display = "block";
  } catch (err) {
    errorEl.textContent = "Could not reach the server.";
    errorEl.classList.add("visible");
  }
});

document.getElementById("generate-bill-btn").addEventListener("click", async function () {
  if (!currentAppointmentNumber) return;
  const errorEl = document.getElementById("search-error");
  errorEl.classList.remove("visible");

  try {
    const response = await apiFetch("/api/bills/" + encodeURIComponent(currentAppointmentNumber) + "/generate", {
      method: "POST"
    });
    if (!response.ok) {
      const data = await response.json();
      errorEl.textContent = data.error || "Could not generate the bill.";
      errorEl.classList.add("visible");
      return;
    }
    const bill = await response.json();
    document.getElementById("b-consultation").textContent = "Rs. " + bill.consultationFee;
    document.getElementById("b-treatment").textContent = "Rs. " + bill.treatmentFee;
    document.getElementById("b-total").textContent = "Rs. " + bill.totalAmount;
    document.getElementById("bill-box").style.display = "block";
  } catch (err) {
    errorEl.textContent = "Could not reach the server.";
    errorEl.classList.add("visible");
  }
});

document.getElementById("download-pdf-btn").addEventListener("click", async function () {
  if (!currentAppointmentNumber) return;
  const errorEl = document.getElementById("search-error");
  errorEl.classList.remove("visible");
  const btn = document.getElementById("download-pdf-btn");
  const originalText = btn.textContent;
  btn.disabled = true;
  btn.textContent = "Preparing...";
  try {
    await downloadFile(
      "/api/bills/" + encodeURIComponent(currentAppointmentNumber) + "/pdf",
      "bill-" + currentAppointmentNumber + ".pdf"
    );
  } catch (err) {
    errorEl.textContent = "Could not download the bill PDF.";
    errorEl.classList.add("visible");
  } finally {
    btn.disabled = false;
    btn.textContent = originalText;
  }
});
