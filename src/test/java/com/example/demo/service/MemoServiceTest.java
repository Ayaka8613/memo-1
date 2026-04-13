package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.entity.Memo;
import com.example.demo.repository.MemoRepository;
import com.example.demo.repository.TagRepository;


@ExtendWith(MockitoExtension.class)
class MemoServiceTest {
    @Mock
    MemoRepository memoRepository;

    @Mock
    TagRepository tagRepository;

    @InjectMocks
    MemoService memoService;

    @Test
    void testFindall() {
        Memo memo = new Memo();
        when(memoRepository.findAllWithTags()).thenReturn(List.of(memo));
        List<Memo> result = memoService.findAll();
        assertNotNull(result);
        assertEquals(1,result.size());
    }
    @Test
    void testFindById() {
        Memo memo = new Memo();
        memo.setId(1L);
        when(memoRepository.findById(1L)).thenReturn(Optional.of(memo));
        Memo result = memoService.findById(1L);
        assertEquals(1,result.getId());
    }
    @Test
    void testCreate_invalidTitle() {
        Memo memo = new Memo();
        memo.setTitle("あ".repeat(101));
        assertThrows(IllegalArgumentException.class,() -> memoService.create(memo));
    }
    @Test
    void testDelete() {
        memoService.delete(1L);
        verify(memoRepository, times(1)).deleteById(1L);
    }

}