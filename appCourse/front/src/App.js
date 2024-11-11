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
	const [books, setBooks] = useState([]);
	const [authors, setAuthors] = useState([]);

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

			fetchBooks();
		} catch (error) {
		}
	}, []);

	const deleteCookieToken = () => {
		try {
			document.cookie = "jwt=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/";

			setIsLoggedIn(false);

			fetchBooks();
		} catch (error) {
			alert('Error occurred while trying to delete the cookie.');
		}
	}

	useEffect(() => {
		tryUseCookieToken()
	}, [tryUseCookieToken]);

	const fetchUserData = async () => {
		try {
			const tokenCookie = document.cookie.split('; ').find(row => row.startsWith('jwt='));
			const token = tokenCookie ? tokenCookie.split('=')[1] : null;

			const response = await axios.post('http://localhost:2999/profile', {
				token: token
			});

			const userData = response.data;

			setUserData(userData);
		} catch (error) {
		}
	};

	const fetchBooks = async () => {
		const response1 = await axios.get('http://localhost:2999/books');
		setBooks(response1.data);

		const response2 = await axios.get('http://localhost:2999/books/authors');
		setAuthors(response2.data);
	};

	const filterBooks = async (filter) => {
		const response = await axios.post('http://localhost:2999/books/filter',
		{
			filter: filter
		});
		setBooks(response.data);
	};

	return (
		<Router>
			<div className="page-background"></div>
			<GlobalNavigation />
			<div className="main-container">
				<div className="page-background"></div>
				<div className="page-container">
					<LocalNavigation/>
					<div className="page has-right-rail">
						<main className="page-main">
							<Routes>
								<Route path="/" element={
									<div>
										<h1>
											<span id="lang-enter">Галоўная</span>
										</h1>

										<div>
											Тут вы можаце знайсці вельмі многа розных кніг.
										</div>

										<br/>

										<select onChange={(e) => filterBooks(e.target.value)}>
											<option value="all">Все</option>
											<option value="pending">В ожидании</option>
											<option value="completed">Завершено</option>
										</select>

										<ul>
											{books.map(book => (
												<li key={book.id}>
													<strong>{book.title} </strong>
													<span>Author: {book.author} </span>
													<span>Description: {book.description} </span>
													<span>Price: {book.price} </span>
													<span>Img Path: {book.imgPath} </span>
												</li>
											))}
										</ul>
									</div>
								}/>
								<Route path="/register" element={
									<Register
										isLoggedIn = {isLoggedIn}
										setIsLoggedIn = {setIsLoggedIn}
										registerData = {registerData}
										setRegisterData = {setRegisterData}
										fetchBooks = {fetchBooks}
										fetchUserData = {fetchUserData}
									/>
								} />
								<Route path="/login" element={
									<Login
										isLoggedIn = {isLoggedIn}
										setIsLoggedIn = {setIsLoggedIn}
										loginData = {loginData}
										setLoginData = {setLoginData}
										fetchBooks = {fetchBooks}
										fetchUserData = {fetchUserData}
									/>
								} />
								<Route path="/profile" element={
									<Profile
										isLoggedIn = {isLoggedIn}
										setIsLoggedIn = {setIsLoggedIn}
										userData = {userData}
										newUsername = {newUsername}
										setNewUsername = {setNewUsername}
										newPassword = {newPassword}
										setNewPassword = {setNewPassword}
										deleteCookieToken = {deleteCookieToken}
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