import { Link } from 'react-router-dom';

export const LocalNavigation = (
	{
		fetchUserData
	}
) => (
	<div className="page-header">
		<nav className="local-navigation">
			<Link to="/">
				<span id="lang-main">Галоўная</span>
			</Link>
			<Link to="/register">
				<span id="lang-register">Рэгістрацыя</span>
			</Link>
			<Link to="/login">
				<span id="lang-enter">Уваход</span>
			</Link>
			<Link to="/profile" onClick={fetchUserData}>
				<span id="lang-profile">Профіль</span>
			</Link>
			<Link to="/cart">
				<span id="lang-cart">Кош</span>
			</Link>
		</nav>
	</div>
);