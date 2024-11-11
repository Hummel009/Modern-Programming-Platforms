export const Profile = (
	{
		isLoggedIn,
		userData,
		newUsername,
		setNewUsername,
		handleChangeUsername,
	    newPassword,
	    setNewPassword,
	    handleChangePassword
    }
) => (
	<div>
		<h1>
			<span id="lang-enter">Профіль</span>
		</h1>
		{isLoggedIn ? (
			<div>
				<div>Імя ўдзельніка: {userData.username}</div>
				<div>Баланс: {userData.balance}$</div>
				<div>
					<input
						type="text"
						placeholder="Новы логін"
						value={newUsername}
						onChange={(e) => setNewUsername(e.target.value)}
					/>
					<button className = "wds-button" onClick={handleChangeUsername}>Змяніць логін</button>
				</div>
				<div>
					<input
						type="text"
						placeholder="Новы пароль"
						value={newPassword}
						onChange={(e) => setNewPassword(e.target.value)}
					/>
					<button className = "wds-button"  onClick={handleChangePassword}>Змяніць пароль</button>
				</div>
			</div>
		) : (
			<p>Калі ласка, увайдзіце ў сістэму, каб убачыць свае дадзеныя.</p>
		)}
	</div>
);