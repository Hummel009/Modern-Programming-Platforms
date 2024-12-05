import axios from 'axios';
import React, {
	useState
} from 'react';
import Cookies from 'js-cookie';
import { Link } from 'react-router-dom';
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

export const PageMain = ({
	books,
	authors,
	types,
	years,
	setBooks,
	handleFetchCartData
}) => {
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

	const handleFilterBooksAuthors = async (authorName) => {
		const response = await axios.get(`http://localhost:2999/api/v1/authors/${authorName}/books`);

		setBooks(response.data);
		setCurrentPage(1);
	};

	const handleFilterBooksTypes = async (typeName) => {
		const response = await axios.get(`http://localhost:2999/api/v1/types/${typeName}/books`);

		setBooks(response.data);
		setCurrentPage(1);
	};

	const handleFilterBooksYears = async (year) => {
		const response = await axios.get(`http://localhost:2999/api/v1/books/years/${year}`);

		setBooks(response.data);
		setCurrentPage(1);
	};

	const handleAddBookToCart = async (book) => {
		try {
			let cart = Cookies.get('cart') ? JSON.parse(Cookies.get('cart')) : [];
			const existingBook = cart.find(item => item.id === book.id);

			if (existingBook) {
				existingBook.quantity += 1;
			} else {
				cart.push({ bookId: book.id, quantity: 1 });
			}

			Cookies.set('cart', JSON.stringify(cart), { expires: 7 });

			handleFetchCartData();

			toast.success('Дадана ў кош!');
		} catch (error) {
			toast.error('Памылка!');
		}
	};

	return (
		<div>
			<h1>
				<span>Галоўная</span>
			</h1>

			<div>
				Тут вы можаце знайсці вельмі многа розных кніг.
			</div>

			<br />

			<div className="change">
				<select onChange={(e) => handleFilterBooksAuthors(e.target.value)}>
					<option value="all">Усе аўтары</option>
					{authors.map(author => (
						<option key={author.id} value={author.id}>{author.name}</option>
					))}
				</select>
				<select onChange={(e) => handleFilterBooksTypes(e.target.value)}>
					<option value="all">Усе тыпы</option>
					{types.map(type => (
						<option key={type.id} value={type.id}>{type.name}</option>
					))}
				</select>
				<select onChange={(e) => handleFilterBooksYears(e.target.value)}>
					<option value="all">Усе годы</option>
					{years.map(year => (
						<option key={year} value={year}>Пасля {year}</option>
					))}
				</select>
			</div>

			<div className="navi">
				{currentBooks.map(book => (
					<div key={book.id}>
						<div className="preamble">
							<div className="title">«{book.name}»</div>
							<div className="author">
								<Link to={book.authorName}>{book.authorName}</Link>
							</div>
							<div className="other">{book.typeName}, {book.year}</div>
							<div className="description">{book.desc}</div>
						</div>
						<button className="wds-button price" onClick={(e) => handleAddBookToCart(book)}>Дадаць у кош ({book.price}$)</button>
						<img src={book.image} width="100%" height="auto" alt="" />
					</div>
				))}
			</div>

			<button className="wds-button prev" onClick={handlePrevPage} disabled={currentPage === 1}>Папярэдняя</button>
			<button className="wds-button next" onClick={handleNextPage} disabled={currentPage === totalPages}>Наступная</button>
		</div>
	)
}