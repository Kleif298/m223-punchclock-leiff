const URL = 'http://localhost:8080';
let entries = [];

const readEntry = (formData) => {
    return {
        checkIn: `${formData.get('checkInDate')}T${formData.get('checkInTime')}`,
        checkOut: `${formData.get('checkOutDate')}T${formData.get('checkOutTime')}`
    };
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

const renderEntries = () => {
    const display = document.querySelector('#entryDisplay');
    display.innerHTML = '';
    entries.forEach((entry) => {
        const row = document.createElement('tr');
        row.appendChild(createCell(entry.id));
        row.appendChild(createCell(new Date(entry.checkIn).toLocaleString()));
        row.appendChild(createCell(new Date(entry.checkOut).toLocaleString()));

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
    indexEntries();
});