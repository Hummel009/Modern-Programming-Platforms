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
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Cookies from 'js-cookie';

function App() {
	const [books, setBooks] = useState([]);
	const [authors, setAuthors] = useState([]);

	const [isLoggedIn, setIsLoggedIn] = useState(false);
	const [profileData, setProfileData] = useState({
		id: null,
		username: '',
		balance: 0
	});
	const [cartData, setCartData] = useState([]);

	const [currentPage, setCurrentPage] = useState(1);

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
			handleClearCart();
		}
	}, []);

	const handleFetchProfileData = useCallback(async () => {
		try {
			const token = Cookies.get('jwt');

			const response = await axios.post('http://localhost:2999/profile', {
				token: token
			});

			setProfileData(response.data);
		} catch (error) {
		}
	}, []);

	const handleFetchCartData = useCallback(async () => {
		try {
			const cartCookie = Cookies.get('cart');
			let cart = cartCookie ? JSON.parse(cartCookie) : [];

			const response = await axios.get('http://localhost:2999/books');

			let chosenBooks = response.data.filter(book =>
				cart.some(item => item.id === book.id)
			);

			const cartDataWithQuantities = chosenBooks.map(book => {
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

	useEffect(() => {
		handleFetchBooks();
		handleFetchAuthors();
		handleUseToken()
		handleFetchCartData();
		handleFetchProfileData();
	}, [handleFetchBooks, handleFetchAuthors, handleUseToken, handleFetchCartData, handleFetchProfileData]);

	const handleFilterBooks = async (author) => {
		const response = await axios.post('http://localhost:2999/books/filter',
		{
			author: author
		});

		setBooks(response.data);
		setCurrentPage(1);
	};

	const handleDeleteToken = () => {
		try {
			Cookies.remove('jwt');

			setIsLoggedIn(false);
		} catch (error) {
		}
	}

	const handleBuyBooks = async () => {
		try {
			const token = Cookies.get('jwt');

			await axios.post('http://localhost:2999/buy', {
				token: token,
				cartData: cartData
			});

			handleClearCart();
			handleFetchProfileData();
		} catch (error) {
		}
	};

	const handleClearCart = () => {
		try {
			Cookies.remove('cart');

			setCartData([]);
		} catch (error) {
		}
	}

	const handleAddToCart = async (book) => {
		try {
			let cart = Cookies.get('cart') ? JSON.parse(Cookies.get('cart')) : [];
			const existingBook = cart.find(item => item.id === book.id);

			if (existingBook) {
				existingBook.quantity += 1;
			} else {
				cart.push({ id: book.id, quantity: 1 });
			}

			Cookies.set('cart', JSON.stringify(cart), { expires: 7 });

			handleFetchCartData();
		} catch (error) {
		}
	};

	const booksPerPage = 4;
	const indexOfLastBook = currentPage * booksPerPage;
	const indexOfFirstBook = indexOfLastBook - booksPerPage;
	const currentBooks = books.slice(indexOfFirstBook, indexOfLastBook);
	const totalPages = Math.ceil(books.length / booksPerPage);

	const handleNextPage = () => {
		if (currentPage < totalPages) {
			setCurrentPage(currentPage + 1);
		}
	};

	const handlePrevPage = () => {
		if (currentPage > 1) {
			setCurrentPage(currentPage - 1);
		}
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

										<select onChange={(e) => handleFilterBooks(e.target.value)}>
											{authors.map(author => (
												<option key={author} value={author}>{author}</option>
											))}
										</select>

										<div className = "navi">
											{currentBooks.map(book => (
												<div key={book.id}>
													<div className="preamble">
														<div className="title">«{book.title}»</div>
														<div className="author">{book.author}</div>
														<div className="description">{book.description}</div>
													</div>
													<button className="wds-button price" onClick={(e) => handleAddToCart(book)}>Дадаць у кош ({book.price}$)</button>
													<img src={book.imgPath} width="100%" height="auto" alt=""/>
												</div>
											))}
										</div>

										<button className="wds-button prev" onClick={handlePrevPage} disabled={currentPage === 1}>Папярэдняя</button>
										<button className="wds-button next" onClick={handleNextPage} disabled={currentPage === totalPages}>Наступная</button>
									</div>
								}/>
								<Route path="/register" element={
									<Register
										isLoggedIn = {isLoggedIn}
										setIsLoggedIn = {setIsLoggedIn}
									/>
								} />
								<Route path="/login" element={
									<Login
										isLoggedIn = {isLoggedIn}
										setIsLoggedIn = {setIsLoggedIn}
									/>
								} />
								<Route path="/profile" element={
									<Profile
										isLoggedIn = {isLoggedIn}
										setIsLoggedIn = {setIsLoggedIn}
										profileData = {profileData}
										handleDeleteToken = {handleDeleteToken}
									/>
								} />
								<Route path="/cart" element={
									<div>
										<h1>
											<span id="lang-enter">Кош</span>
										</h1>
										<div className="total-price">
											{cartData.length > 0 ? (
												<span>Сумарны кошт: {cartData.reduce((total, book) => total + (book.price * book.quantity), 0).toFixed(2)}$</span>
											) : (
												<span>Кош пусты.</span>
											)}
										</div>
										<br/>
										<button className="wds-button prev" onClick={handleBuyBooks} disabled={cartData.length <= 0}>Купіць</button>
										<button className="wds-button next" onClick={handleClearCart} disabled={cartData.length <= 0}>Ачысціць кош</button>
										<br/>
										<br/>
										<div className = "navi">
											{cartData.map(book => (
												<div key={book.id}>
													<div className="preamble">
														<div className="title">«{book.title}»</div>
														<div className="author">{book.author}</div>
														<div className="description">{book.description}</div>
													</div>
													<div className="title">Количество: {book.quantity}</div>
													<img src={book.imgPath} width="100%" height="auto" alt=""/>
												</div>
											))}
										</div>
									</div>
								} />
							</Routes>
						</main>
						<RightRail
							isLoggedIn = {isLoggedIn}
							handleDeleteToken = {handleDeleteToken}
						/>
					</div>
				</div>
			</div>
		</Router>
	);
}

export default App;