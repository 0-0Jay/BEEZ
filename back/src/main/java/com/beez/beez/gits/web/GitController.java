package com.beez.beez.gits.web;

import com.beez.beez.gits.service.GitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/repo")
public class GitController {

  private final GitService gitService;
}
