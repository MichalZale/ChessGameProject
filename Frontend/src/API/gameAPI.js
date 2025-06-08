import axios from "axios";

const API_URL = "http://localhost:8080/api/game";

export function createGame(gameOptions) {
    return axios.post(API_URL, gameOptions)
        .then(res => res.data)
        .then(game => {
            console.log("Created game: ", game);
            return game;
            
        })
        .catch(error => {
            console.error("Failed to create game:", error);
            throw error;
        });
}

export function joinGame(gameCode, userID) {
    return axios.post(`${API_URL}/join`, {
        gameCode,
        userID
    })
    .then(res => res.data)
    .catch(error => {
        console.error("Failed to join game:", error);
        throw error;
    });
}