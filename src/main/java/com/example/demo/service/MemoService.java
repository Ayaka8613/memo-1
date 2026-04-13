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

    public void create(Memo memo) {
        if (memo.getTitle() == null || memo.getTitle().length() > 100) {
            throw new IllegalArgumentException("タイトルは100文字以内で入力してください");
        }

        memo.setUpdateAt(LocalDateTime.now());
        memo.setDeletedFlag(false);
        memoRepository.save(memo);
    }


    public void update(Long id, String title, String content, List<Long> tagIds) {
        if (title == null || title.length() > 100) {
            throw new IllegalArgumentException("タイトルは100文字以内で入力してください");
        }

        Memo memo = memoRepository.findById(id).orElseThrow();
        memo.getTags().size();

        memo.setTitle(title);
        memo.setContent(content);

        List<Tag> tags = (tagIds != null)
                ? tagRepository.findAllById(tagIds)
                : new ArrayList<>();

        memo.setTags(tags);

        memoRepository.save(memo);
    }


    public void delete(Long id) {
        memoRepository.deleteById(id);
    }

    public List<Memo> filter(String keyword, String startDate, String endDate, List<Long> tagIds) {
        List<Memo> memos = memoRepository.findAllWithTags();

        if (keyword != null && !keyword.isEmpty()) {
            memos = memos.stream()
                    .filter(m -> m.getTitle().contains(keyword) || m.getContent().contains(keyword))
                    .toList();
        }
        if (startDate != null && !startDate.isEmpty()) {
            memos = memos.stream()
                    .filter(m -> m.getCreatedAt().toLocalDate().isAfter(LocalDate.parse(startDate).minusDays(1)))
                    .toList();
        }

        if (endDate != null && !endDate.isEmpty()) {
            memos = memos.stream()
                    .filter(m -> m.getCreatedAt().toLocalDate().isBefore(LocalDate.parse(endDate).plusDays(1)))
                    .toList();
        }
        if (tagIds != null && !tagIds.isEmpty()) {
            memos = memos.stream()
                    .filter(m -> tagIds.stream().allMatch(id ->
                            m.getTags().stream().anyMatch(t -> t.getId().equals(id))
                    ))
                    .toList();
        }
        return memos;
    }

    public List<Memo> sortAsc() {
        return memoRepository.findAllByOrderByIdAscWithTags();
    }

    public List<Memo> sortDesc() {
        return memoRepository.findAllByOrderByIdDescWithTags();
    }

}