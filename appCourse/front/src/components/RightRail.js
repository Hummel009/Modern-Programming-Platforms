
import { ToastContainer } from 'react-toastify';
export const RightRail = ({
	isLoggedIn
}) => {
	return (
		<aside className='right-rail search'>
			<div>
				<ToastContainer />

				{isLoggedIn ? (
					<div>
						<span className="status" style={{ color: 'green' }}>Вход осуществлён</span>
						<div>
						</div>
					</div>
				) : (
					<div>
						<span className="status" style={{ color: 'red' }}>Вход не осуществлён</span>
					</div>
				)}
			</div>
		</aside>
	)
};