import axios from 'axios';
import React, {
	useState
} from 'react';
import Cookies from 'js-cookie';
import { toast } from 'react-toastify';

export const PageRegister = ({
	isLoggedIn,
	setIsLoggedIn,
	handleFetchUserData
}) => {
	const [registerData, setRegisterData] = useState({
		username: '',
		password: ''
	});

	const handleRegisterSubmit = async (e) => {
		e.preventDefault();
		try {
			const response = await axios.post(`http://localhost:2999/api/v1/register`, {
				username: registerData.username,
				password: registerData.password
			});

			Cookies.set('jwt', response.data, { path: '/', secure: false, sameSite: 'Lax' });

			setIsLoggedIn(true);

			handleFetchUserData();

			toast.success('Рэгістрацыя прайшла паспяхова!');
		} catch (error) {
			toast.error('Памылка!');
		}
	};

	return (
		<div>
			<h1>
				<span>Рэгістрацыя</span>
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