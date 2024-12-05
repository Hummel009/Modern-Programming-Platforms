import React, {
	useState,
	useEffect,
	useCallback
} from 'react';
import axios from 'axios';
import './App.css'
import { RightRail } from './components/RightRail.js'
import { NavGlobal } from './components/NavGlobal.js'
import { NavLocal } from './components/NavLocal.js'
import { PageMain } from './components/PageMain.js'
import { PageRegister } from './components/PageRegister.js'
import { PageLogin } from './components/PageLogin.js'
import { PageProfile } from './components/PageProfile.js'
import { PageCart } from './components/PageCart.js'
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Cookies from 'js-cookie';

function App() {
	const [userData, setUserData] = useState({
		userId: null,
		username: '',
		balance: 0
	});

	const [cartData, setCartData] = useState([]);
	const [orders, setOrders] = useState([]);

	const [books, setBooks] = useState([]);
	const [authors, setAuthors] = useState([]);

	const [isLoggedIn, setIsLoggedIn] = useState(false);

	const handleFetchBooks = useCallback(async () => {
		const response = await axios.get('http://localhost:2999/books');
		setBooks(response.data);
	}, []);

	const handleFetchAuthors = useCallback(async () => {
		const response = await axios.get('http://localhost:2999/authors');
		setAuthors(response.data);
	}, []);

	const handleUseToken = useCallback(async () => {
		try {
			const token = Cookies.get('jwt');

			await axios.post('http://localhost:2999/token', {
				token: token
			});

			setIsLoggedIn(true);
		} catch (error) {
		}
	}, []);

	const handleFetchUserData = useCallback(async () => {
		try {
			const token = Cookies.get('jwt');

			const response = await axios.post('http://localhost:2999/profile', {
				token: token
			});

			setUserData(response.data);
		} catch (error) {
		}
	}, []);

	const handleFetchCartData = useCallback(async () => {
		try {
			const cartCookie = Cookies.get('cart');
			let cart = cartCookie ? JSON.parse(cartCookie) : [];

			const bookIds = cart.map(item => item.id);

			const response = await axios.post('http://localhost:2999/books/ids', {
				bookIds: bookIds
			});

			const cartDataWithQuantities = response.data.map(book => {
				const itemInCart = cart.find(item => item.id === book.id);
				return {
					...book,
					quantity: itemInCart ? itemInCart.quantity : 0
				};
			});

			setCartData(cartDataWithQuantities);
		} catch (error) {
		}
	}, []);

	const handleFetchOrders = useCallback(async () => {
		try {
			const token = Cookies.get('jwt');

			const response = await axios.post('http://localhost:2999/profile/orders', {
				userId: userData.userId,
				token: token
			});

			setOrders(response.data);
		} catch (error) {
		}
	}, [userData.userId]);

	useEffect(() => {
		handleFetchBooks();
		handleFetchAuthors();
		handleUseToken()
		handleFetchCartData();
		handleFetchUserData();
		handleFetchOrders();
	}, [handleFetchBooks, handleFetchAuthors, handleUseToken, handleFetchCartData, handleFetchUserData, handleFetchOrders]);

	const handleDeleteToken = () => {
		try {
			Cookies.remove('jwt');

			setUserData([]);

			setIsLoggedIn(false);
		} catch (error) {
		}
	}

	return (
		<Router>
			<div className="page-background"></div>
			<NavGlobal />
			<div className="main-container">
				<div className="page-background"></div>
				<div className="page-container">
					<NavLocal />
					<div className="page has-right-rail">
						<main className="page-main">
							<Routes>
								<Route path="/" element={
									<PageMain
										books={books}
										authors={authors}
										setBooks={setBooks}
										handleFetchCartData={handleFetchCartData}
									/>
								} />
								<Route path="/register" element={
									<PageRegister
										isLoggedIn={isLoggedIn}
										setIsLoggedIn={setIsLoggedIn}
										handleFetchUserData={handleFetchUserData}
									/>
								} />
								<Route path="/login" element={
									<PageLogin
										isLoggedIn={isLoggedIn}
										setIsLoggedIn={setIsLoggedIn}
										handleFetchUserData={handleFetchUserData}
									/>
								} />
								<Route path="/profile" element={
									<PageProfile
										isLoggedIn={isLoggedIn}
										userData={userData}
										orders={orders}
										setIsLoggedIn={setIsLoggedIn}
										handleDeleteToken={handleDeleteToken}
										handleFetchUserData={handleFetchUserData}
									/>
								} />
								<Route path="/cart" element={
									<PageCart
										isLoggedIn={isLoggedIn}
										cartData={cartData}
										userData={userData}
										setCartData={setCartData}
										handleFetchOrders={handleFetchOrders}
										handleFetchUserData={handleFetchUserData}
									/>
								} />
							</Routes>
						</main>
						<RightRail
							isLoggedIn={isLoggedIn}
							handleDeleteToken={handleDeleteToken}
						/>
					</div>
				</div>
			</div>
		</Router>
	);
}

export default App;