package ch.zli.m223.controller;

import java.util.List;

import jakarta.inject.Inject;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import ch.zli.m223.dto.EntryDetailDto;
import ch.zli.m223.model.Entry;
import ch.zli.m223.service.EntryService;

@Path("/entry-details")
@Tag(name = "Entry Details", description = "Entries with nested category and tags")
public class EntryDetailController {

    @Inject
    EntryService entryService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Index all entry details.", description = "Returns all entries including category and tags.")
    public List<EntryDetailDto> index() {
        return entryService.findAll().stream().map(EntryDetailDto::fromEntity).toList();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Gets a single entry detail.", description = "Returns the entry with the given id including category and tags.")
    public Response get(@PathParam("id") Long id) {
        Entry entry = entryService.findById(id);
        if (entry == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(EntryDetailDto.fromEntity(entry)).build();
    }
}
