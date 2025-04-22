import js from "@eslint/js";
import globals from "globals";
import reactHooks from "eslint-plugin-react-hooks";
import reactRefresh from "eslint-plugin-react-refresh";
import tseslint from "typescript-eslint";
import jsxA11y from "eslint-plugin-jsx-a11y";
import importPlugin from "eslint-plugin-import";
import promise from "eslint-plugin-promise";
import unicorn from "eslint-plugin-unicorn";
import simpleImportSort from "eslint-plugin-simple-import-sort";
import stylistic from "@stylistic/eslint-plugin";

export default tseslint.config(
  { ignores: ["dist", "node_modules"] },
  {
    extends: [
      js.configs.recommended,
      ...tseslint.configs.recommended,
      "plugin:react/recommended",
      "plugin:react/jsx-runtime",
      "plugin:jsx-a11y/recommended",
      "plugin:import/recommended",
      "plugin:import/typescript",
      "plugin:promise/recommended",
      "plugin:unicorn/recommended",
    ],
    files: ["**/*.{ts,tsx,js,jsx}"],
    languageOptions: {
      ecmaVersion: 2020,
      globals: {
        ...globals.browser,
        ...globals.node,
      },
      parserOptions: {
        project: "./tsconfig.json", // Required for @typescript-eslint/eslint-plugin
      },
    },
    settings: {
      react: {
        version: "detect", // Automatically detect React version
      },
      "import/resolver": {
        typescript: {
          project: "./tsconfig.json",
        },
      },
    },
    plugins: {
      "react-hooks": reactHooks,
      "react-refresh": reactRefresh,
      "jsx-a11y": jsxA11y,
      import: importPlugin,
      promise: promise,
      unicorn: unicorn,
      "simple-import-sort": simpleImportSort,
      "@stylistic": stylistic,
    },
    rules: {
      ...reactHooks.configs.recommended.rules,
      "react-refresh/only-export-components": [
        "warn",
        { allowConstantExport: true },
      ],

      // TypeScript ESLint Rules (Overriding recommended or adding new ones)
      "@typescript-eslint/explicit-function-return-type": "off",
      "@typescript-eslint/no-explicit-any": "warn",
      "@typescript-eslint/no-unused-vars": "warn",
      "@typescript-eslint/no-non-null-assertion": "warn",
      "@typescript-eslint/consistent-type-imports": "warn",
      "@typescript-eslint/no-shadow": "warn", // Consider enabling with more strictness

      // React Rules (Overriding recommended or adding new ones)
      "react/prop-types": "off", // In a TypeScript project, types serve this purpose
      "react/display-name": "off", // Often not necessary with modern React
      "react/jsx-filename-extension": [1, { extensions: [".tsx", ".jsx"] }],

      // Import Rules
      "import/no-unresolved": "error",
      "import/named": "error",
      "import/namespace": "error",
      "import/default": "error",
      "import/export": "error",
      "import/order": "off", // Use simple-import-sort instead
      "import/newline-after-import": "warn",
      "import/no-duplicates": "error",

      // Promise Rules
      "promise/always-return": "warn",
      "promise/no-return-wrap": "warn",
      "promise/catch-or-return": "warn",
      "promise/no-promise-in-callback": "warn",
      "promise/no-callback-in-promise": "warn",

      // Unicorn Rules (Overriding recommended or adding new ones)
      "unicorn/prevent-abbreviations": "warn",
      "unicorn/filename-case": [
        "warn",
        {
          cases: {
            camelCase: true,
            pascalCase: true,
            kebabCase: true,
          },
        },
      ],
      "unicorn/no-null": "off", // Often needed for API responses or state
      "unicorn/consistent-function-scoping": "warn",

      // Simple Import Sort Rules
      "simple-import-sort/imports": "warn",
      "simple-import-sort/exports": "warn",
      "sort-imports": "off", // Disable the built-in sort-imports rule

      // Stylistic Rules (@stylistic/eslint-plugin)
      "@stylistic/indent": ["warn", 2],
      "@stylistic/quotes": ["warn", "single", { avoidEscape: true }],
      "@stylistic/semi": ["warn", "never"],
      "@stylistic/object-curly-spacing": ["warn", "always"],
      "@stylistic/array-bracket-spacing": ["warn", "never"],
      "@stylistic/arrow-parens": ["warn", "always"],
      "@stylistic/comma-dangle": ["warn", "always-multiline"],
      "@stylistic/eol-last": "warn",
      "@stylistic/jsx-curly-brace-presence": ["warn", "prefer"],
      "@stylistic/jsx-quotes": ["warn", "prefer-single"],
      "@stylistic/max-len": ["warn", { code: 120, ignoreComments: true }],
      "@stylistic/no-multiple-empty-lines": ["warn", { max: 2, maxEOF: 1 }],
      "@stylistic/space-before-blocks": "warn",
      "@stylistic/space-before-function-paren": "warn",
      "@stylistic/spaced-comment": "warn",
    },
  }
);
