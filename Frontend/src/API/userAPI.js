import axios from "axios";

const API_URL = "http://localhost:8080/api/user";

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
    console.log('Sending registration request with data:', data);
    
    return axios.post(`${API_URL}/register`, data)
        .then(res => {
            console.log('Registration response:', res.data);
            return res.data;
        })
        .catch(error => {
            console.error('Registration error:', error.response?.data || error.message);
            if (error.response?.data) {
                throw new Error(error.response.data);
            }
            throw new Error("Registration failed. Please try again.");
        });
}

export function createGuestUser() {
    return axios.post(`${API_URL}/guest`)
        .then(res => res.data)
        .catch(error => {
            throw new Error("Failed to create guest user");
        });
}