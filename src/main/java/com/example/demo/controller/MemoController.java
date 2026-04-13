package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.Memo;
import com.example.demo.entity.Tag;
import com.example.demo.repository.MemoRepository;
import com.example.demo.repository.TagRepository;
import com.example.demo.service.MemoService;

@Controller
public class MemoController {

    @Autowired
    MemoService memoService;

    @Autowired
    MemoRepository memoRepository;

    @Autowired
    TagRepository tagRepository;

    @GetMapping("/")
    public String index(
            @RequestParam(value = "filterTagIds", required = false) List<Long> filterTagIds,
            @RequestParam(value = "keyword", required = false ) String keyword,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            Model model) {
        if (filterTagIds == null) {
            filterTagIds = new ArrayList<>();
        }

        model.addAttribute("memos", memoService.findAll());
        model.addAttribute("allTags", tagRepository.findAll());
        model.addAttribute("filterTagIds", filterTagIds);
        model.addAttribute("selectedTags", new ArrayList<Tag>());

        return "index";
    }

    @GetMapping("/memo")
    public String list(Model model) {
        List<Memo> memos = memoService.findAll();
        model.addAttribute("memos", memos);
        model.addAttribute("allTags", tagRepository.findAll());
        model.addAttribute("filterTagIds", new ArrayList<Long>());
        model.addAttribute("selectedTags", new ArrayList<Tag>());
        return "index";
    }

    @GetMapping("/memo/new")
    public String newMemo(Model model) {
        model.addAttribute("memo", new Memo());
        model.addAttribute("memos", new ArrayList<Memo>());
        model.addAttribute("selectedTags", new ArrayList<Tag>());
        model.addAttribute("allTags", tagRepository.findAll());
        return "new";
    }

    @GetMapping("/memo/{id}")
    public String show(@PathVariable("id")  final Long id, Model model) {
        Memo memo = memoService.findById(id);
        model.addAttribute("memo", memo);
        return "show";
    }


    @PostMapping("/memo")
    public String create(@RequestParam ("title") String title,
                         @RequestParam ("content") String content,
                         @RequestParam(value = "tagIds", required = false) List<Long> tagIds) {

        Memo memo = new Memo();
        memo.setTitle(title);
        memo.setContent(content);

        List<Tag> tags = (tagIds != null)
                ? tagRepository.findAllById(tagIds)
                : new ArrayList<>();
        memo.setTags(tags);

        memoService.create(memo);

        return "redirect:/";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "filterTagIds", required = false) List<Long> filterTagIds,
            Model model) {

        if (filterTagIds == null) {
            filterTagIds = new ArrayList<>();
        }

        List<Memo> memos = memoService.filter(keyword, startDate, endDate, filterTagIds);

        model.addAttribute("memos", memos);
        model.addAttribute("allTags", tagRepository.findAll());
        model.addAttribute("filterTagIds", filterTagIds);

        List<Tag> selectedTags = new ArrayList<>();
        if (filterTagIds != null) {
            selectedTags = tagRepository.findAllById(filterTagIds);
        }
        model.addAttribute("selectedTags", selectedTags);
        return "index";
    }

    @GetMapping("/sort")
    public String sort(@RequestParam(name="order", required = false) String order, Model model) {
        if (order == null || order.isEmpty()) {
            model.addAttribute("memos", memoService.sortDesc());
        } else if (order.equals("asc")) {
            model.addAttribute("memos", memoService.sortAsc());
        } else {
            model.addAttribute("memos", memoService.sortDesc());
        }
        model.addAttribute("allTags", tagRepository.findAll());
        model.addAttribute("filterTagIds", new ArrayList<Long>());
        return "index";
    }

    @PostMapping("/memo/{id}/edit")
    public String update(
            @PathVariable("id") Long id,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "tagIds" ,required = false) List<Long> tagIds
    ) {
        memoService.update(id, title, content, tagIds);
        return "redirect:/";
    }

    @GetMapping("/memo/{id}/edit")
    public String edit(@PathVariable("id") Long id, Model model) {
        model.addAttribute("memo", memoService.findById(id));
        model.addAttribute("allTags", tagRepository.findAll());
        return "edit";
    }

    @PostMapping("/memo/{id}/delete")
    public String delete(@PathVariable("id") Long id) {
        memoService.delete(id);
        return "redirect:/";
    }
}