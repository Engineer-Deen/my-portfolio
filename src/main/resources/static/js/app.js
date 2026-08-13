const API_BASE_URL = 'https://portfolio-backend-0zx0.onrender.com';

document.addEventListener('DOMContentLoaded', () => {
    loadProjects();
    loadSkills();
    loadResearch();
    wireSupportButtons();
    wireContactForm();
    wireScrollReveal();
    wireActiveNavHighlight();
    setFooterYear();
    loadHeroPhoto();
    loadPostings(); });

async function loadProjects() {
    const grid = document.getElementById('projectsGrid');
    try {
        const res = await fetch(`${API_BASE_URL}/api/projects`);
        const projects = await res.json();

        document.getElementById('statProjects').textContent = projects.length + '+';

        if (projects.length === 0) {
            grid.innerHTML = `<p style="color:var(--text2);font-family:var(--mono);font-size:0.8rem;">No projects yet.</p>`;
            return;
        }

        grid.innerHTML = projects.map(p => `
      <div class="project-card reveal" data-category="${p.category}">
        <span class="project-category cat-${p.category}">${p.categoryDisplay}</span>
        <h3 class="project-title">${escapeHtml(p.title)}</h3>
        <p class="project-desc">${escapeHtml(p.description)}</p>
        <div class="project-tech">
          ${p.techList.map(t => `<span class="tech-tag">${escapeHtml(t)}</span>`).join('')}
        </div>
        ${(p.githubUrl || p.liveUrl) ? `
          <div class="project-links">
            ${p.githubUrl ? `<a href="${escapeHtml(p.githubUrl)}" target="_blank" rel="noopener">GitHub &rarr;</a>` : ''}
            ${p.liveUrl ? `<a href="${escapeHtml(p.liveUrl)}" target="_blank" rel="noopener">Live Demo &rarr;</a>` : ''}
          </div>
        ` : ''}
      </div>
    `).join('');

        observeRevealEls();
    } catch (err) {
        console.error('Failed to load projects', err);
        grid.innerHTML = `<p style="color:var(--text2);font-family:var(--mono);font-size:0.8rem;">Couldn't load projects right now.</p>`;
    }
}

function filterProjects(cat, btn) {
    document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');
    document.querySelectorAll('.project-card').forEach(c => {
        c.style.display = (cat === 'all' || c.dataset.category === cat) ? 'flex' : 'none';
    });
}

async function loadSkills() {
    const grid = document.getElementById('skillsGrid');
    try {
        const res = await fetch(`${API_BASE_URL}/api/skills`);
        const groups = await res.json();

        grid.innerHTML = groups.map(group => `
      <div class="skill-group reveal">
        <div class="skill-group-label">${escapeHtml(group.name)}</div>
        <div class="skill-pills">
          ${(group.skills || []).map(skill => `
            <span class="pill ${skill.accent ? 'accent' : ''}">${escapeHtml(skill.name)}</span>
          `).join('')}
        </div>
      </div>
    `).join('');

        observeRevealEls();
    } catch (err) {
        console.error('Failed to load skills', err);
    }
}

async function loadResearch() {
    const grid = document.getElementById('researchGrid');
    try {
        const res = await fetch(`${API_BASE_URL}/api/research`);
        const topics = await res.json();

        grid.innerHTML = topics.map((topic, i) => `
      <div class="research-card reveal">
        <div class="research-num">${String(i + 1).padStart(2, '0')}</div>
        <h3 class="research-title">${escapeHtml(topic.title)}</h3>
        <p class="research-desc">${escapeHtml(topic.description)}</p>
        <div class="learning-bar"><div class="learning-fill" style="--progress:${topic.clampedProgress}%"></div></div>
        <div class="learning-label"><span>${escapeHtml(topic.statusLabel)}</span><span>${topic.clampedProgress}%</span></div>
      </div>
    `).join('');

        observeRevealEls();
    } catch (err) {
        console.error('Failed to load research topics', err);
    }
}

function wireSupportButtons() {
    const ids = ['navSupportBtn', 'mobileSupportBtn', 'heroSupportBtn', 'supportCardBtn'];
    ids.forEach(id => {
        const btn = document.getElementById(id);
        if (!btn) return;
        btn.addEventListener('click', async (e) => {
            e.preventDefault();
            try {
                const res = await fetch(`${API_BASE_URL}/api/support/redirect`);
                const data = await res.json();
                if (data.url && data.url !== '#') {
                    window.location.href = data.url;
                }
            } catch (err) {
                console.error('Support redirect failed', err);
            }
        });
    });
}

function wireContactForm() {
    const form = document.getElementById('contactForm');
    const btn = document.getElementById('submitBtn');
    const feedback = document.getElementById('form-feedback');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        btn.textContent = 'Sending...';
        btn.disabled = true;

        document.querySelectorAll('.form-error').forEach(el => {
            el.style.display = 'none';
            el.textContent = '';
        });
        feedback.style.display = 'none';

        const payload = {
            name: document.getElementById('name').value,
            email: document.getElementById('email').value,
            subject: document.getElementById('subject').value,
            message: document.getElementById('message').value,
        };

        try {
            const res = await fetch(`${API_BASE_URL}/api/contact`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload),
            });
            const data = await res.json();

            if (data.success) {
                feedback.style.color = 'var(--neon2)';
                feedback.style.display = 'block';
                feedback.textContent = '✓ ' + data.message;
                form.reset();
            } else {
                Object.entries(data.errors || {}).forEach(([field, errMsg]) => {
                    const el = document.getElementById('err-' + field);
                    if (el) {
                        el.textContent = errMsg;
                        el.style.display = 'block';
                    }
                });
            }
        } catch (err) {
            feedback.style.color = 'var(--red)';
            feedback.style.display = 'block';
            feedback.textContent = 'Network error. Please try again.';
        }

        btn.textContent = 'Send Message';
        btn.disabled = false;
    });
}

function toggleMenu() {
    document.getElementById('mobileMenu').classList.toggle('open');
}

function setFooterYear() {
    document.getElementById('footerYear').textContent = new Date().getFullYear();
}

function loadHeroPhoto() {
    const img = document.getElementById('heroPhoto');
    const testImg = new Image();
    testImg.onload = () => { img.src = `${API_BASE_URL}/api/settings/photo`; };
    testImg.onerror = () => { /* keep the default static images/photo.jpg */ };
    testImg.src = `${API_BASE_URL}/api/settings/photo`;
}

const revealObserver = new IntersectionObserver((entries) => {
    entries.forEach((entry, i) => {
        if (entry.isIntersecting) {
            setTimeout(() => entry.target.classList.add('visible'), 80 * i);
            revealObserver.unobserve(entry.target);
        }
    });
}, { threshold: 0.12 });

function observeRevealEls() {
    document.querySelectorAll('.reveal:not(.visible)').forEach(el => revealObserver.observe(el));
}

function wireScrollReveal() {
    observeRevealEls();
}

function wireActiveNavHighlight() {
    const secs = document.querySelectorAll('section[id]');
    const links = document.querySelectorAll('.nav-links a');
    window.addEventListener('scroll', () => {
        let current = '';
        secs.forEach(s => {
            if (window.scrollY >= s.offsetTop - 100) current = s.id;
        });
        links.forEach(l => {
            l.style.color = l.getAttribute('href') === '#' + current ? 'var(--neon)' : '';
        });
    });
}

function escapeHtml(str) {
    if (str == null) return '';
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
}

async function loadPostings() {
    const intro = document.getElementById('applyIntro');
    const selector = document.getElementById('postingsSelector');

    try {
        const res = await fetch(`${API_BASE_URL}/api/postings`);
        const postings = await res.json();

        if (postings.length === 0) {
            intro.textContent = 'No open positions right now, check back soon.';
            return;
        }

        intro.textContent = 'Select a position below to apply.';
        selector.className = 'postings-grid';
        selector.innerHTML = postings.map(p => `
      <div class="posting-card">
        <div class="posting-card-title">${escapeHtml(p.title)}</div>
        <p class="posting-card-desc">${escapeHtml(p.purpose)}</p>
        <button class="btn-primary apply-btn" data-posting='${escapeHtml(JSON.stringify(p))}'>Apply</button>
      </div>
    `).join('');
    } catch (err) {
        intro.textContent = "Couldn't load open positions right now.";
    }
}

function selectPosting(posting) {
    renderApplicationForm(posting);
    document.getElementById('applicationFormContainer').scrollIntoView({
        behavior: 'smooth', block: 'start' });
}

function renderApplicationForm(posting) {
    const container = document.getElementById('applicationFormContainer');

    const customFieldsHtml = (posting.fields || []).map(f => {
        let inputHtml;

        if (f.type === 'select') {
            const options = (f.options || []).map(o => `<option value="${escapeHtml(o)}">${escapeHtml(o)}</option>`).join('');
            inputHtml = `
        <select id="field_${escapeHtml(f.label)}" ${f.required ? 'required' : ''}>
          <option value="">Select...</option>
          ${options}
        </select>
      `;
        } else if (f.type === 'radio') {
            inputHtml = `
        <div class="radio-group">
          ${(f.options || []).map(o => `
            <label class="radio-option">
              <input type="radio" name="radiofield_${escapeHtml(f.label)}" value="${escapeHtml(o)}" ${f.required ? 'required' : ''}>
              ${escapeHtml(o)}
            </label>
          `).join('')}
        </div>
      `;
        } else if (f.type === 'textarea') {
            inputHtml = `<textarea id="field_${escapeHtml(f.label)}" ${f.required ? 'required' : ''} minlength="10" rows="4"></textarea>`;
        } else {
            const extraAttrs = f.type === 'tel'
                ? 'pattern="[0-9+\\-\\s()]{7,20}" title="Enter a valid phone number"'
                : '';
            inputHtml = `<input type="${f.type}" id="field_${escapeHtml(f.label)}" ${f.required ? 'required' : ''} ${extraAttrs}>`;
        }

        return `
      <div class="form-field">
        <label>${escapeHtml(f.label)}</label>
        ${inputHtml}
      </div>
    `;
    }).join('');

    container.innerHTML = ` 
 <div class="application-form-wrap"> 
 <form class="contact-form" id="applicationForm">
 
      <div class="form-field">
        <label>Your Name</label>
        <input type="text" id="applicantName" required>
      </div>
      <div class="form-field">
        <label>Email Address</label>
        <input type="email" id="applicantEmail" required>
      </div>
      ${customFieldsHtml}
      <div class="form-field">
        <label>CV / Resume (PDF or Word, max 2MB)</label>
        <input type="file" id="applicantCv" accept=".pdf,.doc,.docx" required>
      </div>
      <button type="submit" class="btn-primary" id="applicationSubmitBtn" style="width:100%;text-align:center;">Submit Application</button>
      <div id="application-feedback" style="font-family:var(--mono);font-size:0.72rem;display:none;text-align:center;margin-top:0.5rem;"></div>
    </form>
  `;

    document.getElementById('applicationForm').addEventListener('submit', (e) => handleApplicationSubmit(e, posting));
}

async function handleApplicationSubmit(e, posting) {
    e.preventDefault();
    const btn = document.getElementById('applicationSubmitBtn');
    const feedback = document.getElementById('application-feedback');
    const cvInput = document.getElementById('applicantCv');

    if (!cvInput.files[0]) {
        feedback.style.color = 'var(--red)';
        feedback.style.display = 'block';
        feedback.textContent = 'Please attach your CV/Resume.';
        return;
    }

    if (cvInput.files[0].size > 2 * 1024 * 1024) {
        feedback.style.color = 'var(--red)';
        feedback.style.display = 'block';
        feedback.textContent = 'CV file is too large. Please use a file under 2MB.';
        return;
    }

    btn.textContent = 'Submitting...';
    btn.disabled = true;

    const responses = {};
    (posting.fields || []).forEach(f => {
        if (f.type === 'radio') {
            const checked = document.querySelector(`input[name="radiofield_${f.label}"]:checked`);
            responses[f.label] = checked ? checked.value : '';
        } else {
            const el = document.getElementById(`field_${f.label}`);
            if (el) responses[f.label] = el.value;
        }
    });

    const applicationData = {
        postingId: posting.id,
        postingTitle: posting.title,
        applicantName: document.getElementById('applicantName').value,
        applicantEmail: document.getElementById('applicantEmail').value,
        responses: responses,
    };

    const formData = new FormData();
    formData.append('application', new Blob([JSON.stringify(applicationData)], { type: 'application/json' }));
    formData.append('cv', cvInput.files[0]);

    try {
        const res = await fetch(`${API_BASE_URL}/api/applications`, {
            method: 'POST',
            body: formData,
        });
        const data = await res.json();

        feedback.style.color = data.success ? 'var(--neon2)' : 'var(--red)';
        feedback.style.display = 'block';
        feedback.textContent = data.success ? '✓ ' + data.message : data.message;

        if (data.success) {
            document.getElementById('applicationForm').reset();
        }
    } catch (err) {
        feedback.style.color = 'var(--red)';
        feedback.style.display = 'block';
        feedback.textContent = 'Network error. Please try again.';
    }

    btn.textContent = 'Submit Application';
    btn.disabled = false;
}