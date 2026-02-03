import React from "react";
import { useAuth } from "../context/AuthContext";
import { Link } from "react-router-dom";

const Navbar: React.FC = () => {
  const { user, logout } = useAuth();

  return (
    <div className="navbar">
      <div className="brand">
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" aria-hidden="true">
          <circle cx="12" cy="12" r="10" stroke="#ffffff" strokeWidth="1.5" />
          <path d="M12 6l4 8-8-4 4-4z" fill="#ffffff" />
          <circle cx="12" cy="12" r="2" fill="#ffffff" />
        </svg>
        <Link to="/dashboard">Compass Cars</Link>
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
