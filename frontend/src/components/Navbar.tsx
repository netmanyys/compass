import React from "react";
import { useAuth } from "../context/AuthContext";
import { Link } from "react-router-dom";

const Navbar: React.FC = () => {
  const { user, logout } = useAuth();

  return (
    <div className="navbar">
      <div className="brand">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" aria-hidden="true">
          <circle cx="12" cy="12" r="10" stroke="#E2E8F0" strokeWidth="1.5" />
          <path d="M12 6l4 8-8-4 4-4z" fill="#F97316" />
          <circle cx="12" cy="12" r="2" fill="#E2E8F0" />
        </svg>
        <Link to="/dashboard">Compass Inventory</Link>
      </div>
      <div>
        {user && <span className="role">{user.role}</span>}
        <button className="btn secondary" onClick={logout}>
          Logout
        </button>
      </div>
    </div>
  );
};

export default Navbar;
