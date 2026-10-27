package ch.zli.m223.controller;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.validation.Valid;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.PUT;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import ch.zli.m223.dto.CategoryDto;
import ch.zli.m223.model.Category;
import ch.zli.m223.service.CategoryService;

@Path("/categories")
@Tag(name = "Categories", description = "Handling of categories")
public class CategoryController {

    @Inject
    CategoryService categoryService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Index all Categories.", description = "Returns a list of all categories.")
    public List<CategoryDto> index() {
        return categoryService.findAll().stream().map(CategoryDto::fromEntity).toList();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Gets a single category.", description = "Returns the category with the given id.")
    public Response get(@PathParam("id") Long id) {
        Category category = categoryService.findById(id);
        if (category == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(CategoryDto.fromEntity(category)).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Creates a new category.", description = "Creates a new category and returns it.")
    public CategoryDto create(@Valid CategoryDto dto) {
        return CategoryDto.fromEntity(categoryService.createCategory(dto.toEntity()));
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Updates a category.", description = "Updates the category with the given id and returns it.")
    public Response update(@PathParam("id") Long id, @Valid CategoryDto dto) {
        Category updated = categoryService.updateCategory(id, dto.toEntity());
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(CategoryDto.fromEntity(updated)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Deletes a category.", description = "Deletes the category with the given id.")
    public Response delete(@PathParam("id") Long id) {
        if (!categoryService.deleteCategory(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build(); // 204
    }
}
