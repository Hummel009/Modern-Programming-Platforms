import React, {
	useState,
	useEffect,
	useCallback
} from 'react';
import axios from 'axios';
import './App.css'
import { GlobalNavigation, LocalNavigation } from './AppComponents.js'
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';

function App() {
	const [tasks, setTasks] = useState(new Map());
	const [formData, setFormData] = useState({
		title: '',
		status: 'pending',
		dueDate: '',
		file: null,
	});
	const [loginData, setLoginData] = useState({
		username: '',
		password: ''
	});
	const [registerData, setRegisterData] = useState({
		username: '',
		password: ''
	});
	const [isLoggedIn, setIsLoggedIn] = useState(false);

	const tryUseCookieToken = useCallback(async () => {
		try {
			const tokenCookie = document.cookie.split('; ').find(row => row.startsWith('jwt='));
			const token = tokenCookie ? tokenCookie.split('=')[1] : null;

			await axios.post('http://localhost:2999/token', {
				token: token
			});

			setIsLoggedIn(true);

			fetchTasks();
		} catch (error) {
			console.error("Error using cookie token:", error);
		}
	}, []);

	useEffect(() => {
		tryUseCookieToken()
	}, [tryUseCookieToken]);

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
		const response = await axios.post('http://localhost:2999/filter-tasks',
		{
			filter: filter
		});
		const tasksMap = new Map(Object.entries(response.data));
		setTasks(tasksMap);
	};

	const editTask = async (index) => {
		const taskToEdit = tasks.get(index);
		const title = prompt("Введите новое название задачи:", taskToEdit.title);

		if (title) {
			const response = await axios.put('http://localhost:2999/edit-task',
			{
				index: index, title: title
			});
			const tasksMap = new Map(Object.entries(response.data));
			setTasks(tasksMap);
		}
	};

	const handleLoginSubmit = async (e) => {
		e.preventDefault();
		try {
			const response = await axios.post('http://localhost:2999/login',
			{
				username: loginData.username,
				password: loginData.password
			});

			document.cookie = `jwt=${response.data}; path=/; secure=false; SameSite=Lax`;
			setIsLoggedIn(true);

			fetchTasks();
		} catch (error) {
			alert('Login failed. Please check your credentials.');
		}
	};

	const handleRegisterSubmit = async (e) => {
		e.preventDefault();
		try {
			const response = await axios.post('http://localhost:2999/register',
			{
				username: registerData.username,
				password: registerData.password
			});

			document.cookie = `jwt=${response.data}; path=/; secure=false; SameSite=Lax`;
			setIsLoggedIn(true);

			fetchTasks();
		} catch (error) {
			alert('Register failed. Please check your credentials.');
		}
	};

	const deleteCookieToken = () => {
		try {
			document.cookie = "jwt=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/";

			setIsLoggedIn(false);

			fetchTasks();
		} catch (error) {
			alert('Error occurred while trying to delete the cookie.');
		}
	}

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

	const handleRegisterChange = (e) => {
		const {
			name,
			value
		} = e.target;
		setRegisterData({
			...registerData,
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
		<Router>
			<div className="page-background"></div>
			<GlobalNavigation />
			<div className="main-container">
				<div className="page-background"></div>
				<div className="page-container">
					<LocalNavigation />
					<div class="page has-right-rail">
						<main className="page-main">
							<Routes>
								<Route path="/" element={
									<div>
										<h1>
											<span id="lang-enter">Список задач</span>
										</h1>
										<form onSubmit={handleSubmit} className="main-fieldset">
											<legend id="lang-translate">Добавить задачу</legend>
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
											<button type="submit" className="wds-button">Добавить задачу</button>
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
														<span id="fltright">
															<button onClick={() => editTask(id)}>Редактировать</button>
														</span>
														<span id="fltright">Дата: {task.dueDate}</span>
													</li>
												);
											})}
										</ul>

										<button onClick={clearTasks} className="wds-button">Очистить список задач</button>
									</div>
								}/>
								<Route path="/register" element={
									<div>
										<h1>
											<span id="lang-enter">Рэгістрацыя</span>
										</h1>
										<form onSubmit={handleRegisterSubmit} className="main-fieldset">
											<input
												type="text"
												name="username"
												placeholder="Імя ўдзельніка"
												onChange={handleRegisterChange}
												required
											/>
											<input
												type="password"
												name="password"
												placeholder="Пароль"
												onChange={handleRegisterChange}
												required
											/>
											{isLoggedIn ? (
												<button disabled type="submit" className="wds-button">Зарэгістравацца</button>
											) : (
												<button type="submit" className="wds-button">Зарэгістравацца</button>
											)}
										</form>
									</div>
								} />
								<Route path="/login" element={
									<div>
										<h1>
											<span id="lang-enter">Уваход</span>
										</h1>
										<form onSubmit={handleLoginSubmit} className="main-fieldset">
											<input
												type="text"
												name="username"
												placeholder="Імя ўдзельніка"
												onChange={handleLoginChange}
												required
											/>
											<input
												type="password"
												name="password"
												placeholder="Пароль"
												onChange={handleLoginChange}
												required
											/>
											{isLoggedIn ? (
												<button disabled type="submit" className="wds-button">Увайсці</button>
											) : (
												<button type="submit" className="wds-button">Увайсці</button>
											)}
										</form>
									</div>
								} />
								<Route path="/profile" element={
									<div>
										<h1>
											<span id="lang-enter">Профіль</span>
										</h1>
									</div>
								} />
								<Route path="/cart" element={
									<div>
										<h1>
											<span id="lang-enter">Кош</span>
										</h1>
									</div>
								} />
							</Routes>
						</main>
						<aside className='right-rail search'>
							{isLoggedIn ? (
								<div>
									<h1>
										<span className = "status" style={{ color: 'green' }}>Уваход здзейснены</span>
									</h1>
									<button onClick={deleteCookieToken} className="wds-button">Выйсці</button>
								</div>
							) : (
								<div>
									<h1>
										<span className = "status" style={{ color: 'red' }}>Уваход не здзейснены</span>
									</h1>
								</div>
							)}
						</aside>
					</div>
				</div>
			</div>
		</Router>
	);
}

export default App;