package ch.zli.m223.service;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import ch.zli.m223.model.Tag;
import ch.zli.m223.model.Entry;
import ch.zli.m223.repository.EntryRepository;
import ch.zli.m223.repository.TagRepository;

@ApplicationScoped
public class TagService {
    @Inject
    TagRepository tagRepository;

    @Inject
    EntryRepository entryRepository;

    public List<Tag> findAll() {
        return tagRepository.listAll();
    }

    public Tag findById(Long id) {
        return tagRepository.findById(id);
    }

    @Transactional
    public Tag createTag(Tag tag) {
        tagRepository.persist(tag);
        return tag;
    }

    @Transactional
    public Tag updateTag(Long id, Tag tag) {
        Tag existing = tagRepository.findById(id);
        if (existing == null) {
            return null;
        }
        existing.setTitle(tag.getTitle());
        return existing;
    }

    @Transactional
    public boolean deleteTag(Long id) {
        Tag tag = tagRepository.findById(id);
        if (tag == null) {
            return false;
        }
        // Tag aus allen Entries entfernen, damit keine Einträge in der Join-Tabelle übrig bleiben
        List<Entry> entries = entryRepository.list("select e from Entry e join e.tags t where t.id = ?1", id);
        entries.forEach(entry -> entry.getTags().remove(tag));
        tagRepository.delete(tag);
        return true;
    }
}
