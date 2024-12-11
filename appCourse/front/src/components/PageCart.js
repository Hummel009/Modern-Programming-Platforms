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

			toast.success('Кош ачышчаны!');
		} catch (error) {
			toast.error('Памылка!');
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

			toast.success('Заказ аформлены!');
		} catch (error) {
			toast.error('Памылка!');
		}
	};

	const totalPrice = cartData.reduce((total, book) => {
		return total + (book.price * book.quantity)
	}, 0);

	return (
		<div>
			<h1>
				<span>Кош</span>
			</h1>
			<div className="total-price">
				{cartData.length > 0 ? (
					<span>Агульны кошт: {totalPrice.toFixed(2)}$</span>
				) : (
					<span>Кош пусты.</span>
				)}
			</div>
			<br />
			<button className="wds-button prev" onClick={handleAddUserOrder} disabled={cartData.length <= 0 || !isLoggedIn}>Купіць</button>
			<button className="wds-button next" onClick={handleClearCart} disabled={cartData.length <= 0}>Ачысціць кош</button>
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
						<div className="title">Колькасць: {book.quantity}</div>
						<img src={book.image} width="100%" height="auto" alt="" />
					</div>
				))}
			</div>
		</div>
	);
}