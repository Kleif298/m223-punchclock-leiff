# Diagramme Punchclock

## UML-Klassendiagramm

```mermaid
classDiagram
    direction LR

    class Entry {
        -Long id
        -LocalDateTime checkIn
        -LocalDateTime checkOut
        -Category category
        -Set~Tag~ tags
    }
    class Category {
        -Long id
        -String title
    }
    class Tag {
        -Long id
        -String title
    }

    class EntryDto {
        -Long id
        -LocalDateTime checkIn
        -LocalDateTime checkOut
        -Long categoryId
        -Set~Long~ tagIds
        +isCheckInBeforeCheckOut() boolean
        +fromEntity(Entry) EntryDto$
        +toEntity() Entry
    }
    class EntryDetailDto {
        -Long id
        -LocalDateTime checkIn
        -LocalDateTime checkOut
        -CategoryDto category
        -List~TagDto~ tags
        +fromEntity(Entry) EntryDetailDto$
    }
    class CategoryDto {
        -Long id
        -String title
        +fromEntity(Category) CategoryDto$
        +toEntity() Category
    }
    class TagDto {
        -Long id
        -String title
        +fromEntity(Tag) TagDto$
        +toEntity() Tag
    }

    class EntryController {
        +index() List~EntryDto~
        +create(EntryDto) EntryDto
        +update(Long, EntryDto) Response
        +delete(Long) Response
    }
    class EntryDetailController {
        +index() List~EntryDetailDto~
        +get(Long) Response
    }
    class CategoryController {
        +index() List~CategoryDto~
        +get(Long) Response
        +create(CategoryDto) CategoryDto
        +update(Long, CategoryDto) Response
        +delete(Long) Response
    }
    class TagController {
        +index() List~TagDto~
        +get(Long) Response
        +create(TagDto) TagDto
        +update(Long, TagDto) Response
        +delete(Long) Response
    }

    class EntryService {
        +findAll() List~Entry~
        +findById(Long) Entry
        +createEntry(EntryDto) Entry
        +updateEntry(Long, EntryDto) Entry
        +deleteEntry(Long) boolean
        -applyRelations(Entry, EntryDto) void
    }
    class CategoryService {
        +findAll() List~Category~
        +findById(Long) Category
        +createCategory(Category) Category
        +updateCategory(Long, Category) Category
        +deleteCategory(Long) boolean
    }
    class TagService {
        +findAll() List~Tag~
        +findById(Long) Tag
        +createTag(Tag) Tag
        +updateTag(Long, Tag) Tag
        +deleteTag(Long) boolean
    }

    class EntryRepository
    class CategoryRepository
    class TagRepository
    class PanacheRepository~T~ {
        <<interface>>
    }

    Entry "0..*" --> "0..1" Category : category
    Entry "0..*" --> "0..*" Tag : tags

    EntryDetailDto *-- "0..1" CategoryDto
    EntryDetailDto *-- "0..*" TagDto

    EntryController --> EntryService
    EntryDetailController --> EntryService
    CategoryController --> CategoryService
    TagController --> TagService

    EntryService --> EntryRepository
    EntryService --> CategoryRepository
    EntryService --> TagRepository
    CategoryService --> CategoryRepository
    CategoryService --> EntryRepository
    TagService --> TagRepository
    TagService --> EntryRepository

    EntryRepository ..|> PanacheRepository
    CategoryRepository ..|> PanacheRepository
    TagRepository ..|> PanacheRepository
```

## ERD (Datenbankschema)

```mermaid
erDiagram
    CATEGORY ||--o{ ENTRY : "kategorisiert"
    ENTRY ||--o{ ENTRY_TAG : "hat"
    TAG ||--o{ ENTRY_TAG : "markiert"

    ENTRY {
        bigint id PK
        timestamp checkIn "NOT NULL"
        timestamp checkOut "NOT NULL"
        bigint category_id FK "NULL"
    }
    CATEGORY {
        bigint id PK
        varchar title "NOT NULL"
    }
    TAG {
        bigint id PK
        varchar title "NOT NULL"
    }
    ENTRY_TAG {
        bigint entry_id PK, FK
        bigint tag_id PK, FK
    }
```

- Ein `Entry` hat höchstens eine `Category` (`category_id` ist nullable); eine `Category` kann von beliebig vielen Entries referenziert werden.
- `Entry` ↔ `Tag` ist n:m und wird über die Join-Tabelle `entry_tag` abgebildet.
