const API_BASE_URL = "";

const elements = {
    form: document.querySelector("#reserva-form"),
    filterForm: document.querySelector("#aulas-filter-form"),
    clearFilters: document.querySelector("#clear-filters"),
    refreshReservas: document.querySelector("#refresh-reservas"),
    aula: document.querySelector("#aula"),
    profesor: document.querySelector("#profesor"),
    franja: document.querySelector("#franjaHoraria"),
    aulasList: document.querySelector("#aulas-list"),
    reservasList: document.querySelector("#reservas-list"),
    status: document.querySelector("#global-status"),
    reservasCount: document.querySelector("#reservas-count"),
    aulasCount: document.querySelector("#aulas-count"),
    lastUpdate: document.querySelector("#last-update"),
    fecha: document.querySelector("#fechaReserva")
};

const state = { aulas: [], reservas: [] };

function escapeHtml(value) {
    return String(value ?? "").replace(/[&<>"']/g, (character) => ({
        "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#039;"
    }[character]));
}

function showStatus(message, type = "") {
    elements.status.textContent = message;
    elements.status.className = `status ${type}`;
}

function displayError(error) {
    showStatus(error instanceof Error ? error.message : "No se pudo completar la operación.", "error");
}

async function request(path, options = {}) {
    let response;
    try {
        response = await fetch(`${API_BASE_URL}${path}`, {
            headers: { "Content-Type": "application/json", ...options.headers },
            ...options
        });
    } catch {
        throw new Error("No se pudo conectar con el backend. Comprueba que Spring Boot está ejecutándose en localhost:8080.");
    }

    if (!response.ok) {
        if (response.status === 409) throw new Error("La franja horaria ya está ocupada para esa fecha.");
        if (response.status === 400) throw new Error("La reserva no es válida. La fecha no puede ser anterior a hoy.");
        let detail = "";
        try {
            const body = await response.json();
            detail = body.message || body.error || "";
        } catch {}
        throw new Error(detail || `El servidor respondió con el estado ${response.status}.`);
    }
    return response.status === 204 ? null : response.json();
}

function relationName(relation, fallback = "—") {
    if (!relation) return fallback;
    if (relation.nombre && relation["apellidos"]) return `${relation.nombre} ${relation["apellidos"]}`;
    return relation.nombre || relation["descripcion"] || fallback;
}

function formatTimeRange(franja) {
    if (!franja) return "—";
    return `${String(franja.horaInicio || "").slice(0, 5)} - ${String(franja.horaFin || "").slice(0, 5)}`;
}

function populateSelect(select, items, placeholder, labelBuilder) {
    select.innerHTML = `<option value="">${placeholder}</option>`;
    items.forEach((item) => {
        const option = document.createElement("option");
        option.value = item.id;
        option.textContent = labelBuilder(item);
        select.appendChild(option);
    });
}

function renderAulas(aulas) {
    elements.aulasList.innerHTML = aulas.length
        ? aulas.map((aula) => `
            <article class="room-card">
                <h3>${escapeHtml(aula.nombre)}</h3>
                <p>${escapeHtml(aula.ubicacion || "Ubicación no indicada")}</p>
                <span class="room-badge">${escapeHtml(aula.capacidad)} plazas · ${aula.tieneOrdenadores ? "Con ordenadores" : "Sin ordenadores"}</span>
            </article>`).join("")
        : '<p class="empty-state">No hay aulas que coincidan con los filtros.</p>';
}

function renderReservas(reservas) {
    elements.reservasList.innerHTML = reservas.length
        ? reservas.map((reserva) => `
            <tr>
                <td>${escapeHtml(reserva.fechaReserva)}</td>
                <td>${escapeHtml(relationName(reserva.aula))}</td>
                <td>${escapeHtml(relationName(reserva.profesor))}</td>
                <td>${escapeHtml(formatTimeRange(reserva.franjaHoraria))}</td>
                <td>${escapeHtml(reserva.motivo)}</td>
                <td><button class="button button-danger delete-reserva" data-id="${escapeHtml(reserva.id)}" type="button">Eliminar</button></td>
            </tr>`).join("")
        : '<tr><td colspan="6" class="empty-state">Todavía no hay reservas registradas.</td></tr>';
    elements.reservasCount.textContent = reservas.length;
}

async function loadReferenceData() {
    const [aulas, profesores, franjas] = await Promise.all([
        request("/aulas"),
        request("/profesores"),
        request("/franjasHorarias")
    ]);
    state.aulas = aulas;
    populateSelect(elements.aula, aulas, "Selecciona un aula", (aula) => `${aula.nombre} · ${aula.capacidad} plazas`);
    populateSelect(elements.profesor, profesores, "Selecciona un profesor", (profesor) => `${profesor.nombre} ${profesor.apellidos}`);
    populateSelect(elements.franja, franjas, "Selecciona una franja", (franja) => `${franja.descripcion} (${formatTimeRange(franja)})`);
    elements.aulasCount.textContent = aulas.length;
    renderAulas(aulas);
}

async function loadAulas() {
    const ordenadores = document.querySelector("#ordenadores").value;
    const capacidad = document.querySelector("#capacidad").value;
    const params = new URLSearchParams();
    if (ordenadores) {
        params.set("ordenadores", ordenadores);
        params.set("tieneOrdenadores", ordenadores);
    }
    if (capacidad) params.set("capacidad", capacidad);
    const aulas = await request(`/aulas${params.toString() ? `?${params}` : ""}`);
    renderAulas(aulas);
}

async function loadReservas() {
    state.reservas = await request("/reservas");
    renderReservas(state.reservas);
    elements.lastUpdate.textContent = new Date().toLocaleTimeString("es-ES", { hour: "2-digit", minute: "2-digit" });
}

async function createReserva(event) {
    event.preventDefault();
    const submitButton = elements.form.querySelector("button[type=submit]");
    submitButton.disabled = true;
    showStatus("Guardando reserva…");
    const data = new FormData(elements.form);
    try {
        await request("/reservas", {
            method: "POST",
            body: JSON.stringify({
                fechaReserva: data.get("fechaReserva"),
                motivo: data.get("motivo").trim(),
                aula: { id: Number(data.get("aula")) },
                profesor: { id: Number(data.get("profesor")) },
                franjaHoraria: { id: Number(data.get("franjaHoraria")) }
            })
        });
        showStatus("Reserva creada correctamente.", "success");
        elements.form.reset();
        await loadReservas();
    } catch (error) {
        displayError(error);
    } finally {
        submitButton.disabled = false;
    }
}

async function deleteReserva(id) {
    if (!window.confirm("¿Quieres eliminar esta reserva?")) return;
    try {
        await request(`/reservas/${encodeURIComponent(id)}`, { method: "DELETE" });
        showStatus("Reserva eliminada correctamente.", "success");
        await loadReservas();
    } catch (error) {
        displayError(error);
    }
}

elements.form.addEventListener("submit", createReserva);
elements.filterForm.addEventListener("submit", (event) => {
    event.preventDefault();
    loadAulas().catch(displayError);
});
elements.clearFilters.addEventListener("click", () => {
    elements.filterForm.reset();
    loadAulas().catch(displayError);
});
elements.refreshReservas.addEventListener("click", () => loadReservas().catch(displayError));
elements.reservasList.addEventListener("click", (event) => {
    const button = event.target.closest(".delete-reserva");
    if (button) deleteReserva(button.dataset.id).catch(displayError);
});
elements.fecha.min = new Date().toISOString().split("T")[0];

Promise.all([loadReferenceData(), loadReservas()]).catch(displayError);
