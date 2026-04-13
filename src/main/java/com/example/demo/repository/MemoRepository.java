package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.Memo;

public interface MemoRepository extends JpaRepository<Memo, Long> {

    @Query("SELECT DISTINCT m FROM Memo m LEFT JOIN FETCH m.tags")
    List<Memo> findAllWithTags();

    @Query("SELECT DISTINCT m FROM Memo m LEFT JOIN FETCH m.tags ORDER BY m.id ASC")
    List<Memo> findAllByOrderByIdAscWithTags();

    @Query("SELECT DISTINCT m FROM Memo m LEFT JOIN FETCH m.tags ORDER BY m.id DESC")
    List<Memo> findAllByOrderByIdDescWithTags();
}