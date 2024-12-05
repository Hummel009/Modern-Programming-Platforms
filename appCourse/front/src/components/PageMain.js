import axios from 'axios';
import React, {
	useState
} from 'react';
import Cookies from 'js-cookie';
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

export const PageMain = ({
	books,
	authors,
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

	const handleFilterBooks = async (author) => {
		const response = await axios.post('http://localhost:2999/books/filter', {
			author: author
		});

		setBooks(response.data);
		setCurrentPage(1);
	};

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

			toast.success('Кніга дадана ў кош!');
		} catch (error) {
			toast.error('Памылка!');
		}
	};

	return (
		<div>
			<h1>
				<span id="lang-enter">Галоўная</span>
			</h1>

			<div>
				Тут вы можаце знайсці вельмі многа розных кніг.
			</div>

			<br />

			<select onChange={(e) => handleFilterBooks(e.target.value)}>
				{authors.map(author => (
					<option key={author} value={author}>{author}</option>
				))}
			</select>

			<div className="navi">
				{currentBooks.map(book => (
					<div key={book.id}>
						<div className="preamble">
							<div className="title">«{book.title}»</div>
							<div className="author">{book.author}</div>
							<div className="description">{book.description}</div>
						</div>
						<button className="wds-button price" onClick={(e) => handleAddToCart(book)}>Дадаць у кош ({book.price}$)</button>
						<img src={book.imgPath} width="100%" height="auto" alt="" />
					</div>
				))}
			</div>

			<button className="wds-button prev" onClick={handlePrevPage} disabled={currentPage === 1}>Папярэдняя</button>
			<button className="wds-button next" onClick={handleNextPage} disabled={currentPage === totalPages}>Наступная</button>
		</div>
	)
}