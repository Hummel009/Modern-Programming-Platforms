import axios from 'axios';
import React, {
	useState
} from 'react';
import Cookies from 'js-cookie';
import { toast } from 'react-toastify';

export const PageProfile = ({
	isLoggedIn,
	userData,
	orders,
	setIsLoggedIn,
	handleDeleteToken,
	handleFetchUserData
}) => {
	const [newUsername, setNewUsername] = useState('');
	const [newPassword, setNewPassword] = useState('');
	const [rechargeBalance, setRechargeBalance] = useState('');

	const handleRechargeBalance = async (e) => {
		e.preventDefault();
		try {
			const token = Cookies.get('jwt');

			await axios.put(`http://localhost:2999/api/v1/users/${userData.id}/balance`, {
				userId: userData.id,
				token: token,
				rechargeBalance: rechargeBalance
			});


			handleFetchUserData();

			toast.success('Баланс папоўнены!');
		} catch (error) {
			toast.error('Памылка!');
		}
	};

	const handleChangePassword = async (e) => {
		e.preventDefault();
		try {
			const token = Cookies.get('jwt');

			await axios.put(`http://localhost:2999/api/v1/users/${userData.id}/password`, {
				userId: userData.id,
				token: token,
				newPassword: newPassword
			});

			handleDeleteToken();

			setIsLoggedIn(false);

			toast.success('Пароль зменены!');
		} catch (error) {
			toast.error('Памылка!');
		}
	};

	const handleChangeUsername = async (e) => {
		e.preventDefault();
		try {
			const token = Cookies.get('jwt');

			await axios.put(`http://localhost:2999/api/v1/users/${userData.id}/username`, {
				userId: userData.id,
				token: token,
				newUsername: newUsername
			});

			handleDeleteToken();

			setIsLoggedIn(false);

			toast.success('Логін зменены!');
		} catch (error) {
			toast.error('Памылка!');
		}
	};

	return (
		<div>
			<h1>
				<span>Профіль</span>
			</h1>
			{isLoggedIn ? (
				<div>
					<div>Імя ўдзельніка: {userData.username}</div>
					<div>Баланс: {userData.balance}$</div>

					<div className="change">
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

						<form onSubmit={handleRechargeBalance}>
							<input
								type="balance"
								name="balance"
								placeholder="Колькасць грошай"
								value={rechargeBalance}
								onChange={(e) => setRechargeBalance(e.target.value)}
								required
							/>
							<button type="submit" className="wds-button">Папоўніць баланс</button>
						</form>
					</div>

					{orders.map((order, count) => {
						const totalCost = order.books.reduce((sum, book, bookIndex) => {
							return sum + (book.price * order.quantities[bookIndex]);
						}, 0);

						return (
							<div key={count}>
								<br />
								<h2 className="sale">Пакупка №{count + 1}</h2>
								<h2 className="sum">Агульны кошт: {totalCost.toFixed(2)}$</h2>
								<div className="navi">
									{order.books.map((book, bookIndex) => (
										<div key={book.id}>
											<div className="preamble">
												<div className="title">«{book.name}»</div>
												<div className="author">{book.authorName}</div>
												<div className="description">{book.desc}</div>
											</div>
											<div className="quantity">Колькасць: {order.quantities[bookIndex]}</div>
											<img src={book.image} width="100%" height="auto" alt="" />
										</div>
									))}
								</div>
							</div>
						);
					})}
				</div>
			) : (
				<p>Калі ласка, увайдзіце ў сістэму, каб убачыць свае даныя.</p>
			)}
		</div>
	)
}