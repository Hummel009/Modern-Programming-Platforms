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
		id: null,
		username: '',
		balance: 0
	});

	const [cartData, setCartData] = useState([]);
	const [orders, setOrders] = useState([]);

	const [books, setBooks] = useState([]);
	const [authors, setAuthors] = useState([]);
	const [types, setTypes] = useState([]);
	const [years, setYears] = useState([]);

	const [isLoggedIn, setIsLoggedIn] = useState(false);

	const handleFetchStorageData = useCallback(async () => {
		const responseBooks = await axios.get(`http://localhost:2999/api/v1/books`);
		setBooks(responseBooks.data);

		const responseYears = await axios.get(`http://localhost:2999/api/v1/books/years`);
		setYears(responseYears.data);

		const responseAuthors = await axios.get(`http://localhost:2999/api/v1/authors`);
		setAuthors(responseAuthors.data);

		const responseTypes = await axios.get(`http://localhost:2999/api/v1/types`);
		setTypes(responseTypes.data);
	}, []);

	const handleUseToken = useCallback(async () => {
		try {
			const token = Cookies.get('jwt');

			await axios.post(`http://localhost:2999/api/v1/token`, {}, {
				headers: {
					Authorization: `Bearer ${token}`
				}
			});

			setIsLoggedIn(true);
		} catch (error) {
		}
	}, []);

	const handleFetchCartData = useCallback(async () => {
		try {
			let cart = Cookies.get('cart') ? JSON.parse(Cookies.get('cart')) : [];

			const response = await axios.get(`http://localhost:2999/api/v1/books`);

			const cartDataWithQuantities = response.data
				.filter(book => cart.some(item => item.bookId === book.id))
				.map(book => {
					const itemInCart = cart.find(item => item.bookId === book.id);
					return {
						...book,
						quantity: itemInCart ? itemInCart.quantity : 0
					};
				});

			setCartData(cartDataWithQuantities);
		} catch (error) {
		}
	}, []);

	const handleFetchUserData = useCallback(async () => {
		try {
			const token = Cookies.get('jwt');

			const response = await axios.get(`http://localhost:2999/api/v1/users/info`, {
				headers: {
					Authorization: `Bearer ${token}`
				}
			});

			setUserData(response.data);
		} catch (error) {
		}
	}, []);

	const handleFetchUserOrders = useCallback(async () => {
		try {
			const token = Cookies.get('jwt');

			const response = await axios.get(`http://localhost:2999/api/v1/orders/${userData.id}`, {
				headers: {
					Authorization: `Bearer ${token}`
				}
			});

			setOrders(response.data);
		} catch (error) {
		}
	}, [userData.id]);

	useEffect(() => {
		handleFetchStorageData();
		handleFetchCartData();

		if (!isLoggedIn) {
			handleUseToken()
		} else {
			handleFetchUserData();
			if (userData.id) {
				handleFetchUserOrders();
			}
		}
	}, [isLoggedIn, userData.id, handleFetchStorageData, handleUseToken, handleFetchCartData, handleFetchUserData, handleFetchUserOrders]);

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
										types={types}
										years={years}
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
										handleFetchUserData={handleFetchUserData}
										handleFetchUserOrders={handleFetchUserOrders}
									/>
								} />
								{authors.map((author, index) => (
									<Route key={index} path={author.name} element={
										<div key={index}>
											<h1>
												<span>{author.name}</span>
											</h1>
											<img className="author-img" src={author.image} width="300px" height="auto" alt="" />
											<div className="author-desc">
												{author.desc.split('\n').map((line, index) => (
													<p key={index}>{line}</p>
												))}
											</div>
										</div>
									} />
								))}
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