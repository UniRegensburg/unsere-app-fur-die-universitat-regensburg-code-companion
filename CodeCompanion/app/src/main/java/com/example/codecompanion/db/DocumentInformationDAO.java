package com.example.codecompanion.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface DocumentInformationDAO {

	/**
	 * Find a {@link DocumentInformation} by its ID
	 * @param id the ID that should be searched for
	 * @return a {@link DocumentInformation} with the given id
	 */
	@Query("SELECT * FROM DocumentInformation WHERE id IN (:id) LIMIT 1")
	DocumentInformation findById(long id);

	@Query("SELECT * FROM DocumentInformation WHERE document_name IN (:name) LIMIT 1")
	DocumentInformation findByDocumentName(String name);

	/**
	 * Return all {@link DocumentInformation}
	 * @return a {@link List} of {@link DocumentInformation}, containing all database entities
	 */
	@Query("SELECT * FROM DocumentInformation")
	List<DocumentInformation> findAll();

	/**
	 * Gets the lines of code of a given Project
	 * @param projectId the ID of a {@link ProjectInformation}
	 * @return the lines of code of all documents linked to the project with the given ID
	 */
	@Query("SELECT lines_of_code FROM DocumentInformation WHERE parent_project_id IN (:projectId)")
	List<Integer> getLinesOfCodeByProjectId(long projectId);

	/**
	 * Gets all lines of code from a given Project, except those of a specified document
	 * This is used to observe changes in the currently open document and then add all other lines on top of it
	 * @param projectId the id of the {@link ProjectInformation}
	 * @param documentId the id of the {@link DocumentInformation} that should be excluded
	 * @return A {@link List} of Integers, containing all lines of code except for the specified document
	 */
	@Query("SELECT lines_of_code FROM DocumentInformation WHERE parent_project_id IN (:projectId) AND NOT id IN (:documentId)")
	List<Integer> getLinesOfCodeByProjectIdExceptFromDocument(long projectId, long documentId);

	/**
	 * Updates (a) document(s) in the database
	 * @param documentInformation the document(s) that should be updated
	 */
	@Update
	void updateDocumentInformation(DocumentInformation... documentInformation);

	@Insert
	void insertAll(DocumentInformation... documentInformation);

	@Insert
	long insert(DocumentInformation documentInformation);

}
