import React, {
	useState,
	useEffect
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
	const [errorCode, setErrorCode] = useState(null);
	const [loginData, setLoginData] = useState({
		username: '',
		password: ''
	});
	const [isLoggedIn, setIsLoggedIn] = useState(false);

	useEffect(() => {
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

	const tryUseCookieToken = async () => {
		try {
			const tokenCookie = document.cookie.split('; ').find(row => row.startsWith('jwt='));
			const token = tokenCookie ? tokenCookie.split('=')[1] : null;

			await axios.post('http://localhost:2999/token', {
				token: token
			});

			setIsLoggedIn(true);

			fetchTasks();
		} catch (error) {
			alert('Login failed. Please check your credentials.');
		}
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
								<Route path="/index" element={
										<div>
											{ errorCode ? (
												<ErrorPage message={errorCode} returnBack={returnBack} />
											) : (
												<div>
													<h1>Список задач</h1>
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
													<button id='redfont' onClick={makeError}>Совершить ошибку</button>
												</div>
											)}
										</div>
								}/>
								<Route path="/login" element={
									<div>
										<form onSubmit={handleLoginSubmit} className="main-fieldset">
											<legend id="lang-translate">Вход</legend>
											<input
												type="text"
												name="username"
												placeholder="Имя пользователя"
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
											<button type="submit" className="wds-button">Войти</button>
										</form>
										<br />
										<button id="greenfont" onClick={tryUseCookieToken}>Войти через токен</button>
									</div>
								} />
							</Routes>
						</main>
						<aside className='right-rail search'>
							<div className='noframe'>
								<h1><span id='lang-search'>Пошук</span></h1>
								<form >
									<input className='hstyle' type='search' id='mySearch' name='q'/>
									<button className='wds-button' id='find' type='submit'>Пошук</button>
								</form>
							</div>
							<form>
								<fieldset className='rc-fieldset'>
									<legend id='lang-translate'>Перакласці</legend>
									<label>
										<input type='radio' name='lang' value='be' id='lang-be'/> Беларуская
									</label>
									<label>
										<input type='radio' name='lang' value='en' id='lang-en'/> English
									</label>
								</fieldset>
							</form>
						</aside>
					</div>
				</div>
			</div>
		</Router>
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