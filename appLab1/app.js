const express = require('express');
const bodyParser = require('body-parser');
const multer = require('multer');
const path = require('path');

const app = express();
const PORT = 3000;

const storage = multer.diskStorage({
    destination: (req, file, cb) => {
        cb(null, 'uploads/');
    },
    filename: (req, file, cb) => {
        cb(null, file.originalname);
    }
});

const upload = multer({ storage: storage });

app.set('view engine', 'ejs');
app.set('views', path.join(__dirname, 'views'));

app.use(express.static(path.join(__dirname, 'public')));
app.use(bodyParser.urlencoded({ extended: false }));

let tasks = [];

app.get('/', (req, res) => {
    res.render('index', { tasks });
});

app.post('/add-task', upload.single('file'), (req, res) => {
    const { title, status, dueDate } = req.body;
    const task = {
        title,
        status,
        dueDate,
        file: req.file ? req.file.filename : null
    };
    tasks.push(task);
    res.redirect('/');
});

app.post('/filter-tasks', (req, res) => {
    const { filterStatus } = req.body;
    const filteredTasks = tasks.filter(task => task.status === filterStatus || filterStatus === 'all');
    res.render('index', { tasks: filteredTasks });
});

app.listen(PORT, () => {
    console.log(`Сервер запущен на http://localhost:${PORT}`);
});