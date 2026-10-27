package ch.zli.m223.dto;

import jakarta.validation.constraints.NotBlank;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import ch.zli.m223.model.Category;

public class CategoryDto {

    @Schema(readOnly = true)
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    public static CategoryDto fromEntity(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setTitle(category.getTitle());
        return dto;
    }

    public Category toEntity() {
        Category category = new Category();
        category.setTitle(title);
        return category;
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
