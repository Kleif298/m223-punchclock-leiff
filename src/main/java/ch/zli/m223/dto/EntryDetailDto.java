package ch.zli.m223.dto;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import ch.zli.m223.model.Entry;

public class EntryDetailDto {

    private Long id;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private CategoryDto category;
    private List<TagDto> tags;

    public static EntryDetailDto fromEntity(Entry entry) {
        EntryDetailDto dto = new EntryDetailDto();
        dto.id = entry.getId();
        dto.checkIn = entry.getCheckIn();
        dto.checkOut = entry.getCheckOut();
        dto.category = entry.getCategory() != null ? CategoryDto.fromEntity(entry.getCategory()) : null;
        dto.tags = entry.getTags().stream()
                .map(TagDto::fromEntity)
                .sorted(Comparator.comparing(TagDto::getId))
                .toList();
        return dto;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCheckIn() {
        return checkIn;
    }

    public LocalDateTime getCheckOut() {
        return checkOut;
    }

    public CategoryDto getCategory() {
        return category;
    }

    public List<TagDto> getTags() {
        return tags;
    }
}
