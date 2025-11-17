package com.tkforgeworks.cookconnect.socialservice.controller;

import com.tkforgeworks.cookconnect.socialservice.model.dto.CookbookDto;
import com.tkforgeworks.cookconnect.socialservice.model.dto.CookbookEntryDto;
import com.tkforgeworks.cookconnect.socialservice.model.dto.CookbookNoteDto;
import com.tkforgeworks.cookconnect.socialservice.service.CookbookService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/cookbooks") //TODO: Change to only do cookbooks by social id, not "generally all" cookbooks
@RequiredArgsConstructor
public class CookbookController {
    private final CookbookService cookbookService;

    //GET
    @GetMapping
    @RateLimiter(name = "getAll")
    public ResponseEntity<List<CookbookDto>> getAllCookbooks() {
        return ResponseEntity.ok(cookbookService.getAllCookbooks());
    }
    @GetMapping("/{cookbookId}")
    public ResponseEntity<CookbookDto> getCookbookById(@PathVariable("cookbookId") Long cookbookId) {
        return ResponseEntity.ok(cookbookService.getCookbookById(cookbookId));
    }
    @GetMapping("/{cookbookId}/entries")
    public ResponseEntity<List<CookbookEntryDto>> getAllCookbookEntries(@PathVariable("cookbookId") Long cookbookId) {
        return ResponseEntity.ok(cookbookService.getCookbookEntries(cookbookId));
    }
    @GetMapping("/{cookbookId}/entries/{entryId}")
    public ResponseEntity<CookbookEntryDto> getCookbookEntry(@PathVariable("cookbookId") Long cookbookId,
                                                             @PathVariable("entryId") Long entryId) {
        return ResponseEntity.ok(cookbookService.getCookbookEntryById(cookbookId, entryId));
    }
    @GetMapping("/{cookbookId}/note")
    public ResponseEntity<CookbookNoteDto> getCookbookNote(@PathVariable("cookbookId") Long cookbookId) {
        return ResponseEntity.ok(cookbookService.getCookbookNote(cookbookId));
    }
    //POST
    @PostMapping
    public ResponseEntity<CookbookDto> createCookbook(@RequestBody CookbookDto cookbookDto) {
        CookbookDto createdCbDto = cookbookService.createCookbook(cookbookDto);
        URI location = URI.create("/cookbook/" + createdCbDto.id());
        return ResponseEntity.created(location).body(createdCbDto);
    }
    @PostMapping("{cookbookId}/entries")
    public ResponseEntity<CookbookEntryDto> addEntryToCookbook(@PathVariable("cookbookId") Long cookbookId,
                                                               @RequestBody CookbookEntryDto cookbookEntryDto) {
        CookbookEntryDto createdCBEntryDto = cookbookService.addCookbookEntry(cookbookId, cookbookEntryDto);
        URI location = URI.create("/cookbook/" + cookbookId + "/entries/" + createdCBEntryDto.id());
        return ResponseEntity.created(location).body(createdCBEntryDto);
    }
    @PostMapping("/{cookbookId}/note")
    public ResponseEntity<CookbookNoteDto> addNoteToCookbook(@PathVariable("cookbookId") Long cookbookId,
                                                             @RequestBody CookbookNoteDto cookbookNoteDto) {
        CookbookNoteDto createdCBNoteDto = cookbookService.addCookbookNote(cookbookId, cookbookNoteDto);
        URI location = URI.create("/cookbook/" + cookbookId + "/note/");
        return ResponseEntity.created(location).body(createdCBNoteDto);
    }
    //PUT
    @PutMapping("/{cookbookId}")
    public ResponseEntity<CookbookDto> updateCookbook(@PathVariable("cookbookId") Long cookbookId,
                                                      @RequestBody CookbookDto cookbookDto) {
        return ResponseEntity.accepted().body(cookbookService.updateCookBook(cookbookId, cookbookDto));
    }
    @PutMapping("/{cookbookId}/entries/{entryId}")
    public ResponseEntity<CookbookEntryDto> updateCookbookEntry(@PathVariable("cookbookId") Long cookbookId,
                                                                @PathVariable("entryId") Long entryId,
                                                                @RequestBody CookbookEntryDto cookbookEntryDto) {
        return ResponseEntity.accepted().body(cookbookService.updateCookbookEntry(cookbookId, entryId, cookbookEntryDto));
    }
    @PutMapping("/{cookbookId}/note")
    public ResponseEntity<CookbookNoteDto> updateCookbookNote(@PathVariable("cookbookId") Long cookbookId,
                                                              @RequestBody CookbookNoteDto cookbookNoteDto) {
        return ResponseEntity.accepted().body(cookbookService.updateCookbookNote(cookbookId, cookbookNoteDto));
    }
    //DELETE
    @DeleteMapping("/{cookbookId}")
    public ResponseEntity<String> deleteCookbook(@PathVariable("cookbookId") Long cookbookId) {
        cookbookService.deleteCookbook(cookbookId);
        return ResponseEntity.ok(String.format("Delete cookbook with id %s", cookbookId));
    }
    @DeleteMapping("/{cookbookId}/entries")
    public ResponseEntity<String> deleteAllCookbookEntries(@PathVariable("cookbookId") Long cookbookId) {
        cookbookService.deleteCookbookEntries(cookbookId);
        return ResponseEntity.ok(String.format("Delete all cookbook entries with for cookbook id %s", cookbookId));
    }
    @DeleteMapping("/{cookbookId}/entries/{entryId}")
    public ResponseEntity<String> deleteCookbookEntryById(@PathVariable("cookbookId") Long cookbookId,
                                                          @PathVariable("entryId") Long entryId) {
        cookbookService.deleteCookbookEntryById(cookbookId, entryId);
        return ResponseEntity.ok(String.format("Delete cookbook entry with id %s in cookbook with id %s", entryId, cookbookId));
    }
    @DeleteMapping("/{cookbookId}/note")
    public ResponseEntity<String> deleteCookbookNote(@PathVariable("cookbookId") Long cookbookId) {
        cookbookService.deleteCookbookNote(cookbookId);
        return ResponseEntity.ok(String.format("Delete cookbook note for cookbook id %s", cookbookId));
    }
}
