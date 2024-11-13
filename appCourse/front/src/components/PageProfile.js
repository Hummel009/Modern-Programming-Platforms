import axios from 'axios';
import React, {
	useState
} from 'react';
import Cookies from 'js-cookie';

export const Profile = ({
	isLoggedIn,
	setIsLoggedIn,
	userData,
	orders,
	handleDeleteToken
}) => {
	const [newUsername, setNewUsername] = useState('');
	const [newPassword, setNewPassword] = useState('');

	const handleChangeUsername = async (e) => {
		e.preventDefault();
		try {
			const token = Cookies.get('jwt');

			await axios.put('http://localhost:2999/profile/username', {
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

			await axios.put('http://localhost:2999/profile/password', {
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

					{orders.map(order => {
						return (
							<div>
								<br />
								<h2 className="sale">Пакупка №{order.number}</h2>
								<h2 className="sum">Агульны кошт: {order.totalPrice.toFixed(2)}$</h2>
								<div className="navi">
									{order.books.map((book, index) => (
										<div key={book.id}>
										<div className="preamble">
											<div className="title">«{book.title}»</div>
											<div className="author">{book.author}</div>
											<div className="description">{book.description}</div>
										</div>
										<div className="quantity">Колькасць: {order.quantities[index]}</div>
										<img src={book.imgPath} width="100%" height="auto" alt="" />
										</div>
									))}
								</div>
							</div>
						)
					})}
				</div>
			) : (
				<p>Калі ласка, увайдзіце ў сістэму, каб убачыць свае даныя.</p>
			)}
		</div>
	)
}