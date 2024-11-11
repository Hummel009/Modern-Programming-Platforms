export const Login = (
	{
		isLoggedIn,
		handleLoginSubmit,
		setLoginData,
		loginData
    }
) => (
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
);