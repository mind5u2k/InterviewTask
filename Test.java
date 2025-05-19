package com.abc.test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Problem Statement:
 * 
 * Your school district uses a system to track student performance across
 * multiple subjects and courses. As a senior developer, you've been tasked with
 * implementing a method that analyzes student performance data to identify
 * top-performing students who meet specific criteria.
 * 
 * The system needs to identify students who: 1. Have an average grade of at
 * least 85% across all subjects 2. Have completed at least 3 different subjects
 * 3. Have no failing grades (below 60%) 4. Have at least one course completed
 * within the last 30 days
 * 
 * The method should return a Map with the student's ID as the key and their
 * average grade as the value, sorted by average grade in descending order
 * (highest grades first).
 * 
 * Implement the findTopPerformingStudents method below.
 */
public class Test {

	public static void main(String[] args) {
		// Sample test data
		List<CourseCompletion> completions = generateTestData();

		// Execute the method being tested
		Map<Integer, Double> topStudents = findTopPerformingStudents(completions, LocalDate.now());

		// Display results
		System.out.println("Top performing students (ID, Average Grade):");
		topStudents.forEach((id, avg) -> System.out.printf("Student ID: %d, Average Grade: %.2f%%\n", id, avg));

		// Expected output:
		// Top performing students (ID, Average Grade):
		// Student ID: 103, Average Grade: 92.60%
		// Student ID: 101, Average Grade: 88.75%
	}

	/**
	 * Finds top-performing students based on the specified criteria.
	 * 
	 * @param completions
	 *            List of course completions for all students
	 * @param currentDate
	 *            The current date for recency calculations
	 * @return Map of student IDs to their average grades, sorted by grade
	 *         (descending)
	 */
	public static Map<Integer, Double> findTopPerformingStudents(List<CourseCompletion> completions,
			LocalDate currentDate) {
		// TODO: Implement this method using Java streams, filtering, and data
		// transformation
		// Criteria:
		// 1. Average grade >= 85%
		// 2. At least 3 different subjects
		// 3. No failing grades (below 60%)
		// 4. At least one course completion within the last 30 days

		LocalDate today = LocalDate.now();
		LocalDate before30Days = today.minusDays(30);

		Map<Integer, List<Integer>> allGrades = completions.stream()
				.collect(Collectors.toMap(a -> a.getStudentId(), a -> {
					List<Integer> grades = new ArrayList<Integer>();
					grades.add(a.getGrade());
					return grades;
				}, (a, b) -> {
					a.addAll(b);
					return a;
				}));
		Map<Integer, Double> avgGrades = new HashMap<Integer, Double>();
		allGrades.keySet().forEach(a -> {
			avgGrades.put(a, allGrades.get(a).stream().mapToDouble(Integer::doubleValue).average().getAsDouble());
		});

		Map<Integer, List<String>> totalNoOfSub = completions.stream()
				.collect(Collectors.toMap(a -> a.getStudentId(), a -> {
					List<String> subjects = new ArrayList<String>();
					if (!subjects.contains(a.getSubject())) {
						subjects.add(a.getSubject());
					}
					return subjects;
				}, (a, b) -> {
					a.addAll(b);
					return a;
				}));

		List<Integer> noFailingGrades = completions.stream().filter(a -> a.getGrade() > 60).map(a -> a.getStudentId())
				.collect(Collectors.toList());
		List<Integer> last30daysCompletion = completions.stream()
				.filter(a -> a.getCompletionDate().isAfter(before30Days)).map(a -> a.getStudentId())
				.collect(Collectors.toList());
		List<Integer> moreThan85Grades = avgGrades.keySet().stream().filter(key -> avgGrades.get(key) > 85)
				.collect(Collectors.toList());
		List<Integer> attand3DiffSub = totalNoOfSub.keySet().stream().filter(key -> totalNoOfSub.get(key).size() >= 3)
				.collect(Collectors.toList());

		List<CourseCompletion> finalVal = completions.stream().filter(a -> {
			boolean val = false;
			val = moreThan85Grades.contains(a.getStudentId()) && attand3DiffSub.contains(a.getStudentId())
					&& noFailingGrades.contains(a.getStudentId()) && last30daysCompletion.contains(a.getStudentId());

			return val;
		}).collect(Collectors.toList());

		Map<Integer, Double> finalvaluesWithGrades = new HashMap<Integer, Double>();
		finalVal.forEach(a -> finalvaluesWithGrades.put(a.getStudentId(), avgGrades.get(a.getStudentId())));

		Map<Integer, Double> finalvaluesWithGradesSortedByGrade = finalvaluesWithGrades.entrySet().stream()
				.sorted((a, b) -> {
					return a.getValue() > b.getValue() ? -1 : a.getValue() < b.getValue() ? 1 : 0;
				}).collect(Collectors.toMap(a -> a.getKey(), a -> a.getValue(), (a, b) -> a, LinkedHashMap::new));

		return finalvaluesWithGradesSortedByGrade; // Placeholder return value
	}

	/**
	 * Helper method to generate test data for the main method.
	 * 
	 * @return A list of course completions for testing
	 */
	private static List<CourseCompletion> generateTestData() {
		LocalDate today = LocalDate.now();

		List<CourseCompletion> completions = new ArrayList<>();

		// Student 101 - Should be included (avg: 88.75%, 4 subjects, no fails,
		// recent
		// completion)
		completions.add(new CourseCompletion(101, "MATH101", "Mathematics", 92, today.minusDays(10)));
		completions.add(new CourseCompletion(101, "ENG203", "English", 85, today.minusDays(45)));
		completions.add(new CourseCompletion(101, "HIST101", "History", 88, today.minusDays(60)));
		completions.add(new CourseCompletion(101, "CS101", "Computer Science", 90, today.minusDays(30)));

		// Student 102 - Should NOT be included (has a failing grade)
		completions.add(new CourseCompletion(102, "MATH101", "Mathematics", 91, today.minusDays(15)));
		completions.add(new CourseCompletion(102, "ENG203", "English", 88, today.minusDays(20)));
		completions.add(new CourseCompletion(102, "PHYS101", "Physics", 55, today.minusDays(25))); // failing
																									// grade
		completions.add(new CourseCompletion(102, "CHEM101", "Chemistry", 92, today.minusDays(5)));

		// Student 103 - Should be included (avg: 92.6%, 5 subjects, no fails,
		// recent
		// completion)
		completions.add(new CourseCompletion(103, "MATH101", "Mathematics", 95, today.minusDays(20)));
		completions.add(new CourseCompletion(103, "ENG203", "English", 87, today.minusDays(15)));
		completions.add(new CourseCompletion(103, "HIST101", "History", 91, today.minusDays(10)));
		completions.add(new CourseCompletion(103, "CS101", "Computer Science", 96, today.minusDays(5)));
		completions.add(new CourseCompletion(103, "BIO101", "Biology", 94, today.minusDays(30)));

		// Student 104 - Should NOT be included (average < 85%)
		completions.add(new CourseCompletion(104, "MATH101", "Mathematics", 82, today.minusDays(10)));
		completions.add(new CourseCompletion(104, "ENG203", "English", 79, today.minusDays(15)));
		completions.add(new CourseCompletion(104, "HIST101", "History", 84, today.minusDays(20)));
		completions.add(new CourseCompletion(104, "CS101", "Computer Science", 81, today.minusDays(5)));

		// Student 105 - Should NOT be included (only 2 subjects)
		completions.add(new CourseCompletion(105, "MATH101", "Mathematics", 95, today.minusDays(10)));
		completions.add(new CourseCompletion(105, "MATH202", "Advanced Mathematics", 92, today.minusDays(15)));
		completions.add(new CourseCompletion(105, "MATH303", "Calculus", 91, today.minusDays(20)));

		// Student 106 - Should NOT be included (no recent completions)
		completions.add(new CourseCompletion(106, "MATH101", "Mathematics", 91, today.minusDays(45)));
		completions.add(new CourseCompletion(106, "ENG203", "English", 87, today.minusDays(60)));
		completions.add(new CourseCompletion(106, "HIST101", "History", 90, today.minusDays(50)));
		completions.add(new CourseCompletion(106, "CS101", "Computer Science", 88, today.minusDays(35)));

		return completions;
	}

	/**
	 * Represents a completed course for a student.
	 */
	public static class CourseCompletion {
		private final int studentId;
		private final String courseCode;
		private final String subject;
		private final int grade;
		private final LocalDate completionDate;

		public CourseCompletion(int studentId, String courseCode, String subject, int grade, LocalDate completionDate) {
			this.studentId = studentId;
			this.courseCode = courseCode;
			this.subject = subject;
			this.grade = grade;
			this.completionDate = completionDate;
		}

		public int getStudentId() {
			return studentId;
		}

		public String getCourseCode() {
			return courseCode;
		}

		public String getSubject() {
			return subject;
		}

		public int getGrade() {
			return grade;
		}

		public LocalDate getCompletionDate() {
			return completionDate;
		}
	}
}
