export const RightRail = (
	{
		isLoggedIn,
		deleteCookieToken
	}
) => (
	<aside className='right-rail search'>
		{isLoggedIn ? (
			<div>
				<h1>
					<span className = "status" style={{ color: 'green' }}>Уваход здзейснены</span>
				</h1>
				<button onClick={deleteCookieToken} className="wds-button">Выйсці</button>
			</div>
		) : (
			<div>
				<h1>
					<span className = "status" style={{ color: 'red' }}>Уваход не здзейснены</span>
				</h1>
			</div>
		)}
	</aside>
);