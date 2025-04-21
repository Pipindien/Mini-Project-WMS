import { createBrowserRouter } from "react-router-dom";
import Login from "../pages/auth/loginPage";
import RegisterPage from "../pages/auth/registerPage";
import Layout from "../layout";
import ProtectedRoute from "./protectedRoute";
import ProductDetail from "../pages/detail/productDetail";
import Home from "../pages/home/home";
import HomeAdmin from "../pages/admin/homeAdmin";
import ProductForm from "../pages/admin/productForm";
import HistoryTransaction from "../pages/transaction/historyTransaction";
import BuyTransaction from "../pages/transaction/buyTransaction";
import PaymentPage from "../pages/transaction/paymentPage";
import SellTransaction from "../pages/transaction/sellTransaction";
import GoalDetailPage from "../pages/portfolio/GoalDetailPage";
import HomePortfolio from "../pages/portfolio/PortfolioPage";
import PortfolioForm from "../pages/portfolio/PortfolioForm";
import Information from "../pages/home/information";
import EditGoalPage from "../pages/portfolio/EditGoalPage";
import RecommendationPage from "../pages/portfolio/RecommendationPage";

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
        path: "/sell/:productId",
        element: (
          <ProtectedRoute allowedRoles={["USER"]}>
            <SellTransaction />
          </ProtectedRoute>
        ),
      },
      {
        path: "/payment/:trxNumber",
        element: (
          <ProtectedRoute allowedRoles={["USER"]}>
            <PaymentPage />
          </ProtectedRoute>
        ),
      },
      {
        path: "/history",
        element: (
          <ProtectedRoute allowedRoles={["USER"]}>
            <HistoryTransaction />
          </ProtectedRoute>
        ),
      },
      {
        path: "/portfolio",
        element: (
          <ProtectedRoute allowedRoles={["USER"]}>
            <HomePortfolio />
          </ProtectedRoute>
        ),
      },
      {
        path: "/portfolio/create",
        element: (
          <ProtectedRoute allowedRoles={["USER"]}>
            <PortfolioForm />
          </ProtectedRoute>
        ),
      },
      {
        path: "/portfolio/detail/:goalId",
        element: (
          <ProtectedRoute allowedRoles={["USER"]}>
            <GoalDetailPage />
          </ProtectedRoute>
        ),
      },
      {
        path: "/portfolio/edit/:goalId",
        element: (
          <ProtectedRoute allowedRoles={["USER"]}>
            <EditGoalPage />
          </ProtectedRoute>
        ),
      },
      {
        path: "/portfolio/recommendation/:goalId",
        element: (
          <ProtectedRoute allowedRoles={["USER"]}>
            <RecommendationPage />
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
      {
        path: "/information",
        element: <Information />,
      },
    ],
  },
]);
