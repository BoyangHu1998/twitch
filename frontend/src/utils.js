// If you don't want to host your server code and client code together, you can 
// pay AWS to host your server with HTTPS then config the api url endpoints like below
// const SERVER_ORIGIN = '<Your server's url>'; 
const SERVER_ORIGIN = process.env.REACT_APP_API_URL || 'http://localhost:8080';
// const SERVER_ORIGIN =  'http://localhost:8080';
// const SERVER_ORIGIN = '';


const loginUrl = `${SERVER_ORIGIN}/login`;


// CORS CSRF
// Function to get the CSRF token from the cookies
function getCsrfToken() {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; XSRF-TOKEN=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
}

const csrfToken = getCsrfToken('csrftoken'); // Fetch the token from a cookie


export const login = (credential) => {
  return fetch(loginUrl, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    credentials: 'include',
    mode: 'cors',  // Ensure you're explicitly setting this
    body: JSON.stringify(credential)
  }).then((response) => {
    if (response.status !== 200) {
      throw Error('Fail to log in');
    }

    return response.json();
  })
}

const registerUrl = `${SERVER_ORIGIN}/register`;

export const register = (data) => {
  return fetch(registerUrl, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    mode: 'cors',  // Ensure you're explicitly setting this
    body: JSON.stringify(data)
  }).then((response) => {
    if (response.status !== 200) {
      throw Error('Fail to register');
    }
  })
}

const logoutUrl = `${SERVER_ORIGIN}/logout`;

export const logout = () => {
  return fetch(logoutUrl, {
    method: 'POST',
    credentials: 'include',
    mode: 'cors',  // Ensure you're explicitly setting this

  }).then((response) => {
    if (response.status !== 200) {
      throw Error('Fail to log out');
    }
  })
}

const topGamesUrl = `${SERVER_ORIGIN}/game`;

export const getTopGames = () => {
  return fetch(topGamesUrl,{
    mode: 'cors',  // Ensure you're explicitly setting this
  }).then((response) => {
    if (response.status !== 200) {
      throw Error('Fail to get top games');
    }

    return response.json();
  })
}

const getGameDetailsUrl = `${SERVER_ORIGIN}/game?game_name=`;

const getGameDetails = (gameName) => {
  return fetch(`${getGameDetailsUrl}${gameName}`,{
    mode: 'cors',  // Ensure you're explicitly setting this
  }).then((response) => {
    if (response.status !== 200) {
      throw Error('Fail to find the game');
    }

    return response.json();
  });
}

const searchGameByIdUrl = `${SERVER_ORIGIN}/search?game_id=`;

export const searchGameById = (gameId) => {
  return fetch(`${searchGameByIdUrl}${gameId}`, {
    mode: 'cors',  // Ensure you're explicitly setting this
  }).then((response) => {
    if (response.status !== 200) {
      throw Error('Fail to find the game');
    }
    return response.json();
  })
}

export const searchGameByName = (gameName) => {
  return getGameDetails(gameName).then((data) => {
    if (data && data.id) {
      return searchGameById(data.id);
    }

    throw Error('Fail to find the game')
  })
}

const favoriteItemUrl = `${SERVER_ORIGIN}/favorite`;

export const addFavoriteItem = (favItem) => {
  return fetch(favoriteItemUrl, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-CSRF-TOKEN': csrfToken // Include the CSRF token here
    },
    credentials: 'include',
    mode: 'cors',  // Ensure you're explicitly setting this
    body: JSON.stringify({ favorite: favItem })
  }).then((response) => {
    if (response.status !== 200) {
      throw Error('Fail to add favorite item');
    }
  })
}

export const deleteFavoriteItem = (favItem) => {
  return fetch(favoriteItemUrl, {
    method: 'DELETE',
    headers: {
      'Content-Type': 'application/json',
      'X-CSRF-TOKEN': csrfToken // Include the CSRF token here
    },
    credentials: 'include',
    mode: 'cors',  // Ensure you're explicitly setting this
    body: JSON.stringify({ favorite: favItem })
  }).then((response) => {
    if (response.status !== 200) {
      throw Error('Fail to delete favorite item');
    }
  })
}

export const getFavoriteItem = () => {
  return fetch(favoriteItemUrl, {
    headers: {
      'Content-Type': 'application/json',
      'X-CSRF-TOKEN': csrfToken // Include the CSRF token here
    },
    credentials: 'include',
    mode: 'cors',  // Ensure you're explicitly setting this
  }).then((response) => {
    if (response.status !== 200) {
      throw Error('Fail to get favorite item');
    }

    return response.json();
  })
}

const getRecommendedItemsUrl = `${SERVER_ORIGIN}/recommendation`;

export const getRecommendations = () => {
  return fetch(getRecommendedItemsUrl, {
    headers: {
      'Content-Type': 'application/json',
      'X-CSRF-TOKEN': csrfToken // Include the CSRF token here
    },
    credentials: 'include',
    mode: 'cors',  // Ensure you're explicitly setting this
  }).then((response) => {
    if (response.status !== 200) {
      throw Error('Fail to get recommended item');
    }

    return response.json();
  })
}


