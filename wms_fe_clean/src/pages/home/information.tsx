import React from "react";

const Information: React.FC = () => {
  return (
    <div className="container mx-auto px-4 py-10">
      <h1 className="text-4xl font-bold text-indigo-700 mb-6">
        About WealthScape
      </h1>
      <p className="text-lg text-gray-700 leading-relaxed">
        WealthScape is a modern, intuitive Wealth Management System designed to
        help individuals take control of their financial future. With features
        like goal-based planning, personalized investment portfolios, real-time
        dashboards, and insightful analytics, WealthScape empowers users to make
        informed financial decisions with ease.
      </p>

      <div className="mt-8">
        <h2 className="text-2xl font-semibold text-indigo-600 mb-4">
          Key Features
        </h2>
        <ul className="list-disc list-inside text-gray-800 space-y-2">
          <li>🎯 Financial goal tracking and progress visualization</li>
          <li>
            📈 Personalized portfolio recommendations based on risk tolerance
          </li>
          <li>💹 Real-time dashboard for portfolio performance monitoring</li>
          <li>🧠 What-If simulator to forecast investment outcomes</li>
          <li>🔒 Secure, user-friendly interface</li>
        </ul>
      </div>

      <div className="mt-8">
        <h2 className="text-2xl font-semibold text-indigo-600 mb-4">
          Our Mission
        </h2>
        <p className="text-gray-700">
          Our mission is to simplify the complexities of personal finance and
          investment by providing a platform that is not only powerful and
          comprehensive, but also simple enough for anyone to use confidently.
        </p>
      </div>

      <div className="mt-8">
        <h2 className="text-2xl font-semibold text-indigo-600 mb-4">
          Made with 💜
        </h2>
        <p className="text-gray-700">Pipin, Isyaroh, Hafidz</p>
      </div>
    </div>
  );
};

export default Information;
