package ch.zli.m223.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import ch.zli.m223.dto.EntryDto;
import ch.zli.m223.model.Category;
import ch.zli.m223.model.Entry;
import ch.zli.m223.model.Tag;
import ch.zli.m223.repository.CategoryRepository;
import ch.zli.m223.repository.EntryRepository;
import ch.zli.m223.repository.TagRepository;

@ApplicationScoped
public class EntryService {
    @Inject
    EntryRepository entryRepository;

    @Inject
    CategoryRepository categoryRepository;

    @Inject
    TagRepository tagRepository;

    @Transactional
    public Entry createEntry(EntryDto dto) {
        Entry entry = dto.toEntity();
        applyRelations(entry, dto);
        entryRepository.persist(entry);
        return entry;
    }

    public List<Entry> findAll() {
        return entryRepository.listAll();
    }

    public Entry findById(Long id) {
        return entryRepository.findById(id);
    }

    @Transactional
    public Entry updateEntry(Long id, EntryDto dto) {
        Entry existing = entryRepository.findById(id);
        if (existing == null) {
            return null;
        }
        existing.setCheckIn(dto.getCheckIn());
        existing.setCheckOut(dto.getCheckOut());
        applyRelations(existing, dto);
        // Kein persist nötig: die Entity ist managed und wird beim Commit gespeichert
        return existing;
    }

    @Transactional
    public boolean deleteEntry(Long id) {
        return entryRepository.deleteById(id);
    }

    // Lädt Kategorie und Tags anhand der IDs; unbekannte IDs führen zu HTTP 404
    private void applyRelations(Entry entry, EntryDto dto) {
        Category category = null;
        if (dto.getCategoryId() != null) {
            category = categoryRepository.findByIdOptional(dto.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category " + dto.getCategoryId() + " not found"));
        }
        entry.setCategory(category);

        Set<Tag> tags = new HashSet<>();
        for (Long tagId : dto.getTagIds()) {
            tags.add(tagRepository.findByIdOptional(tagId)
                    .orElseThrow(() -> new NotFoundException("Tag " + tagId + " not found")));
        }
        entry.setTags(tags);
    }
}
