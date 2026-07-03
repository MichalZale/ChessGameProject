import axios from "axios";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";
const API_URL = `${API_BASE_URL}/api/game`;

export function createGame(gameOptions) {
    return axios.post(API_URL, gameOptions)
        .then(res => res.data)
        .catch(error => {
            throw new Error(error.response?.data?.message || "Failed to create game.");
        });
}

export function joinGame(gameCode, userID) {
    return axios.post(`${API_URL}/join`, {
        gameCode,
        userID
    })
    .then(res => res.data)
    .catch(error => {
        throw new Error(error.response?.data?.message || "Failed to join game.");
    });
}