requireLogin();
requireRole(["ADMIN", "RECEPTIONIST"]);
renderNav("register.html");

async function loadTreatments() {

    const select =
        document.getElementById("treatmentType");

    try {

        const response =
            await apiFetch("/api/treatments");

        const treatments =
            await response.json();

        select.innerHTML = "";

        treatments.forEach(function (t) {

            const option =
                document.createElement("option");

            option.value = t.name;
            option.textContent =
                t.name + " (Rs. " + t.fee + ")";

            select.appendChild(option);
        });

    } catch (err) {

        select.innerHTML =
            "<option value=''>Select treatment</option>" +
            "<option value='Scaling'>Scaling</option>" +
            "<option value='Filling'>Filling</option>" +
            "<option value='Extraction'>Extraction</option>";
    }
}

loadTreatments();


document
    .getElementById("appointment-form")
    .addEventListener("submit", async function (event) {

        event.preventDefault();

        const patientName =
            document.getElementById("patientName").value.trim();

        const address =
            document.getElementById("address").value.trim();

        const contactNumber =
            document.getElementById("contactNumber").value.trim();

        const dentistName =
            document.getElementById("dentistName").value;

        const treatmentType =
            document.getElementById("treatmentType").value;

        const appointmentDate =
            document.getElementById("appointmentDate").value;

        const appointmentTime =
            document.getElementById("appointmentTime").value;


        /* ============================
           VALIDATION
        ============================ */

        if (!patientName) {
            showValidationPopup("Patient name is required.");
            return;
        }

        if (!address) {
            showValidationPopup("Address is required.");
            return;
        }

        if (!contactNumber) {
            showValidationPopup("Contact number is required.");
            return;
        }

        if (!/^\d{10}$/.test(contactNumber)) {
            showValidationPopup(
                "Contact number must be exactly 10 digits."
            );
            return;
        }

        if (!dentistName) {
            showValidationPopup("Please select a dentist.");
            return;
        }

        if (!treatmentType) {
            showValidationPopup("Please select a treatment.");
            return;
        }

        if (!appointmentDate) {
            showValidationPopup(
                "Please select an appointment date."
            );
            return;
        }

        if (!appointmentTime) {
            showValidationPopup(
                "Please select an appointment time."
            );
            return;
        }


        /* ============================
           PAYLOAD
        ============================ */

        const payload = {

            patientName: patientName,
            address: address,
            contactNumber: contactNumber,
            dentistName: dentistName,
            treatmentType: treatmentType,
            appointmentDate: appointmentDate,
            appointmentTime: appointmentTime
        };


        const successAlert =
            document.getElementById("success-alert");

        const errorAlert =
            document.getElementById("error-alert");

        successAlert.classList.remove("visible");
        errorAlert.classList.remove("visible");


        try {

            const response =
                await apiFetch(
                    "/api/appointments",
                    {
                        method: "POST",
                        body: JSON.stringify(payload)
                    }
                );

            const data =
                await response.json();


            if (!response.ok) {

                const firstError =
                    typeof data === "object"
                        ? Object.values(data)[0]
                        : "Could not save the appointment.";

                showValidationPopup(firstError);

                return;
            }


            showToast(
                "Appointment saved as " +
                data.appointmentNumber +
                ".",
                "success"
            );

            document
                .getElementById("appointment-form")
                .reset();

            loadTreatments();

        } catch (err) {

            showValidationPopup(
                "Could not reach the server."
            );
        }
    });