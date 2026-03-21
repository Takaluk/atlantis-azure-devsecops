package com.stocklens.content.api;

import com.stocklens.content.infra.ArticleRepository;
import com.stocklens.content.infra.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class HomeController {

    private final KeywordRepository keywordRepository;
    private final ArticleRepository articleRepository;

    @GetMapping("/home")
    public HomeResponse home() {
        var keywords = keywordRepository.findAllByOrderByWordAsc()
                .stream().map(k -> new KeywordDto(k.getWord(), k.getDescription()))
                .toList();

        var articles = articleRepository.findTop4ByOrderByPublishedAtDesc()
                .stream().map(a -> new ArticleDto(a.getTitle(), a.getMeta(), a.getDescription(), a.getPublishedAt().toString()))
                .toList();

        return new HomeResponse(keywords, articles);
    }

    public record HomeResponse(List<KeywordDto> keywords, List<ArticleDto> articles) {}
    public record KeywordDto(String word, String description) {}
    public record ArticleDto(String title, String meta, String description, String publishedAt) {}
}
