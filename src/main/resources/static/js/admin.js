const API_BASE_URL = 'https://portfolio-backend-0zx0.onrender.com';

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
    loadProjectsList();
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

async function handlePhotoUpload() {
    const fileInput = document.getElementById('photoInput');
    const status = document.getElementById('photoStatus');

    if (!fileInput.files[0]) {
        status.textContent = 'Choose a file first.';
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
        isActive: true,
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