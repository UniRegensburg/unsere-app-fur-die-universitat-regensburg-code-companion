package com.example.codecompanion.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProjectInformationDAO {

	/**
	 * Finds a {@link ProjectInformation} by its ID
	 * @param id the id that should be searched for
	 * @return a {@link ProjectInformation} matching the specified tag
	 */
	@Query("SELECT * FROM ProjectInformation WHERE id IN (:id) LIMIT 1")
	ProjectInformation findById(long id);

	/**
	 * Finds a {@link ProjectInformation} by its tag
	 * @param tag the tag that should be searched for
	 * @return a {@link ProjectInformation} matching the specified tag
	 */
	@Query("SELECT * FROM ProjectInformation WHERE project_tag IN (:tag) LIMIT 1")
	ProjectInformation findByTag(String tag);

	/**
	 * @return A List containing all {@link ProjectInformation} objects in the database
	 */
	@Query("SELECT * FROM ProjectInformation")
	List<ProjectInformation> findAll();

	/**
	 * Returns a count of all "errors" of the {@link ProjectInformation}
	 * @param id the id of the {@link ProjectInformation}
	 * @return the count of all errors of the found entity
	 */
	@Query("SELECT total_errors FROM ProjectInformation WHERE id IN (:id)")
	int findTotalErrorsByProjectId(long id);

	/**
	 * Returns a count of all "warnings" of the {@link ProjectInformation}
	 * @param id the id of the {@link ProjectInformation}
	 * @return the count of all warnings of the found entity
	 */
	@Query("SELECT total_warnings FROM ProjectInformation WHERE id IN (:id)")
	int findTotalWarningsByProjectId(long id);

	/**
	 * Updates the time spent on the project (in seconds)
	 * @param seconds the time spent in seconds
	 * @param id the id of the {@link ProjectInformation}
	 */
	@Query("UPDATE ProjectInformation SET seconds_spent_on_project = (:seconds) WHERE id IN (:id)")
	void updateSecondsSpentOnProjectById(int seconds, long id);

	/**
	 * Get the seconds spent on a {@link ProjectInformation}
	 * @param id the id of the {@link ProjectInformation}
	 * @return the time spent in seconds
	 */
	@Query("SELECT seconds_spent_on_project FROM ProjectInformation WHERE id IN (:id)")
	int findSecondsSpentOnProjectById(long id);

	/**
	 * Inserts a new {@link ProjectInformation} and returns its ID
	 * @param projectInformation the entity that should be inserted
	 * @return the ID of the inserted entity
	 */
	@Insert
	long insert(ProjectInformation projectInformation);

	/**
	 * Updates an entity in the database
	 * @param projectInformation the {@link ProjectInformation} that should be updated
	 */
	@Update
	void updateProject(ProjectInformation projectInformation);

}
