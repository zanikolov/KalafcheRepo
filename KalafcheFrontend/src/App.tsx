import React from 'react';
import { Link } from 'react-router-dom';

const App: React.FC = () => {
  const [currentUser, setCurrentUser] = React.useState({ employeeName: '', username: '' });
  const [menu, setMenu] = React.useState([]);
  const [sideNavVisible, setSideNavVisible] = React.useState(false);
  const [isLoading, setIsLoading] = React.useState(false);
  const [pageTitle, setPageTitle] = React.useState('');

  const isAdmin = () => {
    // logic to check if user is admin
  };

  const isManager = () => {
    // logic to check if user is manager
  };

  const isUser = () => {
    // logic to check if user is user
  };

  const logout = () => {
    // logic to logout
  };

  const toggleSidenav = (position: string) => {
    // logic to toggle sidenav
  };

  return (
    <div className="App">
      {sideNavVisible && (
        <div>
          <div>
            <div>
              <div>{currentUser.employeeName}</div>
              <div>
                {currentUser.username}{' '}
                <a href="" onClick={logout}>
                  {/* <md-icon md-svg-src="images/icons/exit_to_app.svg" aria-label="Отпиши ме"></md-icon> */}
                </a>
              </div>
            </div>
          </div>
          <div>
            {menu.map((item, index) => (
              <div key={index}>
                <Link to={item.link}>
                  <div onClick={() => toggleSidenav('left')}>{item.title}</div>
                </Link>
              </div>
            ))}
          </div>
        </div>
      )}
      <div>
        {sideNavVisible && (
          <div>
            <button onClick={() => toggleSidenav('left')} aria-label="Menu">
              {/* <md-icon md-svg-src="images/icons/menu.svg" aria-label="menu"></md-icon> */}
            </button>
            <h3>{pageTitle}</h3>
          </div>
        )}
        <div>
          <div id="view" className={isLoading ? 'disabled-page' : ''}></div>
          {isLoading && (
            <div>
              {/* <md-progress-circular md-mode="indeterminate"></md-progress-circular> */}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default App;
