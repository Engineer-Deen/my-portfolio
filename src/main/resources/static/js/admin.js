const API_BASE_URL = 'https://portfolio-backend-0zx0.onrender.com';

const SIERRA_LEONE_CITIES = [
    'Freetown', 'Bo', 'Kenema', 'Makeni', 'Koidu Town', 'Waterloo', 'Lunsar',
    'Port Loko', 'Kabala', 'Magburaka', 'Kailahun', 'Kambia', 'Moyamba',
    'Bonthe', 'Pujehun', 'Segbwema', 'Bumpe', 'Binkolo', 'Rokupr', 'Pendembu',
    'Yengema', 'Newton', 'Hastings', 'Goderich', 'York', 'Regent', 'Tongo',
    'Falaba', 'Taiama', 'Njala', 'Mattru Jong', 'Panguma', 'Zimmi', 'Daru',
    'Gerihun', 'Jaiama', 'Masingbi', 'Mile 91', 'Kayima', 'Yele', 'Kukuna',
    'Kissy', 'Wellington', 'Songo', 'Rotifunk',
];

const EDUCATION_LEVELS = [
    'Primary Education',
    'Secondary Education (WASSCE)',
    'Vocational / Technical Diploma',
    "Bachelor's Degree",
    "Master's Degree",
    'PhD / Doctorate',
    'Other',
];

const STANDARD_FIELDS = [
    { label: 'Phone Number', type: 'tel', required: true },
    { label: 'City', type: 'select', required: true, options: SIERRA_LEONE_CITIES },
    { label: 'Date of Birth', type: 'date', required: true },
    { label: 'Highest Level of Education', type: 'select', required: true, options: EDUCATION_LEVELS },
    { label: 'Most Recent Employer/Role', type: 'text', required: false },
    { label: 'Years of Relevant Experience', type: 'number', required: true },
    { label: 'Brief Work History', type: 'textarea', required: false },
    { label: 'GitHub Profile/Portfolio Link', type: 'text', required: false },
    { label: 'Technologies/Languages Known', type: 'text', required: false },
    { label: 'LinkedIn Profile', type: 'text', required: false },
    { label: 'Why Do You Want This Role?', type: 'textarea', required: true },
    { label: 'How Did You Hear About This Position?', type: 'text', required: false },
    { label: 'References (Name + Contact)', type: 'textarea', required: false },
];

let customFieldsBuilder = [];

document.addEventListener('DOMContentLoaded', () => {
    const savedPassword = sessionStorage.getItem('adminPassword');
    if (savedPassword) {
        showDashboard();
    }

    document.getElementById('loginBtn').addEventListener('click', handleLogin);
    document.getElementById('logoutBtn').addEventListener('click', handleLogout);
    document.getElementById('uploadPhotoBtn').addEventListener('click', handlePhotoUpload);
    document.getElementById('projectForm').addEventListener('submit', handleProjectSubmit);
    document.getElementById('projCancelBtn').addEventListener('click', resetProjectForm);
    document.getElementById('skillGroupForm').addEventListener('submit', handleSkillGroupSubmit);
    document.getElementById('skillGroupCancelBtn').addEventListener('click', resetSkillGroupForm);
    document.getElementById('researchForm').addEventListener('submit', handleResearchSubmit);
    document.getElementById('researchCancelBtn').addEventListener('click', resetResearchForm);
    document.getElementById('postingForm').addEventListener('submit', handlePostingSubmit);
    document.getElementById('postingCancelBtn').addEventListener('click', resetPostingForm);

    document.getElementById('cfType').addEventListener('change', (e) => {
        const optionsBox = document.getElementById('cfOptions');
        if (e.target.value === 'select' || e.target.value === 'radio') {
            optionsBox.classList.remove('hidden');
        } else {
            optionsBox.classList.add('hidden');
        }
    });
    document.getElementById('addCustomFieldBtn').addEventListener('click', addCustomBuilderField);

    resetPostingForm();
});

function handleLogin() {
    const password = document.getElementById('passwordInput').value;
    if (!password) return;
    sessionStorage.setItem('adminPassword', password);
    showDashboard();
}

function handleLogout() {
    sessionStorage.removeItem('adminPassword');
    document.getElementById('dashboard').classList.add('hidden');
    document.getElementById('loginScreen').classList.remove('hidden');
}

function showDashboard() {
    document.getElementById('loginScreen').classList.add('hidden');
    document.getElementById('dashboard').classList.remove('hidden');
    loadAllAdminData();
}

function loadAllAdminData() {
    loadProjectsList();
    loadSkillGroupsList();
    loadResearchList();
    loadPostingsList();
}

function adminHeaders(extra = {}) {
    return {
        'X-Admin-Password': sessionStorage.getItem('adminPassword'),
        ...extra,
    };
}

function handleAuthFailure() {
    sessionStorage.removeItem('adminPassword');
    document.getElementById('dashboard').classList.add('hidden');
    document.getElementById('loginScreen').classList.remove('hidden');
    document.getElementById('loginError').textContent = 'Session expired or incorrect password. Please log in again.';
}

function escapeHtml(str) {
    if (str == null) return '';
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
}

/* ---------- Photo ---------- */

async function handlePhotoUpload() {
    const fileInput = document.getElementById('photoInput');
    const status = document.getElementById('photoStatus');

    if (!fileInput.files[0]) {
        status.textContent = 'Choose a file first.';
        return;
    }

    if (fileInput.files[0].size > 700 * 1024) {
        status.textContent = 'File is too large. Please use an image under 700KB.';
        return;
    }

    const formData = new FormData();
    formData.append('file', fileInput.files[0]);

    status.textContent = 'Uploading...';

    try {
        const res = await fetch(`${API_BASE_URL}/api/settings/photo`, {
            method: 'POST',
            headers: adminHeaders(),
            body: formData,
        });

        if (res.status === 401) {
            handleAuthFailure();
            return;
        }

        const data = await res.json();
        status.textContent = data.success ? '✓ Photo updated.' : data.message;
    } catch (err) {
        status.textContent = 'Upload failed. Please try again.';
    }
}

/* ---------- Projects ---------- */

async function loadProjectsList() {
    const list = document.getElementById('projectsList');
    try {
        const res = await fetch(`${API_BASE_URL}/api/projects`);
        const projects = await res.json();

        list.innerHTML = projects.map(p => `
      <div class="item-row">
        <span>${p.title}</span>
        <span class="item-actions">
          <button onclick='editProject(${JSON.stringify(p)})'>Edit</button>
          <button onclick="deleteProject('${p.id}')">Delete</button>
        </span>
      </div>
    `).join('');
    } catch (err) {
        list.innerHTML = '<p class="status-text">Failed to load projects.</p>';
    }
}

function editProject(project) {
    document.getElementById('projectId').value = project.id;
    document.getElementById('projTitle').value = project.title;
    document.getElementById('projCategory').value = project.category;
    document.getElementById('projDescription').value = project.description;
    document.getElementById('projPurpose').value = project.purpose || '';
    document.getElementById('projTechnologies').value = project.technologies || '';
    document.getElementById('projGithubUrl').value = project.githubUrl || '';
    document.getElementById('projLiveUrl').value = project.liveUrl || '';
    document.getElementById('projOrder').value = project.order;
    document.getElementById('projSubmitBtn').textContent = 'Save Changes';
    document.getElementById('projCancelBtn').classList.remove('hidden');
    window.scrollTo({ top: document.getElementById('projectForm').offsetTop, behavior: 'smooth' });
}

function resetProjectForm() {
    document.getElementById('projectForm').reset();
    document.getElementById('projectId').value = '';
    document.getElementById('projSubmitBtn').textContent = 'Add Project';
    document.getElementById('projCancelBtn').classList.add('hidden');
}

async function handleProjectSubmit(e) {
    e.preventDefault();
    const status = document.getElementById('projectStatus');

    const id = document.getElementById('projectId').value;
    const payload = {
        title: document.getElementById('projTitle').value,
        category: document.getElementById('projCategory').value,
        description: document.getElementById('projDescription').value,
        purpose: document.getElementById('projPurpose').value,
        technologies: document.getElementById('projTechnologies').value,
        githubUrl: document.getElementById('projGithubUrl').value,
        liveUrl: document.getElementById('projLiveUrl').value,
        order: parseInt(document.getElementById('projOrder').value, 10),
        active: true,
    };

    try {
        const res = await fetch(
            id ? `${API_BASE_URL}/api/projects/${id}` : `${API_BASE_URL}/api/projects`,
            {
                method: id ? 'PUT' : 'POST',
                headers: adminHeaders({ 'Content-Type': 'application/json' }),
                body: JSON.stringify(payload),
            }
        );

        if (res.status === 401) {
            handleAuthFailure();
            return;
        }

        status.textContent = id ? '✓ Project updated.' : '✓ Project added.';
        resetProjectForm();
        loadProjectsList();
    } catch (err) {
        status.textContent = 'Save failed. Please try again.';
    }
}

async function deleteProject(id) {
    if (!confirm('Delete this project?')) return;

    try {
        const res = await fetch(`${API_BASE_URL}/api/projects/${id}`, {
            method: 'DELETE',
            headers: adminHeaders(),
        });

        if (res.status === 401) {
            handleAuthFailure();
            return;
        }

        loadProjectsList();
    } catch (err) {
        alert('Delete failed. Please try again.');
    }
}

/* ---------- Skills ---------- */

async function loadSkillGroupsList() {
    const list = document.getElementById('skillGroupsList');
    try {
        const res = await fetch(`${API_BASE_URL}/api/skills`);
        const groups = await res.json();

        list.innerHTML = groups.map(g => `
      <div class="item-row">
        <span>${g.name} (${(g.skills || []).length} skills)</span>
        <span class="item-actions">
          <button onclick='editSkillGroup(${JSON.stringify(g)})'>Edit</button>
          <button onclick="deleteSkillGroup('${g.id}')">Delete</button>
        </span>
      </div>
    `).join('');
    } catch (err) {
        list.innerHTML = '<p class="status-text">Failed to load skill groups.</p>';
    }
}

function skillsToText(skills) {
    return (skills || []).map(s => s.accent ? `${s.name}|accent` : s.name).join('\n');
}

function textToSkills(text) {
    return text.split('\n').map(line => line.trim()).filter(Boolean).map((line, i) => {
        const [name, flag] = line.split('|').map(s => s.trim());
        return { name, accent: flag === 'accent', order: i + 1 };
    });
}

function editSkillGroup(group) {
    document.getElementById('skillGroupId').value = group.id;
    document.getElementById('skillGroupName').value = group.name;
    document.getElementById('skillGroupOrder').value = group.order;
    document.getElementById('skillGroupSkills').value = skillsToText(group.skills);
    document.getElementById('skillGroupSubmitBtn').textContent = 'Save Changes';
    document.getElementById('skillGroupCancelBtn').classList.remove('hidden');
    window.scrollTo({ top: document.getElementById('skillGroupForm').offsetTop, behavior: 'smooth' });
}

function resetSkillGroupForm() {
    document.getElementById('skillGroupForm').reset();
    document.getElementById('skillGroupId').value = '';
    document.getElementById('skillGroupSubmitBtn').textContent = 'Add Skill Group';
    document.getElementById('skillGroupCancelBtn').classList.add('hidden');
}

async function handleSkillGroupSubmit(e) {
    e.preventDefault();
    const status = document.getElementById('skillGroupStatus');

    const id = document.getElementById('skillGroupId').value;
    const payload = {
        name: document.getElementById('skillGroupName').value,
        order: parseInt(document.getElementById('skillGroupOrder').value, 10),
        skills: textToSkills(document.getElementById('skillGroupSkills').value),
    };

    try {
        const res = await fetch(
            id ? `${API_BASE_URL}/api/skills/${id}` : `${API_BASE_URL}/api/skills`,
            {
                method: id ? 'PUT' : 'POST',
                headers: adminHeaders({ 'Content-Type': 'application/json' }),
                body: JSON.stringify(payload),
            }
        );

        if (res.status === 401) {
            handleAuthFailure();
            return;
        }

        status.textContent = id ? '✓ Skill group updated.' : '✓ Skill group added.';
        resetSkillGroupForm();
        loadSkillGroupsList();
    } catch (err) {
        status.textContent = 'Save failed. Please try again.';
    }
}

async function deleteSkillGroup(id) {
    if (!confirm('Delete this skill group?')) return;

    try {
        const res = await fetch(`${API_BASE_URL}/api/skills/${id}`, {
            method: 'DELETE',
            headers: adminHeaders(),
        });

        if (res.status === 401) {
            handleAuthFailure();
            return;
        }

        loadSkillGroupsList();
    } catch (err) {
        alert('Delete failed. Please try again.');
    }
}

/* ---------- Research ---------- */

async function loadResearchList() {
    const list = document.getElementById('researchList');
    try {
        const res = await fetch(`${API_BASE_URL}/api/research`);
        const topics = await res.json();

        list.innerHTML = topics.map(t => `
      <div class="item-row">
        <span>${t.title}</span>
        <span class="item-actions">
          <button onclick='editResearch(${JSON.stringify(t)})'>Edit</button>
          <button onclick="deleteResearch('${t.id}')">Delete</button>
        </span>
      </div>
    `).join('');
    } catch (err) {
        list.innerHTML = '<p class="status-text">Failed to load research topics.</p>';
    }
}

function editResearch(topic) {
    document.getElementById('researchId').value = topic.id;
    document.getElementById('researchTitle').value = topic.title;
    document.getElementById('researchDescription').value = topic.description;
    document.getElementById('researchProgress').value = topic.progress;
    document.getElementById('researchStatusLabel').value = topic.statusLabel;
    document.getElementById('researchOrder').value = topic.order;
    document.getElementById('researchSubmitBtn').textContent = 'Save Changes';
    document.getElementById('researchCancelBtn').classList.remove('hidden');
    window.scrollTo({ top: document.getElementById('researchForm').offsetTop, behavior: 'smooth' });
}

function resetResearchForm() {
    document.getElementById('researchForm').reset();
    document.getElementById('researchId').value = '';
    document.getElementById('researchSubmitBtn').textContent = 'Add Research Topic';
    document.getElementById('researchCancelBtn').classList.add('hidden');
}

async function handleResearchSubmit(e) {
    e.preventDefault();
    const status = document.getElementById('researchStatus');

    const id = document.getElementById('researchId').value;
    const payload = {
        title: document.getElementById('researchTitle').value,
        description: document.getElementById('researchDescription').value,
        progress: parseInt(document.getElementById('researchProgress').value, 10),
        statusLabel: document.getElementById('researchStatusLabel').value,
        order: parseInt(document.getElementById('researchOrder').value, 10),
        active: true,
    };

    try {
        const res = await fetch(
            id ? `${API_BASE_URL}/api/research/${id}` : `${API_BASE_URL}/api/research`,
            {
                method: id ? 'PUT' : 'POST',
                headers: adminHeaders({ 'Content-Type': 'application/json' }),
                body: JSON.stringify(payload),
            }
        );

        if (res.status === 401) {
            handleAuthFailure();
            return;
        }

        status.textContent = id ? '✓ Research topic updated.' : '✓ Research topic added.';
        resetResearchForm();
        loadResearchList();
    } catch (err) {
        status.textContent = 'Save failed. Please try again.';
    }
}

async function deleteResearch(id) {
    if (!confirm('Delete this research topic?')) return;

    try {
        const res = await fetch(`${API_BASE_URL}/api/research/${id}`, {
            method: 'DELETE',
            headers: adminHeaders(),
        });

        if (res.status === 401) {
            handleAuthFailure();
            return;
        }

        loadResearchList();
    } catch (err) {
        alert('Delete failed. Please try again.');
    }
}

/* ---------- Job Postings ---------- */

async function loadPostingsList() {
    const list = document.getElementById('postingsList');
    try {
        const res = await fetch(`${API_BASE_URL}/api/postings/all`);
        const postings = await res.json();

        list.innerHTML = postings.map(p => `
      <div class="item-row">
        <span>${p.title} ${p.active ? '' : '(inactive)'}</span>
        <span class="item-actions">
          <button onclick='editPosting(${JSON.stringify(p)})'>Edit</button>
          <button onclick="deletePosting('${p.id}')">Delete</button>
        </span>
      </div>
    `).join('');
    } catch (err) {
        list.innerHTML = '<p class="status-text">Failed to load job postings.</p>';
    }
}

function renderStandardFieldsChecklist(existingFields = []) {
    const container = document.getElementById('standardFieldsChecklist');
    container.innerHTML = STANDARD_FIELDS.map((f, i) => {
        const match = existingFields.find(ef => ef.label === f.label);
        const included = !!match;
        const required = match ? match.required : f.required;
        return `
      <div class="field-checkbox-row">
        <span class="field-name">
          <input type="checkbox" class="std-include" data-index="${i}" ${included ? 'checked' : ''}>
          ${escapeHtml(f.label)}
        </span>
        <span class="required-toggle">
          <input type="checkbox" class="std-required" data-index="${i}" ${required ? 'checked' : ''}> Required
        </span>
      </div>
    `;
    }).join('');
}

function renderCustomFieldsBuilderList() {
    const container = document.getElementById('customFieldsBuilderList');
    if (customFieldsBuilder.length === 0) {
        container.innerHTML = '<p class="status-text">No custom fields added yet.</p>';
        return;
    }
    container.innerHTML = customFieldsBuilder.map((f, i) => `
    <div class="item-row">
      <span>${escapeHtml(f.label)} (${f.type}${f.required ? ', required' : ''})</span>
      <span class="item-actions">
        <button type="button" onclick="removeCustomBuilderField(${i})">Remove</button>
      </span>
    </div>
  `).join('');
}

function addCustomBuilderField() {
    const label = document.getElementById('cfLabel').value.trim();
    const type = document.getElementById('cfType').value;
    const required = document.getElementById('cfRequired').checked;
    const optionsRaw = document.getElementById('cfOptions').value;

    if (!label) {
        alert('Please enter a field label.');
        return;
    }

    const field = { label, type, required };
    if (type === 'select' || type === 'radio') {
        field.options = optionsRaw.split('\n').map(o => o.trim()).filter(Boolean);
        if (field.options.length === 0) {
            alert('Please enter at least one option for this field type.');
            return;
        }
    }

    customFieldsBuilder.push(field);
    renderCustomFieldsBuilderList();

    document.getElementById('cfLabel').value = '';
    document.getElementById('cfType').value = 'text';
    document.getElementById('cfRequired').checked = false;
    document.getElementById('cfOptions').value = '';
    document.getElementById('cfOptions').classList.add('hidden');
}

function removeCustomBuilderField(index) {
    customFieldsBuilder.splice(index, 1);
    renderCustomFieldsBuilderList();
}

function collectPostingFields() {
    const fields = [];
    let order = 1;

    document.querySelectorAll('.std-include').forEach(cb => {
        if (!cb.checked) return;
        const i = parseInt(cb.dataset.index, 10);
        const std = STANDARD_FIELDS[i];
        const requiredCb = document.querySelector(`.std-required[data-index="${i}"]`);
        const field = {
            label: std.label,
            type: std.type,
            required: requiredCb.checked,
            order: order++,
        };
        if (std.options) field.options = std.options;
        fields.push(field);
    });

    customFieldsBuilder.forEach(f => {
        fields.push({ ...f, order: order++ });
    });

    return fields;
}

function editPosting(posting) {
    document.getElementById('postingId').value = posting.id;
    document.getElementById('postingTitle').value = posting.title;
    document.getElementById('postingPurpose').value = posting.purpose;
    document.getElementById('postingOrder').value = posting.order;
    document.getElementById('postingActive').checked = posting.active;

    const stdLabels = STANDARD_FIELDS.map(f => f.label);
    customFieldsBuilder = (posting.fields || []).filter(f => !stdLabels.includes(f.label));
    renderStandardFieldsChecklist(posting.fields || []);
    renderCustomFieldsBuilderList();

    document.getElementById('postingSubmitBtn').textContent = 'Save Changes';
    document.getElementById('postingCancelBtn').classList.remove('hidden');
    window.scrollTo({ top: document.getElementById('postingForm').offsetTop, behavior: 'smooth' });
}

function resetPostingForm() {
    document.getElementById('postingForm').reset();
    document.getElementById('postingId').value = '';
    document.getElementById('postingActive').checked = true;
    customFieldsBuilder = [];
    renderStandardFieldsChecklist([]);
    renderCustomFieldsBuilderList();
    document.getElementById('cfOptions').classList.add('hidden');
    document.getElementById('postingSubmitBtn').textContent = 'Add Posting';
    document.getElementById('postingCancelBtn').classList.add('hidden');
}

async function handlePostingSubmit(e) {
    e.preventDefault();
    const status = document.getElementById('postingStatus');

    const id = document.getElementById('postingId').value;
    const payload = {
        title: document.getElementById('postingTitle').value,
        purpose: document.getElementById('postingPurpose').value,
        order: parseInt(document.getElementById('postingOrder').value, 10),
        active: document.getElementById('postingActive').checked,
        fields: collectPostingFields(),
    };

    try {
        const res = await fetch(
            id ? `${API_BASE_URL}/api/postings/${id}` : `${API_BASE_URL}/api/postings`,
            {
                method: id ? 'PUT' : 'POST',
                headers: adminHeaders({ 'Content-Type': 'application/json' }),
                body: JSON.stringify(payload),
            }
        );

        if (res.status === 401) {
            handleAuthFailure();
            return;
        }

        status.textContent = id ? '✓ Posting updated.' : '✓ Posting added.';
        resetPostingForm();
        loadPostingsList();
    } catch (err) {
        status.textContent = 'Save failed. Please try again.';
    }
}

async function deletePosting(id) {
    if (!confirm('Delete this job posting?')) return;

    try {
        const res = await fetch(`${API_BASE_URL}/api/postings/${id}`, {
            method: 'DELETE',
            headers: adminHeaders(),
        });

        if (res.status === 401) {
            handleAuthFailure();
            return;
        }

        loadPostingsList();
    } catch (err) {
        alert('Delete failed. Please try again.');
    }
}