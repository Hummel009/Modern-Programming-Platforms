
import { ToastContainer } from 'react-toastify';
export const RightRail = ({
	isLoggedIn,
	handleDeleteToken
}) => {
	return (
		<aside className='right-rail search'>
			<div>
				<ToastContainer />

				{isLoggedIn ? (
					<div>
						<h1>
							<span className="status" style={{ color: 'green' }}>Уваход здзейснены</span>
						</h1>
						<button onClick={handleDeleteToken} className="wds-button">Выйсці</button>
					</div>
				) : (
					<div>
						<h1>
							<span className="status" style={{ color: 'red' }}>Уваход не здзейснены</span>
						</h1>
					</div>
				)}
			</div>
		</aside>
	)
};