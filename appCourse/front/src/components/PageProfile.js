import axios from 'axios';
import React, { useState } from 'react';
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
				rechargeBalance: rechargeBalance
			}, {
				headers: {
					Authorization: `Bearer ${token}`
				}
			});

			handleFetchUserData();

			toast.success('Баланс пополнен!');
		} catch (error) {
			toast.error('Ошибка!');
		}
	};

	const handleChangePassword = async (e) => {
		e.preventDefault();
		try {
			const token = Cookies.get('jwt');

			await axios.put(`http://localhost:2999/api/v1/users/${userData.id}/password`, {
				newPassword: newPassword
			}, {
				headers: {
					Authorization: `Bearer ${token}`
				}
			});

			handleDeleteToken();

			setIsLoggedIn(false);

			toast.success('Пароль изменён!');
		} catch (error) {
			toast.error('Ошибка!');
		}
	};

	const handleChangeUsername = async (e) => {
		e.preventDefault();
		try {
			const token = Cookies.get('jwt');

			await axios.put(`http://localhost:2999/api/v1/users/${userData.id}/username`, {
				newUsername: newUsername
			}, {
				headers: {
					Authorization: `Bearer ${token}`
				}
			});

			handleDeleteToken();

			setIsLoggedIn(false);

			toast.success('Логин изменён!');
		} catch (error) {
			toast.error('Ошибка!');
		}
	};

	return (
		<div>
			<h1>
				<span>Профиль</span>
			</h1>
			{isLoggedIn ? (
				<div>
					<button onClick={handleDeleteToken} className="wds-button">Выйти</button>

					<br/><br/>

					<div>Имя пользователя: {userData.username}</div>
					<div>Баланс: {userData.balance}$</div>

					<div className="change">
						<form onSubmit={handleChangeUsername}>
							<input
								type="text"
								name="username"
								placeholder="Новое имя пользователя"
								value={newUsername}
								onChange={(e) => setNewUsername(e.target.value)}
								required
							/>
							<button type="submit" className="wds-button">Изменить логин</button>
						</form>

						<form onSubmit={handleChangePassword}>
							<input
								type="password"
								name="password"
								placeholder="Новый пароль"
								value={newPassword}
								onChange={(e) => setNewPassword(e.target.value)}
								required
							/>
							<button type="submit" className="wds-button">Изменить пароль</button>
						</form>

						<form onSubmit={handleRechargeBalance}>
							<input
								type="balance"
								name="balance"
								placeholder="Сумма пополнения"
								value={rechargeBalance}
								onChange={(e) => setRechargeBalance(e.target.value)}
								required
							/>
							<button type="submit" className="wds-button">Пополнить баланс</button>
						</form>
				 </div>

				 {orders.map((order, count) => {
					 const totalCost = order.books.reduce((sum, book, bookIndex) => {
						 return sum + (book.price * order.quantities[bookIndex]);
					 }, 0);

					 return (
						 <div key={count}>
							 <br />
							 <h2 className="sale">Покупка №{count + 1}</h2>
							 <h2 className="sum">Общая стоимость: {totalCost.toFixed(2)}$</h2>
							 <div className="navi">
								 {order.books.map((book, bookIndex) => (
									 <div key={book.id}>
										 <div className="preamble">
											 <div className="title">«{book.name}»</div>
											 <div className="author">{book.authorName}</div>
											 <div className="description">{book.desc}</div>
										 </div>
										 <div className="quantity">Количество: {order.quantities[bookIndex]}</div>
										 <img src={book.image} width="100%" height="auto" alt="" />
									 </div>
								 ))}
							 </div>
						 </div>
					 );
				 })}
			 </div>
		 ) : (
			 <p>Пожалуйста, войдите в систему, чтобы увидеть свои данные.</p>
		 )}
	  </div>
   )
}
