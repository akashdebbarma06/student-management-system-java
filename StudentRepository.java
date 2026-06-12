package com.studentms;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CSV-backed implementation of IStudentRepository.
 *
 * All write operations (add / update / delete) immediately persist
 * data to disk so no changes are lost between sessions.
 *
 * Storage format: students.csv  (one student per line, 6 fields)
 */
public class StudentRepository implements IStudentRepository {

    private static final String FILE_PATH = "students.csv";
    private final List<Student> students  = new ArrayList<>();

    /** Loads existing records from disk on startup. */
    public StudentRepository() {
        loadFromFile();
    }

    // ── CRUD Operations ─────────────────────────────────────────────────────────

    @Override
    public boolean addStudent(Student student) {
        if (findById(student.getStudentId()).isPresent()) return false;   // duplicate guard
        students.add(student);
        saveToFile();
        return true;
    }

    @Override
    public Optional<Student> findById(String id) {
        return students.stream()
                .filter(s -> s.getStudentId().equalsIgnoreCase(id))
                .findFirst();
    }

    @Override
    public List<Student> findByName(String name) {
        return students.stream()
                .filter(s -> s.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateStudent(String id, Student updated) {
        return findById(id).map(existing -> {
            existing.setName(updated.getName());
            existing.setAge(updated.getAge());
            existing.setCourse(updated.getCourse());
            existing.setEmail(updated.getEmail());
            existing.setGpa(updated.getGpa());
            saveToFile();
            return true;
        }).orElse(false);
    }

    @Override
    public boolean deleteStudent(String id) {
        boolean removed = students.removeIf(s -> s.getStudentId().equalsIgnoreCase(id));
        if (removed) saveToFile();
        return removed;
    }

    @Override
    public List<Student> getAllStudents() {
        return Collections.unmodifiableList(students);
    }

    // ── File I/O ─────────────────────────────────────────────────────────────────

    /**
     * Reads all CSV lines from disk into memory.
     * Malformed lines are skipped with a warning rather than crashing the app.
     */
    private void loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;                       // first run – nothing to load

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) {
                    try {
                        students.add(Student.fromCSV(line));
                    } catch (Exception e) {
                        System.err.println("  [WARN] Skipping invalid record → " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("  [ERROR] Failed to load data: " + e.getMessage());
        }
    }

    /**
     * Writes the entire in-memory list back to disk (full rewrite strategy).
     * Chosen for simplicity; suitable for hundreds of records.
     */
    private void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Student s : students) {
                bw.write(s.toCSV());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("  [ERROR] Failed to save data: " + e.getMessage());
        }
    }
}
