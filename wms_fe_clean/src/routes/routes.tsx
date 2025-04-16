import { createBrowserRouter } from "react-router-dom";
import Login from "../pages/auth/loginPage";
import RegisterPage from "../pages/auth/registerPage";
import Layout from "../layout";
import ProtectedRoute from "./protectedRoute";
import ProductDetail from "../pages/detail/productDetail";
import Home from "../pages/home/home";
import HomeAdmin from "../pages/admin/homeAdmin";
import ProductForm from "../pages/admin/productForm";
import BuyTransaction from "../pages/transaction/BuyTransaction";

export const router = createBrowserRouter([
  {
    path: "/",
    element: <Login />,
  },
  {
    path: "/register",
    element: <RegisterPage />,
  },
  {
    path: "/unauthorized",
    element: (
      <div className="text-center text-red-600 mt-20 text-xl">
        Access Denied - Unauthorized
      </div>
    ),
  },
  {
    element: <Layout />,
    children: [
      {
        path: "/dashboard",
        element: (
          <ProtectedRoute allowedRoles={["USER"]}>
            <Home />
          </ProtectedRoute>
        ),
      },
      {
        path: "/product/:id",
        element: (
          <ProtectedRoute allowedRoles={["USER"]}>
            <ProductDetail />
          </ProtectedRoute>
        ),
      },
      {
        path: "/buy/:id",
        element: (
          <ProtectedRoute allowedRoles={["USER"]}>
            <BuyTransaction />
          </ProtectedRoute>
        ),
      },
      {
        path: "/dashboardAdmin",
        children: [
          {
            path: "",
            element: (
              <ProtectedRoute allowedRoles={["ADMIN"]}>
                <HomeAdmin />
              </ProtectedRoute>
            ),
          },
          {
            path: "create",
            element: (
              <ProtectedRoute allowedRoles={["ADMIN"]}>
                <ProductForm />
              </ProtectedRoute>
            ),
          },
          {
            path: "edit/:id",
            element: (
              <ProtectedRoute allowedRoles={["ADMIN"]}>
                <ProductForm />
              </ProtectedRoute>
            ),
          },
        ],
      },
    ],
  },
]);
