import React, {
	useState,
	useEffect
} from 'react';
import axios from 'axios';
import './App.css'

function App() {
	const [tasks, setTasks] = useState(new Map());
	const [formData, setFormData] = useState({
		title: '',
		status: 'pending',
		dueDate: '',
		file: null,
	});
	const [errorCode, setErrorCode] = useState(null);

	useEffect(() => {
		fetchTasks();
	}, []);

	const fetchTasks = async () => {
		const response = await axios.get('http://localhost:2999/get-tasks');
		const tasksMap = new Map(Object.entries(response.data));
		setTasks(tasksMap);
	};

	const clearTasks = async () => {
		const response = await axios.delete('http://localhost:2999/clear-tasks');
		const tasksMap = new Map(Object.entries(response.data));
		setTasks(tasksMap);
	};

	const filterTasks = async (filter) => {
		const response = await axios.post('http://localhost:2999/filter-tasks', {
			filter: filter
		});
		const tasksMap = new Map(Object.entries(response.data));
		setTasks(tasksMap);
	};

	const editTask = async (index) => {
		const taskToEdit = tasks.get(index);
		const title = prompt("Введите новое название задачи:", taskToEdit.title);

		if (title) {
			const response = await axios.put('http://localhost:2999/edit-task', {
				index: index, title: title
			});
			const tasksMap = new Map(Object.entries(response.data));
			setTasks(tasksMap);
		}
	};

	const makeError = async () => {
		try {
			await axios.get('http://localhost:2999/jojoreference');
		} catch (e) {
			setErrorCode(e.response.status);
		}
	}

	const returnBack = async () => {
		fetchTasks()
		setErrorCode(null);
	}

	const handleSubmit = async (e) => {
		e.preventDefault();
		const form = new FormData();
		for (const key in formData) {
			form.append(key, formData[key]);
		}
		await axios.post('http://localhost:2999/add-task', form, {
			headers: {
				'Content-Type': 'multipart/form-data',
			},
		});
		fetchTasks();
	};

	const handleChange = (e) => {
		const {
			name,
			value
		} = e.target;
		setFormData({
			...formData,
			[name]: value
		});
	};

	const handleFileChange = (e) => {
		setFormData({
			...formData,
			file: e.target.files[0]
		});
	};

	return (
		<div>
			{errorCode ? (
				<div>
					<ErrorPage message={errorCode} returnBack={returnBack} />
				</div>
			) : (
				<div>
					<div>
						<h1>Список задач</h1>
						<form onSubmit={handleSubmit}>
							<input
								type="text"
								name="title"
								placeholder="Название задачи"
								required
								onChange={handleChange}
							/>
							<select name="status" onChange={handleChange}>
								<option value="pending">В ожидании</option>
								<option value="completed">Завершено</option>
							</select>
							<input
								type="date"
								name="dueDate"
								required
								onChange={handleChange}
							/>
							<input
								type="file"
								name="file"
								onChange={handleFileChange}
							/>
							<button type="submit">Добавить задачу</button>
						</form>

						<h2>Фильтровать задачи</h2>
						<select onChange={(e) => filterTasks(e.target.value)}>
							<option value="all">Все</option>
							<option value="pending">В ожидании</option>
							<option value="completed">Завершено</option>
						</select>
						<ul>
							{Array.from(tasks).map(([id, task]) => {
								return (
									<li key={id}>
										<strong>{task.title}</strong> - {task.status}
										{task.file && (
											<div>
												<br />
												Прикрепленный файл: {task.file}
											</div>
										)}
										<span id="fltright"><button onClick={() => editTask(id)}>Редактировать</button></span>
										<span id="fltright">Дата: {task.dueDate}</span>
									</li>
								)
							})}
						</ul>
					</div>
					<button onClick={clearTasks}>Очистить список задач</button>
					<button id="redfont" onClick={makeError}>Совершить ошибку</button>
				</div>
			)}
		</div>
	);
}

function ErrorPage({
	message,
	returnBack
}) {
	return (
		<div>
			<h1>Произошла ошибка</h1>
			<p>Код ошибки — {message}</p>
			<button id="greenfont" onClick={returnBack}>Вернуться</button>
		</div>
	);
}

export default App;