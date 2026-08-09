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
});

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