
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
						<span className="status" style={{ color: 'green' }}>Уваход здзейснены</span>
						<div>
						</div>
					</div>
				) : (
					<div>
						<span className="status" style={{ color: 'red' }}>Уваход не здзейснены</span>
					</div>
				)}
			</div>
		</aside>
	)
};