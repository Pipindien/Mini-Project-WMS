import axios from "axios";

const goalApi = axios.create({
  baseURL: import.meta.env.VITE_GOAL_API,
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
});

export default goalApi;
