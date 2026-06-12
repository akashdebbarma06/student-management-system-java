package com.studentms;

/**
 * Represents a student entity with full encapsulation.
 * Supports CSV serialization for file-based persistence.
 */
public class Student {

    private String studentId;
    private String name;
    private int    age;
    private String course;
    private String email;
    private double gpa;

    public Student(String studentId, String name, int age,
                   String course, String email, double gpa) {
        this.studentId = studentId;
        this.name      = name;
        this.age       = age;
        this.course    = course;
        this.email     = email;
        this.gpa       = gpa;
    }

    // ── Getters ────────────────────────────────────────────────────────────────

    public String getStudentId() { return studentId; }
    public String getName()      { return name; }
    public int    getAge()       { return age; }
    public String getCourse()    { return course; }
    public String getEmail()     { return email; }
    public double getGpa()       { return gpa; }

    // ── Setters (used by update operation) ─────────────────────────────────────

    public void setName(String name)     { this.name   = (name   != null && !name.isBlank())   ? name   : this.name; }
    public void setAge(int age)          { this.age    = (age    > 0)                           ? age    : this.age; }
    public void setCourse(String course) { this.course = (course != null && !course.isBlank())  ? course : this.course; }
    public void setEmail(String email)   { this.email  = (email  != null && !email.isBlank())   ? email  : this.email; }
    public void setGpa(double gpa)       { this.gpa    = (gpa    >= 0.0 && gpa <= 4.0)          ? gpa    : this.gpa; }

    // ── CSV Serialization ───────────────────────────────────────────────────────

    /**
     * Serializes this student to a comma-separated string for file storage.
     * Format: id,name,age,course,email,gpa
     */
    public String toCSV() {
        return String.join(",",
                studentId,
                name,
                String.valueOf(age),
                course,
                email,
                String.valueOf(gpa));
    }

    /**
     * Deserializes a CSV line back into a Student object.
     *
     * @param line A valid 6-field CSV line.
     * @throws IllegalArgumentException if the line is malformed.
     */
    public static Student fromCSV(String line) {
        String[] p = line.split(",", 6);
        if (p.length != 6) throw new IllegalArgumentException("Malformed record: " + line);
        return new Student(
                p[0].trim(), p[1].trim(),
                Integer.parseInt(p[2].trim()),
                p[3].trim(), p[4].trim(),
                Double.parseDouble(p[5].trim()));
    }

    // ── Display ─────────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return String.format(
            "%n  ┌──────────────────────────────────────┐%n" +
            "  │  ID     : %-29s│%n" +
            "  │  Name   : %-29s│%n" +
            "  │  Age    : %-29s│%n" +
            "  │  Course : %-29s│%n" +
            "  │  Email  : %-29s│%n" +
            "  │  GPA    : %-29s│%n" +
            "  └──────────────────────────────────────┘",
            studentId, name, age, course, email, String.format("%.2f / 4.00", gpa));
    }
}
