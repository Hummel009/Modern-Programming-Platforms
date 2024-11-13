import axios from 'axios';
import Cookies from 'js-cookie';

export const Cart = ({
	isLoggedIn,
	cartData,
	setCartData,
	userData,
	handleFetchOrders,
	handleFetchUserData
}) => {
	const handleBuyBooks = async () => {
		try {
			const token = Cookies.get('jwt');

			await axios.post('http://localhost:2999/buy', {
				userId: userData.userId,
				token: token,
				cartData: cartData
			});

			handleClearCart();
			handleFetchUserData();
			handleFetchOrders();
		} catch (error) {
			alert('Buy failed. Please check your credentials.');
		}
	};

	const handleClearCart = () => {
		try {
			Cookies.remove('cart');

			setCartData([]);
		} catch (error) {
		}
	}

	return (
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
			<br />
			<button className="wds-button prev" onClick={handleBuyBooks} disabled={cartData.length <= 0 || !isLoggedIn}>Купіць</button>
			<button className="wds-button next" onClick={handleClearCart} disabled={cartData.length <= 0}>Ачысціць кош</button>
			<br />
			<br />
			<div className="navi">
				{cartData.map(book => (
					<div key={book.id}>
						<div className="preamble">
							<div className="title">«{book.title}»</div>
							<div className="author">{book.author}</div>
							<div className="description">{book.description}</div>
						</div>
						<div className="title">Колькасць: {book.quantity}</div>
						<img src={book.imgPath} width="100%" height="auto" alt="" />
					</div>
				))}
			</div>
		</div>
	)
}