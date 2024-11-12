import axios from 'axios';
import React, {
	useState
} from 'react';
import Cookies from 'js-cookie';

export const Login = (
	{
		isLoggedIn,
		setIsLoggedIn,
		handleFetchUserData
	}
) => {
	const [loginData, setLoginData] = useState({
		username: '',
		password: ''
	});

	const handleLoginSubmit = async (e) => {
		e.preventDefault();
		try {
			const response = await axios.post('http://localhost:2999/login',
			{
				username: loginData.username,
				password: loginData.password
			});

			Cookies.set('jwt', response.data, { path: '/', secure: false, sameSite: 'Lax' });

			setIsLoggedIn(true);

			handleFetchUserData();
		} catch (error) {
			alert('Login failed. Please check your credentials.');
		}
	};

	return (
		<div>
			<h1>
				<span id="lang-enter">Уваход</span>
			</h1>
			<form onSubmit={handleLoginSubmit} className="main-fieldset">
				<input
					type="text"
					name="username"
					placeholder="Імя ўдзельніка"
					onChange={(e) => setLoginData({
						...loginData,
						[e.target.name]: e.target.value
					})}
					required
				/>
				<input
					type="password"
					name="password"
					placeholder="Пароль"
					onChange={(e) => setLoginData({
						...loginData,
						[e.target.name]: e.target.value
					})}
					required
				/>
				{isLoggedIn ? (
					<button disabled type="submit" className="wds-button">Увайсці</button>
				) : (
					<button type="submit" className="wds-button">Увайсці</button>
				)}
			</form>
		</div>
	)
}