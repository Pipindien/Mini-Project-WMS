import React from "react";

const Footer: React.FC = () => {
  return (
    <footer className="bg-gradient-to-r from-indigo-600 to-purple-600 text-white py-4 px-6 mt-auto">
      <div className="container mx-auto flex flex-col sm:flex-row justify-between items-center text-sm">
        <p>
          &copy; {new Date().getFullYear()} WealthScape. All rights reserved.
        </p>
        <div className="mt-2 sm:mt-0">
          <span className="block sm:inline text-white">
            Made by TIM 2 JDT 15 - INDIVARA
          </span>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
