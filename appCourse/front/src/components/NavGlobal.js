import { Link } from 'react-router-dom';

export const NavGlobal = (
) => {
	return(
		<div className="global-navigation">
			<nav className="local-navigation">
                <Link to="/">
                    <span>Главная</span>
                </Link>
                <Link to="/register">
                    <span>Регистрация</span>
                </Link>
                <Link to="/login">
                    <span>Вход</span>
                </Link>
                <Link to="/profile">
                    <span>Профиль</span>
                </Link>
                <Link to="/cart">
                    <span>Корзина</span>
                </Link>
            </nav>
		</div>
	)
}