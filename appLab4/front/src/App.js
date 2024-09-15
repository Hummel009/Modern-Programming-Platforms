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
	const [loginData, setLoginData] = useState({
		username: '',
		password: ''
	});
	const [isLoggedIn, setIsLoggedIn] = useState(false);
	const [webSocket, setWebSocket] = useState(null);

	useEffect(() => {
		const ws = new WebSocket('ws://localhost:3000/edit-task');

		ws.onopen = () => console.log('WebSocket connected');
		ws.onmessage = (event) => {
		console.log('test test amogus:', event.data);
			fetchTasks();
		};

		setWebSocket(ws);
	}, []);

	const handleLoginChange = (e) => {
		const {
			name,
			value
		} = e.target;
		setLoginData({
			...loginData,
			[name]: value
		});
	};

	const handleLoginSubmit = async (e) => {
		e.preventDefault();
		try {
			await axios.post('http://localhost:3000/login',
			{
				username: loginData.username,
				password: loginData.password
			},
			{
				withCredentials: true
			});

			setIsLoggedIn(true);
			fetchTasks();
		} catch (error) {
			alert('Login failed. Please check your credentials.');
		}
	};

	const tryUseCookieToken = async () => {
		try {
			await axios.get('http://localhost:3000/token',
			{
				withCredentials: true
			});

			setIsLoggedIn(true);
			fetchTasks();
		} catch (error) {
			alert('Login failed. Please check your credentials.');
		}
	};

	const fetchTasks = async () => {
		try {
			const response = await axios.get('http://localhost:3000/');
			const tasksMap = new Map(Object.entries(response.data));
			setTasks(tasksMap);
		} catch (err) {
			setErrorCode(err.response.status);
		}
	};

	const handleChange = (e) => {
		try {
			const {
				name,
				value
			} = e.target;
			setFormData({
				...formData,
				[name]: value
			});
		} catch (err) {
			setErrorCode(err.response.status);
		}
	};

	const handleFileChange = (e) => {
		try {
			setFormData({
				...formData,
				file: e.target.files[0]
			});
		} catch (err) {
			setErrorCode(err.response.status);
		}
	};

	const handleSubmit = async (e) => {
		try {
			e.preventDefault();
			const form = new FormData();
			for (const key in formData) {
				form.append(key, formData[key]);
			}
			await axios.post('http://localhost:3000/add-task',
			form,
			{
				headers: {
					'Content-Type': 'multipart/form-data',
				},
			});
			fetchTasks();
		} catch (err) {
			setErrorCode(err.response.status);
		}
	};

	const filterTasks = async (filter) => {
		try {
			const response = await axios.post('http://localhost:3000/filter-tasks',
			{
				filterStatus: filter
			},
			{
				headers: {
					'Content-Type': 'application/json'
				}
			});
			const tasksMap = new Map(Object.entries(response.data));
			setTasks(tasksMap);
		} catch (err) {
			setErrorCode(err.response.status);
		}
	};

	const clearTasks = async () => {
		try {
			const response = await axios.delete('http://localhost:3000/clear-tasks');
			fetchTasks()
		} catch (err) {
			setErrorCode(err.response.status);
		}
	};

	const editTask = async (id) => {
		try {
			const taskToEdit = tasks.get(id);
			const newTitle = prompt("Введите новое название задачи:", taskToEdit.title);

			if (newTitle) {
				webSocket.send(JSON.stringify({ id, title: newTitle }));
			}

			fetchTasks()
		} catch (err) {
			setErrorCode(err.response.status);
		}
	};

	const makeError = async () => {
		try {
			await axios.post('http://localhost:3000/jojoreference');
		} catch (err) {
			setErrorCode(err.response.status);
		}
	}

	const returnBack = async () => {
		fetchTasks()
		setErrorCode(null);
	}

	return (
		<div>
			{!isLoggedIn ? (
				<div>
					<form onSubmit={handleLoginSubmit}>
						<input
							type="text"
							name="username"
							placeholder="Username"
							onChange={handleLoginChange}
							required
						/>
						<input
							type="password"
							name="password"
							placeholder="Password"
							onChange={handleLoginChange}
							required
						/>
						<button type="submit">Login</button>
					</form>
					<br />
					<button id="greenfont" onClick={tryUseCookieToken}>Войти через токен</button>
				</div>
			) : (
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
							<button id = "redfont" onClick={makeError}>Совершить ошибку</button>
						</div>
					)}
				</div>
			)}
		</div>
	);
}

function ErrorPage({ message, returnBack }) {
	return (
		<div>
			<h1>Произошла ошибка</h1>
			<p>Код ошибки — {message}</p>
			<button id="greenfont" onClick={returnBack}>Вернуться</button>
		</div>
	);
}

export default App;