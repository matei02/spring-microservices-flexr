import axios from 'axios';

const API_URL = process.env.NEXT_PUBLIC_API_URL
  ? `${process.env.NEXT_PUBLIC_API_URL}/auth`
  : 'http://localhost:8080/auth';

const authService = {
  // Get user by Auth0 ID
  async getUserByAuth0Id(auth0Id, token) {
    const res = await axios.get(`${API_URL}/users/auth0/${auth0Id}`,
      token ? { headers: { Authorization: `Bearer ${token}` } } : undefined
    );
    return res.data;
  },

  // Create a new user (onboarding) - NO TOKEN
  async createUser(userObj) {
    const res = await axios.post(`${API_URL}/users`, userObj);
    return res.data;
  },

  // (Optional) Update user info
  async updateUser(updateObj, token) {
    const res = await axios.put(`${API_URL}/users`, updateObj,
      token ? { headers: { Authorization: `Bearer ${token}` } } : undefined
    );
    return res.data;
  },
};

export default authService;
