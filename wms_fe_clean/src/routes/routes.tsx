import { createBrowserRouter } from "react-router-dom";
import Layout from "../layout";
import HomeAdmin from "../pages/admin/homeAdmin";
import ProductForm from "../pages/admin/productForm";
import ProductDetail from "../pages/detail/productDetail";
import Home from "../pages/home/home";
import Login from "../pages/auth/loginPage";
import ProtectedRoute from "./protectedRoute";
import RegisterPage from "../pages/auth/registerPage";

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
    element: <Layout />,
    children: [
      {
        path: "/dashboard",
        element: (
          <ProtectedRoute>
            <Home />
          </ProtectedRoute>
        ),
      },
      {
        path: "/product/:id",
        element: (
          <ProtectedRoute>
            <ProductDetail />
          </ProtectedRoute>
        ),
      },
      {
        path: "/admin",
        children: [
          {
            path: "",
            element: (
              <ProtectedRoute>
                <HomeAdmin />
              </ProtectedRoute>
            ),
          },
          {
            path: "create",
            element: (
              <ProtectedRoute>
                <ProductForm />
              </ProtectedRoute>
            ),
          },
          {
            path: "edit/:id",
            element: (
              <ProtectedRoute>
                <ProductForm />
              </ProtectedRoute>
            ),
          },
        ],
      },
    ],
  },
]);
