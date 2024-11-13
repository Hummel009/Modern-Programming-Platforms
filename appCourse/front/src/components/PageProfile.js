import axios from 'axios';
import React, {
	useState
} from 'react';
import Cookies from 'js-cookie';

export const Profile = ({
	isLoggedIn,
	setIsLoggedIn,
	userData,
	handleDeleteToken,
	orders
}) => {
	const [newUsername, setNewUsername] = useState('');
	const [newPassword, setNewPassword] = useState('');

	const handleChangeUsername = async (e) => {
		e.preventDefault();
		try {
			const token = Cookies.get('jwt');

			await axios.post('http://localhost:2999/profile/username', {
				userId: userData.userId,
				token: token,
				newUsername: newUsername
			});

			handleDeleteToken();

			setIsLoggedIn(false);
		} catch (error) {
			alert('Change username failed. Please check your credentials.');
		}
	};

	const handleChangePassword = async (e) => {
		e.preventDefault();
		try {
			const token = Cookies.get('jwt');

			await axios.post('http://localhost:2999/profile/password', {
				userId: userData.userId,
				token: token,
				newPassword: newPassword
			});

			handleDeleteToken();

			setIsLoggedIn(false);
		} catch (error) {
			alert('Change password failed. Please check your credentials.');
		}
	};

	return (
		<div>
			<h1>
				<span id="lang-enter">Профіль</span>
			</h1>
			{isLoggedIn ? (
				<div>
					<div>Імя ўдзельніка: {userData.username}</div>
					<div>Баланс: {userData.balance}$</div>
					<form onSubmit={handleChangeUsername}>
						<input
							type="text"
							name="username"
							placeholder="Нове імя ўдзельніка"
							value={newUsername}
							onChange={(e) => setNewUsername(e.target.value)}
							required
						/>
						<button type="submit" className="wds-button">Змяніць логін</button>
					</form>

					<form onSubmit={handleChangePassword}>
						<input
							type="password"
							name="password"
							placeholder="Новы пароль"
							value={newPassword}
							onChange={(e) => setNewPassword(e.target.value)}
							required
						/>
						<button type="submit" className="wds-button">Змяніць пароль</button>
					</form>

					{orders.map(order => (
						<div>
							<span className="id0">ORDER ID: {order.orderId}; </span>
							{order.orderItems.map(item => (
								<div>
									<span className="id1">BOOK ID: {item.bookId}; </span>
									<span className="id2">BOOK QUANT: {item.quantity}; </span>
								</div>
							))}
						</div>
					))}
				</div>
			) : (
				<p>Калі ласка, увайдзіце ў сістэму, каб убачыць свае даныя.</p>
			)}
		</div>
	)
}