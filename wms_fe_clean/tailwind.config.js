// tailwind.config.js
module.exports = {
  content: [
    "./src/**/*.{js,ts,jsx,tsx}", // or wherever your files live
  ],
  theme: {
    extend: {},
  },
  plugins: [require("tailwindcss-animate")],
};
