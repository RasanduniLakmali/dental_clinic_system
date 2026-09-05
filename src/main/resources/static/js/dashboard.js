requireLogin();

const role = currentRole();
renderNav("dashboard.html");

async function loadAppointments() {
    const subtitle = document.getElementById("subtitle");
    const body = document.getElementById("appointments-body");

    try {
        let response;

        if (role === "DENTIST") {
            response = await apiFetch(
                "/api/appointments/dentist/" +
                encodeURIComponent(currentDisplayName())
            );
        } else {
            response = await apiFetch("/api/appointments");
        }

        const appointments = await response.json();

        // ============================
        // DASHBOARD STATISTICS
        // ============================

        const todayCount =
            document.getElementById("today-count");

        const scheduledCount =
            document.getElementById("scheduled-count");

        const completedCount =
            document.getElementById("completed-count");

        if (todayCount) {
            todayCount.textContent = appointments.length;
        }

        if (scheduledCount) {
            scheduledCount.textContent =
                appointments.filter(
                    a => a.status === "SCHEDULED"
                ).length;
        }

        if (completedCount) {
            completedCount.textContent =
                appointments.filter(
                    a => a.status === "COMPLETED"
                ).length;
        }

        // ============================
        // APPOINTMENT TABLE
        // ============================

        subtitle.textContent =
            appointments.length +
            " appointment" +
            (appointments.length === 1 ? "" : "s") +
            (role === "DENTIST"
                ? " assigned to you"
                : " scheduled");

        body.innerHTML = "";

        appointments.forEach(function (a) {
            const row = document.createElement("tr");

            row.innerHTML =
                "<td>" + a.appointmentNumber + "</td>" +
                "<td>" + a.patientName + "</td>" +
                "<td>" + a.dentistName + "</td>" +
                "<td>" + a.treatmentType + "</td>" +
                "<td>" + a.appointmentDate + "</td>" +
                "<td>" + a.appointmentTime + "</td>" +
                "<td>" + a.status + "</td>";

            body.appendChild(row);
        });

    } catch (err) {
        console.error(err);
        subtitle.textContent = "Could not load appointments.";
    }
}

loadAppointments();