package com.epam.jmp.microcollector.controllers;

import com.epam.jmp.microcollector.repositories.MessagesRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class VisualizerController {

    private final MessagesRepository repository;

    public VisualizerController(MessagesRepository repository) {
        this.repository = repository;
    }

    @GetMapping(value="/saved-messages-all")
    public String getMessages(final Model model) {
        model.addAttribute("messagesList", repository.findAll());
        model.addAttribute("totalMessages", repository.count());
        model.addAttribute("paginatedContent", false);

        return "messages";
    }

    @GetMapping(value="/saved-messages")
    public String getMessagesByPage(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                final Model model) {
        var pageNumber = page < 1 ? 0 : page - 1;
        Pageable pageable = PageRequest.of(pageNumber, size, Sort.by("timestamp").descending());
        var messagesPage = repository.findAll(pageable);
        model.addAttribute("messagesList", messagesPage.getContent());
        model.addAttribute("totalMessages", messagesPage.getTotalElements());
        model.addAttribute("paginatedContent", false);

        int totalPages = messagesPage.getTotalPages();
        if (totalPages > 1) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
            model.addAttribute("pageSize", size);
            model.addAttribute("paginatedContent", true);
        }

        return "messages";
    }
}
