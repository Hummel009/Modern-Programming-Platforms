import React, {
	useState,
	useEffect,
	useCallback
} from 'react';
import axios from 'axios';
import './App.css'
import { RightRail } from './components/RightRail.js'
import { GlobalNavigation } from './components/NavGlobal.js'
import { LocalNavigation } from './components/NavLocal.js'
import { Register } from './components/PageRegister.js'
import { Login } from './components/PageLogin.js'
import { Profile } from './components/PageProfile.js'
import { Cart } from './components/PageCart.js'
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
	const [userData, setUserData] = useState({
		id: null,
		username: '',
		balance: 0
	});

	const [newUsername, setNewUsername] = useState('');
	const [newPassword, setNewPassword] = useState('');

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

	const fetchUserData = async () => {
		try {
			const tokenCookie = document.cookie.split('; ').find(row => row.startsWith('jwt='));
			const token = tokenCookie ? tokenCookie.split('=')[1] : null;

			const response = await axios.post('http://localhost:2999/profile', {
				token: token
			});

			const userData = response.data;
			console.log("Данные пользователя:", userData);

			setUserData(userData);
		} catch (error) {
			console.error("Ошибка при получении данных пользователя:", error);
		}
	};

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

	const handleChangeUsername = async (e) => {
		e.preventDefault();
		try {
			const tokenCookie = document.cookie.split('; ').find(row => row.startsWith('jwt='));
			const token = tokenCookie ? tokenCookie.split('=')[1] : null;

			await axios.post('http://localhost:2999/change-username',
			{
				token: token,
				newCredential: newUsername
			});
		} catch (error) {
			alert('Change username failed. Please check your credentials.');
		}
	};

	const handleChangePassword = async (e) => {
		e.preventDefault();
		try {
			const tokenCookie = document.cookie.split('; ').find(row => row.startsWith('jwt='));
			const token = tokenCookie ? tokenCookie.split('=')[1] : null;

			await axios.post('http://localhost:2999/change-password',
			{
				token: token,
				newCredential: newPassword
			});
		} catch (error) {
			alert('Change password failed. Please check your credentials.');
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
		setFormData({
			...formData,
			[e.target.name]: e.target.value
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
					<LocalNavigation
						fetchUserData={fetchUserData}
					/>
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
									<Register
                                        isLoggedIn = {isLoggedIn}
                                        handleRegisterSubmit = {handleRegisterSubmit}
                                        setRegisterData = {setRegisterData}
                                        registerData = {registerData}
                                    />
								} />
								<Route path="/login" element={
									<Login
										isLoggedIn = {isLoggedIn}
										handleLoginSubmit = {handleLoginSubmit}
										setLoginData = {setLoginData}
										loginData = {loginData}
									/>
								} />
								<Route path="/profile" element={
									<Profile
										isLoggedIn = {isLoggedIn}
										userData = {userData}
										newUsername = {newUsername}
										setNewUsername = {setNewUsername}
										handleChangeUsername = {handleChangeUsername}
										newPassword = {newPassword}
										setNewPassword = {setNewPassword}
										handleChangePassword = {handleChangePassword}
									/>
								} />
								<Route path="/cart" element={
									<Cart
									/>
								} />
							</Routes>
						</main>
						<RightRail
							isLoggedIn = {isLoggedIn}
							deleteCookieToken = {deleteCookieToken}
						/>
					</div>
				</div>
			</div>
		</Router>
	);
}

export default App;