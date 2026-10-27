package ch.zli.m223.dto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import ch.zli.m223.model.Entry;
import ch.zli.m223.model.Tag;

public class EntryDto {

    @Schema(readOnly = true)
    private Long id;

    @NotNull(message = "Check-in is required")
    private LocalDateTime checkIn;

    @NotNull(message = "Check-out is required")
    private LocalDateTime checkOut;

    private Long categoryId;

    private Set<Long> tagIds = new HashSet<>();

    @AssertTrue(message = "Check-in must be before check-out")
    public boolean isCheckInBeforeCheckOut() {
        if (checkIn == null || checkOut == null) {
            return true; // wird bereits von @NotNull abgedeckt
        }
        return checkIn.isBefore(checkOut);
    }

    public static EntryDto fromEntity(Entry entry) {
        EntryDto dto = new EntryDto();
        dto.setId(entry.getId());
        dto.setCheckIn(entry.getCheckIn());
        dto.setCheckOut(entry.getCheckOut());
        dto.setCategoryId(entry.getCategory() != null ? entry.getCategory().getId() : null);
        dto.setTagIds(entry.getTags().stream().map(Tag::getId).collect(Collectors.toSet()));
        return dto;
    }

    // Kategorie und Tags werden im EntryService anhand der IDs geladen
    public Entry toEntity() {
        Entry entry = new Entry();
        entry.setCheckIn(checkIn);
        entry.setCheckOut(checkOut);
        return entry;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDateTime checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDateTime getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDateTime checkOut) {
        this.checkOut = checkOut;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Set<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(Set<Long> tagIds) {
        this.tagIds = tagIds != null ? tagIds : new HashSet<>();
    }
}
