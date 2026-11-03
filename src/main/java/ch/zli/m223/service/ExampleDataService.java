package ch.zli.m223.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ch.zli.m223.repository.CategoryRepository;
import ch.zli.m223.repository.EntryRepository;
import ch.zli.m223.repository.TagRepository;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import ch.zli.m223.model.Category;
import ch.zli.m223.model.Tag;
//import ch.zli.m223.model.Entry;

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

    public void startCreateExampledata(@Observes StartupEvent event){
        if (!allowedToRun) {
            LOG.info("Environment doesnt allow creation of example data");
            return;
        }
        if(categoryRepository.count() != 0 || tagRepository.count() != 0) {
            LOG.info("DB not empty");
            return;
        }
        LOG.info("Creating example data...");
        createExampledata();
    }

    @Transactional 
    public void createExampledata() {
        createCategory("my category");
        createTag("new Tag");
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
}