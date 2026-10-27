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

import ch.zli.m223.dto.TagDto;
import ch.zli.m223.model.Tag;
import ch.zli.m223.service.TagService;

@Path("/tags")
@org.eclipse.microprofile.openapi.annotations.tags.Tag(name = "Tags", description = "Handling of tags")
public class TagController {

    @Inject
    TagService tagService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Index all Tags.", description = "Returns a list of all tags.")
    public List<TagDto> index() {
        return tagService.findAll().stream().map(TagDto::fromEntity).toList();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Gets a single tag.", description = "Returns the tag with the given id.")
    public Response get(@PathParam("id") Long id) {
        Tag tag = tagService.findById(id);
        if (tag == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(TagDto.fromEntity(tag)).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Creates a new tag.", description = "Creates a new tag and returns it.")
    public TagDto create(@Valid TagDto dto) {
        return TagDto.fromEntity(tagService.createTag(dto.toEntity()));
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Updates a tag.", description = "Updates the tag with the given id and returns it.")
    public Response update(@PathParam("id") Long id, @Valid TagDto dto) {
        Tag updated = tagService.updateTag(id, dto.toEntity());
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(TagDto.fromEntity(updated)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Deletes a tag.", description = "Deletes the tag with the given id.")
    public Response delete(@PathParam("id") Long id) {
        if (!tagService.deleteTag(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build(); // 204
    }
}
