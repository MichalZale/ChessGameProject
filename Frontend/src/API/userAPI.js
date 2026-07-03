import axios from "axios";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";
const API_URL = `${API_BASE_URL}/api/user`;

export function login(username, password) {
    return axios.post(`${API_URL}/login`, {
        username,
        password
    })
    .then(res => res.data)
    .catch(error => {
        if (error.response?.status === 401) {
            throw new Error("Invalid username or password");
        }
        throw new Error("Login failed. Please try again.");
    });
}

export function register(username, password, email) {
    const data = { username, password, email };
    
    return axios.post(`${API_URL}/register`, data)
        .then(res => res.data)
        .catch(error => {
            if (error.response?.data) {
                const message = typeof error.response.data === "string"
                    ? error.response.data
                    : error.response.data.message;
                throw new Error(message || "Registration failed. Please try again.");
            }
            throw new Error("Registration failed. Please try again.");
        });
}

export function createGuestUser() {
    return axios.post(`${API_URL}/guest`)
        .then(res => res.data)
        .catch(() => {
            throw new Error("Failed to create guest user");
        });
}
