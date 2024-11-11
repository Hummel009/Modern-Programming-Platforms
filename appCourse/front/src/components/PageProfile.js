import axios from 'axios';

export const Profile = (
	{
		isLoggedIn,
		setIsLoggedIn,
		userData,
		newUsername,
		setNewUsername,
		newPassword,
		setNewPassword,
		deleteCookieToken
	}
) => {
	const handleChangeUsername = async (e) => {
		e.preventDefault();
		try {
			const tokenCookie = document.cookie.split('; ').find(row => row.startsWith('jwt='));
			const token = tokenCookie ? tokenCookie.split('=')[1] : null;

			await axios.post('http://localhost:2999/change-username',
			{
				token: token,
				newUsername: newUsername
			});

			deleteCookieToken();

			setIsLoggedIn(false);
		} catch (error) {
			alert('Change username failed. Please check your credentials.');
		}
	};

	const handleChangePassword = async (e) => {
		e.preventDefault();
		try {
			const tokenCookie = document.cookie.split('; ').find(row => row.startsWith('jwt='));
			const token = tokenCookie ? tokenCookie.split('=')[1] : null;

			await axios.post('http://localhost:2999/change-password',
			{
				token: token,
				newPassword: newPassword
			});

			deleteCookieToken();

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
				</div>
			) : (
				<p>Калі ласка, увайдзіце ў сістэму, каб убачыць свае даныя.</p>
			)}
		</div>
	)
}