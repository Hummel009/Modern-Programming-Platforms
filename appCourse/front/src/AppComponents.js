import React from 'react';
import { Link } from 'react-router-dom';

export const GlobalNavigation = () => (
	<div className="global-navigation">
		<div className="dev">
			<img src="avatars/github.jpg" width="100%" height="auto" alt=""/>
			<a href="https://github.com/Hummel009">GitHub</a>
		</div>
		<div className="dev">
			<img src="avatars/discord.jpg" width="100%" height="auto" alt=""/>
			<a href="https://discord.gg/Hx5CksdyvE">Discord</a>
		</div>
		<div className="dev">
			<img src="avatars/youtube.jpg" width="100%" height="auto" alt=""/>
			<a href="https://www.youtube.com/@Hummel009">YouTube</a>
		</div>
	</div>
);

export const LocalNavigation = () => (
	<div className="page-header">
		<nav className="local-navigation">
			<Link to="/index">
				<span id="lang-main">Галоўная</span>
			</Link>
			<Link to="/registration">
				<span id="lang-register">Рэгістрацыя</span>
			</Link>
			<Link to="/login">
				<span id="lang-enter">Уваход</span>
			</Link>
			<Link to="/profile">
				<span id="lang-profile">Профіль</span>
			</Link>
			<Link to="/cart">
				<span id="lang-cart">Кош</span>
			</Link>
		</nav>
	</div>
);