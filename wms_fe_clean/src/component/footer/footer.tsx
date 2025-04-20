import React from "react";
import { Github } from "lucide-react";
import { cn } from "@/lib/utils";

const Footer: React.FC = () => {
  return (
    <footer className="bg-gradient-to-r from-indigo-600 to-purple-600 text-white py-4 px-6 mt-auto">
      <div className="container mx-auto flex flex-col sm:flex-row justify-between items-center text-sm">
        <p>
          &copy; {new Date().getFullYear()} WealthScape. All rights reserved.
        </p>
        <div className="mt-2 sm:mt-0 flex items-center gap-4">
          <a
            href="https://github.com/Pipindien/Mini-Project-WMS.git"
            target="_blank"
            rel="noopener noreferrer"
            className="hover:text-gray-200 transition-colors"
            aria-label="GitHub"
          >
            <Github className="w-5 h-5" />
          </a>
          <span className="text-white">Made by TIM 2 JDT 15 - INDIVARA</span>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
