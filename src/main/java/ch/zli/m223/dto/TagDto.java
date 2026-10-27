package ch.zli.m223.dto;

import jakarta.validation.constraints.NotBlank;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import ch.zli.m223.model.Tag;

public class TagDto {

    @Schema(readOnly = true)
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    public static TagDto fromEntity(Tag tag) {
        TagDto dto = new TagDto();
        dto.setId(tag.getId());
        dto.setTitle(tag.getTitle());
        return dto;
    }

    public Tag toEntity() {
        Tag tag = new Tag();
        tag.setTitle(title);
        return tag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
