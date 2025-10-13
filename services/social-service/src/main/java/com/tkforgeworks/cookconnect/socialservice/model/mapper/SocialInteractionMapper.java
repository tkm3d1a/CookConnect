package com.tkforgeworks.cookconnect.socialservice.model.mapper;

import com.tkforgeworks.cookconnect.socialservice.model.*;
import com.tkforgeworks.cookconnect.socialservice.model.dto.*;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SocialInteractionMapper {
    CookbookDto toCookbookDto(Cookbook cookbook);
    Cookbook toCookbook(CookbookDto cookbookDto);
    CookbookEntryDto toCookbookEntryDto(CookbookEntry cookbookEntry);
    CookbookEntry toCookbookEntry(CookbookEntryDto cookbookEntryDto);
    CookbookNoteDto toCookbookNoteDto(CookbookNote cookbookNote);
    CookbookNote toCookbookNote(CookbookNoteDto cookbookNoteDto);
    EntryNoteDto toEntryNoteDto(EntryNote entryNote);
    EntryNote toEntryNote(EntryNoteDto entryNoteDto);
    SocialInteractionDto toSocialInteractionDto(SocialInteraction socialInteraction);
    SocialInteraction toSocialInteraction(SocialInteractionDto socialInteractionDto);

    List<CookbookDto> toCookbookDtoList(List<Cookbook> cookbookList);
    List<Cookbook> toCookbookList(List<CookbookDto> cookbookDtoList);
    List<CookbookEntryDto> toCookbookEntryDtoList(List<CookbookEntry> cookbookEntryList);
    List<CookbookEntry> toCookbookEntryList(List<CookbookEntry> cookbookEntryList);
    List<CookbookNoteDto> toCookbookNoteDtoList(List<CookbookNote> cookbookNoteList);
    List<CookbookNote> toCookbookNoteList(List<CookbookNote> cookbookNoteList);
    List<EntryNoteDto> toEntryNoteDtoList(List<EntryNote> entryNoteList);
    List<EntryNote> toEntryNoteList(List<EntryNote> entryNoteList);
    List<SocialInteractionDto> toSocialInteractionDtoList(List<SocialInteraction> socialInteractionList);
    List<SocialInteraction> toSocialInteractionList(List<SocialInteractionDto> socialInteractionDtoList);

}
