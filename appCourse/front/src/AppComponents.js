import React from 'react';

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
			<a href="/index">
				<span id="lang-main">Галоўная</span>
			</a>
			<a href="/registration">
				<span id="lang-register">Рэгістрацыя</span>
			</a>
			<a href="/login">
				<span id="lang-enter">Уваход</span>
			</a>
			<a href="/profile">
				<span id="lang-profile">Профіль</span>
			</a>
			<a href="/cart">
				<span id="lang-cart">Кош</span>
			</a>
			<a href="/admin">
				<span id="lang-admin">Кабінет адміністратара</span>
			</a>
		</nav>
	</div>
);