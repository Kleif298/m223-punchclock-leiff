package ch.zli.m223.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ch.zli.m223.repository.CategoryRepository;
import ch.zli.m223.repository.EntryRepository;
import ch.zli.m223.repository.TagRepository;

import java.time.LocalDateTime;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import ch.zli.m223.model.Category;
import ch.zli.m223.model.Tag;
import ch.zli.m223.model.Entry;

import java.util.Set;

import io.quarkus.runtime.StartupEvent;


@ApplicationScoped 
public class ExampleDataService {

    private static final Logger LOG = Logger.getLogger(ExampleDataService.class);

    @Inject 
    EntryRepository entryRepository;
    @Inject 
    CategoryRepository categoryRepository;
    @Inject 
    TagRepository tagRepository;

    @ConfigProperty(name = "punchclock.example-data.enabled", defaultValue = "false") 
    boolean allowedToRun;

    public void startCreateExampleData(@Observes StartupEvent event){
        if (!allowedToRun) {
            LOG.info("Environment doesnt allow creation of example data");
            return;
        }
        if(categoryRepository.count() != 0 || tagRepository.count() != 0 || entryRepository.count() != 0) {
            LOG.info("DB not empty");
            return;
        }
        LOG.info("Creating example data...");
        createExampleData();
    }

    @Transactional 
    public void createExampleData() {
        Category inOffice = createCategory("In Office");
        Category homeOffice = createCategory("Home Office");
        Tag weendTag = createTag("Weekend");
        Tag myTag = createTag("First Tag");
        Tag myTag2 = createTag("Second Tag");
        Tag myTag3 = createTag("Third Tag");
        Tag myTag4 = createTag("Other Tag");

        createCategory("my category");
        createEntry(
            "2023-12-03T12:20:00",
            "2023-12-03T18:00:00",
            myCategory,
            Set.of(myTag, myTag2));
        createEntry(
            "2024-01-04T12:20:00",
            "2024-01-04T18:00:00",
            inOffice,
            Set.of(myTag3, myTag4));
        createEntry(
            "2024-02-17T12:20:00",
            "2024-02-17T18:00:00",
            homeOffice,
            Set.of(weendTag));
        
    }
    
    public Category createCategory(String name) {
        Category category = new Category();
        category.setTitle(name);
        categoryRepository.persist(category);
        return category;
    }

    public Tag createTag(String name) {
        Tag tag = new Tag();
        tag.setTitle(name);
        tagRepository.persist(tag);
        return tag;
    }

    public Entry createEntry(String checkIn, String checkOut, Category category, Set<Tag> tags) {
        Entry entry = new Entry();
        entry.setCheckIn(LocalDateTime.parse(checkIn));
        entry.setCheckOut(LocalDateTime.parse(checkOut));
        entry.setCategory(category);
        entry.setTags(tags);
        entryRepository.persist(entry);
        return entry;
    }
}