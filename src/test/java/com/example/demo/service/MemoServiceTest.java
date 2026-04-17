package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.entity.Memo;
import com.example.demo.entity.Tag;
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
        assertEquals(1, result.size());
    }

    @Test
    void testFindById() {
        Memo memo = new Memo();
        memo.setId(1L);
        when(memoRepository.findById(1L)).thenReturn(Optional.of(memo));
        Memo result = memoService.findById(1L);
        assertEquals(1, result.getId());
    }

    @Test
    void testFindById_notFound() {
        when(memoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            memoService.findById(1L);
        });
    }

    @Test
    void testCreate_invalidTitle() {
        Memo memo = new Memo();
        memo.setTitle("あ".repeat(101));
        assertThrows(IllegalArgumentException.class, () -> memoService.create(memo));
    }

    @Test
    void testCreate_titleExactly100() {
        String title100 = "a".repeat(100);
        Memo request = new Memo(null,title100, "content", LocalDate.now());

        when(memoRepository.save(any())).thenReturn(new Memo());

        assertDoesNotThrow(() -> memoService.create(request));
    }


    @Test
    void testUpdate_success() {
        Memo existing = new Memo(1L, "old", "oldContent", LocalDate.now());
        when(memoRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(memoRepository.save(any())).thenReturn(existing);

        Memo result = memoService.update(1L, "new", "newContent", LocalDate.now());

        assertEquals("new", result.getTitle());
        assertEquals("newContent", result.getContent());
        verify(memoRepository).findById(1L);
        verify(memoRepository).save(existing);
    }

    @Test
    void testUpdate_notFound() {
        when(memoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> memoService.update(1L, "new", "newContent",LocalDate.now()));

        verify(memoRepository, never()).save(any());
    }

    @Test
    void testUpdate_invalidTitle_null() {
        assertThrows(IllegalArgumentException.class,
                () -> memoService.update(1L, null, "newContent",LocalDate.now()));

        verify(memoRepository, never()).save(any());
    }

    @Test
    void testUpdateTags_success() {

        Memo memo = new Memo();
        when(memoRepository.findById(1L)).thenReturn(Optional.of(memo));

        List<Long> tagIds = List.of(1L, 2L);

        Tag t1 = new Tag(1L, "tag1");
        Tag t2 = new Tag(2L, "tag2");
        when(tagRepository.findAllById(tagIds)).thenReturn(List.of(t1, t2));

        memoService.updateTags(1L, tagIds);

        assertEquals(List.of(t1, t2), memo.getTags());
        verify(memoRepository).save(memo);
    }


    @Test
    void testUpdateTags_notFound() {
        when(memoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            memoService.updateTags(1L, List.of(1L));
        });
    }

    @Test
    void testDelete() {
        memoService.delete(1L);
        verify(memoRepository, times(1)).deleteById(1L);
    }

    @Test
    void testCreate_success() {
        Memo memo = new Memo(null, "タイトル", "内容", LocalDate.now());
        when(memoRepository.save(any())).thenReturn(memo);

        Memo result = memoService.create(memo);

        assertEquals(memo, result);
        verify(memoRepository,times(1)).save(memo);
    }

    @Test
    void testCreate_invalidTitle_null() {
        Memo memo = new Memo(null, null, "内容", LocalDate.now());

        assertThrows(IllegalArgumentException.class, () -> memoService.create(memo));
        verify(memoRepository, never()).save(any());
    }

    @Test
    void testFilter_keyword() {
        List<Memo> list = List.of(
                new Memo(1L, "買い物", "内容", LocalDate.now()),
                new Memo(2L, "仕事", "内容", LocalDate.now())
        );
        when(memoRepository.findAll()).thenReturn(list);

        List<Memo> result = memoService.filter("買", null, null, LocalDate.now());

        assertEquals(1, result.size());
        assertEquals("買い物", result.get(0).getTitle());
    }

    @Test
    void testFilter_keywordAndDate() {
        LocalDate date = LocalDate.of(2024, 1, 1);

        when(memoRepository.findAll()).thenReturn(List.of(
                new Memo(1L,"test","content",date)
        ));

        List<Memo> result = memoService.filter("test", date, null,LocalDate.now());

        assertEquals(1, result.size());
    }


    @Test
    void testFilter_date() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        List<Memo> list = List.of(
                new Memo(1L, "a", "内容", today),
                new Memo(2L, "b", "内容", yesterday)
        );
        when(memoRepository.findAll()).thenReturn(list);

        List<Memo> result = memoService.filter(null, today, null, LocalDate.now());

        assertEquals(1, result.size());
        assertEquals("a", result.get(0).getTitle());
    }

    @Test
    void testFilter_tag() {
        Tag work = new Tag(1L, "仕事");
        Tag life = new Tag(2L, "生活");

        Memo m1 = new Memo(1L, "a", "内容", LocalDate.now());
        m1.setTags(List.of(work));

        Memo m2 = new Memo(2L, "b", "内容", LocalDate.now());
        m2.setTags(List.of(life));

        when(memoRepository.findAll()).thenReturn(List.of(m1, m2));

        List<Memo> result = memoService.filter(null, null, "仕事", LocalDate.now());

        assertEquals(1, result.size());
        assertEquals("a", result.get(0).getTitle());
    }


    @Test
    void testSortAsc() {
        List<Memo> list = List.of(
                new Memo(2L, "a", "内容", LocalDate.now()),
                new Memo(1L, "b", "内容", LocalDate.now())
        );
        when(memoRepository.findAllByOrderByIdAscWithTags()).thenReturn(list);

        List<Memo> result = memoService.sortAsc();

        assertEquals("a", result.get(0).getTitle());
    }

    @Test
    void testSortDesc() {
        List<Memo> list = List.of(
                new Memo(2L, "a", "内容", LocalDate.now()),
                new Memo(1L, "b", "内容", LocalDate.now())
        );
        when(memoRepository.findAllByOrderByIdDescWithTags()).thenReturn(list);

        List<Memo> result = memoService.sortDesc();

        assertEquals("a", result.get(0).getTitle());
    }

}