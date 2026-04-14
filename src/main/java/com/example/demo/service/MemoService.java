package com.example.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Memo;
import com.example.demo.entity.Tag;
import com.example.demo.repository.MemoRepository;
import com.example.demo.repository.TagRepository;

@Service
public class MemoService {
    @Autowired
    MemoRepository memoRepository;
    @Autowired
    TagRepository tagRepository;

    public void save(Memo memo) {
        memoRepository.save(memo);
    }
    public List<Memo> findAll() {
        return memoRepository.findAllWithTags();
    }
    public Memo findById(Long id) {
        Memo memo= memoRepository.findById(id).orElseThrow();
        memo.getTags().size();
        return memo;
    }

    public Memo create(Memo memo) {
        if (memo.getTitle() == null || memo.getTitle().length() > 100) {
            throw new IllegalArgumentException("タイトルは100文字以内で入力してください");
        }

        memo.setUpdatedAt(LocalDateTime.now());
        memo.setDeletedFlag(false);
        return memoRepository.save(memo);
    }

    public Memo update(Long id, String title, String content, LocalDate date) {
        if (title == null || title.length() > 100) {
            throw new IllegalArgumentException("タイトルは100文字以内で入力してください");
        }

        Memo memo = memoRepository.findById(id).orElseThrow();
        memo.getTags().size();

        memo.setTitle(title);
        memo.setContent(content);
        memo.setCreatedAt(date.atStartOfDay());

        return memoRepository.save(memo);
    }

    public Memo updateTags(Long id, List<Long> tagIds) {
        Memo memo = memoRepository.findById(id).orElseThrow();
        List<Tag> tags = tagRepository.findAllById(tagIds);
        memo.setTags(tags);
        return memoRepository.save(memo);
    }

    public List<Memo> filter(String keyword, LocalDate start, String tagName, LocalDate now) {

        List<Memo> memos = memoRepository.findAll();

        if (keyword != null && !keyword.isEmpty()) {
            memos = memos.stream()
                    .filter(m -> m.getTitle().contains(keyword) || m.getContent().contains(keyword))
                    .toList();
        }

        if (start != null) {
            memos = memos.stream()
                    .filter(m -> !m.getCreatedAt().toLocalDate().isBefore(start))
                    .toList();
        }

        if (tagName != null && !tagName.isEmpty()) {
            memos = memos.stream()
                    .filter(m -> m.getTags().stream().anyMatch(t -> t.getName().equals(tagName)))
                    .toList();
        }

        return memos;
    }


    public void delete(Long id) {
        memoRepository.deleteById(id);
    }


    public List<Memo> sortAsc() {
        return memoRepository.findAllByOrderByIdAscWithTags();
    }

    public List<Memo> sortDesc() {
        return memoRepository.findAllByOrderByIdDescWithTags();
    }

}