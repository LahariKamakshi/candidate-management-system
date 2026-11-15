const candidateForm = document.querySelector("#candidate-form");
const filterForm = document.querySelector("#filter-form");
const rows = document.querySelector("#candidate-rows");
const message = document.querySelector("#message");
const pageSummary = document.querySelector("#page-summary");
const pageState = { page: 0, size: 10, totalPages: 0 };

function showMessage(text, isError = false) {
	message.textContent = text;
	message.classList.toggle("error", isError);
}

async function request(url, options = {}) {
	const response = await fetch(url, options);
	if (!response.ok) {
		const contentType = response.headers.get("content-type") || "";
		const problem = contentType.includes("json") ? await response.json() : {};
		const fieldErrors = Array.isArray(problem.errors) ? problem.errors.join(" ") : "";
		throw new Error(fieldErrors
			? `${problem.detail || "Invalid request."} ${fieldErrors}`
			: problem.detail || `Request failed (${response.status}).`);
	}
	if (response.status === 204) {
		return null;
	}
	return response.json();
}

function escapeHtml(value) {
	return String(value ?? "").replace(/[&<>"']/g, (character) => ({
		"&": "&amp;",
		"<": "&lt;",
		">": "&gt;",
		'"': "&quot;",
		"'": "&#39;",
	})[character]);
}

function renderLinkedIn(linkedIn) {
	const profileUrl = String(linkedIn ?? "").trim();
	if (!profileUrl) {
		return "-";
	}
	return `<a href="${escapeHtml(profileUrl)}" target="_blank" rel="noopener noreferrer">View Profile</a>`;
}

async function loadCandidates() {
	const params = new URLSearchParams({
		page: String(pageState.page),
		size: String(pageState.size),
		sortBy: "id",
		sortDirection: "desc",
	});
	for (const [key, value] of new FormData(filterForm).entries()) {
		if (String(value).trim()) {
			params.set(key, String(value).trim());
		}
	}

	try {
		const result = await request(`/api/candidates?${params.toString()}`);
		rows.innerHTML = result.content.map((candidate) => `
			<tr>
				<td>${escapeHtml(candidate.fullName)}</td>
				<td>${escapeHtml(candidate.email)}<br>${escapeHtml(candidate.phone)}</td>
				<td>${escapeHtml(candidate.skills)}</td>
				<td>${escapeHtml(candidate.experienceYears)} years</td>
				<td>${escapeHtml(candidate.location)}</td>
				<td>${escapeHtml(candidate.status)}</td>
				<td>${renderLinkedIn(candidate.linkedIn)}</td>
				<td><div class="row-actions">
					<button class="secondary" type="button" data-edit="${candidate.id}">Edit</button>
					<button class="secondary" type="button" data-delete="${candidate.id}">Delete</button>
				</div></td>
			</tr>`).join("");
		if (result.content.length === 0) {
				rows.innerHTML = '<tr><td colspan="8">No candidates match these filters.</td></tr>';
		}
		pageState.totalPages = result.totalPages;
		pageSummary.textContent = result.totalElements === 0
			? "No results"
			: `Page ${result.page + 1} of ${result.totalPages} | ${result.totalElements} candidates`;
		document.querySelector("#previous-page").disabled = result.first;
		document.querySelector("#next-page").disabled = result.last;
	} catch (error) {
		showMessage(error.message, true);
	}
}

function resetCandidateForm() {
	candidateForm.reset();
	document.querySelector("#candidate-id").value = "";
	document.querySelector("#status").value = "ACTIVE";
	document.querySelector("#candidate-form-title").textContent = "Add a candidate";
	document.querySelector("#save-button").textContent = "Save candidate";
	document.querySelector("#cancel-edit").hidden = true;
}

candidateForm.addEventListener("submit", async (event) => {
	event.preventDefault();
	const id = document.querySelector("#candidate-id").value;
	const candidate = {
		fullName: document.querySelector("#full-name").value,
		email: document.querySelector("#email").value,
		phone: document.querySelector("#phone").value,
		location: document.querySelector("#location").value,
		experienceYears: Number(document.querySelector("#experience-years").value),
		status: document.querySelector("#status").value,
		skills: document.querySelector("#skills").value,
		linkedIn: document.querySelector("#linkedIn").value,
	};
	try {
		await request(id ? `/api/candidates/${id}` : "/api/candidates", {
			method: id ? "PUT" : "POST",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify(candidate),
		});
		resetCandidateForm();
		showMessage(id ? "Candidate updated." : "Candidate added.");
		pageState.page = 0;
		await loadCandidates();
	} catch (error) {
		showMessage(error.message, true);
	}
});

document.querySelector("#cancel-edit").addEventListener("click", resetCandidateForm);

filterForm.addEventListener("submit", async (event) => {
	event.preventDefault();
	pageState.page = 0;
	await loadCandidates();
});

document.querySelector("#clear-filters").addEventListener("click", async () => {
	filterForm.reset();
	pageState.page = 0;
	await loadCandidates();
});

document.querySelector("#previous-page").addEventListener("click", async () => {
	if (pageState.page > 0) {
		pageState.page -= 1;
		await loadCandidates();
	}
});

document.querySelector("#next-page").addEventListener("click", async () => {
	if (pageState.page + 1 < pageState.totalPages) {
		pageState.page += 1;
		await loadCandidates();
	}
});

rows.addEventListener("click", async (event) => {
	const editButton = event.target.closest("[data-edit]");
	const deleteButton = event.target.closest("[data-delete]");
	if (editButton) {
		try {
			const candidate = await request(`/api/candidates/${editButton.dataset.edit}`);
			document.querySelector("#candidate-id").value = candidate.id;
			document.querySelector("#full-name").value = candidate.fullName;
			document.querySelector("#email").value = candidate.email;
			document.querySelector("#phone").value = candidate.phone || "";
			document.querySelector("#location").value = candidate.location || "";
			document.querySelector("#experience-years").value = candidate.experienceYears;
			document.querySelector("#status").value = candidate.status;
			document.querySelector("#skills").value = candidate.skills || "";
			document.querySelector("#linkedIn").value = candidate.linkedIn || "";
			document.querySelector("#candidate-form-title").textContent = "Edit candidate";
			document.querySelector("#save-button").textContent = "Update candidate";
			document.querySelector("#cancel-edit").hidden = false;
			window.scrollTo({ top: 0, behavior: "smooth" });
			showMessage("Editing candidate.");
		} catch (error) {
			showMessage(error.message, true);
		}
	} else if (deleteButton) {
		if (!window.confirm("Delete this candidate? This cannot be undone.")) {
			return;
		}
		try {
			await request(`/api/candidates/${deleteButton.dataset.delete}`, { method: "DELETE" });
			showMessage("Candidate deleted.");
			await loadCandidates();
		} catch (error) {
			showMessage(error.message, true);
		}
	}
});

loadCandidates();
