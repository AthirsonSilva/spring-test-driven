package com.springtestdriven.repository;

import com.springtestdriven.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {
    @Query("SELECT e FROM EmployeeEntity e WHERE e.email = :email")
    Optional<EmployeeEntity> findByEmail(String email);

    @Query("SELECT e FROM EmployeeEntity e WHERE e.firstName LIKE ?1 AND e.lastName LIKE ?2")
    Optional<EmployeeEntity> findByFullName(String firstName, String lastName);

    @Query("SELECT e FROM EmployeeEntity e WHERE e.firstName LIKE LOWER(CONCAT('%', :query, '%')) OR e.lastName LIKE LOWER(CONCAT('%', :query, '%'))")
    List<EmployeeEntity> searchByNames(@Param("query") String query);

    @Query(nativeQuery = true, value = "SELECT * FROM employee e WHERE e.id BETWEEN ?1 AND ?2")
    List<EmployeeEntity> findByRangedIDs(Long startId, Long endId);

    @Query(nativeQuery = true,
            value = "SELECT * FROM employee e WHERE e.email LIKE LOWER(CONCAT('%', :query, '%'))",
            countQuery = "SELECT COUNT(*) FROM employee e WHERE e.email LIKE LOWER(CONCAT('%', :query, '%'))")
    List<EmployeeEntity> findByEmailContaining(String query);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM EmployeeEntity e WHERE e.email = :email")
    boolean existsByEmail(String email);
}
