package com.studentms;

import java.util.List;
import java.util.Optional;

/**
 * Contract for all student data operations.
 * Programming to an interface allows easy swapping of storage backends
 * (CSV, database, cloud) without changing business logic.
 */
public interface IStudentRepository {

    /**
     * Adds a new student to the system.
     * @return true if added; false if a student with the same ID already exists.
     */
    boolean addStudent(Student student);

    /**
     * Finds a single student by their unique ID (case-insensitive).
     */
    Optional<Student> findById(String id);

    /**
     * Finds all students whose names contain the given substring (case-insensitive).
     */
    List<Student> findByName(String name);

    /**
     * Overwrites all mutable fields of the student with the given ID.
     * @return true if found and updated; false if no student with that ID exists.
     */
    boolean updateStudent(String id, Student updatedData);

    /**
     * Removes the student with the given ID from the system.
     * @return true if deleted; false if no match was found.
     */
    boolean deleteStudent(String id);

    /**
     * Returns an unmodifiable view of all students currently in the system.
     */
    List<Student> getAllStudents();
}
