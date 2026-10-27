package ch.zli.m223.service;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import ch.zli.m223.model.Category;
import ch.zli.m223.repository.CategoryRepository;
import ch.zli.m223.repository.EntryRepository;

@ApplicationScoped
public class CategoryService {
    @Inject
    CategoryRepository categoryRepository;

    @Inject
    EntryRepository entryRepository;

    public List<Category> findAll() {
        return categoryRepository.listAll();
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id);
    }

    @Transactional
    public Category createCategory(Category category) {
        categoryRepository.persist(category);
        return category;
    }

    @Transactional
    public Category updateCategory(Long id, Category category) {
        Category existing = categoryRepository.findById(id);
        if (existing == null) {
            return null;
        }
        existing.setTitle(category.getTitle());
        return existing;
    }

    @Transactional
    public boolean deleteCategory(Long id) {
        // Referenzen in Entries entfernen, sonst verletzt das Löschen den Foreign Key
        entryRepository.update("category = null where category.id = ?1", id);
        return categoryRepository.deleteById(id);
    }
}
