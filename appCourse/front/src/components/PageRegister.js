export const Register = (
	{
		isLoggedIn,
		handleRegisterSubmit,
		setRegisterData,
		registerData
    }
) => (
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
);