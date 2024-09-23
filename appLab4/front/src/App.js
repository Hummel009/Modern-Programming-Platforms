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

	const [loginWs, setLoginWs] = useState(null);
	const [tokenWs, setTokenWs] = useState(null);
	const [getTasksWs, setGetTasksWs] = useState(null);
	const [clearTasksWs, setClearTasksWs] = useState(null);
	const [editTaskWs, setEditTaskWs] = useState(null);
	const [filterTasksWs, setFilterTasksWs] = useState(null);

	useEffect(() => {
		const loginWs = new WebSocket('ws://localhost:3000/login');
		loginWs.onmessage = function(event) {
			const response = event.data;

			if (response !== "Unauthorized") {
				document.cookie = `jwt=${response}; path=/; secure=false; SameSite=Lax`;
				setIsLoggedIn(true);

				fetchTasks();
			} else {
				alert('Login failed. Please check your credentials.');
			}
		};
		setLoginWs(loginWs);

		const tokenWs = new WebSocket('ws://localhost:3000/token');
		tokenWs.onmessage = function(event) {
			const response = event.data;

			if (response !== "Unauthorized") {
				setIsLoggedIn(true);

				fetchTasks();
			} else {
			 	alert('Login failed. Please check your credentials.');
			}
		};
		setTokenWs(tokenWs);

		const getTasksWs = new WebSocket('ws://localhost:3000/get_tasks');
		getTasksWs.onmessage = function(event) {
			const tasksMap = new Map(Object.entries(JSON.parse(event.data)));
			setTasks(tasksMap);
		};
		setGetTasksWs(getTasksWs);

		const clearTasksWs = new WebSocket('ws://localhost:3000/clear_tasks');
		clearTasksWs.onmessage = function(event) {
			const tasksMap = new Map(Object.entries(JSON.parse(event.data)));
			setTasks(tasksMap);
		};
		setClearTasksWs(clearTasksWs);

		const filterTasksWs = new WebSocket('ws://localhost:3000/filter_tasks');
		filterTasksWs.onmessage = function(event) {
			const tasksMap = new Map(Object.entries(JSON.parse(event.data)));
			setTasks(tasksMap);
		};
		setFilterTasksWs(filterTasksWs);

		const editTaskWs = new WebSocket('ws://localhost:3000/edit_task');
		editTaskWs.onmessage = function(event) {
			const tasksMap = new Map(Object.entries(JSON.parse(event.data)));
			setTasks(tasksMap);
		};
		setEditTaskWs(editTaskWs);
	}, []);

	const fetchTasks = async () => {
		getTasksWs.send("");
	};

	const clearTasks = async () => {
		clearTasksWs.send("");
	};

	const filterTasks = async (filter) => {
		filterTasksWs.send(JSON.stringify({
			filter: filter
		}));
	};

	const editTask = async (index) => {
		const taskToEdit = tasks.get(index);
		const title = prompt("Введите новое название задачи:", taskToEdit.title);

		if (title) {
			editTaskWs.send(JSON.stringify({ index: index, title: title }));
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
		loginWs.send(JSON.stringify({
			username: loginData.username,
			password: loginData.password
		}));
	};

	const tryUseCookieToken = async () => {
		const tokenCookie = document.cookie.split('; ').find(row => row.startsWith('jwt='));
		const token = tokenCookie ? tokenCookie.split('=')[1] : null;

		tokenWs.send(JSON.stringify({
			token: token
		}));
	};

	const handleSubmit = async (e) => {
		e.preventDefault();
		const form = new FormData();
		for (const key in formData) {
			form.append(key, formData[key]);
		}
		await axios.post('http://localhost:2999/add-task',
		form,
		{
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