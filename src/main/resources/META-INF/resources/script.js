const URL = 'http://localhost:8080';
let entries = [];
let categories = [];
let tags = [];

const readEntry = (formData) => {
    return {
        checkIn: `${formData.get('checkInDate')}T${formData.get('checkInTime')}`,
        checkOut: `${formData.get('checkOutDate')}T${formData.get('checkOutTime')}`,
        categoryId: formData.get('categorySelect') ? Number(formData.get('categorySelect')) : null,
        tagIds: formData.getAll('tagIds').map(Number)
    };
};

const fillSelect = (selector, items, withEmptyOption) => {
    document.querySelectorAll(selector).forEach((select) => {
        select.innerHTML = '';
        if (withEmptyOption) {
            select.appendChild(new Option('-- none --', ''));
        }
        items.forEach((item) => select.appendChild(new Option(item.title, item.id)));
    });
};

const loadCategoriesAndTags = () => {
    fetch(`${URL}/categories`)
        .then((result) => result.json())
        .then((result) => {
            categories = result;
            fillSelect('.categorySelect', categories, true);
            renderCategories();
            renderEntries();
        });
    fetch(`${URL}/tags`)
        .then((result) => result.json())
        .then((result) => {
            tags = result;
            fillSelect('.tagSelect', tags, false);
            renderTags();
            renderEntries();
        });
};

const createCategory = (e) => {
    e.preventDefault();
    const title = new FormData(e.target).get('title');
    fetch(`${URL}/categories`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ title })
    }).then(() => {
        e.target.reset();
        loadCategoriesAndTags();
    });
};

const createTag = (e) => {
    e.preventDefault();
    const title = new FormData(e.target).get('title');
    fetch(`${URL}/tags`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ title })
    }).then(() => {
        e.target.reset();
        loadCategoriesAndTags();
    });
};

const deleteCategory = (id) => {
    fetch(`${URL}/categories/${id}`, { method: 'DELETE' }).then(() => {
        loadCategoriesAndTags();
        indexEntries();
    });
};

const deleteTag = (id) => {
    fetch(`${URL}/tags/${id}`, { method: 'DELETE' }).then(() => {
        loadCategoriesAndTags();
        indexEntries();
    });
};

const createEntry = (e) => {
    e.preventDefault();
    const entry = readEntry(new FormData(e.target));

    fetch(`${URL}/entries`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(entry)
    })
        .then((result) => result.json())
        .then((saved) => {
            entries.push(saved);
            renderEntries();
            e.target.reset();
        });
};

const updateEntry = (e) => {
    e.preventDefault();
    const formData = new FormData(e.target);
    const id = formData.get('id');
    const entry = readEntry(formData);

    fetch(`${URL}/entries/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(entry)
    })
        .then((result) => result.json())
        .then((saved) => {
            entries = entries.map((en) => (en.id === saved.id ? saved : en));
            renderEntries();
            e.target.reset();
        });
};

const startEdit = (entry) => {
    const form = document.querySelector('#editEntryForm');
    const [inDate, inTime] = entry.checkIn.split('T');
    const [outDate, outTime] = entry.checkOut.split('T');

    form.elements.id.value = entry.id;
    form.elements.checkInDate.value = inDate;
    form.elements.checkInTime.value = inTime.slice(0, 5);
    form.elements.checkOutDate.value = outDate;
    form.elements.checkOutTime.value = outTime.slice(0, 5);
    form.elements.categorySelect.value = entry.categoryId ?? '';
    const tagIds = (entry.tagIds ?? []).map(String);
    Array.from(form.elements.tagIds.options).forEach((option) => {
        option.selected = tagIds.includes(option.value);
    });
};

const deleteEntry = (id) => {
    fetch(`${URL}/entries/${id}`, {
        method: 'DELETE'
    }).then(() => {
        entries = entries.filter((en) => en.id !== id);
        renderEntries();
    });
};

const indexEntries = () => {
    fetch(`${URL}/entries`)
        .then((result) => result.json())
        .then((result) => {
            entries = result;
            renderEntries();
        });
};

const createCell = (text) => {
    const cell = document.createElement('td');
    cell.innerText = text;
    return cell;
};

const renderTitleTable = (selector, items, onDelete) => {
    const display = document.querySelector(selector);
    display.innerHTML = '';
    items.forEach((item) => {
        const row = document.createElement('tr');
        row.appendChild(createCell(item.id));
        row.appendChild(createCell(item.title));

        const actions = document.createElement('td');
        const deleteButton = document.createElement('button');
        deleteButton.innerText = 'Delete';
        deleteButton.addEventListener('click', () => onDelete(item.id));
        actions.appendChild(deleteButton);
        row.appendChild(actions);

        display.appendChild(row);
    });
};

const renderCategories = () => renderTitleTable('#categoryDisplay', categories, deleteCategory);

const renderTags = () => renderTitleTable('#tagDisplay', tags, deleteTag);

const renderEntries = () => {
    const display = document.querySelector('#entryDisplay');
    display.innerHTML = '';
    entries.forEach((entry) => {
        const row = document.createElement('tr');
        row.appendChild(createCell(entry.id));
        row.appendChild(createCell(new Date(entry.checkIn).toLocaleString()));
        row.appendChild(createCell(new Date(entry.checkOut).toLocaleString()));
        row.appendChild(createCell(categories.find((c) => c.id === entry.categoryId)?.title ?? ''));
        row.appendChild(createCell(
            (entry.tagIds ?? []).map((id) => tags.find((t) => t.id === id)?.title).filter(Boolean).join(', ')
        ));

        const actions = document.createElement('td');
        const editButton = document.createElement('button');
        editButton.innerText = 'Edit';
        editButton.addEventListener('click', () => startEdit(entry));
        actions.appendChild(editButton);

        const deleteButton = document.createElement('button');
        deleteButton.innerText = 'Delete';
        deleteButton.addEventListener('click', () => deleteEntry(entry.id));
        actions.appendChild(deleteButton);
        row.appendChild(actions);

        display.appendChild(row);
    });
};

document.addEventListener('DOMContentLoaded', () => {
    document.querySelector('#createEntryForm').addEventListener('submit', createEntry);
    document.querySelector('#editEntryForm').addEventListener('submit', updateEntry);
    document.querySelector('#createCategoryForm').addEventListener('submit', createCategory);
    document.querySelector('#createTagForm').addEventListener('submit', createTag);
    loadCategoriesAndTags();
    indexEntries();
});