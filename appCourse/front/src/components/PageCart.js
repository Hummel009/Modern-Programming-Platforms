import axios from 'axios';
import Cookies from 'js-cookie';
import { toast } from 'react-toastify';

export const PageCart = ({
	isLoggedIn,
	cartData,
	userData,
	setCartData,
	handleFetchUserData,
	handleFetchUserOrders
}) => {
	const handleClearCart = () => {
		try {
			Cookies.remove('cart');

			setCartData([]);

			toast.success('Корзина очищена!');
		} catch (error) {
			toast.error('Ошибка!');
		}
	}

	const handleAddUserOrder = async () => {
		try {
			const token = Cookies.get('jwt');
			let cart = Cookies.get('cart') ? JSON.parse(Cookies.get('cart')) : [];

			await axios.post(`http://localhost:2999/api/v1/orders/${userData.id}/add`, {
				cart: cart
			}, {
				headers: {
					Authorization: `Bearer ${token}`
				}
			});

			handleClearCart();
			handleFetchUserData();
			handleFetchUserOrders();

			toast.success('Заказ оформлен!');
		} catch (error) {
			toast.error('Ошибка!');
		}
	};

	const totalPrice = cartData.reduce((total, book) => {
		return total + (book.price * book.quantity)
	}, 0);

	return (
		<div>
			<h1>
				<span>Корзина</span>
			</h1>
			<div className="total-price">
				{cartData.length > 0 ? (
					<span>Общая стоимость: {totalPrice.toFixed(2)}$</span>
				) : (
					<span>Корзина пуста.</span>
				)}
			</div>
			<br />
			<button className="wds-button prev" onClick={handleAddUserOrder} disabled={cartData.length <= 0 || !isLoggedIn}>Купить</button>
			<button className="wds-button next" onClick={handleClearCart} disabled={cartData.length <= 0}>Очистить корзину</button>
			<br />
			<br />
			<div className="navi">
				{cartData.map(book => (
					<div key={book.id}>
						<div className="preamble">
							<div className="title">«{book.name}»</div>
							<div className="author">{book.authorName}</div>
							<div className="description">{book.desc}</div>
						</div>
						<div className="title">Количество: {book.quantity}</div>
						<img src={book.image} width="100%" height="auto" alt="" />
					</div>
				))}
			</div>
		</div>
	);
}
