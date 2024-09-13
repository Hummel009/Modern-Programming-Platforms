import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css'

function App() {
    const [tasks, setTasks] = useState([]);
    const [formData, setFormData] = useState({
        title: '',
        status: 'pending',
        dueDate: '',
        file: null,
    });

    const fetchTasks = async () => {
        const response = await axios.get('http://localhost:3000/');
        setTasks(response.data);
    };

    useEffect(() => {
        fetchTasks();
    }, []);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleFileChange = (e) => {
        setFormData({ ...formData, file: e.target.files[0] });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const form = new FormData();
        for (const key in formData) {
            form.append(key, formData[key]);
        }
        await axios.post('http://localhost:3000/add-task', form, {
            headers: {
                'Content-Type': 'multipart/form-data',
            },
        });
        fetchTasks();
    };

    const filterTasks = async (filter) => {
		const response = await axios.post('http://localhost:3000/filter-tasks', 
			{ 
				filterStatus: filter 
			}, 
			{ 
				headers: { 
					'Content-Type': 'application/json' 
				} 
			}
		);
		setTasks(response.data);
	};

    return (
        <div>
            <h1>Список задач</h1>
            <form onSubmit={handleSubmit}>
                <input type="text" name="title" placeholder="Название задачи" required onChange={handleChange} />
                <select name="status" onChange={handleChange}>
                    <option value="pending">В ожидании</option>
                    <option value="completed">Завершено</option>
                </select>
                <input type="date" name="dueDate" required onChange={handleChange} />
                <input type="file" name="file" onChange={handleFileChange} />
                <button type="submit">Добавить задачу</button>
            </form>

            <h2>Фильтровать задачи</h2>
            <select onChange={(e) => filterTasks(e.target.value)}>
                <option value="all">Все</option>
                <option value="pending">В ожидании</option>
                <option value="completed">Завершено</option>
            </select>

            <ul>
                {tasks.map((task, index) => (
                    <li key={index}>
                        <strong>{task.title}</strong> - {task.status} - Дата завершения: {task.dueDate}
                        {task.file && (
                            <>
                                <br />
                                Прикрепленный файл: {task.file}
                            </>
                        )}
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default App;