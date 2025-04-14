// src/router/index.tsx
import React from "react";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";

import Login from "../pages/AuthPages/Login";
import Register from "../pages/AuthPages/Register";
import Layout from "../layout";
import Dashboard from "../pages/dashboard";

const AppRouter: React.FC = () => {
  return (
    <Router>
      <Routes>
        {/* Redirect root to login */}
        <Route path="/" element={<Navigate to="/login" replace />} />

        {/* Public Routes */}
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        {/* Protected Routes */}
        <Route path="/app" element={<Layout />}>
          <Route path="dashboard" element={<Dashboard />} />
          {/* Add more authenticated routes here as needed */}
        </Route>
      </Routes>
    </Router>
  );
};

export default AppRouter;
