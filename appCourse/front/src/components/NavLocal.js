import { Link } from 'react-router-dom';

export const NavLocal = (
) => {
	return (
		<div className="page-header">
			<nav className="local-navigation">
				<Link to="/">
					<span>Галоўная</span>
				</Link>
				<Link to="/register">
					<span>Рэгістрацыя</span>
				</Link>
				<Link to="/login">
					<span>Уваход</span>
				</Link>
				<Link to="/profile">
					<span>Профіль</span>
				</Link>
				<Link to="/cart">
					<span>Кош</span>
				</Link>
			</nav>
		</div>
	)
}