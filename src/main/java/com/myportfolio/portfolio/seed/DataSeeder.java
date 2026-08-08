package com.myportfolio.portfolio.seed;

import com.google.cloud.firestore.Firestore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Component
public class DataSeeder implements CommandLineRunner {

    private final Firestore firestore;

    @Value("${app.seed-data:false}")
    private boolean seedEnabled;

    public DataSeeder(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!seedEnabled) return; // safety switch — see note below

        System.out.println("Seeding Firestore...");
        seedProjects();
        seedSkillGroups();
        seedResearchTopics();
        System.out.println("✓ Firestore seeded successfully!");
    }

    private void seedProjects() throws ExecutionException, InterruptedException {
        List<Map<String, Object>> projects = List.of(
                projectDoc("System Integrity Monitor", "cyber",
                        "A Python-based daemon that continuously monitors critical file system paths, process lists, and open network ports. Generates alerts and logs anomalies to a PostgreSQL audit table.",
                        "Detect early-stage compromise indicators on Linux systems.",
                        "Python, PostgreSQL, Linux, Daemon/Service", 1),
                projectDoc("Port Scanner & Service Fingerprinter", "cyber",
                        "A multithreaded network reconnaissance tool for authorized security audits. Identifies open ports, attempts banner grabbing, and outputs structured reports.",
                        "Authorized network auditing with ethical guardrails.",
                        "Python, Socket, Threading, Network Security", 2),
                projectDoc("Secure Auth API", "backend",
                        "A Django REST Framework authentication service with JWT token rotation, rate-limited login endpoints, bcrypt password hashing, and CSRF protection.",
                        "Reusable auth microservice for other projects.",
                        "Django, DRF, JWT, PostgreSQL, bcrypt", 3),
                projectDoc("Encrypted Notes Service", "backend",
                        "A Flask microservice where notes are AES-256 encrypted at rest. Users authenticate via HMAC-signed tokens. The server never holds plaintext.",
                        "Zero-knowledge note storage demonstration.",
                        "Flask, Python, AES-256, HMAC, SQLite", 4),
                projectDoc("Audit Trail System", "db",
                        "A PostgreSQL-backed audit logging system using triggers and stored procedures to capture every row-level change. Append-only design integrated with Django admin.",
                        "Immutable audit logs for compliance and forensics.",
                        "PostgreSQL, SQL Triggers, Django, Audit Logging", 5),
                projectDoc("Malware Signature Checker", "cyber",
                        "Computes and matches file hashes against a local database of known malicious signatures. Supports MD5, SHA-1, and SHA-256. Built for offline environments.",
                        "Offline malware detection for air-gapped machines.",
                        "Python, Hashing, SQLite, Forensics", 6)
        );

        for (Map<String, Object> p : projects) {
            firestore.collection("projects").add(p).get();
        }
        System.out.println("  ✓ " + projects.size() + " projects");
    }

    private Map<String, Object> projectDoc(String title, String category, String description,
                                           String purpose, String technologies, int order) {
        Map<String, Object> doc = new HashMap<>();
        doc.put("title", title);
        doc.put("category", category);
        doc.put("description", description);
        doc.put("purpose", purpose);
        doc.put("technologies", technologies);
        doc.put("github_url", "");
        doc.put("live_url", "");
        doc.put("order", order);
        doc.put("is_active", true);
        return doc;
    }

    private void seedSkillGroups() throws ExecutionException, InterruptedException {
        List<Map<String, Object>> groups = List.of(
                skillGroupDoc("Programming Languages", 1, List.of(
                        skill("Python", true), skill("Java", false),
                        skill("Bash / Shell", false), skill("SQL", false)
                )),
                skillGroupDoc("Backend Frameworks", 2, List.of(
                        skill("Django", true), skill("Django REST Framework", false),
                        skill("Flask", false), skill("WSGI", false)
                )),
                skillGroupDoc("Database", 3, List.of(
                        skill("PostgreSQL", true), skill("SQLite", false),
                        skill("ORM / Raw SQL", false), skill("Query Optimization", false)
                )),
                skillGroupDoc("Cybersecurity", 4, List.of(
                        skill("Secure Coding", true), skill("Threat Modelling", false),
                        skill("OWASP Top 10", false), skill("System Protection Scripts", false),
                        skill("Network Scanning", false)
                )),
                skillGroupDoc("Dev & Ops", 5, List.of(
                        skill("Git / GitHub", false), skill("Linux (Ubuntu / Kali)", false),
                        skill("Docker (learning)", false), skill("Nginx", false)
                )),
                skillGroupDoc("Mobile (Future)", 6, List.of(
                        skill("Java Android", false), skill("Secure Mobile APIs", false), skill("JWT Auth", false)
                ))
        );

        for (Map<String, Object> g : groups) {
            firestore.collection("skill_groups").add(g).get();
        }
        System.out.println("  ✓ " + groups.size() + " skill groups");
    }

    private Map<String, Object> skillGroupDoc(String name, int order, List<Map<String, Object>> skills) {
        Map<String, Object> doc = new HashMap<>();
        doc.put("name", name);
        doc.put("order", order);
        doc.put("skills", skills);
        return doc;
    }

    private Map<String, Object> skill(String name, boolean accent) {
        Map<String, Object> s = new HashMap<>();
        s.put("name", name);
        s.put("is_accent", accent);
        return s;
    }

    private void seedResearchTopics() throws ExecutionException, InterruptedException {
        List<Map<String, Object>> topics = List.of(
                researchDoc("Network Security", 72, "In Progress", 1,
                        "Studying TCP/IP stack vulnerabilities, packet analysis with Wireshark, intrusion detection patterns, and secure network architecture design."),
                researchDoc("Ethical Hacking Fundamentals", 58, "In Progress", 2,
                        "CEH curriculum: reconnaissance, exploitation basics, post-exploitation, and responsible disclosure. Practicing in legal lab environments."),
                researchDoc("Secure Backend Architecture", 65, "In Progress", 3,
                        "Threat modelling frameworks (STRIDE, PASTA), secure API design patterns, secrets management, and zero-trust architecture principles."),
                researchDoc("Cryptography Applied", 44, "In Progress", 4,
                        "Understanding RSA, elliptic curves, hash functions — the math behind the libraries I use, not just the APIs."),
                researchDoc("Reverse Engineering Basics", 30, "Early Stage", 5,
                        "Reading disassembled code, understanding binary formats, analyzing malware samples in isolated environments. Tools: Ghidra, gdb, strace."),
                researchDoc("OWASP & Secure SDLC", 80, "Strong Foundation", 6,
                        "SAST tools, dependency scanning, security-focused code review, and the OWASP Application Security Verification Standard.")
        );

        for (Map<String, Object> t : topics) {
            firestore.collection("research_topics").add(t).get();
        }
        System.out.println("  ✓ " + topics.size() + " research topics");
    }

    private Map<String, Object> researchDoc(String title, int progress, String statusLabel, int order, String description) {
        Map<String, Object> doc = new HashMap<>();
        doc.put("title", title);
        doc.put("description", description);
        doc.put("progress", progress);
        doc.put("status_label", statusLabel);
        doc.put("order", order);
        doc.put("is_active", true);
        return doc;
    }
}