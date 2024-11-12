import axios from 'axios';
import React, {
	useState
} from 'react';

export const Register = (
	{
		isLoggedIn,
		setIsLoggedIn,
		fetchUserData
	}
) => {
	const [registerData, setRegisterData] = useState({
		username: '',
		password: ''
	});

	const handleRegisterSubmit = async (e) => {
		e.preventDefault();
		try {
			const response = await axios.post('http://localhost:2999/register',
			{
				username: registerData.username,
				password: registerData.password
			});

			document.cookie = `jwt=${response.data}; path=/; secure=false; SameSite=Lax`;
			setIsLoggedIn(true);

			fetchUserData();
		} catch (error) {
			alert('Register failed. Please check your credentials.');
		}
	};

	return (
		<div>
			<h1>
				<span id="lang-enter">Рэгістрацыя</span>
			</h1>
			<form onSubmit={handleRegisterSubmit} className="main-fieldset">
				<input
					type="text"
					name="username"
					placeholder="Імя ўдзельніка"
					onChange={(e) => setRegisterData({
						...registerData,
						[e.target.name]: e.target.value
					})}
					required
				/>
				<input
					type="password"
					name="password"
					placeholder="Пароль"
					onChange={(e) => setRegisterData({
						...registerData,
						[e.target.name]: e.target.value
					})}
					required
				/>
				{isLoggedIn ? (
					<button disabled type="submit" className="wds-button">Зарэгістравацца</button>
				) : (
					<button type="submit" className="wds-button">Зарэгістравацца</button>
				)}
			</form>
		</div>
	)
}