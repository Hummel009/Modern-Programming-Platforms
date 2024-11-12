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
import Cookies from 'js-cookie';

function App() {
	const [books, setBooks] = useState([]);
	const [authors, setAuthors] = useState([]);

	const [isLoggedIn, setIsLoggedIn] = useState(false);
	const [userData, setUserData] = useState({
		id: null,
		username: '',
		balance: 0
	});

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

	const handleFetchBooks = useCallback(async () => {
		const response1 = await axios.get('http://localhost:2999/books');
		setBooks(response1.data);

		const response2 = await axios.get('http://localhost:2999/books/authors');
		setAuthors(response2.data);
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

	useEffect(() => {
		handleFetchUserData();
		handleFetchBooks();
		handleUseToken()
	}, [handleFetchBooks, handleFetchUserData, handleUseToken]);

	const handleDeleteToken = () => {
		try {
			Cookies.remove('jwt');

			setIsLoggedIn(false);

			handleFetchBooks();
		} catch (error) {
		}
	}

	const handleFilterBooks = async (author) => {
		const response = await axios.post('http://localhost:2999/books/filter',
		{
			author: author
		});

		setBooks(response.data);
		setCurrentPage(1);
	};

	const handleAddToCart = async (book) => {
		let cart = Cookies.get('cart') ? JSON.parse(Cookies.get('cart')) : [];
        cart.push(book);
        Cookies.set('cart', JSON.stringify(cart), { expires: 7 });
		console.log(cart);
	};

	const [currentPage, setCurrentPage] = useState(1);
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
										userData = {userData}
										handleDeleteToken = {handleDeleteToken}
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
							handleDeleteToken = {handleDeleteToken}
						/>
					</div>
				</div>
			</div>
		</Router>
	);
}

export default App;